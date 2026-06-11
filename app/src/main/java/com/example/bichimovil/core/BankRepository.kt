package com.example.bichimovil.core

import com.example.bichimovil.core.network.RetrofitClient
import com.example.bichimovil.core.network.data.AccountResponse
import com.example.bichimovil.core.network.data.BeneficiaryResponse
import com.example.bichimovil.core.network.data.TransactionResponse
import com.example.bichimovil.core.network.data.requests.BeneficiaryRequest
import com.example.bichimovil.core.network.data.requests.FundRequest
import com.example.bichimovil.core.network.data.requests.TransactionRequest
import com.example.bichimovil.core.network.data.responses.ApiErrorBody
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

/**
 * Repositorio único para TODAS las llamadas a la API de Banca.
 * Convierte HttpException en ResponseService.Error con el código corto
 * de la API (no_account, insufficient_funds, etc.) y el mensaje en español
 * que devuelve el backend.
 */
class BankRepository private constructor() {

    private val bankAPI = RetrofitClient.bankAPI
    private val gson = Gson()

    companion object {
        @Volatile
        private var instance: BankRepository? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: BankRepository().also { instance = it }
        }
    }

    /** Envuelve cualquier llamada y mapea errores HTTP al formato de la API. */
    private suspend fun <T> safeCall(block: suspend () -> T): ResponseService<T> =
        withContext(Dispatchers.IO) {
            try {
                ResponseService.Success(block())
            } catch (e: HttpException) {
                val raw = e.response()?.errorBody()?.string()
                val body = try {
                    gson.fromJson(raw, ApiErrorBody::class.java)
                } catch (_: Exception) {
                    null
                }
                ResponseService.Error(
                    message = body?.message ?: "Error del servidor (${e.code()})",
                    code = body?.error
                )
            } catch (e: Exception) {
                ResponseService.Error("Sin conexión. Revisa tu internet.", "network")
            }
        }

    // ==================== ACCOUNT ====================

    suspend fun createAccount() = safeCall { bankAPI.createAccount() }

    suspend fun getAccount() = safeCall { bankAPI.getAccount() }

    /**
     * GET /account y, si el usuario aún no tiene cuenta (no_account),
     * la crea automáticamente con POST /account.
     */
    suspend fun getOrCreateAccount(): ResponseService<AccountResponse> {
        val result = getAccount()
        return if (result is ResponseService.Error && result.code == "no_account") {
            createAccount()
        } else {
            result
        }
    }

    suspend fun fundAccount(amountCents: Long) =
        safeCall { bankAPI.fundAccount(FundRequest(amountCents)) }

    // ==================== BENEFICIARIES ====================

    suspend fun createBeneficiary(request: BeneficiaryRequest) =
        safeCall { bankAPI.createBeneficiary(request) }

    suspend fun listBeneficiaries(): ResponseService<List<BeneficiaryResponse>> =
        safeCall { bankAPI.listBeneficiaries() }

    suspend fun getBeneficiary(id: String) = safeCall { bankAPI.getBeneficiary(id) }

    suspend fun updateBeneficiary(id: String, request: BeneficiaryRequest) =
        safeCall { bankAPI.updateBeneficiary(id, request) }

    suspend fun deleteBeneficiary(id: String): ResponseService<Unit> =
        safeCall { bankAPI.deleteBeneficiary(id); Unit }

    // ==================== TRANSACTIONS ====================

    suspend fun createTransaction(request: TransactionRequest): ResponseService<TransactionResponse> =
        safeCall { bankAPI.createTransaction(request) }

    suspend fun listTransactions(): ResponseService<List<TransactionResponse>> =
        safeCall { bankAPI.listTransactions() }
}
