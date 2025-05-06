package com.yourname.dacs.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yourname.dacs.model.DanhMuc

@Composable
fun DeleteDanhMucDialog(
    danhMucs: List<DanhMuc>,
    onDismiss: () -> Unit,
    onDelete: (List<DanhMuc>) -> Unit
) {
    val selectedItems = remember { mutableStateListOf<DanhMuc>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chọn danh mục cần xoá") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                danhMucs.forEach { item ->
                    val isSelected = selectedItems.contains(item)
                    val itemColor = try {
                        Color(android.graphics.Color.parseColor(item.mauSac))
                    } catch (e: Exception) {
                        Color.Gray
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .toggleable(
                                value = isSelected,
                                onValueChange = {
                                    if (isSelected) selectedItems.remove(item)
                                    else selectedItems.add(item)
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Khung danh mục có icon và viền màu
                        Card(
                            modifier = Modifier
                                .size(60.dp)
                                .padding(end = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(
                                width = 2.dp,
                                color = itemColor
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = getIconRes(item.icon)),
                                    contentDescription = item.ten,
                                    tint = itemColor,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }

                        Text(
                            text = item.ten,
                            modifier = Modifier.weight(1f)
                        )

                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = null
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onDelete(selectedItems.toList())
                    onDismiss()
                }
            ) {
                Text("Xoá", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Huỷ")
            }
        }
    )
}
