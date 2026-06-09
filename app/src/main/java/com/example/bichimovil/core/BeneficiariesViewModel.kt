package com.example.bichimovil.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bichimovil.core.network.data.BeneficiaryResponse
import com.example.bichimovil.core.network.data.requests.BeneficiaryRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar CRUD de Beneficiarios
 */
class BeneficiariesViewModel(
    private val bankRepository: BankRepository = BankRepository.getInstance()
) : ViewModel() {

    private val _beneficiariesState =
        MutableStateFlow<ResponseService<List<BeneficiaryResponse>>?>(null)
    val beneficiariesState: StateFlow<ResponseService<List<BeneficiaryResponse>>?> =
        _beneficiariesState.asStateFlow()

    private val _createBeneficiaryState = MutableStateFlow<ResponseService<BeneficiaryResponse>?>(null)
    val createBeneficiaryState: StateFlow<ResponseService<BeneficiaryResponse>?> =
        _createBeneficiaryState.asStateFlow()

    private val _deleteBeneficiaryState = MutableStateFlow<ResponseService<Unit>?>(null)
    val deleteBeneficiaryState: StateFlow<ResponseService<Unit>?> = _deleteBeneficiaryState.asStateFlow()

    fun listBeneficiaries() {
        viewModelScope.launch {
            _beneficiariesState.value = ResponseService.Loading
            _beneficiariesState.value = bankRepository.listBeneficiaries()
        }
    }

    fun createBeneficiary(
        name: String,
        lastName: String,
        accountNumber: String,
        alias: String
    ) {
        viewModelScope.launch {
            _createBeneficiaryState.value = ResponseService.Loading
            val request = BeneficiaryRequest(
                name = name,
                lastName = lastName,
                accountNumber = accountNumber,
                alias = alias
            )
            _createBeneficiaryState.value = bankRepository.createBeneficiary(request)
        }
    }

    fun deleteBeneficiary(id: String) {
        viewModelScope.launch {
            _deleteBeneficiaryState.value = ResponseService.Loading
            _deleteBeneficiaryState.value = bankRepository.deleteBeneficiary(id)
        }
    }
}