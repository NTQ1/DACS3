package com.yourname.dacs.view.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.listenLoiMoi(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lời Mời Tham Gia Hũ") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            items(loiMoiList) { loiMoiWithSender ->
                LoiMoiCard(
                    loiMoiWithSender = loiMoiWithSender,
                    onAccept = { viewModel.acceptLoiMoi(loiMoiWithSender.loimoi) },
                    onDecline = { viewModel.declineLoiMoi(loiMoiWithSender.loimoi) }
                )
            }
        }
    }
}

@Composable
fun LoiMoiCard(
    loiMoiWithSender: LoiMoiWithSender,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    val loimoi = loiMoiWithSender.loimoi
    val tenNguoiGui = loiMoiWithSender.tenNguoiGui ?: "Không xác định"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "📩 Mời từ: $tenNguoiGui", style = MaterialTheme.typography.titleMedium)
            Text(text = "🔐 Hũ chung: ${loimoi.tenHu}")
            Text(text = "⏳ Trạng thái: ${when (loimoi.trangThai) {
                "cho" -> "Chờ phản hồi"
                "dongy" -> "Đã đồng ý"
                "tuchoi" -> "Đã từ chối"
                else -> "Không xác định"
            }}")

            Spacer(modifier = Modifier.height(8.dp))

            if (loimoi.trangThai == "cho") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDecline) {
                        Text("Từ chối")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onAccept) {
                        Text("Chấp nhận")
                    }
                }
            }
        }
    }
}
