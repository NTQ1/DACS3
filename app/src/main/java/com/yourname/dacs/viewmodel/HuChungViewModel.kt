package com.yourname.dacs.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.yourname.dacs.model.HuChung
import java.text.SimpleDateFormat
import java.util.*

class HuChungViewModel : ViewModel() {

    private val huChungRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Huchung")
    val huChungList = mutableStateListOf<HuChung>()

    init {
        loadHuChung()
    }

    private fun loadHuChung() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val accountId = currentUser?.uid ?: return

        huChungRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                huChungList.clear()
                for (child in snapshot.children) {
                    val huChung = child.getValue(HuChung::class.java)
                    if (huChung?.accountId == accountId) {
                        huChungList.add(huChung)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors if needed
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
        huChungRef.child(id).setValue(newHuChung)
    }

    fun deleteHuChung(huChung: HuChung) {
        huChungRef.child(huChung.id).removeValue()
    }
}
