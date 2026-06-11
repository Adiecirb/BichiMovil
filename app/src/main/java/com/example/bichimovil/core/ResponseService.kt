package com.example.bichimovil.core

sealed class ResponseService<out T> {

    data class Success<T>(
        val data: T
    ) : ResponseService<T>()

    data class Error(
        val message: String,
        val code: String? = null   // código corto de la API (no_account, insufficient_funds...)
    ) : ResponseService<Nothing>()

    object Loading : ResponseService<Nothing>()
}
