package com.yourname.dacs.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.yourname.dacs.model.NguoiDung

class RegisterViewModel : ViewModel() {

    fun registerUser(
        context: Context,
        email: String,
        fullName: String,
        password: String,
        confirmPassword: String,
        gender: String,
        birthDate: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (password != confirmPassword) {
            Toast.makeText(context, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show()
            return
        }

        val database = FirebaseDatabase.getInstance()
        val ref = database.getReference("Nguoidung")
        val userId = generateRandomId(8)

        val nguoiDung = NguoiDung(
            Id = userId,
            Email = email,
            MatKhau = password,
            Ten = fullName,
            GioiTinh = gender,
            NgaySinh = birthDate
        )

        ref.child(userId).setValue(nguoiDung)
            .addOnSuccessListener {
                Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it.message ?: "Lỗi không xác định")
            }
    }

    private fun generateRandomId(length: Int = 8): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length).map { chars.random() }.joinToString("")
    }
}
