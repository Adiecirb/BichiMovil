package com.example.bichimovil.home.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bichimovil.core.ResponseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountViewModel(
    private val repository: AccountRepository = AccountRepository()
) : ViewModel() {

    private val _accountState =
        MutableStateFlow<ResponseService<Map<String, Any>>?>(null)
    val accountState: StateFlow<ResponseService<Map<String, Any>>?> =
        _accountState.asStateFlow()

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            _accountState.value = ResponseService.Loading
            _accountState.value = repository.getUserProfile(userId)
        }
    }
}