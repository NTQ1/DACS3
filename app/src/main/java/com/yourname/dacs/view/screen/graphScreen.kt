package com.yourname.dacs.view.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.yourname.dacs.R
import com.yourname.dacs.view.components.MonthlyBudgetComponent
import com.yourname.dacs.view.components.YearlyBudgetComponent



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BudgetTrackerScreen(
    navController: NavHostController,
) {
    var viewMode by remember { mutableStateOf("month") } // "month" or "year"

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 56.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Toggle buttons
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFF0F0F0))
                ) {
                    Box(
                        modifier = Modifier
                            .clickable {
                                viewMode = "month"
                            }
                            .background(if (viewMode == "month") Color(0xFFFF9500) else Color(0xFFF0F0F0))
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Hàng Tháng",
                            color = if (viewMode == "month") Color.White else Color.Black
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clickable {
                                viewMode = "year"
                            }
                            .background(if (viewMode == "year") Color(0xFFFF9500) else Color(0xFFF0F0F0))
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Hàng Năm",
                            color = if (viewMode == "year") Color.White else Color.Black
                        )
                    }
                }
            }


            // Based on view mode, display the appropriate component
            if (viewMode == "month") {
                MonthlyBudgetComponent()
            } else {
                YearlyBudgetComponent()
            }
        }
    }
}