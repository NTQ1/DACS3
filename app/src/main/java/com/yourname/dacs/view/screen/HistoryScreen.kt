package com.yourname.dacs.view.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.yourname.dacs.R
import com.yourname.dacs.model.GiaoDich
import com.yourname.dacs.view.components.*
import com.yourname.dacs.viewmodel.GiaoDichViewModel
import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*




// Convert GiaoDich model to GiaoDichUI for display
fun GiaoDich.toGiaoDichUI(): GiaoDich {
    // Extract date from thoiGian (format: "yyyy-MM-dd HH:mm:ss")
    val date = try {
        val parts = thoiGian.split(" ")[0].split("-")
        "${parts[2]}/${parts[1]}/${parts[0]}" // Convert to "dd/MM/yyyy"
    } catch (e: Exception) {
        "01/01/2025" // Default date if parsing fails
    }

    return GiaoDich(
        id = id, // Add ID to GiaoDichUI
        thoiGian = date,
        tenDanhMuc = tenDanhMuc,
        soTien = soTien,
        loai = loai,
        iconDanhMuc = iconDanhMuc,
        mauSacDanhMuc = mauSacDanhMuc
    )
}

@Composable
fun HistoryScreen(
    navController: NavHostController,
    giaoDichViewModel: GiaoDichViewModel = viewModel(),
    onDeleted: () -> Unit = {}
) {
    // State for transactions
    var transactions by remember { mutableStateOf<List<GiaoDich>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Function to load transactions
    fun loadTransactions() {
        isLoading = true
        giaoDichViewModel.layDanhSachGiaoDich(
            onDataReceived = { giaoDichList ->
                // Convert the GiaoDich models to GiaoDichUI for display
                transactions = giaoDichList.map { it.toGiaoDichUI() }
                isLoading = false
            },
            onFailure = { exception ->
                errorMessage = exception.message
                isLoading = false
            }
        )
    }

    // Load data from Firebase
    LaunchedEffect(key1 = Unit) {
        loadTransactions()
    }

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }

    val selectedDateTransactions = transactions.filter {
        it.thoiGian == selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }

    // Handler for transaction deletion
    val handleDeleteTransaction = { giaoDichId: String ->
        giaoDichViewModel.xoaGiaoDich(
            giaoDichId = giaoDichId,
            onSuccess = {
                // Reload the transactions after successful deletion
                loadTransactions()
                onDeleted() // Call the callback if needed
            },
            onFailure = { exception ->
                errorMessage = "Lỗi khi xoá giao dịch: ${exception.message}"
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Title and Search bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = "Lịch",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }

        if (isLoading) {
            // Show loading indicator
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Đang tải dữ liệu...")
            }
        } else if (errorMessage != null) {
            // Show error message
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Lỗi: $errorMessage", color = Color.Red)
            }
        } else {
            // Calendar component
            Calendar(
                yearMonth = currentYearMonth,
                selectedDate = selectedDate,
                transactions = transactions,
                onDateSelected = { selectedDate = it },
                onNavigateMonth = { isNext ->
                    if (isNext) {
                        currentYearMonth = currentYearMonth.plusMonths(1)
                    } else {
                        currentYearMonth = currentYearMonth.minusMonths(1)
                    }
                }
            )

            // Summary section
            SummarySection(
                transactions = selectedDateTransactions
            )

            // Selected date display
            Text(
                text = "${selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))} ",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 14.sp,
                color = Color.Gray
            )

            // Transaction list for the selected date
            Box(modifier = Modifier.weight(1f)) {
                if (selectedDateTransactions.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Không có giao dịch trong ngày này", color = Color.Gray)
                    }
                } else {
                    TransactionList(
                        transactions = selectedDateTransactions,
                        onDelete = handleDeleteTransaction
                    )
                }
            }
        }

        BottomNavigationBar(navController = navController)
    }
}

@Composable
fun TransactionList(
    transactions: List<GiaoDich>,
    onDelete: (String) -> Unit
) {
    LazyColumn {
        items(transactions) { transaction ->
            TransactionItem(
                transaction = transaction,
                onDelete = onDelete
            )
        }
    }
}

@Composable
fun TransactionItem(
    transaction: GiaoDich,
    onDelete: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Use custom icon from Firebase if available
            val iconRes = when (transaction.iconDanhMuc) {
                "ic_account" -> R.drawable.ic_account
                "ic_bank" -> R.drawable.ic_bank
                "ic_cake" -> R.drawable.ic_cake
                "ic_calendar_today" -> R.drawable.ic_calendar_today
                "ic_car" -> R.drawable.ic_car
                "ic_cart" -> R.drawable.ic_cart
                "ic_default" -> R.drawable.ic_default
                "ic_delete" -> R.drawable.ic_delete
                "ic_diagram" -> R.drawable.ic_diagram
                "ic_electricity" -> R.drawable.ic_electricity
                "ic_food" -> R.drawable.ic_food
                "ic_game" -> R.drawable.ic_game
                "ic_gift" -> R.drawable.ic_gift
                "ic_gym" -> R.drawable.ic_gym
                "ic_history" -> R.drawable.ic_history
                "ic_home" -> R.drawable.ic_home
                "ic_ice" -> R.drawable.ic_ice
                "ic_lock" -> R.drawable.ic_lock
                "ic_mail" -> R.drawable.ic_mail
                "ic_money" -> R.drawable.ic_money
                "ic_plane" -> R.drawable.ic_plane
                "ic_savemoney" -> R.drawable.ic_savemoney
                "ic_setting" -> R.drawable.ic_setting
                "ic_water" -> R.drawable.ic_water
                else -> R.drawable.ic_lock // Default icon
            }


            // Use custom color from Firebase if available
            val iconColor = try {
                Color(android.graphics.Color.parseColor(transaction.mauSacDanhMuc))
            } catch (e: Exception) {
                if (transaction.loai == "thu") Color(0xFF388E3C) else Color(0xFFF89C1B)
            }

            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = transaction.tenDanhMuc,
                fontWeight = FontWeight.Medium
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = formatCurrency(if (transaction.loai == "chi") -transaction.soTien else transaction.soTien),
                color = if (transaction.loai == "thu") Color(0xFF388E3C) else Color.Red,
                fontWeight = FontWeight.Bold
            )

            // Nút X để xoá - Now connected to the delete handler
            IconButton(onClick = {
                // Call the onDelete handler with the transaction ID
                onDelete(transaction.id)
            }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = Color.Red
                )
            }

        }
    }

    Divider()
}