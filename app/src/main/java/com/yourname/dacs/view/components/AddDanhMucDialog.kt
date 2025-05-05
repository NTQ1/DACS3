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
import com.google.accompanist.flowlayout.FlowRow
import com.yourname.dacs.model.DanhMuc
import com.yourname.dacs.R

@Composable
fun AddDanhMucDialog(
    onDismiss: () -> Unit,
    onSave: (DanhMuc) -> Unit
) {
    var ten by remember { mutableStateOf("") }
    var selectedIcon by remember { mutableStateOf("ic_cart") }
    var selectedColor by remember { mutableStateOf("#FFF176") }

    val iconList = listOf("ic_cart", "ic_car", "ic_plane", "ic_food", "ic_cake", "ic_ice", "ic_money")
    val colorList = listOf(
        "#FFF176", "#FFB74D", "#E57373", "#F06292", "#BA68C8", "#64B5F6",
        "#81C784", "#FFD54F", "#A1887F", "#90A4AE", "#4DD0E1", "#AED581"
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
                                    MaterialTheme.colorScheme.primary
                                else
                                    LocalContentColor.current,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Màu sắc")
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 8.dp
                ) {
                    colorList.forEach { colorHex ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(android.graphics.Color.parseColor(colorHex)), shape = CircleShape)
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
        },
        confirmButton = {
            Button(
                onClick = {
                    val newDanhMuc = DanhMuc(
                        id = System.currentTimeMillis().toString(),
                        ten = ten,
                        icon = selectedIcon,
                        mauSac = selectedColor,
                        loai = "" // Sẽ gán sau
                    )
                    onSave(newDanhMuc)
                }
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

        else -> R.drawable.ic_default // fallback
    }
}
