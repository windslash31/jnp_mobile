package com.windslash.itriplanery.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.windslash.itriplanery.data.*
import com.windslash.itriplanery.viewmodel.MainViewModel
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// --- THEME STATE ---
enum class ThemeMode { LIGHT, DARK }
val LocalThemeMode = androidx.compose.runtime.compositionLocalOf { ThemeMode.LIGHT }

val isDark @Composable get() = LocalThemeMode.current == ThemeMode.DARK

// --- THEME COLORS ---
val DarkNav: Color @Composable get() = if (isDark) Color(0xFF1E1E1E) else Color(0xFFF7F9FF)
val DeepBlueCard: Color @Composable get() = if (isDark) Color(0xFF2C2C2C) else Color(0xFFFFFFFF)
val AccentCyan: Color @Composable get() = if (isDark) Color(0xFF86BFFF) else Color(0xFF005FB0)
val ActivePurple: Color @Composable get() = if (isDark) Color(0xFF3B4B63) else Color(0xFFD3E4FF)
val AlertRed = Color(0xFFEF4444)
val AccentAmber = Color(0xFFF59E0B)
val GreenM3 = Color(0xFF10B981)

// Strong accents for SOLID buttons/badges/icons that carry a white foreground.
// These are FIXED (don't invert with the theme) so white text/icons stay readable in
// both light and dark mode — unlike the Bento*TextDark colors, which flip to light in
// dark mode and made white-on-them invisible.
val AccentBlueStrong = Color(0xFF005FB0)
val AccentGreenStrong = Color(0xFF1E7A4D)
val AccentRedStrong = Color(0xFFB3261E)
val AccentLilacStrong = Color(0xFF7E3FA0)
val AccentGrayStrong = Color(0xFF4A4E57)

// --- BENTO GRID DESIGN COLOR SCHEME ---
val BentoBackground: Color @Composable get() = if (isDark) Color(0xFF121212) else Color(0xFFF7F9FF)
val BentoTextDark: Color @Composable get() = if (isDark) Color(0xFFE2E2E2) else Color(0xFF1B1B1F)
val BentoTextSubtle: Color @Composable get() = if (isDark) Color(0xFFA0A0A0) else Color(0xFF44474E)

val BentoBubbleBg: Color @Composable get() = if (isDark) Color(0xFF1F1F1F) else Color.White
val BentoBlueBg: Color @Composable get() = if (isDark) Color(0xFF253347) else Color(0xFFD3E4FF)
val BentoBlueTextDark: Color @Composable get() = if (isDark) Color(0xFFD3E4FF) else Color(0xFF001C38)
val BentoBlueTextMedium: Color @Composable get() = if (isDark) Color(0xFF7FB2E8) else Color(0xFF004881)
val BentoBlueAccent: Color @Composable get() = if (isDark) Color(0xFF5CA3EE) else Color(0xFF005FB0)

val BentoLilacBg: Color @Composable get() = if (isDark) Color(0xFF42274A) else Color(0xFFFAD8FD)
val BentoLilacTextDark: Color @Composable get() = if (isDark) Color(0xFFFAD8FD) else Color(0xFF28132E)
val BentoLilacTextSubtle: Color @Composable get() = if (isDark) Color(0xFFE0BBED) else Color(0xFF523F54)

val BentoGreenBg: Color @Composable get() = if (isDark) Color(0xFF2B3A28) else Color(0xFFDCE5D8)
val BentoGreenTextDark: Color @Composable get() = if (isDark) Color(0xFFDCE5D8) else Color(0xFF141E11)
val BentoGreenTextSubtle: Color @Composable get() = if (isDark) Color(0xFFAABEA3) else Color(0xFF424940)

val BentoRedBg: Color @Composable get() = if (isDark) Color(0xFF5E2729) else Color(0xFFFFDAD6)
val BentoRedTextDark: Color @Composable get() = if (isDark) Color(0xFFFFDAD6) else Color(0xFF410002)
val BentoRedTextSubtle: Color @Composable get() = if (isDark) Color(0xFFE99696) else Color(0xFF93000A)

val BentoGrayBg: Color @Composable get() = if (isDark) Color(0xFF32353A) else Color(0xFFE1E2EC)
val BentoGrayTextDark: Color @Composable get() = if (isDark) Color(0xFFE1E2EC) else Color(0xFF191C20)
val BentoGrayTextSubtle: Color @Composable get() = if (isDark) Color(0xFFA0A5A9) else Color(0xFF44474E)


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainTabApp(viewModel: MainViewModel) {
    val darkMode by viewModel.darkMode.collectAsStateWithLifecycle()
    val themeMode = if (darkMode) ThemeMode.DARK else ThemeMode.LIGHT

    androidx.compose.runtime.CompositionLocalProvider(LocalThemeMode provides themeMode) {
        val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = DarkNav,
            bottomBar = {
                BottomNavigation(
                    activeTab = activeTab,
                    onTabSelected = { viewModel.changeTab(it) }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    AnimatedContent(
                        targetState = activeTab,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "tab_transition"
                    ) { tab ->
                        when (tab) {
                            "plan" -> ItineraryScreen(viewModel)
                            "map" -> MapScreen(viewModel)
                            "gourmet" -> GourmetScreen(viewModel)
                            "budget" -> BudgetScreen(viewModel)
                            "guide" -> IntelScreen(viewModel)
                        }
                    }
                }
            }
        }
    }
}

// --- COMMONS: HELPER FOR MAP REDIRECTION ---
fun launchGoogleMaps(context: Context, query: String) {
    try {
        if (query.startsWith("http://") || query.startsWith("https://")) {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(query))
            context.startActivity(webIntent)
            return
        }
        val intentUri = Uri.parse("geo:0,0?q=" + Uri.encode(query))
        val mapIntent = Intent(Intent.ACTION_VIEW, intentUri).apply {
            setPackage("com.google.android.apps.maps")
        }
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(query)))
            context.startActivity(webIntent)
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open map", Toast.LENGTH_SHORT).show()
    }
}

// --- COMMONS: CURRENCY SYMBOL FROM A TRIP'S ISO CODE ---
fun currencySymbol(code: String): String = when (code.uppercase()) {
    "JPY" -> "¥"
    "IDR" -> "Rp"
    "USD" -> "$"
    "EUR" -> "€"
    "GBP" -> "£"
    "KRW" -> "₩"
    "THB" -> "฿"
    else -> try { java.util.Currency.getInstance(code.uppercase()).symbol } catch (e: Exception) { code }
}

// --- THEMED DIALOG ---
// A Compose Dialog runs in a separate composition that does NOT inherit our
// LocalThemeMode, so dialogs render in light theme even in dark mode. This wrapper
// captures the current theme in the parent scope and re-provides it inside the dialog.
@Composable
fun ThemedDialog(onDismissRequest: () -> Unit, content: @Composable () -> Unit) {
    val mode = LocalThemeMode.current
    Dialog(onDismissRequest = onDismissRequest) {
        androidx.compose.runtime.CompositionLocalProvider(LocalThemeMode provides mode) {
            content()
        }
    }
}

