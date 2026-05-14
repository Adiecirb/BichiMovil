package com.example.bichimovil.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.bichimovil.R
import com.example.bichimovil.signup.SignInViewModel
import com.example.bichimovil.core.FragmentCommunicator
import com.example.bichimovil.core.ResponseService
import com.example.bichimovil.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import android.content.Intent
import com.example.bichimovil.home.HomeActivity

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SignInViewModel>()

    private lateinit var communicator: FragmentCommunicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        communicator = requireActivity() as FragmentCommunicator
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

        binding.emailTiet.error = viewModel.validateEmail(email)
        binding.passwordTil.error = viewModel.validatePassword(password)
        binding.signInButton.isEnabled = viewModel.isLoginFormValid(email, password)
    }

    private fun setupClickListeners() {
        binding.signInButton.setOnClickListener {
            val email = binding.emailTiet.text.toString().trim()
            val password = binding.passwordTiet.text.toString().trim()
            viewModel.requestLogin(email, password)
        }
        binding.registerText.setOnClickListener {
            findNavController()
                .navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.signInState.collect { state ->
                    when (state) {
                        is ResponseService.Loading -> {
                            communicator.manageLoader(true)
                            binding.signInButton.isEnabled = false
                        }
                        is ResponseService.Success -> {
                            communicator.manageLoader(false)

                            val intent = Intent(requireActivity(), HomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)}
                            // TODO: navegar a MainActivity

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