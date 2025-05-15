package com.yourname.dacs.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

        val thoiGian = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        // 🔍 Lấy thông tin Danhmuc
        val danhMucRef = FirebaseDatabase.getInstance().getReference("Danhmuc").child(giaoDich.danhMucId)

        danhMucRef.get().addOnSuccessListener { snapshot ->
            val ten = snapshot.child("ten").getValue(String::class.java) ?: ""
            val icon = snapshot.child("icon").getValue(String::class.java) ?: ""
            val mauSac = snapshot.child("mauSac").getValue(String::class.java) ?: ""

            // 👇 Tạo GiaoDich đầy đủ
            val giaoDichWithInfo = giaoDich.copy(
                id = id,
                accountId = accountId,
                thoiGian = thoiGian,
                tenDanhMuc = ten,
                iconDanhMuc = icon,
                mauSacDanhMuc = mauSac
            )

            db.child(id).setValue(giaoDichWithInfo)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure(it) }

        }.addOnFailureListener {
            onFailure(it)
        }
    }

    // 👉 Hàm mới để lấy danh sách giao dịch của người dùng hiện tại
    fun layDanhSachGiaoDich(
        onDataReceived: (List<GiaoDich>) -> Unit,
        onFailure: (Exception) -> Unit = {}
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val accountId = currentUser?.uid

        if (accountId == null) {
            onFailure(Exception("Không xác định được người dùng."))
            return
        }

        db.orderByChild("accountId").equalTo(accountId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val danhSach = mutableListOf<GiaoDich>()
                    for (child in snapshot.children) {
                        val giaoDich = child.getValue(GiaoDich::class.java)
                        giaoDich?.let { danhSach.add(it) }
                    }
                    onDataReceived(danhSach)
                }

                override fun onCancelled(error: DatabaseError) {
                    onFailure(Exception(error.message))
                }
            })
    }

}
