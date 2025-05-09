package com.yourname.dacs.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.yourname.dacs.model.HuChung
import com.yourname.dacs.view.components.AddHuChungDialog
import com.yourname.dacs.viewmodel.HuChungViewModel
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext

@Composable
fun TogetherScreen(
    navController: NavHostController,
    huChungViewModel: HuChungViewModel = viewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    val huChungList = huChungViewModel.huChungList

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFFFFA726)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Goal", tint = Color.White)
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFFFA726),
                        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Hũ tiêu chung",
                        fontSize = 24.sp,
                        color = Color.White
                    )
                    Text(
                        text = "tiền của bạn rồi cũng sẽ là tiền của tôi =))))",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Danh sách Hũ từ Firebase
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(huChungList) { huChung ->
                    HuChungCard(huChung)
                }
            }
        }

        if (showDialog) {
            AddHuChungDialog(
                onDismiss = { showDialog = false },
                onSave = { huChung ->
                    huChungViewModel.addHuChung(huChung)
                    showDialog = false
                }
            )
        }
    }
}


@Composable
fun HuChungCard(huChung: HuChung) {
    val context = LocalContext.current
    val iconId = remember(huChung.icon) {
        context.resources.getIdentifier(huChung.icon, "drawable", context.packageName)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        Color(android.graphics.Color.parseColor(huChung.mausac)).copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (iconId != 0) {
                    Icon(
                        painter = painterResource(id = iconId),
                        contentDescription = null,
                        tint = Color(android.graphics.Color.parseColor(huChung.mausac)),
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = huChung.ten, fontSize = 16.sp)
                Text(text = huChung.ngayTao, fontSize = 12.sp, color = Color.Gray)
                Text(
                    text = "$0 saved of $0",
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
                LinearProgressIndicator(
                    progress = 0f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    color = Color(android.graphics.Color.parseColor(huChung.mausac))
                )
                Text(
                    text = "0 days left",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Icon(Icons.Default.MoreVert, contentDescription = "Options")
        }
    }
}


