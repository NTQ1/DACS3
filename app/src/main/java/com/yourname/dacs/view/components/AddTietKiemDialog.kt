package com.yourname.dacs.view.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourname.dacs.viewmodel.TietKiemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTietKiemDialog(
    onDismiss: () -> Unit,
    idHu: String,
    viewModel: TietKiemViewModel = viewModel()
) {
    var soTien by remember { mutableStateOf("") }
    var ghiChu by remember { mutableStateOf("") }
    var loai by remember { mutableStateOf("Chi") }

    // Collect UI state
    val uiState by viewModel.uiState.collectAsState()

    // Effect to handle UI state changes
    LaunchedEffect(uiState) {
        when (uiState) {
            is com.yourname.dacs.viewmodel.TietKiemUiState.Success -> {
                // Reset form and close dialog on success
                onDismiss()
                viewModel.resetState()
            }
            else -> {} // Handle other states as needed
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Thêm Giao Dịch",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Số tiền field with currency marker
                OutlinedTextField(
                    value = soTien,
                    onValueChange = { soTien = it },
                    label = { Text("Số tiền") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    trailingIcon = {
                        Text(
                            text = "VNĐ",
                            color = Color.Gray,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFFFF9800),
                        focusedLabelColor = Color(0xFFFF9800),
                        cursorColor = Color(0xFFFF9800)
                    )
                )

                // Improved Transaction Type Selector
                Text(
                    text = "Loại giao dịch",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF666666),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Chi option (expense)
                    val chiBackground by animateColorAsState(
                        targetValue = if (loai == "Chi") Color(0xFFFFECE0) else Color(0xFFF5F5F5)
                    )
                    val chiBorderColor by animateColorAsState(
                        targetValue = if (loai == "Chi") Color(0xFFFF9800) else Color(0xFFE0E0E0)
                    )
                    val chiTextColor by animateColorAsState(
                        targetValue = if (loai == "Chi") Color(0xFFFF5722) else Color(0xFF757575)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(chiBackground)
                            .border(1.dp, chiBorderColor, RoundedCornerShape(8.dp))
                            .clickable { loai = "Chi" }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = chiTextColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Chi tiêu",
                                color = chiTextColor,
                                fontWeight = if (loai == "Chi") FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    // Thu option (income)
                    val thuBackground by animateColorAsState(
                        targetValue = if (loai == "Thu") Color(0xFFE3F2FD) else Color(0xFFF5F5F5)
                    )
                    val thuBorderColor by animateColorAsState(
                        targetValue = if (loai == "Thu") Color(0xFF2196F3) else Color(0xFFE0E0E0)
                    )
                    val thuTextColor by animateColorAsState(
                        targetValue = if (loai == "Thu") Color(0xFF2196F3) else Color(0xFF757575)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(thuBackground)
                            .border(1.dp, thuBorderColor, RoundedCornerShape(8.dp))
                            .clickable { loai = "Thu" }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AddCircle,
                                contentDescription = null,
                                tint = thuTextColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Thu nhập",
                                color = thuTextColor,
                                fontWeight = if (loai == "Thu") FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                // Ghi chú field
                OutlinedTextField(
                    value = ghiChu,
                    onValueChange = { ghiChu = it },
                    label = { Text("Ghi chú") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFFFF9800),
                        focusedLabelColor = Color(0xFFFF9800),
                        cursorColor = Color(0xFFFF9800)
                    )
                )

                // Show error message if any
                if (uiState is com.yourname.dacs.viewmodel.TietKiemUiState.Error) {
                    Text(
                        text = (uiState as com.yourname.dacs.viewmodel.TietKiemUiState.Error).message,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (soTien.isNotBlank()) {
                        viewModel.saveTietKiem(soTien, loai, ghiChu, idHu)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(48.dp),
                enabled = soTien.isNotBlank() && uiState !is com.yourname.dacs.viewmodel.TietKiemUiState.Loading
            ) {
                if (uiState is com.yourname.dacs.viewmodel.TietKiemUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Lưu giao dịch",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Gray
                ),
                border = BorderStroke(1.dp, Color.LightGray),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(48.dp),
                enabled = uiState !is com.yourname.dacs.viewmodel.TietKiemUiState.Loading
            ) {
                Text("Hủy")
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White,
        modifier = Modifier.padding(16.dp)
    )
}