package com.example.bichimovil.core

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository private constructor() : Authentication {

    private val auth = FirebaseAuth.getInstance()

    companion object {
        @Volatile private var instance: AuthRepository? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: AuthRepository().also { instance = it }
        }
    }

    override suspend fun requestLogin(
        email: String,
        password: String
    ): ResponseService<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { ResponseService.Success(it) }
                ?: ResponseService.Error("Usuario no encontrado")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            ResponseService.Error("Correo o contraseña incorrectos")
        } catch (e: FirebaseAuthException) {
            ResponseService.Error(e.localizedMessage ?: "Error de autenticación")
        } catch (e: Exception) {
            ResponseService.Error("Error inesperado")
        }
    }

    override suspend fun requestSignUp(
        email: String,
        password: String
    ): ResponseService<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { ResponseService.Success(it) }
                ?: ResponseService.Error("No se pudo crear el usuario")
        } catch (e: FirebaseAuthUserCollisionException) {
            ResponseService.Error("Este correo ya está registrado")
        } catch (e: FirebaseAuthWeakPasswordException) {
            ResponseService.Error("La contraseña es muy débil")
        } catch (e: Exception) {
            ResponseService.Error("Error inesperado. Intenta nuevamente")
        }
    }
}