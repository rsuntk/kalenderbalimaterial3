@file:OptIn(ExperimentalMaterial3Api::class)

package com.itzkazuri.kalenderbali.ui.screen

import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itzkazuri.kalenderbali.R
import com.itzkazuri.kalenderbali.utils.RahinaCalculator
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    onAddReminderClick: () -> Unit,
    onDateClick: (Int, Int, Int) -> Unit
) {
    var calendarMillis by remember { mutableStateOf(Calendar.getInstance().timeInMillis) }
    val calendar = remember(calendarMillis) {
        Calendar.getInstance().apply { timeInMillis = calendarMillis }
    }

    var selectedDate by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var transitionDirection by remember { mutableStateOf(0) }

    val isDarkMode = isSystemInDarkTheme()
    val textColor = if (isDarkMode) Color.White else Color.Black

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    val fullCalendarGrid by remember(calendarMillis) {
        derivedStateOf {
            val tempCalendar = Calendar.getInstance().apply { timeInMillis = calendarMillis }
            tempCalendar.set(Calendar.DAY_OF_MONTH, 1)
            val daysInMonth = tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            val firstDayOfWeek = tempCalendar.get(Calendar.DAY_OF_WEEK)
            val startOffset = (firstDayOfWeek - 2 + 7) % 7

            val calendarGrid = List(startOffset) { null } + (1..daysInMonth).toList()
            val extraSpaces = (7 - (calendarGrid.size % 7)) % 7
            calendarGrid + List(extraSpaces) { null }
        }
    }

    val rahinaHariIni = RahinaCalculator.getRerahinan(selectedDate, month + 1, year)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kalender", style = MaterialTheme.typography.headlineSmall) },
                actions = {
                    IconButton(onClick = onAddReminderClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = "Tambah Acara"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        when {
                            dragAmount < -50 -> {
                                transitionDirection = 1
                                calendarMillis = calendarMillis.applyMonthOffset(1)
                                selectedDate = minOf(selectedDate, getMaxDayOfMonth(calendarMillis))
                            }
                            dragAmount > 50 -> {
                                transitionDirection = -1
                                calendarMillis = calendarMillis.applyMonthOffset(-1)
                                selectedDate = minOf(selectedDate, getMaxDayOfMonth(calendarMillis))
                            }
                        }
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = {
                    transitionDirection = -1
                    calendarMillis = calendarMillis.applyMonthOffset(-1)
                    selectedDate = minOf(selectedDate, getMaxDayOfMonth(calendarMillis))
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_left),
                        contentDescription = "Bulan Sebelumnya"
                    )
                }

                Text(
                    text = SimpleDateFormat("MMMM yyyy", Locale("id", "ID")).format(calendar.time),
                    style = MaterialTheme.typography.headlineSmall
                )

                IconButton(onClick = {
                    transitionDirection = 1
                    calendarMillis = calendarMillis.applyMonthOffset(1)
                    selectedDate = minOf(selectedDate, getMaxDayOfMonth(calendarMillis))
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = "Bulan Berikutnya"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        color = textColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                fullCalendarGrid.chunked(7).forEach { week ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        week.forEach { day ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (day != null) {
                                    val isSelected = day == selectedDate
                                    val isToday = day == today && month == Calendar.getInstance().get(Calendar.MONTH)

                                    Card(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clickable {
                                                selectedDate = day
                                                onDateClick(year, month, day)
                                            },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = when {
                                                isSelected -> MaterialTheme.colorScheme.primary
                                                isToday -> MaterialTheme.colorScheme.secondaryContainer
                                                else -> MaterialTheme.colorScheme.surface
                                            }
                                        )
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(text = day.toString(), color = textColor, fontSize = 16.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Rahinan Hari Ini 🌺",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = rahinaHariIni.joinToString(", ").ifEmpty { "Tidak Ada Rahinan" },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

// 🔹 Extension Function
fun Long.applyMonthOffset(offset: Int): Long {
    return Calendar.getInstance().apply {
        timeInMillis = this@applyMonthOffset
        add(Calendar.MONTH, offset)
    }.timeInMillis
}

fun getMaxDayOfMonth(millis: Long): Int {
    return Calendar.getInstance().apply {
        timeInMillis = millis
    }.getActualMaximum(Calendar.DAY_OF_MONTH)
}
