package com.yourname.dacs.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.yourname.dacs.viewmodel.LoiMoiViewModel
import com.yourname.dacs.model.LoiMoiWithSender

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoiMoiScreen(navController: NavHostController) {
    val viewModel: LoiMoiViewModel = viewModel()
    val loiMoiList by viewModel.loiMoiWithSenderList.collectAsState()

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Định nghĩa các màu vàng cam để phù hợp với theme
    val orangePrimary = Color(0xFFF57C00)      // Màu vàng cam đậm
    val orangeLight = Color(0xFFFFB74D)        // Màu vàng cam nhạt
    val orangeContainer = Color(0xFFFFE0B2)    // Màu container
    val backgroundColor = Color(0xFFFFF8E1)    // Màu nền nhẹ

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.listenLoiMoi(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Lời Mời Tham Gia Hũ",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = orangePrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        if (loiMoiList.isEmpty()) {
            // Hiển thị thông báo khi không có lời mời
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Không có lời mời nào",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                items(loiMoiList) { loiMoiWithSender ->
                    LoiMoiCard(
                        loiMoiWithSender = loiMoiWithSender,
                        onAccept = { viewModel.acceptLoiMoi(loiMoiWithSender.loimoi) },
                        onDecline = { viewModel.declineLoiMoi(loiMoiWithSender.loimoi) },
                        orangeLight = orangeLight,
                        orangeContainer = orangeContainer
                    )
                }
            }
        }
    }
}

@Composable
fun LoiMoiCard(
    loiMoiWithSender: LoiMoiWithSender,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    orangeLight: Color,
    orangeContainer: Color
) {
    val loimoi = loiMoiWithSender.loimoi
    val tenNguoiGui = loiMoiWithSender.tenNguoiGui ?: "Không xác định"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header với avatar giả và tên người gửi
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                // Avatar giả lập
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(orangeLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tenNguoiGui.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = tenNguoiGui,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Nội dung lời mời
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(8.dp),
                color = orangeContainer.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Hũ chung:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = loimoi.tenHu,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Trạng thái:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        val trangThaiText = when (loimoi.trangThai) {
                            "cho" -> "Chờ phản hồi"
                            "dongy" -> "Đã đồng ý"
                            "tuchoi" -> "Đã từ chối"
                            else -> "Không xác định"
                        }
                        val trangThaiColor = when (loimoi.trangThai) {
                            "cho" -> Color(0xFFFFA000)  // Amber
                            "dongy" -> Color(0xFF4CAF50) // Green
                            "tuchoi" -> Color(0xFFE53935) // Red
                            else -> Color.Gray
                        }
                        Text(
                            text = trangThaiText,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = trangThaiColor
                        )
                    }
                }
            }

            // Nút hành động chỉ hiển thị khi trạng thái là "chờ"
            if (loimoi.trangThai == "cho") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Nút từ chối với icon X
                    IconButton(
                        onClick = onDecline,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFEBEE)) // Màu nền nhẹ cho từ chối
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Từ chối",
                            tint = Color(0xFFE53935) // Màu đỏ
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Nút chấp nhận với icon tích
                    IconButton(
                        onClick = onAccept,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F5E9)) // Màu nền nhẹ cho chấp nhận
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Chấp nhận",
                            tint = Color(0xFF4CAF50) // Màu xanh lá
                        )
                    }
                }
            } else {
                // Hiển thị badge trạng thái cho trạng thái đã hoàn thành
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    val badgeColor = when (loimoi.trangThai) {
                        "dongy" -> Color(0xFF4CAF50) // Green
                        "tuchoi" -> Color(0xFFE53935) // Red
                        else -> Color.Gray
                    }
                    val badgeText = when (loimoi.trangThai) {
                        "dongy" -> "Đã chấp nhận"
                        "tuchoi" -> "Đã từ chối"
                        else -> ""
                    }

                    if (badgeText.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = badgeColor.copy(alpha = 0.15f),
                            modifier = Modifier.padding(4.dp)
                        ) {
                            Text(
                                text = badgeText,
                                color = badgeColor,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}