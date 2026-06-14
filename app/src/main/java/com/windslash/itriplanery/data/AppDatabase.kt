package com.windslash.itriplanery.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow
import java.util.UUID

// --- ENTITIES ---

// Top-level container for a trip. Everything else will hang off this so the app can
// support any destination/currency/budget instead of being hardcoded to Japan.
@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val currencyCode: String,   // ISO 4217, e.g. "JPY", "IDR", "USD"
    val budgetAmount: Double,
    val travelerNames: String
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val desc: String,
    val amount: Double,
    val category: String,
    val date: String,
    val time: String
)

@Entity(tableName = "itinerary_checks")
data class ItineraryCheckEntity(
    @PrimaryKey val key: String, // e.g., "d0-m-0"
    val isCompleted: Boolean
)

@Entity(tableName = "food_checks")
data class FoodCheckEntity(
    @PrimaryKey val itemId: String, // e.g., "f_kamo"
    val isCompleted: Boolean
)

// User-editable itinerary steps. These used to live only in memory (seeded from
// ItineraryData) and were lost on restart. They are now persisted here.
@Entity(tableName = "itinerary_steps")
data class StepEntity(
    @PrimaryKey val id: String,
    val dayIndex: Int,
    val period: String, // "morning" | "afternoon" | "evening" | "alternative"
    val time: String,
    val text: String,
    val meta: String,
    val cost: Int,
    val type: String,
    val details: String?,
    val mapQuery: String?
)

// Mappers between the persisted entity and the UI/domain model used everywhere else.
fun StepEntity.toStep(): ItineraryStep =
    ItineraryStep(time = time, text = text, meta = meta, cost = cost, type = type, details = details, mapQuery = mapQuery, id = id)

fun ItineraryStep.toEntity(dayIndex: Int, period: String, id: String = UUID.randomUUID().toString()): StepEntity =
    StepEntity(id = id, dayIndex = dayIndex, period = period, time = time, text = text, meta = meta, cost = cost, type = type, details = details, mapQuery = mapQuery)

// --- DAOS ---

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY id DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(id: Long)
}

@Dao
interface ItineraryCheckDao {
    @Query("SELECT * FROM itinerary_checks")
    fun getAllChecks(): Flow<List<ItineraryCheckEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheck(check: ItineraryCheckEntity)

    @Query("DELETE FROM itinerary_checks WHERE key = :key")
    suspend fun deleteCheck(key: String)
}

@Dao
interface FoodCheckDao {
    @Query("SELECT * FROM food_checks")
    fun getAllFoodChecks(): Flow<List<FoodCheckEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodCheck(check: FoodCheckEntity)
}

@Dao
interface TripDao {
    @Query("SELECT * FROM trips ORDER BY id LIMIT 1")
    fun getActiveTrip(): Flow<TripEntity?>

    @Query("SELECT COUNT(*) FROM trips")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(trip: TripEntity)

    @Update
    suspend fun update(trip: TripEntity)
}

@Dao
interface ItineraryStepDao {
    @Query("SELECT * FROM itinerary_steps")
    fun getAllSteps(): Flow<List<StepEntity>>

