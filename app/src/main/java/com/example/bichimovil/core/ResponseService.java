package com.example.bichimovil.core;

public class ResponseService {

    data class Success(val value: Boolean)
    data class Error(val error: String)
}