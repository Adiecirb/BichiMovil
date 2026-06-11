package com.example.bichimovil.home.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.bichimovil.core.ResponseService
import com.example.bichimovil.databinding.FragmentAccountBinding
import com.example.bichimovil.signup.MainActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

/**
 * Cuenta y configuración:
 * - Muestra los datos que el usuario ingresó en la app
 *   (nombre, correo, teléfono, fecha de nacimiento)
 * - Cierre de sesión con diálogo de confirmación
 */
class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)

        showAuthData()
        setupClickListeners()
        observeProfile()
        loadProfile()

        return binding.root
    }

    /** Datos inmediatos desde Firebase Auth (lo que el usuario registró). */
    private fun showAuthData() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        binding.tvUserEmail.text = user.email ?: "—"
        binding.tvUserName.text = user.displayName
            ?: user.email?.substringBefore("@")
                    ?: "Usuario"
        binding.tvNombreCompleto.text = user.displayName ?: "—"
    }

    private fun setupClickListeners() {
        binding.btnLogout.setOnClickListener {
            confirmLogout()
        }
    }

    /** Flujo de seguridad: confirmación explícita antes de cerrar sesión. */
    private fun confirmLogout() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cerrar sesión")
            .setMessage("¿Estás seguro de que deseas cerrar tu sesión?")
            .setPositiveButton("Sí, cerrar sesión") { _, _ ->
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(requireActivity(), MainActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

                requireActivity().finish()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun loadProfile() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModel.loadProfile(uid)
    }

    private fun observeProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.accountState.collect { state ->
                    when (state) {
                        is ResponseService.Success -> {
                            val data = state.data
                            val firstName = data["firstName"]?.toString().orEmpty()
                            val lastName = data["lastName"]?.toString().orEmpty()
                            val fullName = "$firstName $lastName".trim()

                            if (fullName.isNotEmpty()) {
                                binding.tvUserName.text = fullName
                                binding.tvNombreCompleto.text = fullName
                            }
                            binding.tvPhone.text =
                                data["phone"]?.toString()?.ifBlank { "—" } ?: "—"
                            binding.tvBirthDate.text =
                                data["birthDate"]?.toString()?.ifBlank { "—" } ?: "—"
                        }
                        is ResponseService.Error -> {
                            // Perfil opcional: mantenemos los datos de Firebase Auth
                            binding.tvPhone.text = "—"
                            binding.tvBirthDate.text = "—"
                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
