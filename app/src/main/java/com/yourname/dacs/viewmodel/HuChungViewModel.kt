package com.yourname.dacs.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.yourname.dacs.model.HuChung
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

class HuChungViewModel : ViewModel() {

    private val huChungRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Huchung")
    val huChungList = mutableStateListOf<HuChung>()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadHuChung()
    }

    private fun loadHuChung() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: return

        _isLoading.value = true
        Log.d("HuChungVM", "Bắt đầu tải danh sách hũ chung cho user: $userId")

        // Xóa danh sách cũ
        huChungList.clear()

        val thanhVienRef = FirebaseDatabase.getInstance().getReference("Thanhvien")

        thanhVienRef.orderByChild("idNguoiDung").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("HuChungVM", "Đã tìm thấy ${snapshot.childrenCount} bản ghi Thanhvien")

                    val huIds = mutableListOf<String>()
                    for (child in snapshot.children) {
                        val idHu = child.child("idHu").getValue(String::class.java)
                        if (idHu != null) {
                            huIds.add(idHu)
                            Log.d("HuChungVM", "Thêm idHu: $idHu vào danh sách")
                        }
                    }

                    // Tải tất cả hũ chung
                    huChungRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(huSnapshot: DataSnapshot) {
                            Log.d("HuChungVM", "Đã tìm thấy ${huSnapshot.childrenCount} bản ghi Huchung")

                            // Xóa danh sách cũ trước khi thêm mới
                            huChungList.clear()

                            for (child in huSnapshot.children) {
                                val huChung = child.getValue(HuChung::class.java)

                                if (huChung != null) {
                                    Log.d("HuChungVM", "Đang kiểm tra hũ: ${huChung.id}, accountId: ${huChung.accountId}")

                                    if (huChung.accountId == userId || huIds.contains(huChung.id)) {
                                        huChungList.add(huChung)
                                        Log.d("HuChungVM", "Thêm hũ ${huChung.ten} vào danh sách")
                                    }
                                }
                            }

                            Log.d("HuChungVM", "Tổng số hũ sau khi lọc: ${huChungList.size}")
                            _isLoading.value = false
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("HuChungVM", "Lỗi khi tải hũ chung: ${error.message}")
                            _isLoading.value = false
                        }
                    })
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HuChungVM", "Lỗi khi tải thành viên: ${error.message}")
                    _isLoading.value = false
                }
            })
    }

    fun addHuChung(huChung: HuChung) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val accountId = currentUser?.uid ?: return
        val id = huChungRef.push().key ?: return

        // Get current time as string
        val ngayTao = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val newHuChung = huChung.copy(
            id = id,
            accountId = accountId,
            ngayTao = ngayTao
        )

        Log.d("HuChungVM", "Thêm hũ mới: ${newHuChung.ten}")
        huChungRef.child(id).setValue(newHuChung)
            .addOnSuccessListener {
                Log.d("HuChungVM", "Đã thêm hũ thành công: $id")
            }
            .addOnFailureListener { e ->
                Log.e("HuChungVM", "Lỗi khi thêm hũ: ${e.message}")
            }
    }

    fun deleteHuChung(huChung: HuChung) {
        Log.d("HuChungVM", "Xóa hũ: ${huChung.id} - ${huChung.ten}")
        huChungRef.child(huChung.id).removeValue()
            .addOnSuccessListener {
                Log.d("HuChungVM", "Đã xóa hũ thành công: ${huChung.id}")
            }
            .addOnFailureListener { e ->
                Log.e("HuChungVM", "Lỗi khi xóa hũ: ${e.message}")
            }
    }
}
