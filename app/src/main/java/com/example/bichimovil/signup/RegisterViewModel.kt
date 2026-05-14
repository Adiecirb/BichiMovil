package com.example.bichimovil.signup

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bichimovil.core.AuthRepository
import com.example.bichimovil.core.ResponseService
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository = AuthRepository.getInstance()
): ViewModel() {
    private val _registerState = MutableStateFlow<ResponseService<FirebaseUser>?>(null)
    val registerState: StateFlow<ResponseService<FirebaseUser>?> = _registerState.asStateFlow()

    fun validateEmail(email: String): String? {
        if (email.isBlank()) return "El correo es requerido"
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Correo inválido"
        return null
    }

    fun validatePassword(password: String): String? {
        if (password.isBlank()) return "La contraseña es requerida"
        if (password.length < 8) return "Mínimo 8 caracteres"
        // Agregar regla de complejidad
        if (!password.any { it.isDigit() }) return "Debe contener al menos un número"
        if (!password.any { it.isUpperCase() }) return "Debe contener mayúscula"
        return null
    }

    fun validateConfirmPassword(password: String, confirm: String): String? {
        if (confirm.isBlank()) return "La confirmación es requerida"
        if (password != confirm) return "Las contraseñas no coinciden"
        return null
    }

    fun isRegisterFormValid(email: String, pass: String, confirm: String): Boolean {
        return validateEmail(email) == null &&
                validatePassword(pass) == null &&
                validateConfirmPassword(pass, confirm) == null
    }

    fun requestSignUp(email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = ResponseService.Loading
            _registerState.value = authRepository.requestSignUp(email, password)
        }
    }
}