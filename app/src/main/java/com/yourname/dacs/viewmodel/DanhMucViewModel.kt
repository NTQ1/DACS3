package com.yourname.dacs.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.yourname.dacs.model.DanhMuc

class DanhMucViewModel : ViewModel() {

    val danhMucList = mutableStateListOf<DanhMuc>()
    private val danhMucRef = FirebaseDatabase.getInstance().getReference("Danhmuc")

    init {
        loadDanhMuc()
    }

    private fun loadDanhMuc() {
        danhMucRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                danhMucList.clear()
                for (child in snapshot.children) {
                    val danhMuc = child.getValue(DanhMuc::class.java)
                    danhMuc?.let { danhMucList.add(it) }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Có thể log lỗi tại đây nếu cần
            }
        })
    }

    fun addDanhMuc(danhMuc: DanhMuc) {
        val id = generateShortId()
        val newDanhMuc = danhMuc.copy(id = id)
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
