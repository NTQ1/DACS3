package com.yourname.dacs.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.yourname.dacs.model.DanhMuc
import com.yourname.dacs.model.GiaoDich

class DanhMucViewModel : ViewModel() {

    val danhMucList = mutableStateListOf<DanhMuc>()
    private val danhMucRef = FirebaseDatabase.getInstance().getReference("Danhmuc")

    init {
        loadDanhMuc()
    }

    private fun loadDanhMuc() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val accountId = currentUser?.uid ?: return

        danhMucRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                danhMucList.clear()
                for (child in snapshot.children) {
                    val danhMuc = child.getValue(DanhMuc::class.java)
                    if (danhMuc?.accountId == accountId) {
                        danhMucList.add(danhMuc)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Log nếu cần
            }
        })
    }


    fun addDanhMuc(danhMuc: DanhMuc) {
        val id = generateShortId()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val accountId = currentUser?.uid ?: return
        val newDanhMuc = danhMuc.copy(id = id, accountId = accountId)
        danhMucRef.child(id).setValue(newDanhMuc)
    }


    fun deleteDanhMuc(danhMuc: DanhMuc) {
        danhMucRef.child(danhMuc.id).removeValue()
    }

    private fun generateShortId(length: Int = 8): String {
        val allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }


}
