package com.yourname.dacs.view.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.yourname.dacs.R
import com.yourname.dacs.model.DanhMuc
import com.yourname.dacs.view.components.AddDanhMucDialog
import com.yourname.dacs.view.components.getIconRes
import com.yourname.dacs.viewmodel.DanhMucViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    val viewModel: DanhMucViewModel = viewModel()
    val tabs = listOf("Thu nhập", "Chi tiêu")
    var selectedTabIndex by remember { mutableStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }

    val allDanhMuc = viewModel.danhMucList
    val categories = allDanhMuc.filter { it.loai == if (selectedTabIndex == 0) "thu" else "chi" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(200.dp)
                                .padding(end = 8.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = null)
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            val activeColor = Color(0xFFFF9800)

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.White,
                contentColor = activeColor,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier
                            .tabIndicatorOffset(tabPositions[selectedTabIndex])
                            .height(4.dp),
                        color = activeColor
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
                                fontSize = if (selectedTabIndex == index) 16.sp else 14.sp,
                                color = if (selectedTabIndex == index) activeColor else Color.Gray
                            )
                        }
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(categories) { item ->
                    CategoryCard(item)
                }

                item {
                    AddCategoryCard { showDialog = true }
                }
            }

            if (showDialog) {
                AddDanhMucDialog(
                    onDismiss = { showDialog = false },
                    onSave = { danhMuc ->
                        val newDanhMuc = danhMuc.copy(loai = if (selectedTabIndex == 0) "thu" else "chi")
                        viewModel.addDanhMuc(newDanhMuc)
                        showDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryCard(item: DanhMuc) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                // TODO: xử lý khi click vào danh mục
            }
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .size(80.dp)
                .border(
                    width = 2.dp,
                    color = Color(android.graphics.Color.parseColor(item.mauSac)),
                    shape = RoundedCornerShape(16.dp)
                ),
            colors = CardDefaults.cardColors(containerColor = Color.White), // ✅ NỀN TRẮNg
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = getIconRes(item.icon)),
                    contentDescription = item.ten,
                    modifier = Modifier.size(36.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                        Color(android.graphics.Color.parseColor(item.mauSac))
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = item.ten, fontSize = 14.sp)
    }
}


@Composable
fun AddCategoryCard(onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() }
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.size(80.dp),
            colors = CardDefaults.cardColors(containerColor = Color.LightGray),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Thêm danh mục",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Thêm", fontSize = 14.sp)
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        "home" to R.drawable.ic_home,
        "history" to R.drawable.ic_history,
        "diagram" to R.drawable.ic_diagram,
        "savemoney" to R.drawable.ic_savemoney,
        "setting" to R.drawable.ic_setting
    )

    Column {
        // 🔹 Gạch phân cách phía trên thanh điều hướng
        Divider(color = Color.LightGray, thickness = 1.dp)

        NavigationBar(
            containerColor = Color.White // 🔹 Nền trắng
        ) {
            items.forEach { (route, iconRes) ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = route,
                            modifier = Modifier.size(28.dp) // 🔹 Icon to hơn
                        )
                    },
                    selected = false, // Nếu muốn, có thể xử lý trạng thái chọn
                    onClick = { navController.navigate(route) },
                    alwaysShowLabel = false // 🔹 Không hiện chữ
                )
            }
        }
    }
}


