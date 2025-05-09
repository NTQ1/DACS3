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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yourname.dacs.ui.theme.DACSTheme
import com.yourname.dacs.view.screen.HomeScreen
import com.yourname.dacs.view.screen.LoginScreenUI
import com.yourname.dacs.view.screen.RegisterScreen
import com.yourname.dacs.view.screen.TogetherScreen

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
                    onNavigateToForgotPassword = {
                        // TODO: Thêm màn hình quên mật khẩu
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


        }
    }
}

