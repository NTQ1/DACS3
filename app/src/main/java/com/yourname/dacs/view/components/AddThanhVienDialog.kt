package com.yourname.dacs.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yourname.dacs.model.NguoiDung
import com.yourname.dacs.viewmodel.ThanhVienViewModel

@Composable
fun AddThanhVienDialog(
    huChungId: String,
    tenHuChung: String,
    thanhVienViewModel: ThanhVienViewModel,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val searchResult by thanhVienViewModel.searchResult.collectAsState()
    val guiLoiMoiThanhCong by thanhVienViewModel.guiLoiMoiThanhCong.collectAsState()
    var hasSearched by remember { mutableStateOf(false) }
    var daGuiLoiMoi by remember { mutableStateOf(false) }

    // Gửi lời mời thành công thì tắt dialog
    LaunchedEffect(guiLoiMoiThanhCong) {
        if (guiLoiMoiThanhCong == true && daGuiLoiMoi) {
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Gửi lời mời vào hũ") },
        text = {
            Column {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Nhập ID hoặc Email") },
                    trailingIcon = {
                        IconButton(onClick = {
                            if (query.isNotBlank()) {
                                hasSearched = true
                                thanhVienViewModel.searchNguoiDung(query)
                            }
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Tìm kiếm")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (hasSearched && searchResult != null) {
                    val user = searchResult!!
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA))
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Tên: ${user.Ten}", style = MaterialTheme.typography.bodyLarge)
                            Text("Email: ${user.Email}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            user.Id?.let {
                                daGuiLoiMoi = true
                                thanhVienViewModel.guiLoiMoi(it, huChungId, tenHuChung)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Gửi lời mời")
                    }
                }

                if (hasSearched && searchResult == null && query.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Không tìm thấy người dùng.", color = Color.Red)
                }

                if (guiLoiMoiThanhCong == false && daGuiLoiMoi) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Gửi lời mời thất bại.", color = Color.Red)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

