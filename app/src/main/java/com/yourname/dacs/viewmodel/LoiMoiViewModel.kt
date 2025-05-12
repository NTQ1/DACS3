package com.yourname.dacs.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.yourname.dacs.model.Loimoi
import com.yourname.dacs.model.LoiMoiWithSender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoiMoiViewModel : ViewModel() {

    private val dbLoiMoi = FirebaseDatabase.getInstance().getReference("LoiMoi")
    private val dbNguoiDung = FirebaseDatabase.getInstance().getReference("Nguoidung")

    private val _loiMoiWithSenderList = MutableStateFlow<List<LoiMoiWithSender>>(emptyList())
    val loiMoiWithSenderList: StateFlow<List<LoiMoiWithSender>> = _loiMoiWithSenderList

    // Lắng nghe lời mời đến cho user hiện tại và lấy tên người gửi
    fun listenLoiMoi(userId: String) {
        dbLoiMoi.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<LoiMoiWithSender>()

                val loiMoiList = snapshot.children.mapNotNull { it.getValue(Loimoi::class.java) }
                    .filter { it.idNguoiNhan == userId && it.trangThai == "cho" }

                if (loiMoiList.isEmpty()) {
                    _loiMoiWithSenderList.value = emptyList()
                    return
                }

                var completed = 0
                for (loiMoi in loiMoiList) {
                    dbNguoiDung.child(loiMoi.idNguoiGui).child("ten")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(senderSnapshot: DataSnapshot) {
                                val senderName = senderSnapshot.getValue(String::class.java) ?: "Không rõ"
                                tempList.add(LoiMoiWithSender(loiMoi, senderName))
                                completed++
                                if (completed == loiMoiList.size) {
                                    _loiMoiWithSenderList.value = tempList
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                completed++
                                if (completed == loiMoiList.size) {
                                    _loiMoiWithSenderList.value = tempList
                                }
                            }
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Chấp nhận lời mời => thêm vào bảng Thành viên và xóa lời mời
    fun chapNhanLoiMoi(userId: String, huChungId: String) {
        dbLoiMoi.orderByChild("idNguoiNhan").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        val loiMoi = child.getValue(Loimoi::class.java)
                        if (loiMoi != null && loiMoi.idHuChung == huChungId) {

                            // Thêm vào bảng Thanhvien
                            val thanhVienRef = FirebaseDatabase.getInstance().getReference("Thanhvien").push()
                            val thanhVienData = mapOf(
                                "idNguoiDung" to userId,
                                "idHu" to huChungId
                            )

                            thanhVienRef.setValue(thanhVienData).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Xóa lời mời sau khi thêm thành công
                                    child.ref.removeValue()
                                }
                            }

                            break
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }


    // Từ chối lời mời => chỉ xóa lời mời
    fun tuChoiLoiMoi(userId: String, huChungId: String) {
        dbLoiMoi.orderByChild("idNguoiNhan").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        val loiMoi = child.getValue(Loimoi::class.java)
                        if (loiMoi != null && loiMoi.idHuChung == huChungId) {
                            child.ref.removeValue()
                            break
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    // Gọi từ UI để chấp nhận
    fun acceptLoiMoi(loiMoi: Loimoi) {
        chapNhanLoiMoi(loiMoi.idNguoiNhan, loiMoi.idHuChung)
    }

    // Gọi từ UI để từ chối
    fun declineLoiMoi(loiMoi: Loimoi) {
        tuChoiLoiMoi(loiMoi.idNguoiNhan, loiMoi.idHuChung)
    }
}
