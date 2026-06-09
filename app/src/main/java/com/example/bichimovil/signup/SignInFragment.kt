package com.example.bichimovil.signup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.bichimovil.R
import com.example.bichimovil.core.FragmentCommunicator
import com.example.bichimovil.core.ResponseService
import com.example.bichimovil.databinding.FragmentSigninBinding
import com.example.bichimovil.home.HomeActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SignInFragment : Fragment() {

    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SignInViewModel>()

    private lateinit var loaderCommunicator: FragmentCommunicator
    private lateinit var authCommunicator: AuthFragmentCommunicator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSigninBinding.inflate(inflater, container, false)
        loaderCommunicator = requireActivity() as FragmentCommunicator
        authCommunicator = requireActivity() as AuthFragmentCommunicator

        setupValidation()
        setupClickListeners()
        observeState()

        return binding.root
    }

    private fun setupValidation() {
        binding.signInButton.isEnabled = false

        binding.emailTiet.addTextChangedListener { validateAndEnable() }
        binding.passwordTiet.addTextChangedListener { validateAndEnable() }
    }

    private fun validateAndEnable() {
        val email = binding.emailTiet.text.toString().trim()
        val password = binding.passwordTiet.text.toString().trim()

        // Limpiar errores previos
        binding.emailTil.error = viewModel.validateEmail(email)
        binding.passwordTil.error = null

        // Habilitar si todo es válido
        binding.signInButton.isEnabled =
            binding.emailTil.error == null && password.isNotEmpty()
    }

    private fun setupClickListeners() {
        binding.signInButton.setOnClickListener {
            val email = binding.emailTiet.text.toString().trim()
            val password = binding.passwordTiet.text.toString().trim()

            loaderCommunicator.manageLoader(true)
            viewModel.requestLogin(email, password)
        }

        // Navegar a Sign Up
        binding.tvGoToSignUp.setOnClickListener {
            authCommunicator.goToSignUp()
        }

        // Navegar a Forgot Password
        binding.tvForgotPassword.setOnClickListener {
            authCommunicator.goToForgotPassword()
        }
    }
    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.signInState.collect { state ->
                when (state) {
                    is ResponseService.Success -> {
                        loaderCommunicator.manageLoader(false)

                        startActivity(
                            Intent(requireContext(), HomeActivity::class.java)
                        )
                        requireActivity().finish()
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