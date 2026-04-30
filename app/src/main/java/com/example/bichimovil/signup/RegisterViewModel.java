package com.example.bichimovil.signup;

import androidx.lifecycle.ViewModel
        import com.example.bichimovil.core.AuthRepository
                import com.example.bichimovil.core.ResponseService
                        import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
        import kotlinx.coroutines.flow.StateFlow
        import kotlinx.coroutines.flow.asStateFlow


 class RegisterViewModel: ViewModel()
{
    private val authRepository = AuthRepository()
            private val _registerState = MutableStateFlow<ResponseService<FirebaseUser>?>(null)
                    val registerState: StateFlow<ResponseService<FirebaseUser>?> = _registerState.asStateFlow()
}

//---Validacion
fun validateEmail(email: String): String? {/*igual que SignInViewModel*/return null}
        fun validatePassword(password: String): String? {/*igual*/return null}

 fun validateConfirmPassword(password: String, confirm: String): String? {
    if (confirm.isBlank()) return "Confirmar contraseña"
         if (password != confirm) return "Las contraseñas no coinciden"
         return null

         }

         fun isRegisterFormValid(email: String, password: String, confirm: String): Boolean {
    return validateEmail(email) == null &&
            validatePassword(password) == null &&
            validateConfirmPassword(password, confirm) == null
         }
//--Operacion sw registro
fun requestSignUp(email: String, password: String) {

    viewModelScope.launch {
        _registerState.value = ResponseService.Loading
_registerState.value = authRepository.requestSignUp(email, password)
    }
}

 }