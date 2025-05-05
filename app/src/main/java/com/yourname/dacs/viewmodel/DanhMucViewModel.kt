package com.yourname.dacs.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yourname.dacs.model.DanhMuc
import java.util.UUID

class DanhMucViewModel : ViewModel() {
    val danhMucList = mutableStateListOf<DanhMuc>()
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val userId = auth.currentUser?.uid.orEmpty()
    private val danhMucRef = database.getReference("Danhmuc").child(userId)

    init {
        loadDanhMuc()
    }

    private fun loadDanhMuc() {
        danhMucRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                danhMucList.clear()
                for (child in snapshot.children) {
                    val danhMuc = child.getValue(DanhMuc::class.java)
                    danhMuc?.let {
                        danhMucList.add(it)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // xử lý lỗi nếu cần
            }
        })
    }

    fun addDanhMuc(danhMuc: DanhMuc) {
        val id = UUID.randomUUID().toString()
        val newDanhMuc = danhMuc.copy(id = id, userId = userId)
        danhMucRef.child(id).setValue(newDanhMuc)
        danhMucList.add(newDanhMuc)
    }
}