    @Query("SELECT COUNT(*) FROM itinerary_steps")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(step: StepEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(steps: List<StepEntity>)

    @Query("DELETE FROM itinerary_steps WHERE id = :id")
    suspend fun deleteById(id: String)
}

// --- DATABASE ---

@Database(
    entities = [TransactionEntity::class, ItineraryCheckEntity::class, FoodCheckEntity::class, StepEntity::class, TripEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun itineraryCheckDao(): ItineraryCheckDao
    abstract fun foodCheckDao(): FoodCheckDao
    abstract fun itineraryStepDao(): ItineraryStepDao
    abstract fun tripDao(): TripDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // v1 -> v2: add the itinerary_steps table without wiping existing data.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `itinerary_steps` (" +
                        "`id` TEXT NOT NULL, `dayIndex` INTEGER NOT NULL, `period` TEXT NOT NULL, " +
                        "`time` TEXT NOT NULL, `text` TEXT NOT NULL, `meta` TEXT NOT NULL, " +
                        "`cost` INTEGER NOT NULL, `type` TEXT NOT NULL, `details` TEXT, `mapQuery` TEXT, " +
                        "PRIMARY KEY(`id`))"
                )
            }
        }

        // v2 -> v3: add the trips table (seeded at runtime via seedTripIfEmpty). SQL matches
        // Room's generated schema for TripEntity so post-migration validation passes.
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `trips` (" +
                        "`id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` TEXT NOT NULL, " +
                        "`destination` TEXT NOT NULL, `startDate` TEXT NOT NULL, `endDate` TEXT NOT NULL, " +
                        "`currencyCode` TEXT NOT NULL, `budgetAmount` REAL NOT NULL, `travelerNames` TEXT NOT NULL)"
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "japan_mission_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .fallbackToDestructiveMigration() // safety net for unhandled version jumps
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// --- REPOSITORY ---

class JapanMissionRepository(private val db: AppDatabase) {
    val allTransactions: Flow<List<TransactionEntity>> = db.transactionDao().getAllTransactions()
    val allItineraryChecks: Flow<List<ItineraryCheckEntity>> = db.itineraryCheckDao().getAllChecks()
    val allFoodChecks: Flow<List<FoodCheckEntity>> = db.foodCheckDao().getAllFoodChecks()
    val allSteps: Flow<List<StepEntity>> = db.itineraryStepDao().getAllSteps()
    val activeTrip: Flow<TripEntity?> = db.tripDao().getActiveTrip()

    suspend fun insertTransaction(tx: TransactionEntity) {
        db.transactionDao().insertTransaction(tx)
    }

    suspend fun deleteTransaction(id: Long) {
        db.transactionDao().deleteTransaction(id)
    }

    suspend fun toggleItineraryCheck(key: String, completed: Boolean) {
        db.itineraryCheckDao().insertCheck(ItineraryCheckEntity(key, completed))
    }

    suspend fun toggleFoodCheck(itemId: String, completed: Boolean) {
        db.foodCheckDao().insertFoodCheck(FoodCheckEntity(itemId, completed))
    }

    // --- Trip ---
    suspend fun updateTrip(trip: TripEntity) = db.tripDao().update(trip)

    // Seed a single default trip on first run, using the values that used to be hardcoded
    // in the UI. Once the multi-trip UI exists this becomes "create trip".
    suspend fun seedTripIfEmpty() {
        val dao = db.tripDao()
        if (dao.count() > 0) return
        dao.upsert(
            TripEntity(
                name = "Japan Mission",
                destination = "Tokyo, Shinjuku",
                startDate = "Oct 9",
                endDate = "Oct 19",
                currencyCode = "JPY",
                budgetAmount = 150000.0,
                travelerNames = "Jessi & Putra"
            )
        )
    }

    // --- Itinerary steps ---
    suspend fun upsertStep(step: StepEntity) = db.itineraryStepDao().upsert(step)

    suspend fun deleteStep(id: String) = db.itineraryStepDao().deleteById(id)

    // Seed the persisted steps from the static itinerary the first time the app runs
    // (empty table). After this, the database is the source of truth and edits stick.
    suspend fun seedStepsIfEmpty(days: List<ItineraryDay>) {
        val dao = db.itineraryStepDao()
        if (dao.count() > 0) return
        val seed = buildList {
            days.forEachIndexed { dayIndex, day ->
                day.morning.forEach { add(it.toEntity(dayIndex, "morning")) }
                day.afternoon.forEach { add(it.toEntity(dayIndex, "afternoon")) }
                day.evening.forEach { add(it.toEntity(dayIndex, "evening")) }
                day.customAlts.forEach { add(it.toEntity(dayIndex, "alternative")) }
            }
        }
        dao.upsertAll(seed)
    }
}
