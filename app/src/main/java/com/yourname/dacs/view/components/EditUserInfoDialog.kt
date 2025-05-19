package com.yourname.dacs.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.yourname.dacs.model.NguoiDung
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image

import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Add

// Định nghĩa các màu chủ đạo
private val OrangeMain = Color(0xFFFF9800)
private val OrangeDeep = Color(0xFFF57C00)
private val TextColor = Color(0xFF333333)

/**
 * Dialog để chỉnh sửa thông tin người dùng
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserInfoDialog(
    showDialog: Boolean,
    currentUser: NguoiDung?,
    onDismiss: () -> Unit,
    onSave: (NguoiDung) -> Unit
) {
    if (!showDialog) return

    var tenText by remember { mutableStateOf("") }
    var ngaySinhText by remember { mutableStateOf("") }
    var gioiTinhText by remember { mutableStateOf("") }

    // Khởi tạo giá trị ban đầu từ thông tin người dùng hiện tại
    LaunchedEffect(currentUser) {
        currentUser?.let {
            tenText = it.Ten ?: ""
            ngaySinhText = it.NgaySinh ?: ""
            gioiTinhText = it.GioiTinh ?: ""
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
        title = {
            Text(
                text = "Chỉnh sửa thông tin",
                fontWeight = FontWeight.Bold,
                color = OrangeDeep
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Vui lòng cập nhật thông tin của bạn:",
                    color = TextColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Trường họ tên
                EditDialogField(
                    label = "Họ và tên",
                    value = tenText,
                    onValueChange = { tenText = it },
                    icon = Icons.Default.Person
                )

                // Trường ngày sinh
                EditDialogField(
                    label = "Ngày sinh",
                    value = ngaySinhText,
                    onValueChange = { ngaySinhText = it },
                    icon = Icons.Default.Add,
                    keyboardType = KeyboardType.Number,
                    placeholder = "dd/MM/yyyy"
                )

                // Trường giới tính
                EditDialogField(
                    label = "Giới tính",
                    value = gioiTinhText,
                    onValueChange = { gioiTinhText = it },
                    icon = Icons.Default.Face,
                    placeholder = "Nam/Nữ/Khác"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedUser = NguoiDung().apply {
                        Ten = tenText.ifEmpty { null }
                        NgaySinh = ngaySinhText.ifEmpty { null }
                        GioiTinh = gioiTinhText.ifEmpty { null }
                    }
                    onSave(updatedUser)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangeMain
                )
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = TextColor
                )
            ) {
                Text("Hủy")
            }
        }
    )
}

/**
 * Dialog để đổi mật khẩu
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    if (!showDialog) return

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Đổi mật khẩu",
                fontWeight = FontWeight.Bold,
                color = OrangeDeep
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Nhập thông tin để đổi mật khẩu:",
                    color = TextColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Trường mật khẩu hiện tại
                PasswordField(
                    label = "Mật khẩu hiện tại",
                    value = currentPassword,
                    onValueChange = { currentPassword = it }
                )

                // Trường mật khẩu mới
                PasswordField(
                    label = "Mật khẩu mới",
                    value = newPassword,
                    onValueChange = { newPassword = it }
                )

                // Xác nhận mật khẩu mới
                PasswordField(
                    label = "Xác nhận mật khẩu mới",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it }
                )

                if (hasError) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                        hasError = true
                        errorMessage = "Vui lòng nhập đầy đủ thông tin."
                    } else if (newPassword != confirmPassword) {
                        hasError = true
                        errorMessage = "Mật khẩu xác nhận không khớp."
                    } else if (newPassword.length < 6) {
                        hasError = true
                        errorMessage = "Mật khẩu phải có ít nhất 6 ký tự."
                    } else {
                        onSave(currentPassword, newPassword)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = OrangeMain
                )
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = TextColor
                )
            ) {
                Text("Hủy")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditDialogField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    placeholder: String = ""
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = OrangeMain
            )
        },
        placeholder = {
            if (placeholder.isNotEmpty()) {
                Text(placeholder, color = TextColor.copy(alpha = 0.5f))
            }
        },
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = OrangeMain,
            focusedLabelColor = OrangeMain,
            cursorColor = OrangeMain
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = OrangeMain,
            focusedLabelColor = OrangeMain,
            cursorColor = OrangeMain
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
    )
}