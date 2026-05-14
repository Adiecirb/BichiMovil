package com.example.bichimovil.home.transactions

import com.example.bichimovil.core.ResponseService

interface TransactionService {
    suspend fun getUserTransactions(userId: String): ResponseService<List<Transaction>>
}