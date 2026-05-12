package com.example.bichimovil

import android.os.Bundle
import android.view.LayoutInflater
import android.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewModels
import android.lifecycle.Lifecycle
import android.lifecycle.lifecycleScope
import com.example.bichimovil.FragmentCommunicator
import com.example.bichimovil.core.ResponseService
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.example.bichimovil.databinding.FragmentRegisterBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bichimovil.signup.SignInViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlin.io.root


class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SignInViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.signInButton.setOnClickListener {
            viewModel.requestSignUp(
                binding.emailTiet.text.toString().trim(),
                binding.passwordTiet.text.toString().trim()
            )
        }
        return binding.root
    }

    override fun onViewCreated(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        communicator = requireActivity() as FragmentCommunicator
        setupValidation()
        setupClickListeners()
        observeState()
        return binding.root
    }


    private fun setupValidation() {
        binding.btnRegistrar.isEnabled = false
        val watcher = { validateAndEnable() }
        binding.emailTiet.addTextChangedListener { validateAndEnable() }
        binding.passwordTiet.addTextChangedListener { validateAndEnable() }
        binding.confirmPasswordTiet.addTextChangedListener { validateAndEnable() }
    }

    private fun validateAndEnable() {
        val email = binding.emailTiet.text.toString().trim()
        val pass = binding.passwordTiet.text.toString().trim()
        val confirm = binding.confirmPasswordTiet.text.toString().trim()

        binding.emailTil.error = viewModel.validateEmail(email)
        binding.passwordTil.error = viewModel.validatePassword(pass)
        binding.confirmPasswordTil.error =
            viewModel.validateConfirmPassword(pass, confirm)

        binding.btnRegistrar.isEnabled =
            viewModel.isRegisterFormValid(email, pass, confirm)
    }

    private fun setupClickListeners() {
        binding.btnRegistrar.setOnClickListener {
            val email = binding.emailTiet.text.toString().trim()
            val password = binding.passwordTiet.text.toString().trim()
            viewModel.requestSignUp(email, password)
        }
        binding.registerText.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registerState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> {
                            communicator.manageLoader(true)
                            binding.btnRegistrar.isEnabled = false
                        }
                        is ResponseService.Success -> {
                            communicator.manageLoader(false)
                            // TODO: navegar a pantalla de datos personales
                        }
                        is ResponseService.Error -> {
                            communicator.manageLoader(false)
                            binding.signInButton.isEnabled = true
                            Snackbar.make(binding.root, state.error,
                                Snackbar.LENGTH_LONG).show()
                        }
                        null -> Unit
                    }
                }
            }
        }
    }

}