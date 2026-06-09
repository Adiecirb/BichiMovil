package com.example.bichimovil.signup

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bichimovil.core.AuthRepository
import com.example.bichimovil.core.BankRepository
import com.example.bichimovil.core.ResponseService
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository = AuthRepository.getInstance(),
    private val bankRepository: BankRepository = BankRepository.getInstance()  // ← NUEVO
) : ViewModel() {

    private val _registerState = MutableStateFlow<ResponseService<FirebaseUser>?>(null)
    val registerState: StateFlow<ResponseService<FirebaseUser>?> = _registerState.asStateFlow()

    private val _accountCreationState = MutableStateFlow<Boolean>(false)  // ← NUEVO
    val accountCreationState: StateFlow<Boolean> = _accountCreationState.asStateFlow()

    fun validateEmail(email: String): String? {
        if (email.isBlank()) return "El correo es requerido"
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) return "Correo inválido"
        return null
    }

    fun validatePassword(password: String): String? {
        if (password.isBlank()) return "La contraseña es requerida"
        if (password.length < 8) return "Mínimo 8 caracteres"
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

            // Paso 1: Crear usuario en Firebase Auth
            val firebaseResult = authRepository.requestSignUp(email, password)

            if (firebaseResult is ResponseService.Success) {
                // Paso 2: Crear cuenta en la API de Banca
                val accountResult = bankRepository.createAccount()

                when (accountResult) {
                    is ResponseService.Success -> {
                        _accountCreationState.value = true
                        _registerState.value = firebaseResult
                    }
                    is ResponseService.Error -> {
                        // Firebase OK pero API falló - aún pasamos el éxito
                        _accountCreationState.value = false
                        _registerState.value = firebaseResult
                    }
                    is ResponseService.Loading -> {}
                }
            } else {
                _registerState.value = firebaseResult
            }
        }
    }
}