// --- COMMONS: CATEGORY PILL FILTER ---
@Composable
fun CategoryPill(
    id: String,
    name: String,
    icon: String,
    isSelected: Boolean,
    selectedBg: Color = BentoBlueBg,
    selectedTextColor: Color = BentoBlueTextDark,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isSelected) selectedBg else DeepBlueCard)
            .border(
                width = 1.dp,
                color = if (isSelected) selectedBg else BentoTextSubtle.copy(alpha = 0.15f),
                shape = CircleShape
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 12.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = name,
                color = if (isSelected) selectedTextColor else BentoTextSubtle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// --- 1. ITINERARY SCREEN ("plan") ---
@Composable
fun ItineraryScreen(viewModel: MainViewModel) {
    val selectedDayIndex by viewModel.selectedDayIndex.collectAsStateWithLifecycle()
    val checks by viewModel.itineraryChecks.collectAsStateWithLifecycle()
    val progressPercent by viewModel.progressPercent.collectAsStateWithLifecycle()
    val itineraryDays by viewModel.itineraryDays.collectAsStateWithLifecycle()
    val trip by viewModel.activeTrip.collectAsStateWithLifecycle()
    val gamification by viewModel.gamificationEnabled.collectAsStateWithLifecycle()
    val symbol = currencySymbol(trip?.currencyCode ?: "JPY")
    val context = LocalContext.current

    val day = itineraryDays.getOrNull(selectedDayIndex) ?: itineraryDays.firstOrNull() ?: return

    var selectedIntelStep by remember { mutableStateOf<ItineraryStep?>(null) }
    var pendingExpenseStep by remember { mutableStateOf<Pair<String, com.windslash.itriplanery.data.ItineraryStep>?>(null) }
    var stepEditorState by remember { mutableStateOf<Triple<String, Int, com.windslash.itriplanery.data.ItineraryStep>?>(null) }
    var stepToDelete by remember { mutableStateOf<Pair<String, Int>?>(null) }
    
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Header rank determination (pure values memoized on progress)
    val rankText = remember(progressPercent) {
        when {
            progressPercent == 100 -> "SHOGUN"
            progressPercent >= 80 -> "SAMURAI"
            progressPercent >= 60 -> "NINJA"
            progressPercent >= 40 -> "VETERAN"
            progressPercent >= 20 -> "SCOUT"
            else -> "ROOKIE"
        }
    }
    val rankIcon = remember(progressPercent) {
        when {
            progressPercent == 100 -> Icons.Filled.Star
            progressPercent >= 80 -> Icons.Filled.Star
            progressPercent >= 60 -> Icons.Filled.Info
            progressPercent >= 40 -> Icons.Filled.Info
            progressPercent >= 20 -> Icons.Filled.Search
            else -> Icons.Filled.Star
        }
    }
    val rankColor = when {
        progressPercent >= 80 -> AccentAmber
        progressPercent >= 50 -> ActivePurple
        else -> AccentCyan
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // --- BENTO DESIGN HEADER ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BentoBackground)
                .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = trip?.name ?: "Itinerary",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = BentoTextSubtle
                    )
                    Text(
                        text = trip?.destination ?: "",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextDark,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Text(
                        text = "Travel Checklist: ${trip?.travelerNames ?: ""}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = BentoTextSubtle,
                        modifier = Modifier.padding(top = 1.dp)
                    )
                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Progress Bar
                        Box(
                            modifier = Modifier
                                .width(80.dp)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(BentoBlueBg)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(fraction = progressPercent / 100f)
                                    .clip(CircleShape)
                                    .background(BentoBlueAccent)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$progressPercent% Complete",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoBlueAccent
                        )
                    }
                }
                
                // Rank Avatar Container (as per HTML spec)
                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(BentoBlueBg)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "👤",
                            fontSize = 22.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    if (gamification) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = rankIcon,
                                contentDescription = "Rank Icon",
                                tint = BentoBlueAccent,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = rankText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = BentoBlueAccent,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }

        // SCROLLABLE CONTENT BODY
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Day selector slider
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(itineraryDays) { index, item ->
                    val isSelected = index == selectedDayIndex
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) BentoBlueBg else DeepBlueCard)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) BentoBlueAccent else BentoTextSubtle.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.selectDay(index) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = item.date,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) BentoBlueAccent else BentoTextSubtle,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = item.day,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isSelected) BentoBlueTextDark else BentoTextDark,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            // DAY CARD DETAILS
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = DeepBlueCard),
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, if (isDark) BentoTextSubtle.copy(alpha = 0.15f) else Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = day.title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = BentoTextDark,
                                lineHeight = 24.sp
                            )
                            Row(
                                modifier = Modifier.padding(top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = "Pin",
                                    tint = BentoBlueAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = day.location,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoTextSubtle
                                )
                            }
                        }
                        FootstepsIndicator(steps = day.steps)
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = BentoTextSubtle.copy(alpha = 0.15f)
                    )

                    // TIMELINE
                    TimelineItemSection("Morning", day.morning, selectedDayIndex, "m", checks, context, currencySymbol = symbol,
                        onCheckChanged = { key, isChecked, item ->
                            viewModel.toggleItineraryCheck(key, isChecked)
                            if (isChecked) {
                                pendingExpenseStep = key to item
                            } else {
                                // optional: undo transaction logic here if requested, but need transaction ID
                            }
                        },
                        onStrategyClicked = { selectedIntelStep = it },
                        onEditClicked = { idx -> stepEditorState = Triple("morning", idx, day.morning[idx]) },
                        onRemoveClicked = { idx -> stepToDelete = Pair("morning", idx) }
                    )
                    TextButton(onClick = { 
                        stepEditorState = Triple("morning", -1, com.windslash.itriplanery.data.ItineraryStep("12:00", "", "", 0, "visit", null, null))
                    }, modifier = Modifier.padding(bottom = 16.dp)) {
                        Text("+ Add Step", color = BentoBlueAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    TimelineItemSection("Afternoon", day.afternoon, selectedDayIndex, "a", checks, context, currencySymbol = symbol,
                        onCheckChanged = { key, isChecked, item ->
                            viewModel.toggleItineraryCheck(key, isChecked)
                            if (isChecked) {
                                pendingExpenseStep = key to item
                            }
                        },
                        onStrategyClicked = { selectedIntelStep = it },
                        onEditClicked = { idx -> stepEditorState = Triple("afternoon", idx, day.afternoon[idx]) },
                        onRemoveClicked = { idx -> stepToDelete = Pair("afternoon", idx) }
                    )
                    TextButton(onClick = { 
                        stepEditorState = Triple("afternoon", -1, com.windslash.itriplanery.data.ItineraryStep("12:00", "", "", 0, "visit", null, null))
                    }, modifier = Modifier.padding(bottom = 16.dp)) {
                        Text("+ Add Step", color = BentoBlueAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    TimelineItemSection("Evening", day.evening, selectedDayIndex, "e", checks, context, currencySymbol = symbol,
                        onCheckChanged = { key, isChecked, item ->
                            viewModel.toggleItineraryCheck(key, isChecked)
                            if (isChecked) {
                                pendingExpenseStep = key to item
                            }
                        },
                        onStrategyClicked = { selectedIntelStep = it },
                        onEditClicked = { idx -> stepEditorState = Triple("evening", idx, day.evening[idx]) },
                        onRemoveClicked = { idx -> stepToDelete = Pair("evening", idx) }
                    )
                    TextButton(onClick = { 
                        stepEditorState = Triple("evening", -1, com.windslash.itriplanery.data.ItineraryStep("12:00", "", "", 0, "visit", null, null))
                    }, modifier = Modifier.padding(bottom = 16.dp)) {
                        Text("+ Add Step", color = BentoBlueAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    TimelineItemSection("Alternative", day.customAlts, selectedDayIndex, "alt", checks, context, currencySymbol = symbol,
                        onCheckChanged = { key, isChecked, item ->
                            viewModel.toggleItineraryCheck(key, isChecked)
                            if (isChecked) {
                                pendingExpenseStep = key to item
                            }
                        },
                        onStrategyClicked = { selectedIntelStep = it },
                        onEditClicked = { idx -> stepEditorState = Triple("alternative", idx, day.customAlts[idx]) },
                        onRemoveClicked = { idx -> stepToDelete = Pair("alternative", idx) }
                    )
                    TextButton(onClick = { 
                        stepEditorState = Triple("alternative", -1, com.windslash.itriplanery.data.ItineraryStep("12:00", "", "", 0, "visit", null, null))
                    }, modifier = Modifier.padding(bottom = 16.dp)) {
                        Text("+ Add Alternative", color = BentoBlueAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            // PRIORITIES & BACKUP OBJECTIVES
            if (day.food != null || day.snack != null || day.alts.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepBlueCard),
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, BentoTextSubtle.copy(alpha = 0.12f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Tactical Objectives",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = BentoTextDark,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        if (day.food != null) {
                            ObjectiveRow(title = "Target Alpha (Food)", obj = day.food, context = context, currencySymbol = symbol)
                        }

                        if (day.snack != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            ObjectiveRow(title = "Target Bravo (Snack)", obj = day.snack, context = context, currencySymbol = symbol)
                        }

                        if (day.alts.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(18.dp))
                            Text(
                                text = "Contingencies (Alts)".uppercase(Locale.getDefault()),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextSubtle,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            day.alts.forEachIndexed { i, alt ->
                                if (i > 0) Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            BentoGrayBg,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .border(BorderStroke(1.dp, BentoGrayTextSubtle.copy(alpha = 0.15f)), RoundedCornerShape(12.dp))
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(alt.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BentoGrayTextDark)
                                        Text(alt.desc, fontSize = 11.sp, color = BentoGrayTextSubtle)
                                    }
                                    IconButton(
                                        onClick = { launchGoogleMaps(context, alt.query) },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(AccentGrayStrong, CircleShape)
                                    ) {
                                        Icon(Icons.Filled.LocationOn, contentDescription = "Navigate", tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // TACTICAL STRATEGY POPUP
    selectedIntelStep?.let { step ->
        ThemedDialog(onDismissRequest = { selectedIntelStep = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoBackground),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, BentoTextSubtle.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tactical Strategy",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = BentoTextDark
                        )
                        IconButton(onClick = { selectedIntelStep = null }) {
                            Icon(Icons.Filled.Close, contentDescription = "Close", tint = BentoTextSubtle)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = step.text,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoBlueAccent
                    )
                    step.details?.let { details ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DeepBlueCard, RoundedCornerShape(12.dp))
                                .border(1.dp, BentoTextSubtle.copy(alpha = 0.10f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = parseHtmlToAnnotatedString(details),
                                fontSize = 12.sp,
                                color = BentoTextDark,
                                lineHeight = 16.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { selectedIntelStep = null }) {
                            Text("Dismiss", color = BentoTextSubtle, fontWeight = FontWeight.Bold)
                        }
                        step.mapQuery?.let { query ->
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    launchGoogleMaps(context, query)
                                    selectedIntelStep = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BentoBlueAccent)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.LocationOn, contentDescription = "Go", tint = Color.White, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Open Maps", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // EXPENSE AUTOMATION MODAL
    pendingExpenseStep?.let { pair ->
        val step = pair.second
        var expenseAmount by remember { mutableStateOf(step.cost.toString()) }
        var expenseCategory by remember {
            mutableStateOf(
                when (step.type) {
                    "food" -> "Food"
                    "transit" -> "Transport"
                    "shopping" -> "Shopping"
                    else -> "Other"
                }
            )
        }

        ThemedDialog(onDismissRequest = { pendingExpenseStep = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepBlueCard),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Auto Expense Logger",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = BentoTextDark
                    )
                    Text(
                        text = "Would you like to record an expense for completing this objective?",
                        fontSize = 11.sp,
                        color = BentoTextSubtle,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = step.text,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = expenseAmount,
                        onValueChange = { expenseAmount = it },
                        label = { Text("Actual Cost ($symbol)", color = BentoTextSubtle) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = BentoTextDark,
                            unfocusedTextColor = BentoTextDark,
                            focusedContainerColor = DeepBlueCard,
                            unfocusedContainerColor = DeepBlueCard,
                            focusedIndicatorColor = AccentCyan,
                            unfocusedIndicatorColor = BentoTextSubtle.copy(alpha=0.3f),
                            cursorColor = AccentCyan
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Category", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoTextSubtle)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Food", "Transport", "Shopping", "Hotel", "Other").forEach { category ->
                            val isSelected = expenseCategory == category
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ActivePurple else Color.Transparent)
                                    .border(1.dp, if (isSelected) AccentCyan else BentoTextSubtle.copy(alpha=0.2f), RoundedCornerShape(8.dp))
                                    .clickable { expenseCategory = category }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(category, color = if (isSelected) AccentCyan else BentoTextSubtle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { 
                                viewModel.toggleItineraryCheck(pair.first, false)
                                pendingExpenseStep = null
                            },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, BentoTextSubtle.copy(alpha=0.15f))
                        ) {
                            Text("Cancel", color = BentoTextSubtle)
                        }
                        OutlinedButton(
                            onClick = { pendingExpenseStep = null },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, BentoTextSubtle.copy(alpha=0.15f))
                        ) {
                            Text("Skip", color = BentoTextDark)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            val costVal = expenseAmount.toDoubleOrNull() ?: 0.0
                            val df = SimpleDateFormat("MMM d", Locale.US)
                            val tf = SimpleDateFormat("HH:mm", Locale.US)
                            val dStr = df.format(Date())
                            val tStr = tf.format(Date())

                            viewModel.addTransaction(
                                desc = step.text.replace(Regex("<[^>]*>"), ""),
                                amount = costVal,
                                category = expenseCategory,
                                date = dStr,
                                time = tStr
                            )
                            pendingExpenseStep = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
                    ) {
                        Text("Log Ledger", color = DarkNav, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }

    // DELETE CONFIRMATION DIALOG
    stepToDelete?.let { (period, idx) ->
        AlertDialog(
            onDismissRequest = { stepToDelete = null },
            title = { Text("Delete Step?", color = BentoTextDark, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this itinerary step? This action can be undone briefly.", color = BentoTextSubtle) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removeStep(selectedDayIndex, period, idx)
                        stepToDelete = null
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Step deleted",
                                actionLabel = "UNDO",
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.undoLastDelete()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { stepToDelete = null }) {
                    Text("Cancel", color = BentoTextSubtle)
                }
            },
            containerColor = BentoBackground
        )
    }

    // ADD NEW STEP DIALOG
    stepEditorState?.let { editorState ->
        val period = editorState.first
        val step = editorState.third
        var newTime by remember { mutableStateOf(step.time) }
        var newTitle by remember { mutableStateOf(step.text) }
        var newDetails by remember { mutableStateOf(step.details ?: "") }
        var newMapUrl by remember { mutableStateOf(step.mapQuery ?: "") }
        var newCost by remember { mutableStateOf(if (step.cost > 0) step.cost.toString() else "") }
        var newCategory by remember { mutableStateOf(step.type.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() }) }
        
        ThemedDialog(onDismissRequest = { stepEditorState = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = BentoBackground),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, BentoTextSubtle.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
                    Text(
                        text = "${if (editorState.second >= 0) "Edit" else "Add"} Step in ${period.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() }}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = BentoTextDark,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    OutlinedTextField(
                        value = newTime,
                        onValueChange = { newTime = it },
                        label = { Text("Time (e.g., 14:00)", color = BentoTextSubtle) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BentoTextDark,
                            unfocusedTextColor = BentoTextDark,
                            focusedBorderColor = BentoBlueAccent,
                            unfocusedBorderColor = BentoTextSubtle.copy(alpha = 0.2f),
                            focusedContainerColor = DeepBlueCard,
                            unfocusedContainerColor = DeepBlueCard,
                            cursorColor = BentoBlueAccent
                        )
                    )
                    
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Title / Place", color = BentoTextSubtle) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BentoTextDark,
                            unfocusedTextColor = BentoTextDark,
                            focusedBorderColor = BentoBlueAccent,
                            unfocusedBorderColor = BentoTextSubtle.copy(alpha = 0.2f),
                            focusedContainerColor = DeepBlueCard,
                            unfocusedContainerColor = DeepBlueCard,
                            cursorColor = BentoBlueAccent
                        )
                    )
                    
                    OutlinedTextField(
                        value = newDetails,
                        onValueChange = { newDetails = it },
                        label = { Text("Intel / Strategy", color = BentoTextSubtle) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BentoTextDark,
                            unfocusedTextColor = BentoTextDark,
                            focusedBorderColor = BentoBlueAccent,
                            unfocusedBorderColor = BentoTextSubtle.copy(alpha = 0.2f),
                            focusedContainerColor = DeepBlueCard,
                            unfocusedContainerColor = DeepBlueCard,
                            cursorColor = BentoBlueAccent
                        )
                    )

                    OutlinedTextField(
                        value = newMapUrl,
                        onValueChange = { newMapUrl = it },
                        label = { Text("Map Query / Link (Optional)", color = BentoTextSubtle) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BentoTextDark,
                            unfocusedTextColor = BentoTextDark,
                            focusedBorderColor = BentoBlueAccent,
                            unfocusedBorderColor = BentoTextSubtle.copy(alpha = 0.2f),
                            focusedContainerColor = DeepBlueCard,
                            unfocusedContainerColor = DeepBlueCard,
                            cursorColor = BentoBlueAccent
                        )
                    )

                    OutlinedTextField(
                        value = newCost,
                        onValueChange = { newCost = it },
                        label = { Text("Estimated Cost ($symbol - Optional)", color = BentoTextSubtle) },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BentoTextDark,
                            unfocusedTextColor = BentoTextDark,
                            focusedBorderColor = BentoBlueAccent,
                            unfocusedBorderColor = BentoTextSubtle.copy(alpha = 0.2f),
                            focusedContainerColor = DeepBlueCard,
                            unfocusedContainerColor = DeepBlueCard,
                            cursorColor = BentoBlueAccent
                        )
                    )

                    Text("Category", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoTextSubtle)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Food", "Transit", "Visit", "Shopping", "Sweets", "Street", "Logistics", "Other").forEach { category ->
                            val isSelected = newCategory == category
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) BentoBlueAccent else Color.Transparent)
                                    .border(1.dp, if (isSelected) BentoBlueAccent else BentoTextSubtle.copy(alpha=0.2f), RoundedCornerShape(8.dp))
                                    .clickable { newCategory = category }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(category, color = if (isSelected) Color.White else BentoTextSubtle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { stepEditorState = null }) {
                            Text("Cancel", color = BentoTextSubtle, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val costVal = newCost.toIntOrNull() ?: 0
                                val step = com.windslash.itriplanery.data.ItineraryStep(
                                    time = newTime,
                                    text = newTitle,
                                    meta = if (editorState.second >= 0) editorState.third.meta else "Added",
                                    cost = costVal,
                                    type = newCategory.lowercase(),
                                    details = if (newDetails.isNotBlank()) newDetails else null,
                                    mapQuery = if (newMapUrl.isNotBlank()) newMapUrl else null
                                )
                                if (editorState.second >= 0) {
                                    viewModel.updateStep(selectedDayIndex, period, editorState.second, step)
                                } else {
                                    viewModel.addStep(selectedDayIndex, period, step)
                                }
                                stepEditorState = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BentoBlueAccent),
                            enabled = newTitle.isNotBlank()
                        ) {
                            Text("Save Step", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp)
    )
    }
}

@Composable
fun TimelineItemSection(
    title: String,
    items: List<ItineraryStep>,
    dayIndex: Int,
    sectionTag: String,
    checks: Map<String, Boolean>,
    context: Context,
    currencySymbol: String = "¥",
    onCheckChanged: (String, Boolean, ItineraryStep) -> Unit,
    onStrategyClicked: (com.windslash.itriplanery.data.ItineraryStep) -> Unit,
    onEditClicked: ((Int) -> Unit)? = null,
    onRemoveClicked: ((Int) -> Unit)? = null
) {
    if (items.isEmpty()) return

    Column {
        Text(
            text = title.uppercase(Locale.getDefault()),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = BentoTextSubtle,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        items.forEachIndexed { i, item ->
            // Prefer the step's stable id; fall back to positional key for any step
            // without one (shouldn't happen once steps are loaded from the database).
            val checkKey = item.id.ifEmpty { "d$dayIndex-$sectionTag-$i" }
            val isCompleted = checks[checkKey] == true

            @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
            val dismissState = androidx.compose.material3.rememberSwipeToDismissBoxState(
                confirmValueChange = { dismissValue ->
                    when (dismissValue) {
                        androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd -> {
                            if (onEditClicked != null && !isCompleted) {
                                onEditClicked(i)
                            }
                            false
                        }
                        androidx.compose.material3.SwipeToDismissBoxValue.EndToStart -> {
                            if (onRemoveClicked != null) {
                                onRemoveClicked(i)
                            }
                            false
                        }
                        else -> false
                    }
                }
            )

            @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
            androidx.compose.material3.SwipeToDismissBox(
                state = dismissState,
                modifier = Modifier.padding(bottom = 12.dp),
                backgroundContent = {
                    val direction = dismissState.dismissDirection
                    if (direction == androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(BentoBlueBg, RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = BentoBlueAccent)
                        }
                    } else if (direction == androidx.compose.material3.SwipeToDismissBoxValue.EndToStart) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFFFEAEA), RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                },
                content = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = if (isCompleted) BentoBubbleBg.copy(alpha = 0.5f) else BentoBubbleBg,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isCompleted) GreenM3.copy(alpha = 0.4f) else BentoTextSubtle.copy(alpha=0.15f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Time box
                        Text(
                            text = item.time,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = BentoBlueAccent,
                            modifier = Modifier.width(44.dp)
                        )

                        // Divider line
                        Box(
                            modifier = Modifier
                                .height(28.dp)
                                .width(1.dp)
                                .background(BentoTextSubtle.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))

                        // Detail
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.text,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCompleted) BentoTextDark.copy(alpha = 0.4f) else BentoTextDark,
                                textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                            )
                            @OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
                            androidx.compose.foundation.layout.FlowRow(
                                modifier = Modifier.padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = item.meta.uppercase(Locale.getDefault()),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BentoTextSubtle,
                                    modifier = Modifier
                                        .background(BentoBlueBg.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                                if (item.cost > 0) {
                                    Text(
                                        text = "$currencySymbol${item.cost}",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoTextSubtle,
                                        modifier = Modifier
                                            .background(BentoBlueBg.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                                if (item.details != null) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(BentoBlueAccent)
                                            .clickable { onStrategyClicked(item) }
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("INTEL", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.White)
                                    }
                                }
                            }
                        }

                        // Quick map icon
                        if (item.mapQuery != null) {
                            IconButton(
                                onClick = { launchGoogleMaps(context, item.mapQuery) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .padding(end = 4.dp)
                                    .testTag("quick_map_$checkKey")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = "Quick Map",
                                    tint = BentoBlueAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        
                        // Checkbox
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .border(2.dp, if (isCompleted) GreenM3 else BentoTextSubtle.copy(alpha = 0.4f), CircleShape)
                                .background(if (isCompleted) GreenM3 else Color.Transparent)
                                .clickable { onCheckChanged(checkKey, !isCompleted, item) }
                                .testTag("check_box_$checkKey"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(Icons.Filled.Check, "", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun FootstepsIndicator(steps: Int) {
    val level = when {
        steps >= 20000 -> 4
        steps >= 15000 -> 3
        steps >= 10000 -> 2
        else -> 1
    }
    val label = when (level) {
        4 -> "SURVIVAL"
        3 -> "HEAVY"
        2 -> "MODERATE"
        else -> "LIGHT"
    }
    val tintColor = when (level) {
        4 -> AlertRed
        3 -> AccentAmber
        2 -> Color(0xFF3B82F6)
        else -> GreenM3
    }

    Column(
        horizontalAlignment = Alignment.End,
        modifier = Modifier
            .background(tintColor.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
            .border(1.dp, tintColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(6.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            (1..4).forEach { i ->
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "",
                    tint = if (i <= level) tintColor else BentoTextSubtle.copy(alpha = 0.3f),
                    modifier = Modifier.size(10.dp)
                )
            }
        }
        Text(
            text = label,
            fontSize = 8.sp,
            fontWeight = FontWeight.Black,
            color = tintColor,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(top = 1.dp)
        )
        Text(
            text = "${steps / 1000}k steps",
            fontSize = 7.sp,
            fontFamily = FontFamily.Monospace,
            color = BentoTextSubtle
        )
    }
}

@Composable
fun ObjectiveRow(title: String, obj: PriorityObjective, context: Context, currencySymbol: String = "¥") {
    val isFood = title.contains("Food", ignoreCase = true)
    val bg = if (isFood) BentoLilacBg else BentoGreenBg
    val textDark = if (isFood) BentoLilacTextDark else BentoGreenTextDark
    val textSubtle = if (isFood) BentoLilacTextSubtle else BentoGreenTextSubtle
    val buttonBg = if (isFood) AccentLilacStrong else AccentGreenStrong
    val buttonTint = Color.White

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, textSubtle.copy(alpha = 0.15f)), RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title.uppercase(Locale.getDefault()),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = textSubtle,
                    letterSpacing = 0.5.sp
                )
                obj.time?.let {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "• $it", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = textSubtle)
                }
            }
            Text(obj.name, fontSize = 14.sp, fontWeight = FontWeight.Black, color = textDark)
            Text("${obj.type} • Est: $currencySymbol${obj.budget}", fontSize = 11.sp, color = textSubtle)
        }
        IconButton(
            onClick = { launchGoogleMaps(context, obj.query) },
            modifier = Modifier
                .size(40.dp)
                .background(buttonBg, CircleShape)
        ) {
            Icon(Icons.Filled.LocationOn, contentDescription = "Navigate to Objective", tint = buttonTint, modifier = Modifier.size(18.dp))
        }
    }
}

// --- 2. MAP SCREEN ("map") ---
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MapScreen(viewModel: MainViewModel) {
    val selectedDayIndex by viewModel.selectedDayIndex.collectAsStateWithLifecycle()
    val itineraryDays by viewModel.itineraryDays.collectAsStateWithLifecycle()
    val activeDayIndex = selectedDayIndex
    val context = LocalContext.current
    var recenterTrigger by remember { mutableStateOf(0) }

    val day = itineraryDays.getOrNull(activeDayIndex) ?: itineraryDays.firstOrNull() ?: return
    val markers = day.markers
    val alts = day.alts

    var selectedMarker by remember { mutableStateOf<com.windslash.itriplanery.data.MapMarker?>(null) }
    var webViewRef by remember { mutableStateOf<android.webkit.WebView?>(null) }

    LaunchedEffect(selectedMarker) {
        selectedMarker?.let { pt ->
            webViewRef?.evaluateJavascript("if(typeof map !== 'undefined') map.setView([${pt.lat}, ${pt.lng}], 16);", null)
        }
    }

    // Recenter the map to fit all pins when the refresh button is tapped.
    LaunchedEffect(recenterTrigger) {
        if (recenterTrigger > 0) {
            webViewRef?.evaluateJavascript(
                "if (typeof map !== 'undefined' && typeof bounds !== 'undefined' && bounds.length > 0) { try { map.fitBounds(bounds, {padding:[50,50]}); } catch(e){} }",
                null
            )
        }
    }

    // HTML with Leaflet and map pins. Keep the map on readable light 'voyager' tiles even
    // in dark mode — legibility matters more here than matching the dark chrome.
    val mapBg = "#F7F9FF"
    val tileUrl = "https://{s}.basemaps.cartocdn.com/rastertiles/voyager/{z}/{x}/{y}{r}.png"
    val allSteps = day.morning + day.afternoon + day.evening + day.customAlts
    val htmlContent = remember(activeDayIndex, day, markers, alts) {
        val markersArray = JSONArray()
        var seqId = 1
        allSteps.forEach { step ->
            val match = markers.find { it.query == step.mapQuery || it.title == step.text }
            val obj = JSONObject().apply {
                if (match != null) {
                    put("lat", match.lat)
                    put("lng", match.lng)
                } else {
                    put("lat", JSONObject.NULL)
                    put("lng", JSONObject.NULL)
                }
                put("type", step.type)
                put("seq", seqId++)
                put("title", step.text)
                put("meta", step.meta)
                put("query", step.mapQuery ?: step.text)
            }
            if (step.mapQuery != null || step.text.isNotBlank()) {
                markersArray.put(obj)
            }
        }
        alts.forEachIndexed { idx, alt ->
            val obj = JSONObject().apply {
                put("lat", JSONObject.NULL)
                put("lng", JSONObject.NULL)
                put("type", "alt")
                put("seq", 0)
                put("title", alt.name)
                put("meta", alt.desc)
                put("query", alt.query)
            }
            markersArray.put(obj)
        }

        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
            <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" crossorigin=""/>
            <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" crossorigin=""></script>
            <style>
                body, html { margin:0; padding:0; height:100%; width:100%; background:$mapBg; overflow: hidden; }
                #map { height: 100vh; width: 100vw; }
                .leaflet-container { background: $mapBg; }
                .station-icon { background: #D3E4FF; color: #001C38; border: 2px solid #005FB0; border-radius: 50%; width:24px; height:24px; text-align:center; line-height:24px; font-size:12px; font-weight:bold; }
                .food-icon { background: #FAD8FD; color: #28132E; border: 2px solid #28132E; border-radius: 50%; width:24px; height:24px; text-align:center; line-height:24px; font-size:12px; font-weight:bold; }
                .number-icon { background: #005FB0; color: white; border: 2px solid white; border-radius: 50%; width:24px; height:24px; text-align:center; line-height:24px; font-size:11px; font-weight:bold; }
                .alt-icon { background: #E1E2EC; color: #191C20; border: 2px solid #44474E; border-radius: 50%; width:24px; height:24px; text-align:center; line-height:24px; font-size:10px; font-weight:bold; }
            </style>
        </head>
        <body>
            <div id="map"></div>
            <script>
                var map = L.map('map', { zoomControl: false }).setView([35.6895, 139.6917], 13);
                L.tileLayer('$tileUrl', {
                    attribution: ''
                }).addTo(map);

                var markersGroup = L.featureGroup().addTo(map);
                var bounds = [];
                var points = $markersArray;
                function esc(s){ return (s||'').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;'); }

                points.forEach(function(pt) {
                    var html = "";
                    var t = (pt.title || "").toLowerCase();
                    var m = (pt.meta || "").toLowerCase();
                    var q = (pt.query || "").toLowerCase();
                    if (pt.type === 'station' || pt.type === 'transit') html = '<div class="station-icon">🚉</div>';
                    else if (pt.type === 'food') {
                        var icon = "🍽️";
                        if (t.includes("sushi") || m.includes("sushi") || q.includes("sushi") || m.includes("seafood") || q.includes("seafood")) icon = "🍣";
                        else if (t.includes("beef") || t.includes("meat") || t.includes("cutlet") || m.includes("beef") || m.includes("meat") || m.includes("cutlet") || q.includes("beef") || q.includes("meat") || q.includes("cutlet") || t.includes("steak") || t.includes("wagyu") || q.includes("steak") || q.includes("wagyu")) icon = "🥩";
                        else if (t.includes("ramen") || t.includes("udon") || m.includes("ramen") || m.includes("udon") || q.includes("ramen") || q.includes("udon") || m.includes("soup") || m.includes("soba") || t.includes("soba") || q.includes("soba")) icon = "🍜";
                        else if (t.includes("coffee") || t.includes("cafe") || m.includes("coffee") || m.includes("cafe") || q.includes("cafe") || q.includes("coffee")) icon = "☕";
                        else if (t.includes("bread") || t.includes("melonpan") || m.includes("bread") || m.includes("pastry") || q.includes("bread") || q.includes("pastry") || t.includes("bakery") || m.includes("pancake") || t.includes("pancake") || t.includes("sweet") || q.includes("sweet")) icon = "🥐";
                        else if (t.includes("eel") || m.includes("eel") || q.includes("eel") || t.includes("unagi") || m.includes("unagi") || q.includes("unagi")) icon = "🍱";
                        html = '<div class="food-icon">' + icon + '</div>';
                    }
                    else if (pt.type === 'alt') html = '<div class="alt-icon">ALT</div>';
                    else html = '<div class="number-icon">' + pt.seq + '</div>';

                    var customIcon = L.divIcon({
                        className: 'custom-icon',
                        html: html,
                        iconSize: [24, 24]
                    });

                    if (pt.lat !== null && pt.lng !== null) {
                        var marker = L.marker([pt.lat, pt.lng], { icon: customIcon }).addTo(markersGroup);
                        marker.bindPopup("<b>" + esc(pt.title) + "</b><br><small>" + esc(pt.meta) + "</small>");
                        bounds.push([pt.lat, pt.lng]);
                    } else {
                        // fallback geocoding
                        fetch('https://nominatim.openstreetmap.org/search?format=json&q=' + encodeURIComponent(pt.query))
                            .then(response => response.json())
                            .then(data => {
                                if (data && data.length > 0) {
                                    var lat = parseFloat(data[0].lat);
                                    var lon = parseFloat(data[0].lon);
                                    var marker = L.marker([lat, lon], { icon: customIcon }).addTo(markersGroup);
                                    marker.bindPopup("<b>" + esc(pt.title) + "</b><br><small>" + esc(pt.meta) + "</small>");
                                    bounds.push([lat, lon]);
                                    map.fitBounds(bounds, { padding: [50, 50] });
                                }
                            });
                    }
                });

                if (bounds.length > 0) {
                    map.fitBounds(bounds, { padding: [50, 50] });
                }
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // TOP NAV BANNER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DeepBlueCard)
                .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tactical Map",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = BentoTextDark
                    )
                    Text(
                        text = "Interactive GPS coordinates",
                        fontSize = 12.sp,
                        color = BentoTextSubtle
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(BentoBlueBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(BentoBlueAccent)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LIVE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = BentoBlueAccent,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        // Horizontal day slider
        Box(modifier = Modifier.fillMaxWidth().background(DeepBlueCard).padding(bottom = 12.dp)) {
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(itineraryDays) { index, item ->
                    val isSelected = index == activeDayIndex
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (isSelected) BentoBlueBg else DeepBlueCard)
                            .border(1.dp, if (isSelected) BentoBlueAccent else BentoTextSubtle.copy(alpha = 0.15f), CircleShape)
                            .clickable { viewModel.selectDay(index) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = item.day,
                            color = if (isSelected) BentoBlueTextDark else BentoTextSubtle,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // WEBVIEW MAP VIEW
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = android.view.ViewGroup.LayoutParams(
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                            android.view.ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        // All map resources are HTTPS, so no need to allow insecure mixed content.
                        settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                        webViewClient = WebViewClient()
                        webChromeClient = android.webkit.WebChromeClient()
                        setTag(id, htmlContent)
                        loadDataWithBaseURL("https://app.local", htmlContent, "text/html", "UTF-8", null)
                        webViewRef = this
                    }
                },
                update = { webView ->
                    if (webView.getTag(webView.id) != htmlContent) {
                        webView.setTag(webView.id, htmlContent)
                        webView.loadDataWithBaseURL("https://app.local", htmlContent, "text/html", "UTF-8", null)
                    }
                    webViewRef = webView
                },
                modifier = Modifier.fillMaxSize()
            )

            // FLOATING CAROUSEL LIST AT BOTTOM
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Reset Button
                    FloatingActionButton(
                        onClick = { recenterTrigger++ },
                        containerColor = DeepBlueCard,
                        contentColor = BentoTextDark,
                        modifier = Modifier.size(50.dp)
                    ) {
                        Icon(Icons.Filled.Refresh, "Recenter")
                    }

                    // Horizontal slides card list
                    LazyRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(markers, key = { it.title }) { pt ->
                            val isItemActive = selectedMarker == pt
                            Card(
                                modifier = Modifier
                                    .width(200.dp)
                                    .clickable { selectedMarker = pt },
                                colors = CardDefaults.cardColors(containerColor = if (isItemActive) BentoBlueBg else DeepBlueCard),
                                border = BorderStroke(if (isItemActive) 2.dp else 1.dp, if (isItemActive) BentoBlueAccent else BentoTextSubtle.copy(alpha = 0.15f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = pt.title,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BentoTextDark,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = pt.type.uppercase(Locale.getDefault()),
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (pt.type == "food") BentoLilacTextDark else BentoBlueAccent
                                        )
                                    }
                                    pt.meta?.let {
                                        Text(
                                            text = it,
                                            fontSize = 11.sp,
                                            color = BentoTextSubtle,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                                        )
                                    }
                                    Button(
                                        onClick = { launchGoogleMaps(context, pt.query) },
                                        colors = ButtonDefaults.buttonColors(containerColor = BentoBlueAccent),
                                        modifier = Modifier.fillMaxWidth().height(28.dp),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("Launch Maps", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 3. GOURMET SCREEN ("gourmet") ---
@Composable
fun GourmetScreen(viewModel: MainViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("all") }
    val foodChecks by viewModel.foodChecks.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val totalCount = FoodData.categories.sumOf { it.items.size }
    val completedCount = FoodData.categories.sumOf { cat ->
        cat.items.count { foodChecks[it.id] == true }
    }
    val percentPercent = if (totalCount == 0) 0 else ((completedCount.toFloat() / totalCount) * 100).toInt()

    Column(modifier = Modifier.fillMaxSize()) {
        // Lilac-themed Bento top bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BentoBackground)
                .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Gourmet Intel",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = BentoTextDark
                    )
                    Text(
                        text = "The S-Tier culinary list",
                        fontSize = 12.sp,
                        color = BentoTextSubtle
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$completedCount / $totalCount",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = BentoLilacTextDark
                    )
                    Box(
                        modifier = Modifier
                            .width(80.dp)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(BentoLilacBg)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(fraction = percentPercent / 100f)
                                .clip(CircleShape)
                                .background(BentoLilacTextDark)
                        )
                    }
                    Text(
                        text = "Eat Protocol $percentPercent%",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = BentoLilacTextSubtle,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        // SEARCH INPUT BAR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search ramen, sushi, meat targets...", color = BentoTextSubtle) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "", tint = BentoLilacTextSubtle) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BentoLilacTextDark,
                    unfocusedBorderColor = BentoLilacTextSubtle.copy(alpha = 0.2f),
                    focusedTextColor = BentoTextDark,
                    unfocusedTextColor = BentoTextDark,
                    focusedContainerColor = DeepBlueCard,
                    unfocusedContainerColor = DeepBlueCard
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // CATEGORY PILLS FILTER
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                CategoryPill("all", "All", "🍱", selectedCategory == "all", selectedBg = BentoLilacBg, selectedTextColor = BentoLilacTextDark) { selectedCategory = "all" }
            }
            items(FoodData.categories) { cat ->
                CategoryPill(cat.id, cat.name, cat.icon, selectedCategory == cat.id, selectedBg = BentoLilacBg, selectedTextColor = BentoLilacTextDark) { selectedCategory = cat.id }
            }
        }

        // LIST
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FoodData.categories.forEach { category ->
                if (selectedCategory == "all" || selectedCategory == category.id) {
                    val filteredItems = category.items.filter {
                        searchQuery.isEmpty() ||
                                it.name.contains(searchQuery, ignoreCase = true) ||
                                it.dish.contains(searchQuery, ignoreCase = true)
                    }
                    if (filteredItems.isNotEmpty()) {
                        item {
                            Text(
                                text = (category.name + " (${category.tagline})").uppercase(Locale.getDefault()),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextSubtle,
                                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp),
                                letterSpacing = 0.5.sp
                            )
                        }
                        items(filteredItems, key = { it.id }) { item ->
                            val isChecked = foodChecks[item.id] == true
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DeepBlueCard),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isChecked) GreenM3.copy(alpha = 0.4f) else BentoTextSubtle.copy(alpha = 0.12f)
                                ),
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Food icon background
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (isChecked) GreenM3.copy(alpha = 0.15f)
                                                else BentoLilacBg.copy(alpha = 0.6f)
                                            )
                                            .clickable { viewModel.toggleFoodCheck(item.id, !isChecked) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (isChecked) "✔️" else category.icon,
                                            fontSize = 18.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = item.name,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isChecked) BentoTextSubtle else BentoLilacTextDark,
                                                textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
                                            )
                                            if (item.mustTry) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .background(AccentLilacStrong, RoundedCornerShape(4.dp))
                                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                                ) {
                                                    Text("MUST", fontSize = 7.sp, fontWeight = FontWeight.Black, color = Color.White)
                                                }
                                            }
                                        }
                                        Text(item.dish, fontSize = 12.sp, color = BentoLilacTextSubtle)
                                        Row(
                                            modifier = Modifier.padding(top = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Filled.LocationOn, "", tint = BentoLilacTextSubtle, modifier = Modifier.size(10.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(item.area, fontSize = 10.sp, color = BentoLilacTextSubtle)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(item.note, fontSize = 10.sp, color = BentoLilacTextSubtle, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                        }
                                    }

                                    IconButton(
                                        onClick = { launchGoogleMaps(context, item.name + " " + item.area) },
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(AccentLilacStrong, CircleShape)
                                    ) {
                                        Icon(Icons.Filled.LocationOn, "Go", tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 4. BUDGET SCREEN ("budget") ---
@Composable
fun BudgetScreen(viewModel: MainViewModel) {
    val txs by viewModel.transactions.collectAsStateWithLifecycle()
    val trip by viewModel.activeTrip.collectAsStateWithLifecycle()
    var isAddModalOpen by remember { mutableStateOf(false) }

    val symbol = currencySymbol(trip?.currencyCode ?: "JPY")
    val totalSpent = txs.sumOf { it.amount }
    val budgetEstimate = trip?.budgetAmount ?: 150000.0
    val fractionSpent = if (budgetEstimate <= 0.0) 0f else (totalSpent / budgetEstimate).coerceIn(0.0, 1.0).toFloat()
    val percentSpentText = (fractionSpent * 100).toInt()

    val catTotals = remember(txs) {
        val map = mutableMapOf("Food" to 0.0, "Transport" to 0.0, "Shopping" to 0.0, "Hotel" to 0.0, "Other" to 0.0)
        txs.forEach { t ->
            val cat = t.category
            map[cat] = (map[cat] ?: 0.0) + t.amount
        }
        map
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // WALLET HEADER (BENTO GREEN THEME)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BentoBackground)
                .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 12.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Mission Wallet",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = BentoTextDark
                        )
                        Text(
                            text = "Real-time Expense Auditor",
                            fontSize = 12.sp,
                            color = BentoTextSubtle
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Total Spent".uppercase(Locale.getDefault()),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoGreenTextSubtle
                        )
                        Text(
                            text = "$symbol${String.format("%,.0f", totalSpent)}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = BentoGreenTextDark,
                            fontFamily = FontFamily.Monospace
                        )
                        val homeCode = trip?.homeCurrencyCode ?: ""
                        val fxRate = trip?.exchangeRate ?: 0.0
                        if (homeCode.isNotBlank() && fxRate > 0.0) {
                            Text(
                                text = "≈ ${currencySymbol(homeCode)}${String.format("%,.0f", totalSpent * fxRate)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BentoGreenTextSubtle,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(BentoGreenBg)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = fractionSpent)
                            .clip(CircleShape)
                            .background(BentoGreenTextDark)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${symbol}0", color = BentoTextSubtle, fontSize = 10.sp)
                    Text("$percentSpentText% of $symbol${String.format("%,.0f", budgetEstimate)} Limit", color = BentoGreenTextDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("$symbol${String.format("%,.0f", budgetEstimate)}", color = BentoTextSubtle, fontSize = 10.sp)
                }
            }
        }

        // STATS SPLIT
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = DeepBlueCard),
            border = BorderStroke(1.dp, BentoTextSubtle.copy(alpha = 0.12f)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Category Metrics".uppercase(Locale.getDefault()),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextSubtle
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    catTotals.forEach { (cat, total) ->
                        if (total > 0.0) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = when (cat) {
                                        "Food" -> "🍔"
                                        "Transport" -> "🚇"
                                        "Shopping" -> "🛍️"
                                        "Hotel" -> "🏨"
                                        else -> "🏷️"
                                    },
                                    fontSize = 18.sp
                                )
                                Text(cat, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BentoTextSubtle, modifier = Modifier.padding(top = 2.dp))
                                Text(
                                    text = "$symbol${String.format("%,.0f", total)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BentoTextDark,
                                    modifier = Modifier.padding(top = 1.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Text(
            text = "Audit Trails".uppercase(Locale.getDefault()),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = BentoTextSubtle,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
            letterSpacing = 0.5.sp
        )

        if (txs.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Info, "", tint = BentoTextSubtle, modifier = Modifier.size(54.dp))
                    Text("No expenses audited yet.", fontSize = 14.sp, color = BentoTextSubtle, modifier = Modifier.padding(top = 8.dp))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(txs, key = { it.id }) { t ->
                    @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
                    val dismissState = androidx.compose.material3.rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            when (dismissValue) {
                                androidx.compose.material3.SwipeToDismissBoxValue.EndToStart -> {
                                    viewModel.deleteTransaction(t.id)
                                    true
                                }
                                else -> false
                            }
                        }
                    )

                    @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
                    androidx.compose.material3.SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFFFEAEA), RoundedCornerShape(16.dp))
                                    .padding(16.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red)
                            }
                        },
                        content = {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DeepBlueCard),
                                border = BorderStroke(1.dp, BentoTextSubtle.copy(alpha = 0.10f)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(BentoGreenBg, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = when (t.category) {
                                                "Food" -> "🍔"
                                                "Transport" -> "🚇"
                                                "Shopping" -> "🛍️"
                                                "Hotel" -> "🏨"
                                                else -> "🏷️"
                                            },
                                            fontSize = 16.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(t.desc, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BentoTextDark)
                                        Text("${t.date} • ${t.time}", fontSize = 10.sp, color = BentoTextSubtle)
                                    }

                                    Text(
                                        text = "$symbol${String.format("%,.0f", t.amount)}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = BentoGreenTextDark,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    // FAB
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        FloatingActionButton(
            onClick = { isAddModalOpen = true },
            containerColor = AccentGreenStrong,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 8.dp)
                .testTag("add_expense_fab")
        ) {
            Icon(Icons.Filled.Add, "Add Expense", tint = Color.White, modifier = Modifier.size(28.dp))
        }
    }

    // Entry field modal
    if (isAddModalOpen) {
        var addDesc by remember { mutableStateOf("") }
        var addAmount by remember { mutableStateOf("") }
        var addCategory by remember { mutableStateOf("Food") }

        ThemedDialog(onDismissRequest = { isAddModalOpen = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepBlueCard),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, BentoTextSubtle.copy(alpha = 0.15f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Add Ledger Record",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = BentoTextDark
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = addDesc,
                        onValueChange = { addDesc = it },
                        label = { Text("Description", color = BentoTextSubtle) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BentoTextDark,
                            unfocusedTextColor = BentoTextDark,
                            focusedBorderColor = BentoGreenTextDark,
                            unfocusedBorderColor = BentoTextSubtle.copy(alpha = 0.2f),
                            focusedContainerColor = DeepBlueCard,
                            unfocusedContainerColor = DeepBlueCard
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("add_desc_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = addAmount,
                        onValueChange = { addAmount = it },
                        label = { Text("Amount ($symbol)", color = BentoTextSubtle) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = BentoTextDark,
                            unfocusedTextColor = BentoTextDark,
                            focusedBorderColor = BentoGreenTextDark,
                            unfocusedBorderColor = BentoTextSubtle.copy(alpha = 0.2f),
                            focusedContainerColor = DeepBlueCard,
                            unfocusedContainerColor = DeepBlueCard
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("add_amount_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Category", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoTextSubtle)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Food", "Transport", "Shopping", "Hotel", "Other").forEach { category ->
                            val isSelected = addCategory == category
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) BentoGreenBg else DeepBlueCard)
                                    .border(1.dp, if (isSelected) BentoGreenTextDark else BentoTextSubtle.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .clickable { addCategory = category }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(category, color = if (isSelected) BentoGreenTextDark else BentoTextSubtle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { isAddModalOpen = false },
                            modifier = Modifier.weight(1f),
                            border = BorderStroke(1.dp, BentoTextSubtle.copy(alpha = 0.2f))
                        ) {
                            Text("Cancel", color = BentoTextSubtle)
                        }
                        Button(
                            onClick = {
                                if (addDesc.isNotEmpty() && addAmount.isNotEmpty()) {
                                    val costVal = addAmount.toDoubleOrNull() ?: 0.0
                                    val df = SimpleDateFormat("MMM d", Locale.US)
                                    val tf = SimpleDateFormat("HH:mm", Locale.US)
                                    val dStr = df.format(Date())
                                    val tStr = tf.format(Date())

                                    viewModel.addTransaction(
                                        desc = addDesc,
                                        amount = costVal,
                                        category = addCategory,
                                        date = dStr,
                                        time = tStr
                                    )
                                    isAddModalOpen = false
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentGreenStrong)
                        ) {
                            Text("Save", color = Color.White, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}

fun parseHtmlToAnnotatedString(html: String): AnnotatedString {
    val cleanText = html.replace("<br/>", "\n").replace("<br>", "\n")
    return buildAnnotatedString {
        var cursor = 0
        while (cursor < cleanText.length) {
            val startBoldIndex = cleanText.indexOf("<b>", cursor)
            if (startBoldIndex == -1) {
                append(cleanText.substring(cursor))
                break
            }
            append(cleanText.substring(cursor, startBoldIndex))
            val endBoldIndex = cleanText.indexOf("</b>", startBoldIndex + 3)
            if (endBoldIndex == -1) {
                append(cleanText.substring(startBoldIndex))
                break
            }
            val boldText = cleanText.substring(startBoldIndex + 3, endBoldIndex)
            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
            append(boldText)
            pop()
            cursor = endBoldIndex + 4
        }
    }
}

// --- 5. INTEL SCREEN ("guide") ---
@Composable
fun IntelScreen(viewModel: MainViewModel) {
    var subTab by remember { mutableStateOf("bookings") }
    var selectedLangCat by remember { mutableStateOf("All") }
    var showSettings by remember { mutableStateOf(false) }
    var showTripEditor by remember { mutableStateOf(false) }
    var showImport by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    if (showSettings) {
        SettingsDialog(
            viewModel = viewModel,
            onDismiss = { showSettings = false },
            onEditTrip = { showSettings = false; showTripEditor = true },
            onImport = { showSettings = false; showImport = true }
        )
    }
    if (showTripEditor) {
        TripEditorDialog(viewModel = viewModel, onDismiss = { showTripEditor = false })
    }
    if (showImport) {
        ImportDialog(viewModel = viewModel, onDismiss = { showImport = false })
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // TOP BANNER (BENTO COMPACT OFF-WHITE STYLE)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(BentoBackground)
                .padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tactical Briefing",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = BentoTextDark
                    )
                    Text(
                        text = "Protocols, Language & Travel Guides",
                        fontSize = 12.sp,
                        color = BentoTextSubtle
                    )
                }
                androidx.compose.material3.IconButton(onClick = { showSettings = true }) {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Settings",
                        tint = BentoBlueAccent,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // SLIDER TAB SELECTION (WHITE GLASS BOX WITH GRAY BORDER)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(DeepBlueCard, RoundedCornerShape(16.dp))
                .border(BorderStroke(1.dp, BentoTextSubtle.copy(alpha = 0.12f)), RoundedCornerShape(16.dp))
                .padding(4.dp)
        ) {
            listOf(
                "bookings" to "TARGETS",
                "language" to "TRANSLATE",
                "tips" to "TRAVEL HACK",
                "packing" to "PACKING"
            ).forEach { (id, label) ->
                val isSelected = subTab == id
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) BentoBlueBg else Color.Transparent)
                        .clickable { subTab = id }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) BentoBlueTextDark else BentoTextSubtle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // CONTENT BODY
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            when (subTab) {
                "bookings" -> {
                    Text(
                        text = "DEFCON 1: CRITICAL TARGETS".uppercase(Locale.getDefault()),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = BentoRedTextSubtle,
                        modifier = Modifier.padding(bottom = 12.dp),
                        letterSpacing = 1.sp
                    )

                    TipsData.criticalBookings.forEach { target ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DeepBlueCard),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, BentoRedTextSubtle.copy(alpha = 0.15f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(target.title, fontSize = 16.sp, fontWeight = FontWeight.Black, color = BentoRedTextDark)
                                    Box(
                                        modifier = Modifier
                                            .background(BentoRedBg, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(target.date, fontSize = 9.sp, fontWeight = FontWeight.Black, color = BentoRedTextDark)
                                    }
                                }
                                Row(
                                    modifier = Modifier.padding(top = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Filled.Info, "", tint = BentoTextSubtle, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("BOOK DATE: ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoTextSubtle)
                                    Text(target.bookDate, fontSize = 11.sp, fontWeight = FontWeight.Black, color = BentoRedTextSubtle)
                                }
                                Text(
                                    text = target.note,
                                    fontSize = 12.sp,
                                    color = BentoTextSubtle,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                if (target.strategy.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(BentoBackground, RoundedCornerShape(12.dp))
                                            .border(1.dp, BentoRedTextSubtle.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = "TACTICAL STRATEGY",
                                                textDecoration = TextDecoration.None,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = BentoRedTextSubtle,
                                                letterSpacing = 0.5.sp,
                                                modifier = Modifier.padding(bottom = 6.dp)
                                            )
                                            Text(
                                                text = parseHtmlToAnnotatedString(target.strategy),
                                                fontSize = 11.sp,
                                                color = BentoTextDark,
                                                lineHeight = 15.sp
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(target.link))
                                        context.startActivity(intent)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentRedStrong),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("LOCK BOOKING SITE", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "DEFCON 2: ACQUIRED REQUISITES".uppercase(Locale.getDefault()),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = BentoBlueTextMedium,
                        modifier = Modifier.padding(bottom = 12.dp),
                        letterSpacing = 1.sp
                    )

                    TipsData.recommendedBookings.forEach { target ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DeepBlueCard),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, BentoBlueTextMedium.copy(alpha = 0.15f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(target.title, fontSize = 16.sp, fontWeight = FontWeight.Black, color = BentoTextDark)
                                    Box(
                                        modifier = Modifier
                                            .background(BentoBlueBg, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(target.date, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BentoBlueTextMedium)
                                    }
                                }
                                Text(
                                    text = target.note,
                                    fontSize = 12.sp,
                                    color = BentoTextSubtle,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                if (target.strategy.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(BentoBackground, RoundedCornerShape(12.dp))
                                            .border(1.dp, BentoBlueAccent.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = "TACTICAL STRATEGY",
                                                textDecoration = TextDecoration.None,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Black,
                                                color = BentoBlueAccent,
                                                letterSpacing = 0.5.sp,
                                                modifier = Modifier.padding(bottom = 6.dp)
                                            )
                                            Text(
                                                text = parseHtmlToAnnotatedString(target.strategy),
                                                fontSize = 11.sp,
                                                color = BentoTextDark,
                                                lineHeight = 15.sp
                                            )
                                        }
                                    }
                                }
                                if (!target.isPhoneOnly) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(target.link))
                                            context.startActivity(intent)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlueStrong),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("BOOK VIA TABLECHECK", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "CONCIERGE PROTOCOL TEMPLATE".uppercase(Locale.getDefault()),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = BentoTextSubtle,
                        modifier = Modifier.padding(bottom = 8.dp),
                        letterSpacing = 1.sp
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BentoGrayBg),
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, BentoTextSubtle.copy(alpha = 0.12f)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Copy this pre-trip translation template to email to your Tokyo hospitality team for securing manual phone-only restaurants:",
                                fontSize = 11.sp,
                                color = BentoGrayTextSubtle,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            SelectionContainer {
                                Text(
                                    text = TipsData.conciergeScript,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = BentoTextDark,
                                    modifier = Modifier
                                        .background(DeepBlueCard, RoundedCornerShape(8.dp))
                                        .border(BorderStroke(1.dp, BentoGrayTextSubtle.copy(alpha = 0.15f)), RoundedCornerShape(8.dp))
                                        .padding(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = {
                                    clipboard.setText(AnnotatedString(TipsData.conciergeScript))
                                    Toast.makeText(context, "Copy successful", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AccentBlueStrong),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.Share, "copy", modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Copy Script")
                                }
                            }
                        }
                    }
                }

                "language" -> {
                    Text(
                        text = "NATIVE TRANSLATOR COMMANDS".uppercase(Locale.getDefault()),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = BentoTextDark,
                        modifier = Modifier.padding(bottom = 12.dp),
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val cats = listOf("All") + TipsData.languageGroups.map { it.title }
                        cats.forEach { cat ->
                            val isSelected = selectedLangCat == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) BentoBlueAccent else Color.Transparent)
                                    .border(1.dp, if (isSelected) BentoBlueAccent else BentoTextSubtle.copy(alpha=0.2f), RoundedCornerShape(8.dp))
                                    .clickable { selectedLangCat = cat }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(cat, color = if (isSelected) Color.White else BentoTextSubtle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    TipsData.languageGroups.filter { selectedLangCat == "All" || it.title == selectedLangCat }.forEach { group ->
                        Text(
                            text = group.title.uppercase(Locale.getDefault()),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextSubtle,
                            modifier = Modifier.padding(top = 10.dp, bottom = 6.dp)
                        )
                        group.phrases.forEach { item ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DeepBlueCard),
                                border = BorderStroke(1.dp, BentoTextSubtle.copy(alpha = 0.10f)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(item.english, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BentoTextDark)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text("FORMAL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BentoTextSubtle)
                                            Text(item.formal, fontSize = 12.sp, color = BentoTextDark)
                                        }
                                        Column(
                                            modifier = Modifier
                                                .background(BentoLilacBg, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp),
                                            horizontalAlignment = Alignment.End
                                        ) {
                                            Text("RAPID SPEAK", fontSize = 8.sp, fontWeight = FontWeight.Black, color = BentoLilacTextDark)
                                            Text(item.native, fontSize = 12.sp, fontWeight = FontWeight.Black, color = BentoLilacTextDark)
                                        }
                                    }
                                    item.note?.let {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("* Note: $it", fontSize = 10.sp, color = BentoLilacTextSubtle, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                    }
                                }
                            }
                        }
                    }
                }

                "packing" -> {
                    PackingSection(viewModel)
                }

                "tips" -> {
                    Text(
                        text = "30 FIELD MANUAL HACKS".uppercase(Locale.getDefault()),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = BentoTextDark,
                        modifier = Modifier.padding(bottom = 12.dp),
                        letterSpacing = 1.sp
                    )
                    TipsData.tipSections.forEach { section ->
                        Text(
                            text = section.category.uppercase(Locale.getDefault()),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoBlueTextMedium,
                            modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
                        )
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DeepBlueCard),
                            border = BorderStroke(1.dp, BentoTextSubtle.copy(alpha = 0.12f)),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                section.items.forEachIndexed { idx, item ->
                                    if (idx > 0) HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = BentoTextSubtle.copy(alpha = 0.10f))
                                    Row(verticalAlignment = Alignment.Top) {
                                        Text(
                                            text = "${idx + 1}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            color = BentoBlueAccent,
                                            modifier = Modifier.width(20.dp)
                                        )
                                        Text(
                                            text = item,
                                            fontSize = 13.sp,
                                            color = BentoTextDark,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

// --- SETTINGS DIALOG ---
@Composable
fun SettingsDialog(viewModel: MainViewModel, onDismiss: () -> Unit, onEditTrip: () -> Unit, onImport: () -> Unit) {
    val darkMode by viewModel.darkMode.collectAsStateWithLifecycle()
    val gamification by viewModel.gamificationEnabled.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ThemedDialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = BentoBackground),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, BentoTextSubtle.copy(alpha = 0.15f)),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Settings", fontSize = 18.sp, fontWeight = FontWeight.Black, color = BentoTextDark)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = BentoTextSubtle)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                SettingToggleRow("Dark mode", "Use a dark theme (saved across launches)", darkMode) { viewModel.setDarkMode(it) }
                Spacer(modifier = Modifier.height(4.dp))
                SettingToggleRow("Tactical rank", "Show the Rookie → Shogun progression", gamification) { viewModel.setGamificationEnabled(it) }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onEditTrip,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlueStrong)
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit trip details", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = {
                        val json = viewModel.exportJson()
                        if (json.isNullOrBlank()) {
                            Toast.makeText(context, "Nothing to export yet", Toast.LENGTH_SHORT).show()
                        } else {
                            val send = Intent(Intent.ACTION_SEND).apply {
                                type = "application/json"
                                putExtra(Intent.EXTRA_TEXT, json)
                                putExtra(Intent.EXTRA_TITLE, "itinerary.json")
                            }
                            context.startActivity(Intent.createChooser(send, "Share trip"))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, BentoTextSubtle.copy(alpha = 0.3f))
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null, tint = BentoTextDark, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export / share trip (JSON)", color = BentoTextDark, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onImport,
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, BentoTextSubtle.copy(alpha = 0.3f))
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, tint = BentoTextDark, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Import trip (JSON / AI)", color = BentoTextDark, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SettingToggleRow(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BentoTextDark)
            Text(subtitle, fontSize = 11.sp, color = BentoTextSubtle)
        }
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = BentoBlueAccent
            )
        )
    }
}

// --- TRIP EDITOR ---
@Composable
fun TripEditorDialog(viewModel: MainViewModel, onDismiss: () -> Unit) {
    val trip by viewModel.activeTrip.collectAsStateWithLifecycle()
    val current = trip ?: return

    var name by remember(current.id) { mutableStateOf(current.name) }
    var destination by remember(current.id) { mutableStateOf(current.destination) }
    var startDate by remember(current.id) { mutableStateOf(current.startDate) }
    var endDate by remember(current.id) { mutableStateOf(current.endDate) }
    var travelers by remember(current.id) { mutableStateOf(current.travelerNames) }
    var budget by remember(current.id) { mutableStateOf(if (current.budgetAmount > 0) current.budgetAmount.toLong().toString() else "") }
    var currency by remember(current.id) { mutableStateOf(current.currencyCode) }
    var homeCurrency by remember(current.id) { mutableStateOf(current.homeCurrencyCode) }
    var rate by remember(current.id) { mutableStateOf(if (current.exchangeRate > 0) current.exchangeRate.toString() else "") }

    ThemedDialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = BentoBackground),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, BentoTextSubtle.copy(alpha = 0.15f)),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                Text("Edit Trip", fontSize = 18.sp, fontWeight = FontWeight.Black, color = BentoTextDark, modifier = Modifier.padding(bottom = 12.dp))
                TripField("Trip name", name) { name = it }
                TripField("Destination", destination) { destination = it }
                TripField("Start date", startDate) { startDate = it }
                TripField("End date", endDate) { endDate = it }
                TripField("Traveler names", travelers) { travelers = it }
                TripField("Budget", budget, numeric = true) { v -> budget = v.filter { it.isDigit() } }

                Text("Currency", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoTextSubtle, modifier = Modifier.padding(top = 4.dp, bottom = 4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("JPY", "IDR", "USD", "EUR", "GBP", "KRW", "THB").forEach { code ->
                        val selected = currency == code
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) AccentBlueStrong else Color.Transparent)
                                .border(1.dp, if (selected) AccentBlueStrong else BentoTextSubtle.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .clickable { currency = code }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(code, color = if (selected) Color.White else BentoTextSubtle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Convert to (home currency, optional)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BentoTextSubtle, modifier = Modifier.padding(bottom = 4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    (listOf("") + listOf("IDR", "USD", "EUR", "JPY", "GBP", "KRW", "THB")).forEach { code ->
                        val selected = homeCurrency == code
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) AccentBlueStrong else Color.Transparent)
                                .border(1.dp, if (selected) AccentBlueStrong else BentoTextSubtle.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .clickable { homeCurrency = code }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(if (code.isEmpty()) "None" else code, color = if (selected) Color.White else BentoTextSubtle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                if (homeCurrency.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TripField("Rate: 1 $currency = ? $homeCurrency", rate) { rate = it }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = BentoTextSubtle, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            viewModel.updateTrip(
                                current.copy(
                                    name = name.ifBlank { current.name },
                                    destination = destination,
                                    startDate = startDate,
                                    endDate = endDate,
                                    travelerNames = travelers,
                                    currencyCode = currency,
                                    budgetAmount = budget.toDoubleOrNull() ?: current.budgetAmount,
                                    homeCurrencyCode = homeCurrency,
                                    exchangeRate = if (homeCurrency.isBlank()) 0.0 else (rate.toDoubleOrNull() ?: 0.0)
                                )
                            )
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlueStrong),
                        enabled = name.isNotBlank()
                    ) {
                        Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun TripField(label: String, value: String, numeric: Boolean = false, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label, color = BentoTextSubtle) },
        singleLine = true,
        keyboardOptions = if (numeric) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = BentoTextDark,
            unfocusedTextColor = BentoTextDark,
            focusedBorderColor = BentoBlueAccent,
            unfocusedBorderColor = BentoTextSubtle.copy(alpha = 0.2f),
            focusedContainerColor = DeepBlueCard,
            unfocusedContainerColor = DeepBlueCard,
            cursorColor = BentoBlueAccent
        )
    )
}

// --- IMPORT ---
private const val IMPORT_AI_PROMPT = """Create a travel itinerary as JSON. Output ONLY the JSON (no commentary), matching this EXACT structure:
{
  "schemaVersion": 1,
  "trip": { "name": "", "destination": "", "startDate": "", "endDate": "", "currencyCode": "JPY", "budgetAmount": 0, "travelerNames": "" },
  "days": [
    { "date": "Day 1", "title": "", "location": "",
      "morning":   [ { "time": "09:00", "text": "", "type": "visit", "cost": 0 } ],
      "afternoon": [ { "time": "13:00", "text": "", "type": "food", "cost": 0 } ],
      "evening":   [ { "time": "19:00", "text": "", "type": "food", "cost": 0 } ] }
  ]
}
Fill it with a realistic plan for: [DESTINATION], [DATES], [NUMBER OF TRAVELERS]. Use the local ISO currency code (e.g. JPY, USD, EUR). "type" must be one of: visit, food, transit, shopping, logistics, sightseeing. Include one object per day in "days"."""

@Composable
fun ImportDialog(viewModel: MainViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var json by remember { mutableStateOf("") }

    ThemedDialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = BentoBackground),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, BentoTextSubtle.copy(alpha = 0.15f)),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
                Text("Import Trip", fontSize = 18.sp, fontWeight = FontWeight.Black, color = BentoTextDark)
                Text(
                    "Copy the AI prompt, ask any AI (ChatGPT / Claude / Gemini) to fill it in, then paste the JSON back here. This replaces your current itinerary.",
                    fontSize = 11.sp, color = BentoTextSubtle, modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )
                OutlinedButton(
                    onClick = {
                        clipboard.setText(AnnotatedString(IMPORT_AI_PROMPT))
                        Toast.makeText(context, "AI prompt copied", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, BentoTextSubtle.copy(alpha = 0.3f))
                ) {
                    Icon(Icons.Filled.Share, contentDescription = null, tint = BentoTextDark, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Copy AI prompt", color = BentoTextDark, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = json,
                    onValueChange = { json = it },
                    label = { Text("Paste itinerary JSON here", color = BentoTextSubtle) },
                    minLines = 5,
                    maxLines = 10,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = BentoTextDark, unfocusedTextColor = BentoTextDark,
                        focusedBorderColor = BentoBlueAccent, unfocusedBorderColor = BentoTextSubtle.copy(alpha = 0.2f),
                        focusedContainerColor = DeepBlueCard, unfocusedContainerColor = DeepBlueCard, cursorColor = BentoBlueAccent
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = BentoTextSubtle, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (viewModel.importJson(json)) {
                                Toast.makeText(context, "Trip imported", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            } else {
                                Toast.makeText(context, "Invalid JSON — check the format", Toast.LENGTH_LONG).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlueStrong),
                        enabled = json.isNotBlank()
                    ) {
                        Text("Import", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// --- PACKING LIST (Intel sub-tab) ---
@Composable
fun PackingSection(viewModel: MainViewModel) {
    val items by viewModel.packingItems.collectAsStateWithLifecycle()
    var newItem by remember { mutableStateOf("") }
    val packed = items.count { it.checked }

    Column {
        Text(
            text = "PACKING LIST",
            fontSize = 12.sp, fontWeight = FontWeight.Black, color = BentoTextDark,
            letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 2.dp)
        )
        Text(
            text = if (items.isEmpty()) "Add what you need to pack" else "$packed / ${items.size} packed",
            fontSize = 11.sp, color = BentoTextSubtle, modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newItem,
                onValueChange = { newItem = it },
                placeholder = { Text("Add an item…", color = BentoTextSubtle) },
                singleLine = true,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = BentoTextDark, unfocusedTextColor = BentoTextDark,
                    focusedBorderColor = BentoBlueAccent, unfocusedBorderColor = BentoTextSubtle.copy(alpha = 0.2f),
                    focusedContainerColor = DeepBlueCard, unfocusedContainerColor = DeepBlueCard, cursorColor = BentoBlueAccent
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { viewModel.addPackingItem(newItem); newItem = "" },
                enabled = newItem.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlueStrong),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        items.forEach { item ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepBlueCard),
                border = BorderStroke(1.dp, if (item.checked) GreenM3.copy(alpha = 0.4f) else BentoTextSubtle.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .border(2.dp, if (item.checked) GreenM3 else BentoTextSubtle.copy(alpha = 0.4f), CircleShape)
                            .background(if (item.checked) GreenM3 else Color.Transparent)
                            .clickable { viewModel.togglePacking(item) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (item.checked) Icon(Icons.Filled.Check, "", tint = Color.White, modifier = Modifier.size(15.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = item.label,
                        fontSize = 14.sp,
                        color = if (item.checked) BentoTextSubtle else BentoTextDark,
                        textDecoration = if (item.checked) TextDecoration.LineThrough else TextDecoration.None,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.deletePacking(item.id) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = BentoTextSubtle, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// --- BOTTOM NAVIGATION BAR ---
@Composable
fun BottomNavigation(
    activeTab: String,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(
        containerColor = DeepBlueCard,
        contentColor = BentoTextDark,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        NavigationBarItem(
            selected = activeTab == "plan",
            onClick = { onTabSelected("plan") },
            label = { Text("Plan", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Plan") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BentoBlueAccent,
                selectedTextColor = BentoBlueAccent,
                indicatorColor = BentoBlueBg.copy(alpha = 0.5f),
                unselectedIconColor = BentoTextSubtle,
                unselectedTextColor = BentoTextSubtle
            ),
            modifier = Modifier.testTag("nav_plan_tab")
        )
        NavigationBarItem(
            selected = activeTab == "map",
            onClick = { onTabSelected("map") },
            label = { Text("Map", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            icon = { Icon(Icons.Filled.LocationOn, contentDescription = "Map") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BentoBlueAccent,
                selectedTextColor = BentoBlueAccent,
                indicatorColor = BentoBlueBg.copy(alpha = 0.5f),
                unselectedIconColor = BentoTextSubtle,
                unselectedTextColor = BentoTextSubtle
            ),
            modifier = Modifier.testTag("nav_map_tab")
        )
        NavigationBarItem(
            selected = activeTab == "gourmet",
            onClick = { onTabSelected("gourmet") },
            label = { Text("Food", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            icon = { Icon(Icons.Filled.LocationOn, contentDescription = "Food") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BentoBlueAccent,
                selectedTextColor = BentoBlueAccent,
                indicatorColor = BentoBlueBg.copy(alpha = 0.5f),
                unselectedIconColor = BentoTextSubtle,
                unselectedTextColor = BentoTextSubtle
            ),
            modifier = Modifier.testTag("nav_food_tab")
        )
        NavigationBarItem(
            selected = activeTab == "budget",
            onClick = { onTabSelected("budget") },
            label = { Text("Wallet", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            icon = { Icon(Icons.Filled.Info, contentDescription = "Wallet") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BentoBlueAccent,
                selectedTextColor = BentoBlueAccent,
                indicatorColor = BentoBlueBg.copy(alpha = 0.5f),
                unselectedIconColor = BentoTextSubtle,
                unselectedTextColor = BentoTextSubtle
            ),
            modifier = Modifier.testTag("nav_wallet_tab")
        )
        NavigationBarItem(
            selected = activeTab == "guide",
            onClick = { onTabSelected("guide") },
            label = { Text("Intel", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
            icon = { Icon(Icons.Filled.Info, contentDescription = "Intel") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BentoBlueAccent,
                selectedTextColor = BentoBlueAccent,
                indicatorColor = BentoBlueBg.copy(alpha = 0.5f),
                unselectedIconColor = BentoTextSubtle,
                unselectedTextColor = BentoTextSubtle
            ),
            modifier = Modifier.testTag("nav_intel_tab")
        )
    }
}
