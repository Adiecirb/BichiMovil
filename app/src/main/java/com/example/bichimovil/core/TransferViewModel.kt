package com.example.bichimovil.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bichimovil.core.network.data.BeneficiaryResponse
import com.example.bichimovil.core.network.data.TransactionResponse
import com.example.bichimovil.core.network.data.requests.TransactionRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar el flujo completo de transferencias
 * Paso 1: Seleccionar beneficiario
 * Paso 2: Ingresar monto
 * Paso 3: Confirmar y transferir
 */
class TransferViewModel(
    private val bankRepository: BankRepository = BankRepository.getInstance()
) : ViewModel() {

    private val _selectedBeneficiary = MutableStateFlow<BeneficiaryResponse?>(null)
    val selectedBeneficiary: StateFlow<BeneficiaryResponse?> = _selectedBeneficiary.asStateFlow()

    private val _transferState = MutableStateFlow<ResponseService<TransactionResponse>?>(null)
    val transferState: StateFlow<ResponseService<TransactionResponse>?> = _transferState.asStateFlow()

    private val _currentAccount = MutableStateFlow<Long>(0)  // Saldo en centavos
    val currentAccount: StateFlow<Long> = _currentAccount.asStateFlow()

    fun selectBeneficiary(beneficiary: BeneficiaryResponse) {
        _selectedBeneficiary.value = beneficiary
    }

    fun loadCurrentBalance() {
        viewModelScope.launch {
            when (val result = bankRepository.getAccount()) {
                is ResponseService.Success -> {
                    _currentAccount.value = result.data.balance
                }
                is ResponseService.Error -> {
                    // Handle error
                }
                is ResponseService.Loading -> {}
            }
        }
    }

    fun transferMoney(
        toBeneficiaryId: String,
        amountCents: Long,
        description: String?
    ) {
        viewModelScope.launch {
            _transferState.value = ResponseService.Loading

            val request = TransactionRequest(
                toBeneficiaryId = toBeneficiaryId,
                amount = amountCents,
                description = description
            )

            _transferState.value = bankRepository.createTransaction(request)
        }
    }

    fun validateAmount(amountCents: Long): String? {
        return when {
            amountCents <= 0 -> "El monto debe ser mayor a 0"
            amountCents > _currentAccount.value -> "Saldo insuficiente"
            else -> null
        }
    }
}