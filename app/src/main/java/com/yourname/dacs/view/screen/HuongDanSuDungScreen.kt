package com.yourname.dacs.view.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// Định nghĩa màu chủ đạo là vàng cam
val OrangePrimary = Color(0xFFFFA000)
val OrangeLight = Color(0xFFFFD149)
val OrangeDark = Color(0xFFC67100)
val TextWhite = Color(0xFFFFFAF0)
val BackgroundLight = Color(0xFFFFF8E1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HuongDanSuDungScreen(
    navController: NavHostController,
) {
    var currentPage by remember { mutableStateOf(0) }

    val guidePages = listOf(
        GuideItem(
            title = "Chào mừng đến với ứng dụng Quản lý Chi tiêu",
            description = "Ứng dụng giúp bạn theo dõi, quản lý và phân tích chi tiêu hàng ngày một cách hiệu quả",
            iconId = "welcome_icon"
        ),
        GuideItem(
            title = "Thêm Danh Mục Chi Tiêu",
            description = "Bạn nhấn vào biểu tượng dấu cộng (+) trên màn hình chính để tạo danh mục mới. Sau đó, nhập tên danh mục, " +
                    "chọn biểu tượng (icon) và màu sắc phù hợp để dễ phân biệt. Cuối cùng, nhấn lưu để thêm danh mục vào hệ thống.",
            iconId = "add_expense_icon"
        ),
        GuideItem(
            title = "Thêm số tiền thu hoặc chi",
            description = "Khi muốn ghi nhận giao dịch, bạn chọn danh mục tương ứng rồi nhập số tiền. Bạn có thể chọn loại giao dịch là thu nhập hoặc chi tiêu, " +
                    "sau đó lưu lại để hệ thống ghi nhận dữ liệu trên thiết bị.",
            iconId = "stats_icon"
        ),
        GuideItem(
            title = "Xem lịch sử thu chi hàng ngày",
            description = "Bạn nhấn vào biểu tượng cuốn lịch để mở giao diện lịch. Chọn ngày bất kỳ trong tháng, ứng dụng sẽ hiển thị chi tiết các khoản thu chi trong ngày đó, " +
                    "đồng thời tính tổng số tiền thu và chi giúp bạn dễ dàng theo dõi.",
            iconId = "limit_icon"
        ),
        GuideItem(
            title = "Xem sơ đồ so sánh thu chi theo năm",
            description = "Ứng dụng sẽ tổng hợp số tiền thu và chi theo từng tháng trong năm và hiển thị bằng biểu đồ trực quan." +
                    " Bạn có thể dễ dàng so sánh và theo dõi xu hướng tài chính cá nhân qua các tháng.",
            iconId = "report_icon"
        ),
        GuideItem(
            title = "Hũ tiêu chung",
            description = "Bạn có thể tạo các hũ tiêu chung để tiết kiệm cùng nhóm bạn hoặc gia đình. Mỗi hũ sẽ có mô tả mục đích tiết kiệm, và bạn có thể mời thêm thành viên tham gia. Mọi " +
                    "đóng góp của các thành viên sẽ được ghi lại và hiển thị lịch sử chi tiết, giúp quản lý quỹ chung minh bạch và thuận tiện.",
            iconId = "report_icon"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hướng dẫn sử dụng") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = OrangePrimary,
                    titleContentColor = TextWhite
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundLight),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GuideContent(
                guidePages = guidePages,
                currentPage = currentPage,
                onPageChange = { currentPage = it }
            )
        }
    }
}

@Composable
fun GuideContent(
    guidePages: List<GuideItem>,
    currentPage: Int,
    onPageChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Guide content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Placeholder icon
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(OrangeLight),
                    contentAlignment = Alignment.Center
                ) {
                    // Thay thế bằng icon thực tế
                    Text(
                        text = (currentPage + 1).toString(),
                        color = TextWhite,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = guidePages[currentPage].title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = OrangeDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Text(
                        text = guidePages[currentPage].description,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }
            }
        }

        // Indicator and navigation
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Indicator dots
            Row(
                modifier = Modifier.padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                for (i in guidePages.indices) {
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(
                                if (i == currentPage) OrangePrimary else Color.LightGray
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = {
                        if (currentPage > 0) onPageChange(currentPage - 1)
                    },
                    enabled = currentPage > 0
                ) {
                    Text("Trước", color = if (currentPage > 0) OrangeDark else Color.Gray)
                }

                // Ẩn nút "Tiếp theo" khi đến trang cuối cùng
                if (currentPage < guidePages.size - 1) {
                    Button(
                        onClick = {
                            onPageChange(currentPage + 1)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangePrimary
                        )
                    ) {
                        Text("Tiếp theo", color = TextWhite)
                    }
                } else {
                    // Có thể thêm nút "Hoàn thành" hoặc để trống ở đây
                    Spacer(modifier = Modifier.width(88.dp)) // Khoảng trống có độ rộng tương đương với nút
                }
            }
        }
    }
}

data class GuideItem(
    val title: String,
    val description: String,
    val iconId: String
)