package com.example.bichimovil.home.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bichimovil.core.BankRepository
import com.example.bichimovil.core.ResponseService
import com.example.bichimovil.core.network.data.AccountResponse
import com.example.bichimovil.core.network.data.TransactionResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel del Home (fragment_transactions).
 * TODO el dato viene de la API de Banca:
 *  - GET /account (y POST /account si el usuario aún no tiene cuenta)
 *  - PUT /account  → simulador de fondeo
 *  - GET /transaction → historial de movimientos
 */
class TransactionsViewModel(
    private val repo: BankRepository = BankRepository.getInstance()
) : ViewModel() {

    private val _accountState = MutableStateFlow<ResponseService<AccountResponse>?>(null)
    val accountState: StateFlow<ResponseService<AccountResponse>?> = _accountState.asStateFlow()

    private val _transactionsState =
        MutableStateFlow<ResponseService<List<TransactionResponse>>?>(null)
    val transactionsState: StateFlow<ResponseService<List<TransactionResponse>>?> =
        _transactionsState.asStateFlow()

    private val _fundState = MutableStateFlow<ResponseService<AccountResponse>?>(null)
    val fundState: StateFlow<ResponseService<AccountResponse>?> = _fundState.asStateFlow()

    /** Carga cuenta (creándola si no existe) + historial. */
    fun loadHome() {
        viewModelScope.launch {
            _accountState.value = ResponseService.Loading
            val account = repo.getOrCreateAccount()
            _accountState.value = account

            if (account is ResponseService.Success) {
                _transactionsState.value = ResponseService.Loading
                _transactionsState.value = repo.listTransactions()
            }
        }
    }

    /** Simulador de fondeo: PUT /account. Al terminar refresca el historial. */
    fun fundAccount(amountCents: Long) {
        viewModelScope.launch {
            _fundState.value = ResponseService.Loading
            val result = repo.fundAccount(amountCents)
            _fundState.value = result
            if (result is ResponseService.Success) {
                _accountState.value = result          // saldo actualizado al instante
                _transactionsState.value = repo.listTransactions()
            }
        }
    }

    fun clearFundState() {
        _fundState.value = null
    }
}
