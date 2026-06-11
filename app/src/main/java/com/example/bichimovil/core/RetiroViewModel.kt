package com.example.bichimovil.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bichimovil.core.network.data.TransactionResponse
import com.example.bichimovil.core.network.data.requests.TransactionRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Retiro sin tarjeta.
 *
 * La API no expone un endpoint de retiro, así que el retiro se procesa como
 * una transferencia real (POST /transaction) hacia el beneficiario "CAJERO"
 * del usuario. La API descuenta el saldo de forma atómica → conciliación
 * automática del saldo al confirmar.
 *
 * Requisito de configuración (una sola vez): el usuario debe tener un
 * beneficiario cuyo alias contenga "cajero", apuntando a la cuenta del
 * usuario cajero del sistema.
 */
class RetiroViewModel(
    private val bankRepository: BankRepository = BankRepository.getInstance()
) : ViewModel() {

    private val _currentAccount = MutableStateFlow<Long>(0)  // Saldo en centavos (API)
    val currentAccount: StateFlow<Long> = _currentAccount.asStateFlow()

    private val _retiroPin = MutableStateFlow("")
    val retiroPin: StateFlow<String> = _retiroPin.asStateFlow()

    private val _retiroAmountCents = MutableStateFlow<Long>(0)
    val retiroAmountCents: StateFlow<Long> = _retiroAmountCents.asStateFlow()

    private val _retiroState = MutableStateFlow<ResponseService<TransactionResponse>?>(null)
    val retiroState: StateFlow<ResponseService<TransactionResponse>?> = _retiroState.asStateFlow()

    fun loadCurrentBalance() {
        viewModelScope.launch {
            when (val result = bankRepository.getAccount()) {
                is ResponseService.Success -> _currentAccount.value = result.data.balance
                else -> Unit
            }
        }
    }

    fun generateRetiroPin(amountCents: Long): String {
        _retiroAmountCents.value = amountCents
        val pin = (100000..999999).random().toString()
        _retiroPin.value = pin
        return pin
    }

    /**
     * Confirma el retiro: transfiere el monto al beneficiario CAJERO vía la
     * API, lo que descuenta el saldo de la cuenta origen.
     */
    fun confirmRetiro() {
        viewModelScope.launch {
            _retiroState.value = ResponseService.Loading

            // 1) Buscar el beneficiario cajero del usuario
            val beneficiarios = bankRepository.listBeneficiaries()
            if (beneficiarios !is ResponseService.Success) {
                _retiroState.value =
                    ResponseService.Error("No se pudo consultar tus beneficiarios")
                return@launch
            }

            val cajero = beneficiarios.data.firstOrNull {
                it.alias.contains("cajero", ignoreCase = true)
            }

            if (cajero == null) {
                _retiroState.value = ResponseService.Error(
                    "Configura primero un beneficiario con alias \"CAJERO\" " +
                            "apuntando a la cuenta del cajero.",
                    code = "no_cajero"
                )
                return@launch
            }

            // 2) Transferencia real → la API descuenta el saldo atómicamente
            _retiroState.value = bankRepository.createTransaction(
                TransactionRequest(
                    toBeneficiaryId = cajero.id,
                    amount = _retiroAmountCents.value,
                    description = "Retiro sin tarjeta · clave ${_retiroPin.value}"
                )
            )
        }
    }

    fun clearRetiroState() {
        _retiroState.value = null
    }

    fun validateAmount(amountCents: Long): String? {
        return when {
            amountCents <= 0 -> "El monto debe ser mayor a 0"
            amountCents > _currentAccount.value -> "Saldo insuficiente"
            else -> null
        }
    }
}
