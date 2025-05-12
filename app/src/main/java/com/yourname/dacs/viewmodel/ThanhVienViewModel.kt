package com.yourname.dacs.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.yourname.dacs.model.Loimoi
import com.yourname.dacs.model.NguoiDung
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThanhVienViewModel : ViewModel() {

    private val dbNguoiDung = FirebaseDatabase.getInstance().getReference("Nguoidung")
    private val dbThanhVien = FirebaseDatabase.getInstance().getReference("Thanhvien")
    private val dbLoiMoi = FirebaseDatabase.getInstance().getReference("LoiMoi")

    private val _searchResult = MutableStateFlow<NguoiDung?>(null)
    val searchResult: StateFlow<NguoiDung?> = _searchResult

    private val _thanhVienNguoiDung = MutableStateFlow<List<NguoiDung>>(emptyList())
    val thanhVienNguoiDung: StateFlow<List<NguoiDung>> = _thanhVienNguoiDung

    private val _guiLoiMoiThanhCong = MutableStateFlow<Boolean?>(null)
    val guiLoiMoiThanhCong: StateFlow<Boolean?> = _guiLoiMoiThanhCong




    // ====== Tìm kiếm người dùng theo ID hoặc Email ======
    fun searchNguoiDung(query: String) {
        dbNguoiDung.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var foundUser: NguoiDung? = null
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(NguoiDung::class.java)
                    if (user != null && (user.Id == query || user.Email == query)) {
                        foundUser = user
                        break
                    }
                }
                _searchResult.value = foundUser
            }

            override fun onCancelled(error: DatabaseError) {
                _searchResult.value = null
            }
        })
    }

    fun guiLoiMoi(idNguoiNhan: String, idHuChung: String, tenHuChung: String) {
        val idNguoiGui = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val key = dbLoiMoi.push().key ?: return

        val loiMoi = Loimoi(
            idLoiMoi = key,
            idNguoiGui = idNguoiGui,
            idNguoiNhan = idNguoiNhan,
            idHuChung = idHuChung,
            trangThai = "cho",
            tenHu = tenHuChung // ← thêm tên hũ
        )

        dbLoiMoi.child(key).setValue(loiMoi).addOnCompleteListener { task ->
            _guiLoiMoiThanhCong.value = task.isSuccessful
        }
    }





    // ====== Tải danh sách thành viên của 1 hũ ======
    fun taiDanhSachThanhVien(idHu: String) {
        dbThanhVien.orderByChild("idHu").equalTo(idHu)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userIds = snapshot.children.mapNotNull {
                        it.child("idNguoiDung").getValue(String::class.java)
                    }

                    if (userIds.isEmpty()) {
                        _thanhVienNguoiDung.value = emptyList() // Cập nhật StateFlow
                        return
                    }

                    val thanhVienList = mutableListOf<NguoiDung>()
                    var dem = 0

                    for (userId in userIds) {
                        dbNguoiDung.child(userId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(userSnapshot: DataSnapshot) {
                                    val nguoiDung = userSnapshot.getValue(NguoiDung::class.java)
                                    nguoiDung?.let {
                                        thanhVienList.add(it)
                                    }
                                    dem++
                                    if (dem == userIds.size) {
                                        _thanhVienNguoiDung.value = thanhVienList // Cập nhật StateFlow
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("ThanhVienVM", "Lỗi tải NguoiDung: ${error.message}")
                                    dem++
                                    if (dem == userIds.size) {
                                        _thanhVienNguoiDung.value = thanhVienList // Cập nhật StateFlow
                                    }
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ThanhVienVM", "Lỗi tải Thanhvien: ${error.message}")
                }
            })
    }
}
