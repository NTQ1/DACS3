package com.yourname.dacs.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourname.dacs.view.screen.formatCurrency
import java.time.LocalDate
import java.time.YearMonth
import java.util.*

data class CalendarDay(
    val date: Int,
    val isCurrentMonth: Boolean = true,
    val isToday: Boolean = false,
    val isSelected: Boolean = false,
    val transactions: List<GiaoDichUI> = emptyList()
)

data class GiaoDichUI(
    val ngay: String,
    val danhMuc: String,
    val soTien: Double,
    val loai: String, // "thu" hoặc "chi"
    val iconDanhMuc: String = "", // Icon ID from Firebase
    val mauSacDanhMuc: String = "#000000" // Color hex code from Firebase
)

@Composable
fun Calendar(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    transactions: List<GiaoDichUI>,
    onDateSelected: (LocalDate) -> Unit,
    onNavigateMonth: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        CalendarHeader(yearMonth, onNavigateMonth)
        Spacer(modifier = Modifier.height(8.dp))
        CalendarGrid(yearMonth, selectedDate, transactions, onDateSelected)
    }
}

@Composable
fun CalendarHeader(
    yearMonth: YearMonth,
    onNavigateMonth: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFFF6E3))
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { onNavigateMonth(false) }) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Tháng trước")
            }

            Text(
                text = "${yearMonth.month.getDisplayName(java.time.format.TextStyle.FULL, Locale("vi"))} ${yearMonth.year}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = { onNavigateMonth(true) }) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Tháng sau")
            }
        }
    }
}

@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    transactions: List<GiaoDichUI>,
    onDateSelected: (LocalDate) -> Unit
) {
    val calendarDays = prepareCalendarDays(yearMonth, selectedDate, transactions)
    val daysOfWeek = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            daysOfWeek.forEach { day ->
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = when (day) {
                            "T7" -> Color(0xFF1976D2)
                            "CN" -> Color(0xFFD32F2F)
                            else -> Color.Gray
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Divider(color = Color.LightGray)

        LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.height(280.dp)) {
            items(calendarDays) { day ->
                CalendarDayItem(day) {
                    if (day.isCurrentMonth) {
                        onDateSelected(LocalDate.of(yearMonth.year, yearMonth.monthValue, day.date))
                    }
                }
            }
        }
    }
}

fun prepareCalendarDays(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    transactions: List<GiaoDichUI>
): List<CalendarDay> {
    val days = mutableListOf<CalendarDay>()
    val today = LocalDate.now()

    val firstDayOfMonth = yearMonth.atDay(1).dayOfWeek.value % 7 // % 7 để CN = 0
    val totalDaysInMonth = yearMonth.lengthOfMonth()
    val previousMonth = yearMonth.minusMonths(1)
    val daysInPreviousMonth = previousMonth.lengthOfMonth()

    // Thêm ngày của tháng trước để lấp đầy hàng đầu tiên
    for (i in firstDayOfMonth downTo 1) {
        days.add(
            CalendarDay(
                date = daysInPreviousMonth - i + 1,
                isCurrentMonth = false
            )
        )
    }

    // Ngày của tháng hiện tại
    for (day in 1..totalDaysInMonth) {
        val currentDate = LocalDate.of(yearMonth.year, yearMonth.month, day)
        val transInDay = transactions.filter {
            it.ngay == currentDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        }

        days.add(
            CalendarDay(
                date = day,
                isCurrentMonth = true,
                isToday = currentDate == today,
                isSelected = currentDate == selectedDate,
                transactions = transInDay
            )
        )
    }

    // Thêm ngày của tháng sau để đủ 6 hàng (6 x 7 = 42 ô)
    while (days.size % 7 != 0) {
        days.add(CalendarDay(date = days.size - totalDaysInMonth + 1, isCurrentMonth = false))
    }

    return days
}


@Composable
fun CalendarDayItem(day: CalendarDay, onClick: () -> Unit) {
    val background = when {
        day.isSelected -> Color(0xFFB3E5FC)
        else -> Color.Transparent
    }

    val textColor = when {
        !day.isCurrentMonth -> Color.LightGray
        day.isToday -> Color(0xFF4CAF50)
        else -> Color.Black
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.date.toString(),
                color = textColor,
                fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal
            )

//           hiển thị số tièn trong từng ô lich
//            val thu = day.transactions.filter { it.loai == "thu" }.sumOf { it.soTien }
//            val chi = day.transactions.filter { it.loai == "chi" }.sumOf { it.soTien }
//
//            if (thu > 0) {
//                Text(formatCurrency(thu), fontSize = 8.sp, color = Color(0xFF388E3C))
//            }
//            if (chi > 0) {
//                Text(formatCurrency(chi), fontSize = 8.sp, color = Color.Red)
//            }
        }
    }
}

@Composable
fun SummarySection(transactions: List<GiaoDichUI>) {
    val thu = transactions.filter { it.loai == "thu" }.sumOf { it.soTien }
    val chi = transactions.filter { it.loai == "chi" }.sumOf { it.soTien }
    val tong = thu - chi

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SummaryItem("Thu nhập", thu, Color(0xFF388E3C))
        SummaryItem("Chi tiêu", -chi, Color.Red) // ✅ dấu trừ ở đây
        SummaryItem("Tổng", tong, if (tong >= 0) Color(0xFF388E3C) else Color.Red)
    }

    Divider()
}


@Composable
fun SummaryItem(title: String, amount: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, fontSize = 14.sp, color = Color.Gray)
        Text(
            text = formatCurrency(amount),
            color = color,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}