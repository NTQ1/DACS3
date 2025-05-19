package com.yourname.dacs.view.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourname.dacs.R
import com.yourname.dacs.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreenUI(
    onNavigateToRegister: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    customModifier: Modifier = Modifier,
    loginViewModel: LoginViewModel = viewModel()
) {
    val OrangeYellow = Color(0xFFFFA726)

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberPassword by remember { mutableStateOf(false) }

    var loginMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Load saved credentials if available
    LaunchedEffect(Unit) {


    }

    Box(
        modifier = customModifier
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
                    .height(240.dp)
                    .padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                    Text(
                        text = "Đăng nhập",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_account),
                                contentDescription = "Account icon"
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mật khẩu") },
                        leadingIcon = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_lock),
                                contentDescription = "Lock icon"
                            )
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val icon = if (passwordVisible)
                                painterResource(id = R.drawable.visibility)
                            else
                                painterResource(id = R.drawable.visibilityoff)

                            Icon(
                                painter = icon,
                                contentDescription = null,
                                modifier = Modifier.clickable {
                                    passwordVisible = !passwordVisible
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Remember Password Checkbox
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberPassword,
                            onCheckedChange = { rememberPassword = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = OrangeYellow,
                                uncheckedColor = Color.Gray
                            )
                        )
                        Text(
                            text = "Ghi nhớ mật khẩu",
                            modifier = Modifier.clickable { rememberPassword = !rememberPassword }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                loginViewModel.login(
                                    email.trim(),
                                    password.trim(),
                                    onResult = { success, message ->
                                        if (success) {
                                            loginMessage = "Đăng nhập thành công!"

                                            onLoginSuccess()
                                        } else {
                                            loginMessage = message ?: "Đăng nhập thất bại!"
                                        }
                                    },
                                    onUserLoaded = { user ->
                                        // Optional: bạn có thể lưu người dùng vào ViewModel/State/Session tại đây nếu cần
                                    }
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeYellow)
                    ) {
                        Text("Đăng nhập", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    loginMessage?.let {
                        Text(
                            text = it,
                            color = if (it.contains("thành công")) Color.Green else Color.Red,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row {
                        Text("Chưa có tài khoản?")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Đăng ký ngay!!",
                            fontWeight = FontWeight.Bold,
                            color = OrangeYellow,
                            modifier = Modifier.clickable { onNavigateToRegister() }
                        )
                    }
                }
            }
        }
    }
}