package com.yourname.dacs.repository

import com.google.firebase.database.*
import com.yourname.dacs.model.NguoiDung

class UserRepository {
    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Nguoidung")

    fun login(email: String, password: String, onResult: (NguoiDung?) -> Unit) {
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(NguoiDung::class.java)
                    if (user?.Email == email && user.MatKhau == password) {
                        onResult(user)
                        return
                    }
                }
                onResult(null) // Không tìm thấy
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(null) // Có lỗi
            }
        })
    }
}
