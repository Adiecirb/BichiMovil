package com.example.bichimovil.core

import com.example.bichimovil.core.network.RetrofitClient
import com.example.bichimovil.core.network.data.AccountResponse
import com.example.bichimovil.core.network.data.BeneficiaryResponse
import com.example.bichimovil.core.network.data.TransactionResponse
import com.example.bichimovil.core.network.data.requests.BeneficiaryRequest
import com.example.bichimovil.core.network.data.requests.FundRequest
import com.example.bichimovil.core.network.data.requests.TransactionRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para manejar todas las llamadas a la API de Banca
 * Singleton pattern para reutilizar la misma instancia
 */
class BankRepository private constructor() {

    private val bankAPI = RetrofitClient.bankAPI

    companion object {
        @Volatile
        private var instance: BankRepository? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: BankRepository().also { instance = it }
        }
    }

    // ==================== ACCOUNT ====================

    suspend fun createAccount(): ResponseService<AccountResponse> = withContext(Dispatchers.IO) {
        try {
            val response = bankAPI.createAccount()
            ResponseService.Success(response)
        } catch (e: Exception) {
            ResponseService.Error(e.message ?: "Error creando cuenta")
        }
    }

    suspend fun getAccount(): ResponseService<AccountResponse> = withContext(Dispatchers.IO) {
        try {
            val response = bankAPI.getAccount()
            ResponseService.Success(response)
        } catch (e: Exception) {
            ResponseService.Error(e.message ?: "Error obteniendo cuenta")
        }
    }

    suspend fun fundAccount(amountCents: Long): ResponseService<AccountResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = bankAPI.fundAccount(FundRequest(amountCents))
                ResponseService.Success(response)
            } catch (e: Exception) {
                ResponseService.Error(e.message ?: "Error fondeando cuenta")
            }
        }

    // ==================== BENEFICIARIES ====================

    suspend fun createBeneficiary(request: BeneficiaryRequest): ResponseService<BeneficiaryResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = bankAPI.createBeneficiary(request)
                ResponseService.Success(response)
            } catch (e: Exception) {
                ResponseService.Error(e.message ?: "Error creando beneficiario")
            }
        }

    suspend fun listBeneficiaries(): ResponseService<List<BeneficiaryResponse>> =
        withContext(Dispatchers.IO) {
            try {
                val response = bankAPI.listBeneficiaries()
                ResponseService.Success(response)
            } catch (e: Exception) {
                ResponseService.Error(e.message ?: "Error cargando beneficiarios")
            }
        }

    suspend fun getBeneficiary(id: String): ResponseService<BeneficiaryResponse> =
        withContext(Dispatchers.IO) {
            try {
                val response = bankAPI.getBeneficiary(id)
                ResponseService.Success(response)
            } catch (e: Exception) {
                ResponseService.Error(e.message ?: "Error obteniendo beneficiario")
            }
        }

    suspend fun updateBeneficiary(
        id: String,
        request: BeneficiaryRequest
    ): ResponseService<BeneficiaryResponse> = withContext(Dispatchers.IO) {
        try {
            val response = bankAPI.updateBeneficiary(id, request)
            ResponseService.Success(response)
        } catch (e: Exception) {
            ResponseService.Error(e.message ?: "Error actualizando beneficiario")
        }
    }

    suspend fun deleteBeneficiary(id: String): ResponseService<Unit> =
        withContext(Dispatchers.IO) {
            try {
                bankAPI.deleteBeneficiary(id)
                ResponseService.Success(Unit)
            } catch (e: Exception) {
                ResponseService.Error(e.message ?: "Error eliminando beneficiario")
            }
        }

    // ==================== TRANSACTIONS ====================

    suspend fun createTransaction(
        request: TransactionRequest
    ): ResponseService<TransactionResponse> = withContext(Dispatchers.IO) {
        try {
            val response = bankAPI.createTransaction(request)
            ResponseService.Success(response)
        } catch (e: Exception) {
            ResponseService.Error(e.message ?: "Error en transferencia")
        }
    }

    suspend fun listTransactions(): ResponseService<List<TransactionResponse>> =
        withContext(Dispatchers.IO) {
            try {
                val response = bankAPI.listTransactions()
                ResponseService.Success(response)
            } catch (e: Exception) {
                ResponseService.Error(e.message ?: "Error cargando transacciones")
            }
        }
}