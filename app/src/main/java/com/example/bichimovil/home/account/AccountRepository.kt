package com.example.bichimovil.home.account

import com.example.bichimovil.core.ResponseService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AccountRepository : AccountService {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("users")

    override suspend fun getUserProfile(
        userId: String
    ): ResponseService<Map<String, Any>> = withContext(Dispatchers.IO) {
        try {
            val result = collection
                .document(userId)
                .get()
                .await()
            val data = result.data
            if (data != null) {
                ResponseService.Success(data)
            } else {
                ResponseService.Error("Perfil no encontrado")
            }
        } catch (e: Exception) {
            ResponseService.Error("No se pudo cargar el perfil")
        }
    }
}