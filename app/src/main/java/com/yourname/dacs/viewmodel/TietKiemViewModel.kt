package com.yourname.dacs.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.yourname.dacs.model.TietKiem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class TietKiemViewModel : ViewModel() {

    // UI state flow for save operations
    private val _uiState = MutableStateFlow<TietKiemUiState>(TietKiemUiState.Idle)
    val uiState: StateFlow<TietKiemUiState> = _uiState.asStateFlow()

    // List of TietKiem items
    val tietKiemList = mutableStateListOf<TietKiem>()

    // Filtered TietKiem items by hũ
    private val _filteredTietKiemList = MutableStateFlow<List<TietKiem>>(emptyList())
    val filteredTietKiemList: StateFlow<List<TietKiem>> = _filteredTietKiemList.asStateFlow()

    private val tietKiemRef = FirebaseDatabase.getInstance().getReference("Tietkiem")
    private val auth = FirebaseAuth.getInstance()
    private val nguoiDungRef = FirebaseDatabase.getInstance().getReference("Nguoidung")

    init {
        loadTietKiem()
    }

    // Load all TietKiem records for current user
    private fun loadTietKiem() {
        tietKiemRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tietKiemList.clear()
                for (child in snapshot.children) {
                    val tietKiem = child.getValue(TietKiem::class.java)
                    if (tietKiem != null) {
                        tietKiemList.add(tietKiem)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _uiState.value = TietKiemUiState.Error("Lỗi khi tải dữ liệu: ${error.message}")
            }
        })
    }

    // Filter TietKiem by idHu
    fun filterByHu(idHu: String) {
        _filteredTietKiemList.value = tietKiemList.filter { it.idHu == idHu }
    }

    // Save a new transaction to Firebase
    fun saveTietKiem(soTien: String, loai: String, ghiChu: String, idHu: String) {
        viewModelScope.launch {
            try {
                _uiState.value = TietKiemUiState.Loading

                // Convert soTien to Double
                val soTienValue = soTien.toDoubleOrNull() ?: run {
                    _uiState.value = TietKiemUiState.Error("Số tiền không hợp lệ")
                    return@launch
                }

                // Validate idHu
                if (idHu.isBlank()) {
                    _uiState.value = TietKiemUiState.Error("Vui lòng chọn hũ tiết kiệm")
                    return@launch
                }

                // Generate a random 10-character ID
                val idGiaoDich = generateRandomId(10)

                // Get current date and time
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val ngayGio = dateFormat.format(Date())

                // Get current user ID
                val idNguoiDung = auth.currentUser?.uid ?: run {
                    _uiState.value = TietKiemUiState.Error("Người dùng chưa đăng nhập")
                    return@launch
                }

                // Create TietKiem object with idHu
                val tietKiem = TietKiem(
                    idGiaoDich = idGiaoDich,
                    ngayGio = ngayGio,
                    loaiGiaoDich = loai.lowercase(), // Convert to lowercase to match your model ("thu" or "chi")
                    ghiChu = ghiChu,
                    soTien = soTienValue,
                    idNguoiDung = idNguoiDung,
                    idHu = idHu  // Adding the idHu field
                )

                // Save to Realtime Database
                tietKiemRef.child(idGiaoDich)
                    .setValue(tietKiem)
                    .await()

                _uiState.value = TietKiemUiState.Success(tietKiem)
            } catch (e: Exception) {
                _uiState.value = TietKiemUiState.Error(e.message ?: "Lỗi không xác định")
            }
        }
    }

    fun getTenNguoiDungById(idNguoiDung: String, onResult: (String) -> Unit) {
        nguoiDungRef.child(idNguoiDung).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ten = snapshot.child("ten").getValue(String::class.java) ?: "Không rõ"
                onResult(ten)
            }

            override fun onCancelled(error: DatabaseError) {
                onResult("Không rõ")
            }
        })
    }




    // Generate a random ID for transactions
    private fun generateRandomId(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    // Reset UI state (e.g., after handling a successful save or error)
    fun resetState() {
        _uiState.value = TietKiemUiState.Idle
    }
    fun getThuNhapForHu(idHu: String): Double {
        return tietKiemList.filter { it.idHu == idHu && it.loaiGiaoDich == "thu" }
            .sumOf { it.soTien }
    }

    fun getChiTieuForHu(idHu: String): Double {
        return tietKiemList.filter { it.idHu == idHu && it.loaiGiaoDich == "chi" }
            .sumOf { it.soTien }
    }
    fun getTietKiemForHu(idHu: String): StateFlow<List<TietKiem>> {
        filterByHu(idHu) // Cập nhật danh sách lọc mỗi khi gọi
        return filteredTietKiemList
    }


}

// UI State for TietKiem operations
sealed class TietKiemUiState {
    object Idle : TietKiemUiState()
    object Loading : TietKiemUiState()
    data class Success(val tietKiem: TietKiem) : TietKiemUiState()

    data class Error(val message: String) : TietKiemUiState()
}