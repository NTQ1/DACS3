package com.yourname.dacs.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.yourname.dacs.model.NguoiDung

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("Nguoidung")

    fun login(
        email: String,
        password: String,
        onResult: (Boolean, String?) -> Unit,
        onUserLoaded: (NguoiDung?) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        database.child(userId).get()
                            .addOnSuccessListener { snapshot ->
                                val user = snapshot.getValue(NguoiDung::class.java)
                                onUserLoaded(user)
                                onResult(true, null)
                            }
                            .addOnFailureListener { error ->
                                onResult(false, error.message)
                            }
                    } else {
                        onResult(false, "Không tìm thấy ID người dùng.")
                    }
                } else {
                    onResult(false, task.exception?.message)
                }
            }
    }
}
