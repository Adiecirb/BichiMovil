package com.example.bichimovil.home.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.bichimovil.core.BeneficiariesViewModel
import com.example.bichimovil.core.ResponseService
import com.example.bichimovil.databinding.FragmentAddBeneficiaryBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * Alta de beneficiario contra POST /beneficiaries.
 *
 * IMPORTANTE: la API identifica las cuentas por el accountNumber de 10
 * dígitos. Este fragment acepta:
 *  - El número de cuenta de 10 dígitos (con o sin espacios), o
 *  - La CLABE de 18 dígitos generada por la app (prefijo 646180), de la
 *    cual se extrae automáticamente el número de cuenta embebido.
 */
class AddBeneficiaryFragment : Fragment() {

    private var _binding: FragmentAddBeneficiaryBinding? = null
    private val binding get() = _binding!!

    private val beneficiariesViewModel by activityViewModels<BeneficiariesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBeneficiaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        observeCreateState()

        binding.btnGuardarBeneficiario.setOnClickListener {
            val cuentaInput = binding.etClabe.text.toString()
            val banco = binding.etBanco.text.toString().trim()
            val nombreCompleto = binding.etNombreDestino.text.toString().trim()
            val aliasInput = binding.etAlias.text.toString().trim()

            val cuenta = extractAccountNumber(cuentaInput)

            when {
                cuenta == null -> showError(
                    "Cuenta inválida. Usa el número de cuenta de 10 dígitos " +
                            "que aparece en \"Más datos\" del destinatario."
                )
                nombreCompleto.isEmpty() ->
                    showError("Ingresa el nombre del destinatario")
                else -> {
                    binding.tvError.visibility = View.GONE

                    val partes = nombreCompleto.split(" ", limit = 2)
                    val name = partes[0]
                    val lastName = partes.getOrElse(1) { "." }

                    val alias = when {
                        aliasInput.isNotEmpty() -> aliasInput
                        banco.isNotEmpty() -> "$name ($banco)"
                        else -> name
                    }

                    binding.btnGuardarBeneficiario.isEnabled = false
                    beneficiariesViewModel.createBeneficiary(
                        name = name,
                        lastName = lastName,
                        accountNumber = cuenta,
                        alias = alias
                    )
                }
            }
        }
    }

    /**
     * Normaliza lo capturado al accountNumber de 10 dígitos que entiende la API.
     * - 10 dígitos → se usa tal cual.
     * - 18 dígitos con prefijo 646180 (CLABE derivada por esta app) →
     *   se extrae el número de cuenta embebido (posiciones 7 a 17).
     * - Cualquier otra cosa → inválido.
     */
    private fun extractAccountNumber(input: String): String? {
        val digits = input.filter { it.isDigit() }
        return when {
            digits.length == 10 -> digits
            digits.length == 18 && digits.startsWith("646180") ->
                digits.substring(6, 17).trimStart('0')
                    .takeIf { it.length == 10 }
            else -> null
        }
    }

    private fun observeCreateState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                beneficiariesViewModel.createBeneficiaryState.collect { state ->
                    when (state) {
                        is ResponseService.Success -> {
                            beneficiariesViewModel.clearCreateState()
                            beneficiariesViewModel.listBeneficiaries()
                            Snackbar.make(
                                binding.root,
                                "Beneficiario guardado",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            findNavController().popBackStack()
                        }
                        is ResponseService.Error -> {
                            binding.btnGuardarBeneficiario.isEnabled = true
                            showError(state.message)
                            beneficiariesViewModel.clearCreateState()
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    private fun showError(msg: String) {
        binding.tvError.visibility = View.VISIBLE
        binding.tvError.text = msg
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
