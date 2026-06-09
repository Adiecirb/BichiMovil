package com.example.bichimovil.core.network

import com.example.bichimovil.core.network.data.AccountResponse
import com.example.bichimovil.core.network.data.BeneficiaryResponse
import com.example.bichimovil.core.network.data.TransactionResponse
import com.example.bichimovil.core.network.data.requests.BeneficiaryRequest
import com.example.bichimovil.core.network.data.requests.FundRequest
import com.example.bichimovil.core.network.data.requests.TransactionRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Retrofit Interface para API de Banca
 * Base URL: https://us-central1-bankapp-e47b0.cloudfunctions.net/api/
 *
 * IMPORTANTE:
 * - Todos los requests requieren: Authorization: Bearer <idToken>
 * - El AuthInterceptor inyecta esto automáticamente
 * - El dinero siempre va en CENTAVOS (Long, no Double)
 */
interface BankAPI {

    // ==================== ACCOUNT ====================

    /**
     * POST /account
     * Crea la cuenta bancaria del usuario autenticado (balance = 0)
     * Solo se puede crear UNA cuenta por usuario (409 si ya existe)
     */
    @POST("account")
    suspend fun createAccount(): AccountResponse

    /**
     * GET /account
     * Obtiene la cuenta del usuario autenticado
     * 404 si no existe (usuario no ha creado cuenta aún)
     */
    @GET("account")
    suspend fun getAccount(): AccountResponse

    /**
     * PUT /account
     * Fondea la cuenta (suma dinero al balance actual)
     * Body: { "amount": 5000 }  ← centavos
     */
    @PUT("account")
    suspend fun fundAccount(
        @Body request: FundRequest
    ): AccountResponse

    // ==================== BENEFICIARIES ====================

    /**
     * POST /beneficiaries
     * Crea un nuevo beneficiario para el usuario autenticado
     * El accountNumber debe corresponder a una cuenta real en el sistema
     */
    @POST("beneficiaries")
    suspend fun createBeneficiary(
        @Body request: BeneficiaryRequest
    ): BeneficiaryResponse

    /**
     * GET /beneficiaries
     * Lista TODOS los beneficiarios del usuario autenticado
     * Retorna array (puede estar vacío)
     */
    @GET("beneficiaries")
    suspend fun listBeneficiaries(): List<BeneficiaryResponse>

    /**
     * GET /beneficiaries/{id}
     * Obtiene un beneficiario específico por su ID
     * 404 si no existe o pertenece a otro usuario
     */
    @GET("beneficiaries/{id}")
    suspend fun getBeneficiary(
        @Path("id") beneficiaryId: String
    ): BeneficiaryResponse

    /**
     * PUT /beneficiaries/{id}
     * Actualiza todos los campos de un beneficiario
     * Debe enviar los 4 campos completos (name, lastName, accountNumber, alias)
     */
    @PUT("beneficiaries/{id}")
    suspend fun updateBeneficiary(
        @Path("id") beneficiaryId: String,
        @Body request: BeneficiaryRequest
    ): BeneficiaryResponse

    /**
     * DELETE /beneficiaries/{id}
     * Borra un beneficiario
     * Retorna: { "id": "...", "deleted": true }
     */
    @DELETE("beneficiaries/{id}")
    suspend fun deleteBeneficiary(
        @Path("id") beneficiaryId: String
    ): Map<String, Any>  // { "id": "...", "deleted": true }

    // ==================== TRANSACTIONS ====================

    /**
     * POST /transaction
     * Realiza una transferencia de dinero
     * La operación es atómica: se descuenta de ti y se acredita al otro, o no pasa nada
     *
     * Errores posibles:
     * - 400 insufficient_funds: No tienes saldo suficiente
     * - 400 same_account: El beneficiario apunta a tu propia cuenta
     * - 404 beneficiary_not_found: El beneficiario no existe o no es tuyo
     * - 404 dest_account_not_found: La cuenta destino no existe
     */
    @POST("transaction")
    suspend fun createTransaction(
        @Body request: TransactionRequest
    ): TransactionResponse

    /**
     * GET /transaction
     * Lista movimientos de tu cuenta (enviados y recibidos)
     * Ordenados del más reciente al más antiguo
     *
     * Campo "direction" indica tu perspectiva:
     * - "out": Tú enviaste el dinero (mostrar en rojo, signo -)
     * - "in": Tú recibiste el dinero (mostrar en verde, signo +)
     */
    @GET("transaction")
    suspend fun listTransactions(): List<TransactionResponse>
}