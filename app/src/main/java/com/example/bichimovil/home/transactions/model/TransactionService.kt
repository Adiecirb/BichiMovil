package com.example.bichimovil.home.transactions

import com.example.bichimovil.core.ResponseService
import com.example.bichimovil.home.transactions.model.Transaction

interface TransactionService {
    suspend fun getUserTransactions(userId: String): ResponseService<List<Transaction>>
}