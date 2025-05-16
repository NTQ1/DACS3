package com.yourname.dacs.view.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourname.dacs.model.GiaoDich
import com.yourname.dacs.viewmodel.GiaoDichViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExpensesIncomeTabContent(periodType: String, period: Pair<Int, Int>) {
    // Add state to track which tab is selected
    var selectedTab by remember { mutableStateOf("chi") }

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

    // Filter transactions by selected period (month/year or just year)
    val (month, year) = period
    val filteredTransactions = remember(transactions, month, year, selectedTab, periodType) {
        transactions.filter { giaoDich ->
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = dateFormat.parse(giaoDich.thoiGian)
                val calendar = Calendar.getInstance()
                calendar.time = date

                val transMonth = calendar.get(Calendar.MONTH) + 1
                val transYear = calendar.get(Calendar.YEAR)

                // For yearly view, we only check the year
                // For monthly view, we check both month and year
                val periodMatch = if (periodType == "year") {
                    transYear == year
                } else {
                    transMonth == month && transYear == year
                }

                periodMatch && giaoDich.loai == selectedTab
            } catch (e: Exception) {
                false
            }
        }
    }

    // Group transactions by category
    val groupedTransactions = remember(filteredTransactions) {
        filteredTransactions.groupBy { it.danhMucId }
    }

    // Calculate totals and percentages for each category
    val categories = remember(groupedTransactions) {
        val totalAmount = filteredTransactions.sumOf { it.soTien }

        groupedTransactions.map { (_, transactions) ->
            val firstTransaction = transactions.first()
            val categoryAmount = transactions.sumOf { it.soTien }
            val percentage = if (totalAmount > 0) (categoryAmount / totalAmount * 100).toFloat() else 0f

            mapOf(
                "name" to firstTransaction.tenDanhMuc,
                "amount" to categoryAmount.toLong(),
                "percentage" to percentage,
                "color" to Color(android.graphics.Color.parseColor(firstTransaction.mauSacDanhMuc)),
                "icon" to firstTransaction.iconDanhMuc
            )
        }.sortedByDescending { it["amount"] as Long } // Sort by amount in descending order
    }

    // Calculate summary amounts - modified to consider periodType
    val expensesTotal = remember(transactions, month, year, periodType) {
        transactions.filter { giaoDich ->
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = dateFormat.parse(giaoDich.thoiGian)
                val calendar = Calendar.getInstance()
                calendar.time = date

                val transMonth = calendar.get(Calendar.MONTH) + 1
                val transYear = calendar.get(Calendar.YEAR)

                val periodMatch = if (periodType == "year") {
                    transYear == year
                } else {
                    transMonth == month && transYear == year
                }

                periodMatch && giaoDich.loai == "chi"
            } catch (e: Exception) {
                false
            }
        }.sumOf { it.soTien }.toLong()
    }

    val incomeTotal = remember(transactions, month, year, periodType) {
        transactions.filter { giaoDich ->
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = dateFormat.parse(giaoDich.thoiGian)
                val calendar = Calendar.getInstance()
                calendar.time = date

                val transMonth = calendar.get(Calendar.MONTH) + 1
                val transYear = calendar.get(Calendar.YEAR)

                val periodMatch = if (periodType == "year") {
                    transYear == year
                } else {
                    transMonth == month && transYear == year
                }

                periodMatch && giaoDich.loai == "thu"
            } catch (e: Exception) {
                false
            }
        }.sumOf { it.soTien }.toLong()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Tabs with different colors based on selected tab
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clickable { selectedTab = "chi" },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Chi tiêu",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (selectedTab == "chi") Color(0xFFFF9500) else Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    if (selectedTab == "chi") {
                        Divider(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .height(2.dp)
                                .width(80.dp)
                                .background(Color(0xFFFF9500))
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clickable { selectedTab = "thu" },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Thu nhập",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (selectedTab == "thu") Color(0xFF34C759) else Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    if (selectedTab == "thu") {
                        Divider(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .height(2.dp)
                                .width(80.dp)
                                .background(Color(0xFF34C759))
                        )
                    }
                }
            }
        }

        Divider(Modifier.fillMaxWidth())

        // Appropriate chart display based on period type
        if (categories.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                DonutChart(categories, modifier = Modifier.size(200.dp))
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Không có dữ liệu",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
            }
        }

        Divider(Modifier.fillMaxWidth())

        // Display categories
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(categories) { category ->
                CategoryItem(
                    name = category["name"] as String,
                    amount = category["amount"] as Long,
                    percentage = category["percentage"] as Float,
                    color = category["color"] as Color,
                    icon = category["icon"] as String,

                )
            }
        }
    }
}

@Composable
fun CategoryItem(
    name: String,
    amount: Long,
    percentage: Float,
    color: Color,
    icon: String,

) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = getIconResourceFromName(icon)),
                        contentDescription = name,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text(
                        text = name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    // Add progress bar for both year and month views
                    LinearProgressIndicator(
                        progress = percentage / 100f,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .height(4.dp)
                            .width(80.dp),
                        color = color,
                        trackColor = Color.LightGray
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "${formatCurrency(amount)}đ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${String.format("%.1f", percentage)} %",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "View details",
                    tint = Color.Gray,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
fun DonutChart(categories: List<Map<String, Any>>, modifier: Modifier = Modifier) {
    val sweepAngles = mutableListOf<Float>()
    val colors = mutableListOf<Color>()

    categories.forEach { category ->
        val percentage = category["percentage"] as Float
        sweepAngles.add(3.6f * percentage)
        colors.add(category["color"] as Color)
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            var startAngle = -90f
            val donutThickness = size.width * 0.2f
            val radius = size.width / 2 - donutThickness / 2

            sweepAngles.forEachIndexed { index, sweepAngle ->
                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = donutThickness, cap = StrokeCap.Butt),
                    size = Size(radius * 2, radius * 2),
                    topLeft = Offset(donutThickness / 2, donutThickness / 2)
                )
                startAngle += sweepAngle
            }
        }

        if (categories.isNotEmpty()) {
            // For single category chart, display the name in center
            if (categories.size == 1) {
                Text(
                    text = categories[0]["name"] as String,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            } else {
                // For multiple categories, show total amount
                val totalAmount = categories.sumOf { it["amount"] as Long }
                Text(
                    text = formatCurrency(totalAmount) + "đ",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

fun formatCurrency(amount: Long): String {
    return NumberFormat.getNumberInstance(Locale.getDefault()).format(amount)
}

// Helper function to get drawable resource ID by icon name
fun getIconResourceFromName(iconName: String): Int {
    return try {
        val drawableClass = Class.forName("com.yourname.dacs.R\$drawable")
        val field = drawableClass.getField(iconName)
        field.getInt(null)
    } catch (e: Exception) {
        // Return a default icon if there's an error
        android.R.drawable.ic_menu_add
    }
}