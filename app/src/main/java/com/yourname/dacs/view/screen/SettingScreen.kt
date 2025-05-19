package com.yourname.dacs.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.yourname.dacs.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController? = null,
    onHelpSupportClick: () -> Unit = {},
) {
    // Định nghĩa các màu vàng cam
    val orangePrimary = Color(0xFFF57C00)      // Màu vàng cam đậm

    val orangeDark = Color(0xFFE65100)         // Màu vàng cam tối
    val backgroundColor = Color(0xFFFFF8E1)    // Màu nền nhẹ

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cài đặt ứng dụng",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = orangePrimary,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = backgroundColor,
        bottomBar = {
            navController?.let {
                BottomNavigationBar(navController = it)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(backgroundColor)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Tiêu đề Tài khoản
            CategoryHeader(title = "Tài khoản", color = orangeDark)

            // Items trong category Tài khoản
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    SettingItem(
                        icon = painterResource(id = R.drawable.ic_account),
                        title = "Thông tin tài khoản",
                        onClick = {
                            navController?.navigate("thongtin")
                        },
                        iconTint = orangePrimary
                    )

                    Divider(
                        modifier = Modifier.padding(start = 56.dp),
                        color = Color.LightGray.copy(alpha = 0.5f)
                    )

                    SettingItem(
                        icon = Icons.Default.Notifications,
                        title = "Thông báo",
                        onClick = {
                            navController?.navigate("loimoi")
                        },
                        iconTint = orangePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tiêu đề Hoạt động
            CategoryHeader(title = "Hoạt động", color = orangeDark)

            // Items trong category Hoạt động
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column {
                    SettingItem(
                        icon = painterResource(id = R.drawable.ic_help),
                        title = "Trợ giúp & Hỗ trợ",
                        onClick = {
                            navController?.navigate("hotro")
                        },
                        iconTint = orangePrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nút đăng xuất được tách riêng và nổi bật
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                SettingItem(
                    icon = painterResource(id = R.drawable.ic_logout),
                    title = "Đăng xuất",
                    onClick = {
                        navController?.navigate("login") {
                            popUpTo(0)
                        }
                    },
                    titleColor = Color.Red,
                    iconTint = Color.Red
                )
            }
        }
    }
}

@Composable
fun CategoryHeader(title: String, color: Color) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = color,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SettingItem(
    icon: Any,
    title: String,
    onClick: () -> Unit,
    titleColor: Color = Color.DarkGray,
    iconTint: Color = Color.Gray
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (icon) {
            is Painter -> Icon(
                painter = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            is ImageVector -> Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = titleColor,
            modifier = Modifier.weight(1f)
        )

        // Mũi tên đã được xóa theo yêu cầu
    }
}

