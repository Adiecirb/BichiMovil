package com.example.bichimovil.home.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.bichimovil.databinding.FragmentAddBeneficiaryBinding

class AddBeneficiaryFragment : Fragment() {

    private var _binding: FragmentAddBeneficiaryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddBeneficiaryBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnGuardarBeneficiario.setOnClickListener {

            val clabe = binding.etClabe.text.toString().trim()
            val banco = binding.etBanco.text.toString().trim()
            val nombre = binding.etNombreDestino.text.toString().trim()

            if (clabe.isEmpty()) {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = "Ingresa una CLABE"
                return@setOnClickListener
            }

            if (nombre.isEmpty()) {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = "Ingresa el nombre del destinatario"
                return@setOnClickListener
            }

            binding.tvError.visibility = View.GONE

            // TODO:
            // Aquí después guardaremos el beneficiario
            // usando tu BeneficiariesViewModel

            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}