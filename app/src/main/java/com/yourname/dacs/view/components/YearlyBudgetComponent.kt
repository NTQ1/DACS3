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
fun YearlyBudgetComponent() {
    // State for current year
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
                // Handle error
                println("Error loading transactions: ${exception.message}")
            }
        )
    }

    // Filter transactions by selected year
    val filteredTransactions = remember(transactions, currentYear) {
        transactions.filter { giaoDich ->
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = dateFormat.parse(giaoDich.thoiGian)
                val calendar = Calendar.getInstance()
                calendar.time = date

                val transYear = calendar.get(Calendar.YEAR)
                transYear == currentYear
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

    // Format the period display string
    val currentPeriod = "$currentYear (01/01—31/12)"

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Year selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF5F5F5))  // Light gray background
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Previous Year",
                modifier = Modifier
                    .padding(start = 16.dp)
                    .clickable {
                        currentYear--
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
                contentDescription = "Next Year",
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable {
                        currentYear++
                    },
                tint = Color.Gray
            )
        }

        // Summary Cards
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF8F9FA)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Tổng quan năm $currentYear",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Income Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Text(
                            "Thu nhập",
                            modifier = Modifier.padding(start = 8.dp),
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    Text(
                        "+${formatCurrency(income)}đ",
                        fontSize = 16.sp,
                        color = Color(0xFF34C759),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Expense Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Text(
                            "Chi tiêu",
                            modifier = Modifier.padding(start = 8.dp),
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                    Text(
                        "-${formatCurrency(expenses)}đ",
                        fontSize = 16.sp,
                        color = Color(0xFFFF3B30),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Balance Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "tổng",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (balance >= 0) "+${formatCurrency(balance)}đ" else "-${formatCurrency(Math.abs(balance))}đ",
                        fontSize = 18.sp,
                        color = if (balance >= 0) Color(0xFF34C759) else Color(0xFFFF3B30),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Expense & Income Tabs with detailed categories
        ExpensesIncomeTabContent(
            periodType = "year",  // Set to "year" mode
            period = Pair(0, currentYear)  // We use 0 for month to indicate yearly view
        )
    }
}

