package com.example.bichimovil.home

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.bichimovil.R
import com.example.bichimovil.core.FragmentCommunicator
import com.example.bichimovil.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity(), FragmentCommunicator {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("HomeActivity", "🏠 onCreate iniciado")

        try {
            // Inflar binding
            binding = ActivityHomeBinding.inflate(layoutInflater)
            setContentView(binding.root)
            Log.d("HomeActivity", "✅ ContentView seteado")

            // Obtener NavHostFragment de forma segura
            val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            Log.d("HomeActivity", "🔍 Fragment obtenido: ${fragment?.javaClass?.simpleName}")

            when (fragment) {
                is NavHostFragment -> {
                    Log.d("HomeActivity", "✅ NavHostFragment es correcto")
                    val navController = fragment.navController
                    Log.d("HomeActivity", "✅ NavController obtenido")

                    binding.bottomNavigation.setupWithNavController(navController)
                    Log.d("HomeActivity", "✅ BottomNav setup exitoso")
                }
                else -> {
                    Log.e("HomeActivity", "❌ Fragment incorrecto o no encontrado")
                    Toast.makeText(
                        this,
                        "Error: NavHostFragment no encontrado",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } catch (e: ClassCastException) {
            Log.e("HomeActivity", " ClassCastException: ${e.message}", e)
            e.printStackTrace()
            Toast.makeText(
                this,
                "Error de casting: ${e.localizedMessage}",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Log.e("HomeActivity", " Exception: ${e.message}", e)
            e.printStackTrace()
            Toast.makeText(
                this,
                "Error al iniciar: ${e.localizedMessage}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun manageLoader(show: Boolean) {
        // Si quieres agregar un loader global aquí después, lo conectas aquí
    }
}