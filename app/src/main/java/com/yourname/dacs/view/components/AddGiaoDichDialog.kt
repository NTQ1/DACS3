package com.yourname.dacs.view.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yourname.dacs.model.GiaoDich
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddGiaoDichDialog(
    danhMucId: String,
    loai: String,
    accountId: String,
    onDismiss: () -> Unit,
    onSave: (GiaoDich) -> Unit
) {
    var soTien by remember { mutableStateOf("") }
    var ghiChu by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val thoiGian = remember { getCurrentFormattedTime() }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val parsedSoTien = soTien.toDoubleOrNull()
                    if (parsedSoTien != null) {
                        val giaoDich = GiaoDich(
                            soTien = parsedSoTien,
                            ghiChu = ghiChu,
                            danhMucId = danhMucId,
                            accountId = accountId,
                            thoiGian = thoiGian,
                            loai = loai
                        )
                        onSave(giaoDich)
                    } else {
                        errorMessage = "Số tiền không hợp lệ!"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Lưu", color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Hủy")
            }
        },
        title = { Text("Thêm Giao Dịch", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "Thời gian: $thoiGian",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = soTien,
                    onValueChange = {
                        soTien = it
                        errorMessage = null
                    },
                    label = { Text("Số tiền") },
                    singleLine = true,
                    isError = errorMessage != null,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = ghiChu,
                    onValueChange = { ghiChu = it },
                    label = { Text("Ghi chú") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

// Trả về thời gian hiện tại dưới dạng chuỗi định dạng chuẩn
fun getCurrentFormattedTime(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return sdf.format(Date())
}
