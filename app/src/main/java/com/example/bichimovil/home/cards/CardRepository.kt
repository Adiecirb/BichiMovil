package com.example.bichimovil.home.cards

import com.example.bichimovil.core.ResponseService
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CardRepository : CardService {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("cards")

    override suspend fun getUserCards(
        userId: String
    ): ResponseService<List<Card>> = withContext(Dispatchers.IO) {
        try {
            val result = collection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val cards = result.toObjects(Card::class.java)
            ResponseService.Success(cards)
        } catch (e: Exception) {
            ResponseService.Error("No se pudieron cargar las tarjetas")
        }
    }
}