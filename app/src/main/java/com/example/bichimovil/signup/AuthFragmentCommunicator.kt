package com.example.bichimovil.signup

/**
 * Interface para comunicación entre fragments de autenticación
 * Permite navegar entre Sign In, Sign Up y Forgot Password
 */
interface AuthFragmentCommunicator {
    fun goToSignUp()
    fun goToSignIn()
    fun goToForgotPassword()
    fun goToPersonalInfo(email: String, password: String)
}