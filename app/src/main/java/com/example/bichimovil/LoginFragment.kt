package com.example.bichimovil

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.example.bichimovil.databinding.FragmentLoginBinding
import androidx.fragment.app.viewModels

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SignInViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupValidation()
        setupNavigation()
    }

    private fun setupNavigation() {
        // Navegación al Main al dar clic en Ingresar
        binding.btnIngresar.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_mainActivity)
        }

        // Navegación a Registro
        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // Navegación a Recuperar Contraseña
        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_recoverPasswordFragment)
        }
    }

    private fun setupValidation() {
        binding.btnIngresar.isEnabled = false

        binding.emailTiet.addTextChangedListener {
            validateFields()
        }
        binding.passwordTiet.addTextChangedListener {
            validateFields()
        }
    }

    private fun validateFields() {
        val email = binding.emailTiet.text.toString().trim()
        val password = binding.passwordTiet.text.toString().trim()

        val isEmailValid = isValidEmail(email)
        val isPasswordValid = password.length >= 8

        // Gestión de errores visuales en los campos
        binding.emailTiet.error = if (email.isEmpty() || isEmailValid) null else "Correo inválido"
        binding.passwordTiet.error = if (password.isEmpty() || isPasswordValid) null else "Mínimo 8 caracteres"

        // El botón solo se activa si todo es válido
        binding.btnIngresar.isEnabled = email.isNotEmpty() && password.isNotEmpty() && isEmailValid && isPasswordValid
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}