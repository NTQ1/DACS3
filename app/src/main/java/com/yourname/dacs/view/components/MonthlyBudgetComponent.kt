package com.yourname.dacs.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourname.dacs.model.GiaoDich
import com.yourname.dacs.viewmodel.GiaoDichViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MonthlyBudgetComponent() {
    var currentMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH) + 1) }
    var currentYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }

    // ViewModel to fetch transaction data
    val viewModel = remember { GiaoDichViewModel() }

    // State for storing the transactions data
    var transactions by remember { mutableStateOf<List<GiaoDich>>(emptyList()) }

    // Fetch transactions from Firebase
    LaunchedEffect(key1 = Unit) {
        viewModel.layDanhSachGiaoDich(
            onDataReceived = { danhSach ->
                transactions = danhSach
            },
            onFailure = { exception ->
                // Handle error - could add a state to show error message
                println("Error loading transactions: ${exception.message}")
            }
        )
    }

    // Filter transactions by selected month and year
    val filteredTransactions = remember(transactions, currentMonth, currentYear) {
        transactions.filter { giaoDich ->
            // Parse the date and check if it belongs to the selected month and year
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = dateFormat.parse(giaoDich.thoiGian)
                val calendar = Calendar.getInstance()
                calendar.time = date

                val transMonth = calendar.get(Calendar.MONTH) + 1
                val transYear = calendar.get(Calendar.YEAR)

                transMonth == currentMonth && transYear == currentYear
            } catch (e: Exception) {
                false
            }
        }
    }

    // Calculate summary amounts
    val expenses = remember(filteredTransactions) {
        filteredTransactions.filter { it.loai == "chi" }.sumOf { it.soTien }.toLong()
    }

    val income = remember(filteredTransactions) {
        filteredTransactions.filter { it.loai == "thu" }.sumOf { it.soTien }.toLong()
    }

    val balance = income - expenses

    val monthPadded = String.format("%02d", currentMonth)
    val lastDay = when (currentMonth) {
        4, 6, 9, 11 -> 30
        2 -> if (currentYear % 4 == 0 && (currentYear % 100 != 0 || currentYear % 400 == 0)) 29 else 28
        else -> 31
    }
    val currentPeriod = "$monthPadded/$currentYear (01/$monthPadded—$lastDay/$monthPadded)"

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Bộ chọn tháng
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFFF8E1))
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Previous",
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable {
                        if (currentMonth > 1) currentMonth-- else {
                            currentMonth = 12
                            currentYear--
                        }
                    },
                tint = Color.Gray
            )

            Text(
                text = currentPeriod,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Next",
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable {
                        if (currentMonth < 12) currentMonth++ else {
                            currentMonth = 1
                            currentYear++
                        }
                    },
                tint = Color.Gray
            )
        }

        // Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f).padding(end = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Chi tiêu", fontSize = 14.sp, color = Color.Gray)
                    Text("-${formatCurrency(expenses)}đ", fontSize = 18.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }

            Card(
                modifier = Modifier.weight(1f).padding(start = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Thu nhập", fontSize = 14.sp, color = Color.Gray)
                    Text("+${formatCurrency(income)}đ", fontSize = 18.sp, color = Color(0xFF34C759), fontWeight = FontWeight.Bold)
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Tổng", fontSize = 14.sp, color = Color.Gray)
                Text(
                    text = if (balance >= 0) "+${formatCurrency(balance)}đ" else "-${formatCurrency(Math.abs(balance))}đ",
                    fontSize = 18.sp,
                    color = if (balance >= 0) Color(0xFF34C759) else Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Tabs
        ExpensesIncomeTabContent(
            periodType = "month",
            period = Pair(currentMonth, currentYear)
        )
    }
}

