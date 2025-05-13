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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.yourname.dacs.view.components.AddThanhVienDialog
import com.yourname.dacs.view.components.AddTietKiemDialog
import com.yourname.dacs.viewmodel.HuChungViewModel
import com.yourname.dacs.viewmodel.ThanhVienViewModel
import com.yourname.dacs.viewmodel.TietKiemViewModel
import com.yourname.dacs.model.TietKiem

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChiTietHuChungScreen(
    huChungId: String,
    navController: NavHostController,
    viewModel: HuChungViewModel = viewModel(),
    tietKiemViewModel: TietKiemViewModel = viewModel(),
    thanhVienViewModel: ThanhVienViewModel = viewModel()
) {
    val huChung = viewModel.huChungList.find { it.id == huChungId }
    var showAddDialog by remember { mutableStateOf(false) }
    var showAddGiaoDichDialog by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableStateOf(0) }

    val thanhVienList by thanhVienViewModel.thanhVienNguoiDung.collectAsState()
    val danhSachGiaoDich by tietKiemViewModel.getTietKiemForHu(huChungId).collectAsState(initial = emptyList())

    val thuNhap = tietKiemViewModel.getThuNhapForHu(huChungId)
    val chiTieu = tietKiemViewModel.getChiTieuForHu(huChungId)
    val tongTien = thuNhap - chiTieu

    val tabs = listOf("Giao Dịch", "Thành Viên")

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
                    contentColor = Color.White
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
                        Box(modifier = Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(bottom = 80.dp)
                            ) {
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

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Thu nhập", color = Color.Black)
                                        Text(
                                            formatCurrency(thuNhap),
                                            color = Color(0xFF4CAF50),
                                            fontSize = 20.sp
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Chi tiêu", color = Color.Black)
                                        Text(
                                            // Thêm dấu trừ trước số chi tiêu nếu chưa có
                                            text = if (chiTieu >= 0) "-${formatCurrency(chiTieu)}" else formatCurrency(chiTieu),
                                            color = Color(0xFFFF5722),
                                            fontSize = 20.sp
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Tổng", color = Color.Black)
                                        Text(
                                            text = if (tongTien >= 0) "+${formatCurrency(tongTien)}" else "-${formatCurrency(-tongTien)}",
                                            color = if (tongTien >= 0) Color(0xFF4CAF50) else Color(0xFFFF5722),
                                            fontSize = 20.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Lịch sử giao dịch",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .padding(horizontal = 16.dp)
                                ) {
                                    items(danhSachGiaoDich) { giaoDich ->
                                        var tenNguoiDung by remember { mutableStateOf("Đang tải...") }

                                        LaunchedEffect(giaoDich.idNguoiDung) {
                                            tietKiemViewModel.getTenNguoiDungById(giaoDich.idNguoiDung) { ten ->
                                                tenNguoiDung = ten
                                            }
                                        }

                                        TransactionListItem(
                                            tenNguoiDung = tenNguoiDung,
                                            moTa = giaoDich.ghiChu,
                                            soTien = formatCurrency(giaoDich.soTien),
                                            ngayGio = giaoDich.ngayGio,
                                            loai = giaoDich.loaiGiaoDich  // Use the loaiGiaoDich field from the database
                                        )
                                        Divider()
                                    }

                                }

                            }

                            FloatingActionButton(
                                onClick = {
                                    showAddGiaoDichDialog = true
                                },
                                containerColor = Color(0xFFFF9800),
                                contentColor = Color.White,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(16.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Thêm giao dịch")
                            }
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
        if (showAddGiaoDichDialog) {
            AddTietKiemDialog(
                onDismiss = { showAddGiaoDichDialog = false },
                idHu = huChungId
            )
        }
    }
}

@Composable
fun TransactionListItem(
    tenNguoiDung: String,
    moTa: String,
    soTien: String,
    ngayGio: String,
    loai: String
) {
    // Format số tiền với dấu trừ nếu là chi
    val displayAmount = if (loai.equals("chi", ignoreCase = true)) {
        // Nếu soTien đã có dấu trừ ở đầu, giữ nguyên, nếu không thêm dấu trừ
        if (soTien.startsWith("-")) soTien else "-$soTien"
    } else {
        // Nếu là thu, đảm bảo không có dấu trừ
        if (soTien.startsWith("-")) soTien.substring(1) else soTien
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = tenNguoiDung,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = moTa,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = displayAmount,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (loai.equals("chi", ignoreCase = true)) Color(0xFFD32F2F) else Color(0xFF388E3C)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = ngayGio,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
// Format tiền
fun formatCurrency(amount: Double): String {
    val symbols = DecimalFormatSymbols().apply {
        groupingSeparator = '.'
    }
    val formatter = DecimalFormat("#,###", symbols)
    return formatter.format(amount) + "đ"
}
