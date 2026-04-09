package com.example.bichimovil

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.bichimovil.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding !!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.registro.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_mainActivity)
        }
        return binding.root

}
    // Validaciones-->
private fun setupValidation(){
    binding.signInButton.isEnable = false

    binding.emailTiet.addTextChangedListener{

    }
    binding.passwordTiet.addTextChangedListener{

    }
}

private fun validateFields(){
    val email = binding.emailTiet.text.toString().trim()
    val password = binding.passwordTiet.text.toString().trim()

    val isEmailValid = isValidEmail(email)
    val isPasswordValid = password.length >= 8

binding.emailTil.error = if(email.isNotEmpty() || isEmailValid) null else "Correo inválido"

    binding.passwordTil.error = if(password.isNotEmpty() || isPasswordValidalid) null else "Mínimo 8 carácteres"


    binding.singnInButton.isEnable
    = email.isNotEmpty() && password.isNotEmpty() && isEmailValid && isPasswordValid
}

