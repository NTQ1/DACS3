package com.yourname.dacs.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yourname.dacs.model.DanhMuc
import com.yourname.dacs.R
import com.google.accompanist.flowlayout.FlowRow

@Composable
fun AddDanhMucDialog(
    onDismiss: () -> Unit,
    onSave: (DanhMuc) -> Unit
) {
    var ten by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("ic_cart") }
    var selectedColor by remember { mutableStateOf("#FFF176") }

    val iconList = listOf(
        "ic_cart", "ic_car", "ic_plane", "ic_food", "ic_cake", "ic_ice", "ic_money",
        "ic_bank", "ic_electricity", "ic_game", "ic_gift", "ic_gym", "ic_water","ic_savemoney"
    )

    val colorList = listOf(
        "#FFF176", "#FFB74D", "#E57373", "#F06292", "#BA68C8", "#64B5F6",
        "#81C784", "#FFD54F", "#A1887F", "#90A4AE", "#4DD0E1", "#AED581",
        "#FF8A65", "#9575CD", "#4DB6AC", "#7986CB", "#DCE775", "#FFF59D",
        "#FFCC80", "#E1BEE7", "#B39DDB", "#80CBC4", "#C5E1A5", "#F48FB1",
        "#CE93D8", "#B0BEC5", "#D7CCC8", "#A5D6A7", "#EF9A9A", "#FF7043"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tạo mới danh mục") },
        text = {
            Column {
                OutlinedTextField(
                    value = ten,
                    onValueChange = { ten = it },
                    label = { Text("Tên danh mục") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Biểu tượng")
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp
                ) {
                    iconList.forEach { iconName ->
                        IconButton(onClick = { selectedIcon = iconName }) {
                            Icon(
                                painter = painterResource(id = getIconRes(iconName)),
                                contentDescription = iconName,
                                tint = if (selectedIcon == iconName)
                                    Color(0xFFFFA726)
                                else
                                    LocalContentColor.current,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Màu sắc")
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colorList.chunked(10).forEach { rowColors ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowColors.forEach { colorHex ->
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(
                                            color = Color(android.graphics.Color.parseColor(colorHex)),
                                            shape = CircleShape
                                        )
                                        .border(
                                            width = if (selectedColor == colorHex) 3.dp else 1.dp,
                                            color = if (selectedColor == colorHex) Color.Black else Color.Gray,
                                            shape = CircleShape
                                        )
                                        .clickable { selectedColor = colorHex }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newDanhMuc = DanhMuc(
                        id = System.currentTimeMillis().toString(),
                        ten = ten,
                        icon = selectedIcon,
                        mauSac = selectedColor,
                        loai = "" // Gán sau nếu cần
                    )
                    onSave(newDanhMuc)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726))
            ) {
                Text("Lưu")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

fun getIconRes(name: String): Int {
    return when (name) {
        "ic_cart" -> R.drawable.ic_cart
        "ic_car" -> R.drawable.ic_car
        "ic_plane" -> R.drawable.ic_plane
        "ic_food" -> R.drawable.ic_food
        "ic_cake" -> R.drawable.ic_cake
        "ic_ice" -> R.drawable.ic_ice
        "ic_money" -> R.drawable.ic_money
        "ic_bank" -> R.drawable.ic_bank
        "ic_electricity" -> R.drawable.ic_electricity
        "ic_game" -> R.drawable.ic_game
        "ic_gift" -> R.drawable.ic_gift
        "ic_gym" -> R.drawable.ic_gym
        "ic_water" -> R.drawable.ic_water
        "ic_savemoney" -> R.drawable.ic_savemoney
        else -> R.drawable.ic_default
    }
}
