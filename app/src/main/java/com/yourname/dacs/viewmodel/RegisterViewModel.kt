package com.yourname.dacs.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.yourname.dacs.model.NguoiDung

class RegisterViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

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
        if (email.isBlank() || fullName.isBlank() || password.isBlank() || confirmPassword.isBlank() || birthDate.isBlank()) {
            Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        if (password != confirmPassword) {
            Toast.makeText(context, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener

                    // 👉 Dùng UID làm Id luôn
                    val nguoiDung = NguoiDung(
                        Id = userId,
                        Email = email,
                        MatKhau = "", // không lưu mật khẩu rõ ràng
                        Ten = fullName,
                        GioiTinh = gender,
                        NgaySinh = birthDate
                    )

                    val ref = FirebaseDatabase.getInstance().getReference("Nguoidung")
                    ref.child(userId).setValue(nguoiDung)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                            onSuccess()
                        }
                        .addOnFailureListener {
                            onFailure(it.message ?: "Lỗi khi lưu thông tin người dùng")
                        }
                } else {
                    onFailure(task.exception?.message ?: "Lỗi khi đăng ký tài khoản")
                }
            }
    }
}
