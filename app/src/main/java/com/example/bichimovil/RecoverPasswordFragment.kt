package com.example.bichimovil

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.bichimovil.databinding.FragmentRecoverPasswordBinding

class RecoverPasswordFragment : Fragment() {
    private var _binding: FragmentRecoverPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRecoverPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSendInstructions.setOnClickListener {
            Toast.makeText(requireContext(), "Instrucciones enviadas al correo", Toast.LENGTH_LONG).show()
            findNavController().popBackStack() // Regresa al Login
        }

        binding.ivBackRecover.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}