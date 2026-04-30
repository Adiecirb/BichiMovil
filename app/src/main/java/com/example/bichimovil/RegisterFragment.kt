package com.example.bichimovil

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.bichimovil.databinding.FragmentRegisterBinding
import androidx.fragment.app.viewModels
import com.example.bichimovil.signup.SignInViewModel

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegistrar.setOnClickListener {
            // Navega al siguiente paso del registro
            findNavController().navigate(R.id.action_registerFragment_to_personalInfoFragment)
        }

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}