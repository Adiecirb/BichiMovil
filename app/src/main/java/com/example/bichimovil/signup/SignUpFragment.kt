package com.example.bichimovil.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.bichimovil.core.FragmentCommunicator
import com.example.bichimovil.core.ResponseService
import com.example.bichimovil.databinding.FragmentSignupBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<RegisterViewModel>()

    private lateinit var loaderCommunicator: FragmentCommunicator
    private lateinit var authCommunicator: AuthFragmentCommunicator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        loaderCommunicator = requireActivity() as FragmentCommunicator
        authCommunicator = requireActivity() as AuthFragmentCommunicator

        setupValidation()
        setupClickListeners()
        observeState()

        return binding.root
    }

    private fun setupValidation() {
        binding.registerButton.isEnabled = false

        binding.emailTiet.addTextChangedListener { validateAndEnable() }
        binding.passwordTiet.addTextChangedListener { validateAndEnable() }
        binding.confirmPasswordTiet.addTextChangedListener { validateAndEnable() }
    }

    private fun validateAndEnable() {
        val email = binding.emailTiet.text.toString().trim()
        val password = binding.passwordTiet.text.toString().trim()
        val confirmPassword = binding.confirmPasswordTiet.text.toString().trim()

        // Limpiar errores previos
        binding.emailTil.error = viewModel.validateEmail(email)
        binding.passwordTil.error = viewModel.validatePassword(password)

        // Validar que las contraseñas coincidan
        if (password != confirmPassword && confirmPassword.isNotEmpty()) {
            binding.confirmPasswordTil.error = "Las contraseñas no coinciden"
        } else {
            binding.confirmPasswordTil.error = null
        }

        // Habilitar si todo es válido
        binding.registerButton.isEnabled =
            binding.emailTil.error == null &&
                    binding.passwordTil.error == null &&
                    binding.confirmPasswordTil.error == null &&
                    password.isNotEmpty()
    }

    private fun setupClickListeners() {
        binding.registerButton.setOnClickListener {
            val email = binding.emailTiet.text.toString().trim()
            val password = binding.passwordTiet.text.toString().trim()

            loaderCommunicator.manageLoader(true)
            viewModel.requestSignUp(email, password)
        }

        // Volver a Sign In
        binding.tvGoToSignIn.setOnClickListener {
            authCommunicator.goToSignIn()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.registerState.collect { state ->
                when (state) {
                    is ResponseService.Success -> {
                        loaderCommunicator.manageLoader(false)
                        Snackbar.make(
                            binding.root,
                            "Registro exitoso",
                            Snackbar.LENGTH_SHORT
                        ).show()

                        // Verificar si la cuenta en API se creó
                        viewLifecycleOwner.lifecycleScope.launch {
                            viewModel.accountCreationState.collect { accountCreated ->
                                if (!accountCreated) {
                                    Snackbar.make(
                                        binding.root,
                                        "Advertencia: No se pudo crear cuenta bancaria",
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }

                        // Ir a Personal Info
                        authCommunicator.goToPersonalInfo(
                            binding.emailTiet.text.toString(),
                            binding.passwordTiet.text.toString()
                        )
                    }
                    is ResponseService.Error -> {
                        loaderCommunicator.manageLoader(false)
                        Snackbar.make(
                            binding.root,
                            state.message,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    is ResponseService.Loading -> {
                        loaderCommunicator.manageLoader(true)
                    }
                    null -> Unit
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}