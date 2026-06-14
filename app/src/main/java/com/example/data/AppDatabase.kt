package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// --- ENTITIES ---

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

// --- DATABASE ---

@Database(
    entities = [TransactionEntity::class, ItineraryCheckEntity::class, FoodCheckEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun itineraryCheckDao(): ItineraryCheckDao
    abstract fun foodCheckDao(): FoodCheckDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "japan_mission_database"
                )
                    .fallbackToDestructiveMigration()
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
}
