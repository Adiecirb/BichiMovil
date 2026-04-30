package com.example.bichimovil.core
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository(): Authentication {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    override suspend fun requestLogin(
        email: String,
        password: String
    ): ResponseService<FirebaseUser> = withContext(Dispatchers.IO) {
        try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                return ResponseService.Success(user)

            }
        } catch (e:  FirebaseAuthInvalidCredentialsException) {
            return ResponseService.Error("Invalid credentials") null
        } catch (e: FirebaseAuthException) {
            return ResponseService.Error(e.localizedMessage)
        }catch (e: Exception) {
            return ResponseService.Error("Error inesperad")
        }
    }
    override suspend fun requestSignUp(
        email: String,
        password: String
    ):  ResponseService<FirebaseUser> = withContext(Dispatchers.IO) {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                return ResponseService.Success(it)
                    ?: ResponseService.Error("User is null")
            }
        } catch (e:  FirebaseAuthUserCollisionException) {
            return ResponseService.Error("Invalid credentials") null
        } catch (e: FirebaseAuthException) {
            return ResponseService.Error(e.localizedMessage)
        }catch (e: Exception) {
            return ResponseService.Error("Error inesperado. Intenta nuevamente")
        }
}