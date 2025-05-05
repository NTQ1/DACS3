package com.yourname.dacs.viewmodel

import androidx.lifecycle.ViewModel
import com.yourname.dacs.model.NguoiDung
import com.yourname.dacs.repository.UserRepository

class LoginViewModel : ViewModel() {
    private val userRepository = UserRepository()

    fun login(email: String, password: String, onResult: (NguoiDung?) -> Unit) {
        userRepository.login(email, password, onResult)
    }
}
