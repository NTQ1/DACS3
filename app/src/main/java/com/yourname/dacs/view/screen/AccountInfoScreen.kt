package com.yourname.dacs.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yourname.dacs.model.NguoiDung
import com.yourname.dacs.view.component.EditUserInfoDialog
import com.yourname.dacs.view.component.ChangePasswordDialog
import com.yourname.dacs.viewmodel.UserInfoViewModel

// Định nghĩa các màu chủ đạo
private val OrangeMain = Color(0xFFFF9800)
private val OrangeDeep = Color(0xFFF57C00)
private val BackgroundColor = Color(0xFFFFFBF5)
private val TextColor = Color(0xFF333333)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountInfoScreen(
    navController: NavHostController,
    userInfoViewModel: UserInfoViewModel
) {
    // Collect states from ViewModel
    val nguoiDung by userInfoViewModel.nguoiDung.collectAsState()
    val isLoading by userInfoViewModel.isLoading.collectAsState()
    val error by userInfoViewModel.error.collectAsState()
    val currentUser = userInfoViewModel.currentUser.collectAsState().value
    val updateResult by userInfoViewModel.updateResult.collectAsState()

    // State for dialogs
    var showEditDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showOptionsDialog by remember { mutableStateOf(false) }

    // Load data when screen is launched
    LaunchedEffect(Unit) {
        userInfoViewModel.loadUserData()
    }

    // Show snackbar on update result
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(updateResult) {
        updateResult?.let {
            when (it) {
                is UserInfoViewModel.UpdateResult.Success -> {
                    snackbarHostState.showSnackbar(it.message)
                    userInfoViewModel.clearUpdateResult()
                }
                is UserInfoViewModel.UpdateResult.Error -> {
                    snackbarHostState.showSnackbar(it.message)
                    userInfoViewModel.clearUpdateResult()
                }
            }
        }
    }

    // Edit user info dialog
    if (showEditDialog) {
        EditUserInfoDialog(
            showDialog = true,
            currentUser = nguoiDung,
            onDismiss = { showEditDialog = false },
            onSave = { updatedUser ->
                userInfoViewModel.updateUserInfo(updatedUser)
                showEditDialog = false
            }
        )
    }

    // Change password dialog
    if (showPasswordDialog) {
        ChangePasswordDialog(
            showDialog = true,
            onDismiss = { showPasswordDialog = false },
            onSave = { currentPassword, newPassword ->
                userInfoViewModel.updatePassword(currentPassword, newPassword)
                showPasswordDialog = false
            }
        )
    }

    // Options dialog
    if (showOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showOptionsDialog = false },
            title = { Text("Chọn tác vụ") },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showOptionsDialog = false }) {
                    Text("Đóng")
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            showEditDialog = true
                            showOptionsDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeMain),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Chỉnh sửa thông tin cá nhân")
                    }

                    Button(
                        onClick = {
                            showPasswordDialog = true
                            showOptionsDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeDeep),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Đổi mật khẩu")
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông tin tài khoản", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = OrangeMain
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showOptionsDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Chỉnh sửa",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        containerColor = BackgroundColor,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = OrangeMain)
                    Text(
                        "Đang tải...",
                        modifier = Modifier.padding(top = 16.dp),
                        color = OrangeDeep
                    )
                }
            }

            error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Lỗi: $error",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Button(
                        onClick = { userInfoViewModel.loadUserData() },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeMain),
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Thử lại")
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header with avatar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(OrangeLight)
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Avatar with first letter
                            val firstLetter = nguoiDung?.Ten?.firstOrNull()?.uppercase() ?: "?"

                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(OrangeMain),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = firstLetter,
                                    color = Color.White,
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Display user name
                            nguoiDung?.Ten?.let {
                                Text(
                                    text = it,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextColor
                                )
                            }

                            // Display email
                            currentUser?.email?.let {
                                Text(
                                    text = it,
                                    fontSize = 16.sp,
                                    color = TextColor.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // User information cards
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Personal information card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Thông tin cá nhân",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = OrangeDeep
                                    )

                                    IconButton(
                                        onClick = { showEditDialog = true },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Chỉnh sửa thông tin cá nhân",
                                            tint = OrangeMain
                                        )
                                    }
                                }

                                Divider(color = OrangeLight, thickness = 1.dp)

                                InfoRow("Họ và tên", nguoiDung?.Ten ?: "Chưa cập nhật")
                                InfoRow("Ngày sinh", nguoiDung?.NgaySinh ?: "Chưa cập nhật")
                                InfoRow("Giới tính", nguoiDung?.GioiTinh ?: "Chưa cập nhật")
                            }
                        }

                        // Account information card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Thông tin tài khoản",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = OrangeDeep
                                    )

                                    IconButton(
                                        onClick = { showPasswordDialog = true },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Đổi mật khẩu",
                                            tint = OrangeMain
                                        )
                                    }
                                }

                                Divider(color = OrangeLight, thickness = 1.dp)

                                InfoRow("Email", currentUser?.email ?: "Chưa cập nhật")
                                InfoRow("Trạng thái", if (currentUser != null) "Đã xác minh" else "Chưa xác minh")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = TextColor.copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )

        Text(
            text = value,
            color = TextColor,
            fontWeight = FontWeight.SemiBold
        )
    }
}