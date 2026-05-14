package com.example.bichimovil.home.account

import com.example.bichimovil.core.ResponseService

interface AccountService {
    suspend fun getUserProfile(userId: String): ResponseService<Map<String, Any>>
}