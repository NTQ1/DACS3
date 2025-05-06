package com.yourname.dacs.view.screen

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourname.dacs.R
import com.yourname.dacs.viewmodel.RegisterViewModel
import java.util.*

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val OrangeYellow = Color(0xFFFFA726)

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var email by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Nam") }
    var birthDate by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, day: Int ->
            birthDate = "$day/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val registerViewModel: RegisterViewModel = viewModel()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(32.dp))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .height(200.dp)
                    .padding(bottom = 16.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Đăng ký tài khoản", fontSize = 24.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { Icon(painterResource(id = R.drawable.ic_mail), null) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Họ và tên") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { Icon(painterResource(id = R.drawable.ic_account), null) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mật khẩu") },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { Icon(painterResource(id = R.drawable.ic_lock), null) },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = if (passwordVisible) R.drawable.visibility else R.drawable.visibilityoff),
                                contentDescription = null,
                                modifier = Modifier.clickable { passwordVisible = !passwordVisible }
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Nhập lại mật khẩu") },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { Icon(painterResource(id = R.drawable.ic_lock), null) },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(id = if (confirmPasswordVisible) R.drawable.visibility else R.drawable.visibilityoff),
                                contentDescription = null,
                                modifier = Modifier.clickable { confirmPasswordVisible = !confirmPasswordVisible }
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = birthDate,
                        onValueChange = {},
                        label = { Text("Ngày sinh") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datePickerDialog.show() },
                        readOnly = true,
                        enabled = false,
                        leadingIcon = { Icon(painter = painterResource(id = R.drawable.ic_calendar_today), contentDescription = null) },
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Giới tính", fontWeight = FontWeight.Medium)
                    Row {
                        listOf("Nam", "Nữ", "Khác").forEach { option ->
                            Row(
                                modifier = Modifier
                                    .selectable(
                                        selected = (gender == option),
                                        onClick = { gender = option },
                                        role = Role.RadioButton
                                    )
                                    .padding(end = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = (gender == option), onClick = null)
                                Text(option)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            registerViewModel.registerUser(
                                context = context,
                                email = email,
                                fullName = fullName,
                                password = password,
                                confirmPassword = confirmPassword,
                                gender = gender,
                                birthDate = birthDate,
                                onSuccess = onRegisterSuccess,
                                onFailure = { msg ->
                                    Toast.makeText(context, "Lỗi: $msg", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeYellow)
                    ) {
                        Text("Đăng ký", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Đã có tài khoản? Quay lại",
                        color = OrangeYellow,
                        modifier = Modifier.clickable { onBackToLogin() }
                    )
                }
            }
        }
    }
}
