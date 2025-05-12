package com.yourname.dacs.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.yourname.dacs.view.components.AddThanhVienDialog
import com.yourname.dacs.viewmodel.HuChungViewModel
import com.yourname.dacs.viewmodel.ThanhVienViewModel
import com.yourname.dacs.model.NguoiDung

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChiTietHuChungScreen(
    huChungId: String,
    navController: NavHostController,
    viewModel: HuChungViewModel = viewModel()
) {
    val huChung = viewModel.huChungList.find { it.id == huChungId }
    var showAddDialog by remember { mutableStateOf(false) }
    val thanhVienViewModel: ThanhVienViewModel = viewModel()
    val thanhVienList by thanhVienViewModel.thanhVienNguoiDung.collectAsState()
    val tabs = listOf("Giao Dịch", "Thành Viên")
    var selectedTabIndex by remember { mutableStateOf(0) }

    LaunchedEffect(huChungId) {
        thanhVienViewModel.taiDanhSachThanhVien(huChungId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết hũ") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTabIndex == 1) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = Color(0xFFFF9800),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Thêm thành viên")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = Color(0xFFFF9800),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier
                            .tabIndicatorOffset(tabPositions[selectedTabIndex])
                            .height(3.dp),
                        color = Color(0xFFFF9800)
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTabIndex == index) Color(0xFFFF9800) else Color.Gray
                            )
                        }
                    )
                }
            }

            if (huChung != null) {
                when (selectedTabIndex) {
                    0 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFFFF9800),
                                    shape = RoundedCornerShape(bottomStart = 25.dp, bottomEnd = 25.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Text(
                                text = huChung.ten,
                                fontSize = 24.sp,
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = huChung.mota,
                                fontSize = 18.sp,
                                color = Color.White,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Text(
                                text = "Ngày tạo: ${huChung.ngayTao}",
                                fontSize = 14.sp,
                                color = Color.White,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    1 -> {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Danh sách thành viên", fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            if (thanhVienList.isEmpty()) {
                                Text("Chưa có thành viên.", color = Color.Gray)
                            } else {
                                LazyColumn {
                                    items(thanhVienList) { user ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Text("Tên: ${user.Ten}")
                                                Text("Email: ${user.Email}")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Không tìm thấy hũ này.")
                }
            }
        }

        if (showAddDialog) {
            AddThanhVienDialog(
                huChungId = huChungId,
                tenHuChung = huChung?.ten ?: "",
                thanhVienViewModel = thanhVienViewModel,
                onDismiss = { showAddDialog = false }
            )
        }
    }
}
