package com.yourname.dacs.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.yourname.dacs.model.GiaoDich

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

        val giaoDichWithId = giaoDich.copy(id = id, accountId = accountId)

        db.child(id).setValue(giaoDichWithId)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}
