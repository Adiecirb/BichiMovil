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
import com.example.bichimovil.databinding.FragmentForgotPasswordBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SignInViewModel>()

    private lateinit var loaderCommunicator: FragmentCommunicator
    private lateinit var authCommunicator: AuthFragmentCommunicator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        loaderCommunicator = requireActivity() as FragmentCommunicator
        authCommunicator = requireActivity() as AuthFragmentCommunicator

        setupValidation()
        setupClickListeners()
//        observeState()

        return binding.root
    }

    private fun setupValidation() {
        binding.sendButton.isEnabled = false
        binding.emailTiet.addTextChangedListener { validateAndEnable() }
    }

    private fun validateAndEnable() {
        val email = binding.emailTiet.text.toString().trim()
        binding.emailTil.error = viewModel.validateEmail(email)
        binding.sendButton.isEnabled = binding.emailTil.error == null
    }

    private fun setupClickListeners() {
        binding.sendButton.setOnClickListener {
            Snackbar.make(
                binding.root,
                "Email de recuperación enviado. Revisa tu bandeja.",
                Snackbar.LENGTH_LONG
            ).show()

            binding.root.postDelayed({
                authCommunicator.goToSignIn()
            }, 2000)
        }

        binding.tvGoToSignIn.setOnClickListener {
            authCommunicator.goToSignIn()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}