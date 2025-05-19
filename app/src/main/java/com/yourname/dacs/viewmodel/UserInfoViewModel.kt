package com.yourname.dacs.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yourname.dacs.model.NguoiDung
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserInfoViewModel : ViewModel() {
    // Firebase instances
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    // Current Firebase user
    private val _currentUser = MutableStateFlow<FirebaseUser?>(firebaseAuth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    // App-specific user model
    private val _nguoiDung = MutableStateFlow<NguoiDung?>(null)
    val nguoiDung: StateFlow<NguoiDung?> = _nguoiDung.asStateFlow()

    // UI State
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // State for update result
    private val _updateResult = MutableStateFlow<UpdateResult?>(null)
    val updateResult: StateFlow<UpdateResult?> = _updateResult.asStateFlow()

    // Editing state
    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    init {
        loadUserData()
    }

    fun loadUserData() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            _isLoading.value = true
            _error.value = null

            viewModelScope.launch {
                loadUserProfile(userId)
                _isLoading.value = false
            }
        } else {
            _error.value = "Người dùng chưa đăng nhập"
            _isLoading.value = false
        }
    }

    private fun loadUserProfile(userId: String) {
        val userRef = database.getReference("Nguoidung").child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _nguoiDung.value = snapshot.getValue(NguoiDung::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                _error.value = "Không thể tải thông tin người dùng: ${error.message}"
            }
        })
    }

    // Set editing state
    fun setEditingMode(isEditing: Boolean) {
        _isEditing.value = isEditing
        // Reset update result when starting to edit
        if (isEditing) {
            _updateResult.value = null
        }
    }

    // Update user information
    fun updateUserInfo(updatedUser: NguoiDung) {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) {
            _error.value = "Người dùng chưa đăng nhập"
            _updateResult.value = UpdateResult.Error("Người dùng chưa đăng nhập")
            return
        }

        _isLoading.value = true
        _error.value = null

        viewModelScope.launch {
            try {
                // Make sure we have the latest data
                val currentData = _nguoiDung.value ?: NguoiDung()

                // Create a new user object with updated fields but preserve other fields
                val mergedUser = currentData.copy(
                    Ten = updatedUser.Ten.takeIf { !it.isNullOrBlank() } ?: currentData.Ten,
                    NgaySinh = updatedUser.NgaySinh.takeIf { !it.isNullOrBlank() } ?: currentData.NgaySinh,
                    GioiTinh = updatedUser.GioiTinh.takeIf { !it.isNullOrBlank() } ?: currentData.GioiTinh,
                    // Add other fields as needed
                )

                // Update in Firebase
                database.getReference("Nguoidung").child(userId).setValue(mergedUser).await()

                // Update local state
                _nguoiDung.value = mergedUser
                _updateResult.value = UpdateResult.Success("Cập nhật thông tin thành công")

                // Turn off editing mode
                _isEditing.value = false
            } catch (e: Exception) {
                _error.value = "Không thể cập nhật thông tin: ${e.message}"
                _updateResult.value = UpdateResult.Error("Không thể cập nhật thông tin: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }





    // Update password
    fun updatePassword(currentPassword: String, newPassword: String) {
        val user = firebaseAuth.currentUser
        if (user == null) {
            _error.value = "Người dùng chưa đăng nhập"
            _updateResult.value = UpdateResult.Error("Người dùng chưa đăng nhập")
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                // Re-authenticate the user first
                val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(user.email!!, currentPassword)
                user.reauthenticate(credential).await()

                // Update password
                user.updatePassword(newPassword).await()

                _updateResult.value = UpdateResult.Success("Cập nhật mật khẩu thành công")
            } catch (e: Exception) {
                _error.value = "Không thể cập nhật mật khẩu: ${e.message}"
                _updateResult.value = UpdateResult.Error("Không thể cập nhật mật khẩu: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Clear any update results
    fun clearUpdateResult() {
        _updateResult.value = null
    }

    // Helper class for update results
    sealed class UpdateResult {
        data class Success(val message: String) : UpdateResult()
        data class Error(val message: String) : UpdateResult()
    }
}

// Extension function to copy NguoiDung instance with null safety
// Add this if your NguoiDung class doesn't have a copy method
private fun NguoiDung.copy(
    Ten: String? = this.Ten,
    NgaySinh: String? = this.NgaySinh,
    GioiTinh: String? = this.GioiTinh
): NguoiDung {
    val newUser = NguoiDung()
    newUser.Ten = Ten
    newUser.NgaySinh = NgaySinh
    newUser.GioiTinh = GioiTinh
    return newUser
}