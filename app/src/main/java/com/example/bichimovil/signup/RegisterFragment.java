package com.example.bichimovil.signup;


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
import com.example.bichimovil.core.FragmentCommunicator
import com.example.bichimovil.databinding.FragmentRegisterBinding;


class RegisterFragment : Fragment() {

    private var _binding:FragmentLoginBinding ? = null
    private val binding get() = _binding !!

    private val viewModel by viewModels<RegisterViewModel > ()
    private lateinit var communicator: FragmentCommunicator


    override fun onCreateView(
            inflater:LayoutInflater, container:ViewGroup ?,
            savedInstanceState:Bundle ?
    ):View ? {
            _binding = FragmentRegisterBinding.inflate(inflater, container, false)
            communicator = requireActivity() as FragmentCommunicator
            setupValidation()
            setupClickListeners()
            observeState()
    return binding.root
    }
}

private fun setupValidation(){
    binding.btnIngresar.isEnabled = false
            val watcher ={validateEnable()}
                    binding.emailTiet.addTextChangedListener{validateEnable()}
    binding.passwordTiet.addTextChangedListener{validateEnable()}
    binding.confirmPasswordTiet.addTextChangedListener{validateEnable()}

}
private fun validateEnable(){
    val email = binding.emailTiet.text.toString().trim()
    val password = binding.passwordTiet.text.toString().trim()
    val confirmPassword = binding.confirmPasswordTiet.text.toString().trim()

            binding.emailTil.error = viewModel.validateEmail(email)
                    binding.passwordTil.error = viewModel.validatePassword(pass)
    binding.confirmPasswordTil.error = viewModel.validateConfirmPassword(pass, confirm)
    binding.btnIngresar.isEnabled = viewModel.isRegisterFormValid(email, pass, confirm)
}


private fun setupClickListeners(){
    binding.signInButton.setOnClickListener{
        val email = binding.emailEditText.text.toString().tirm()
                val password = binding.passwordEditText.text.toString().trim()
                        viewModel.requestLogin(email, password)
    }
    binding.registerText.setOnClickListener{
        findNavController().navigateUp()
    }
}

private fun observeState(){
    viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED){
            viewModel.registerState.collect {
                state ->
                        when(state) {
                    is ResponseService.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is ResponseService.Success -> {
                        comminuicator.manageLoader(true)
                        binding.btnIngresar.isEnabled = false

                    }
                    is ResponseService.Succes -> {
                        commincator.manageLoader(false)
                        //Todo: Navegar a panatalla datos personales

                    }
                    is ResponseService.Error -> {
                        comminuicator.manageLoader(false)
                        binding.btnIngresar.isEnabled = true
                        Snackbar.nake(binding.root, state.message, Snackbar.LENGTH_LONG).show()

                    }
                    null ->Unit

                }

            }}}}









