package com.yourname.dacs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yourname.dacs.ui.theme.DACSTheme
import com.yourname.dacs.view.screen.AccountInfoScreen
import com.yourname.dacs.view.screen.BudgetTrackerScreen
import com.yourname.dacs.view.screen.ChiTietHuChungScreen
import com.yourname.dacs.view.screen.HistoryScreen
import com.yourname.dacs.view.screen.HomeScreen
import com.yourname.dacs.view.screen.HuongDanSuDungScreen
import com.yourname.dacs.view.screen.LoginScreenUI
import com.yourname.dacs.view.screen.LoiMoiScreen
import com.yourname.dacs.view.screen.RegisterScreen
import com.yourname.dacs.view.screen.SettingsScreen
import com.yourname.dacs.view.screen.TogetherScreen
import com.yourname.dacs.viewmodel.UserInfoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DACSTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    val userInfoViewModel: UserInfoViewModel = viewModel()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreenUI(
                    onNavigateToRegister = {
                        navController.navigate("register")
                    },

                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate("login") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    onBackToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            composable("home") {
                HomeScreen(navController)
            }
            composable("savemoney") {
                TogetherScreen(navController)
            }
            composable("loimoi") { LoiMoiScreen(navController) }
            composable("history") { HistoryScreen(navController) }
            composable("diagram") { BudgetTrackerScreen(navController) }
            composable("setting") { SettingsScreen(navController) }
            composable("hotro") { HuongDanSuDungScreen(navController) }

            composable("thongtin") {
                // Pass the instance of UserInfoViewModel to AccountInfoScreen
                AccountInfoScreen(navController, userInfoViewModel)
            }

            composable("chitiet/{huChungId}") { backStackEntry ->
                val huChungId = backStackEntry.arguments?.getString("huChungId") ?: ""
                ChiTietHuChungScreen(huChungId = huChungId, navController = navController)
            }
        }
    }
}