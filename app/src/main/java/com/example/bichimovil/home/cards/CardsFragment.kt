package com.example.bichimovil.home.cards

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.bichimovil.core.BankRepository
import com.example.bichimovil.core.ResponseService
import com.example.bichimovil.core.toCurrencyMXN
import com.example.bichimovil.databinding.FragmentCardsBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

/**
 * Módulo Tarjetas.
 * - El saldo y los dígitos de la tarjeta vienen de GET /account (API real).
 * - "Solicitar tarjeta" (MVP) redirige a un enlace externo de solicitud.
 */
class CardsFragment : Fragment() {

    private var _binding: FragmentCardsBinding? = null
    private val binding get() = _binding!!

    private val bankRepository = BankRepository.getInstance()

    companion object {
        // MVP: enlace externo de solicitud de tarjeta.
        // Sustituye por tu Google Form / landing real cuando lo tengas.
        private const val CARD_REQUEST_URL = "https://forms.google.com"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardsBinding.inflate(inflater, container, false)

        setupClickListeners()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadAccount()
    }

    private fun loadAccount() {
        viewLifecycleOwner.lifecycleScope.launch {
            when (val result = bankRepository.getAccount()) {
                is ResponseService.Success -> {
                    binding.tvErrorTarjetas.visibility = View.GONE
                    binding.tvSaldoCard.text = result.data.balance.toCurrencyMXN()
                    binding.tvCardDigits.text =
                        "**** ${result.data.accountNumber.takeLast(4)}"
                }
                is ResponseService.Error -> {
                    binding.tvErrorTarjetas.visibility = View.VISIBLE
                    binding.tvErrorTarjetas.text = result.message
                }
                else -> Unit
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSolicitarTarjeta.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(CARD_REQUEST_URL)))
            } catch (e: Exception) {
                Snackbar.make(
                    binding.root,
                    "No se pudo abrir el enlace de solicitud",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
