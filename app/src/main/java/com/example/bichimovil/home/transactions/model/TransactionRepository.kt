package com.example.bichimovil.home.transactions

import com.example.bichimovil.core.ResponseService
import com.example.bichimovil.home.transactions.model.Transaction
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TransactionRepository : TransactionService {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("transactions")

    override suspend fun getUserTransactions(
        userId: String
    ): ResponseService<List<Transaction>> = withContext(Dispatchers.IO) {
        try {
            val result = collection
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
            val transactions = result.toObjects(Transaction::class.java)
            ResponseService.Success(transactions)
        } catch (e: Exception) {
            ResponseService.Error("No se pudieron cargar las transacciones")
        }
    }
}