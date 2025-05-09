package com.yourname.dacs.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.yourname.dacs.model.GiaoDich
import java.text.SimpleDateFormat
import java.util.*

class GiaoDichViewModel : ViewModel() {

    private val db = FirebaseDatabase.getInstance().getReference("giaodich")

    fun themGiaoDich(
        giaoDich: GiaoDich,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val id = db.push().key
        val currentUser = FirebaseAuth.getInstance().currentUser
        val accountId = currentUser?.uid

        if (id == null || accountId == null) {
            onFailure(Exception("Không thể tạo giao dịch."))
            return
        }

        // 🕒 Thêm ngày tạo dạng chuẩn
        val thoiGian = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        // 🟢 Copy lại và thêm id + accountId + ngayTao
        val giaoDichWithInfo = giaoDich.copy(
            id = id,
            accountId = accountId,
            thoiGian = thoiGian
        )

        db.child(id).setValue(giaoDichWithInfo)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}
