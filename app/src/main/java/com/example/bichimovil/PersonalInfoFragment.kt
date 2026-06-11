package com.example.bichimovil

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.bichimovil.databinding.FragmentPersonalInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Captura los datos personales durante el registro y los GUARDA:
 * - displayName en Firebase Auth
 * - perfil completo en Firestore users/{uid}
 * Después se muestran en la pantalla de Cuenta.
 */
class PersonalInfoFragment : Fragment() {

    private var _binding: FragmentPersonalInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBirthDatePicker()

        binding.btnFinish.setOnClickListener {
            saveProfileAndContinue()
        }

        binding.ivBackPersonal.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun saveProfileAndContinue() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "Sesión no válida", Toast.LENGTH_SHORT).show()
            return
        }

        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val birthDate = binding.etBirthDate.text.toString().trim()

        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Ingresa tu nombre y apellidos",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        binding.btnFinish.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // 1) displayName en Firebase Auth
                user.updateProfile(
                    userProfileChangeRequest {
                        displayName = "$firstName $lastName"
                    }
                ).await()

                // 2) Perfil en Firestore users/{uid}
                val profile = mapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "username" to username,
                    "phone" to phone,
                    "birthDate" to birthDate,
                    "email" to (user.email ?: "")
                )
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(user.uid)
                    .set(profile)
                    .await()
            } catch (e: Exception) {
                // No bloquea el flujo: el displayName/email seguirán disponibles
            }

            Toast.makeText(requireContext(), "¡Registro completado!", Toast.LENGTH_SHORT)
                .show()

            val intent = Intent(
                requireActivity(),
                com.example.bichimovil.home.HomeActivity::class.java
            )
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun setupBirthDatePicker() {
        binding.etBirthDate.setOnClickListener {
            val calendar = Calendar.getInstance()

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val birthDate = String.format(
                        "%02d/%02d/%04d",
                        selectedDay,
                        selectedMonth + 1,
                        selectedYear
                    )
                    binding.etBirthDate.setText(birthDate)
                },
                year, month, day
            )
            datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
            datePickerDialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
