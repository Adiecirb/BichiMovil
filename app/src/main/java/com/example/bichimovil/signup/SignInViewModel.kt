package com.example.bichimovil.signup

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bichimovil.core.AuthRepository
import com.example.bichimovil.core.Authentication
import com.example.bichimovil.core.ResponseService
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
class SignInViewModel: ViewModel() {
    val repository = AuthRepository()
    fun requestLogin(){

        fun requestSignUp(email: String, password: String) {
            viewModelScope.launch {
                val result = repository.requestSignUp(email, password)
                result?.let { user ->
                    Log.i("Session", "Se ha creado el usuario ${user.uid}")
                } ?: run {
                    Log.e("Error", "Hubo un error al crear al usuario")
                }
            }
    }
}