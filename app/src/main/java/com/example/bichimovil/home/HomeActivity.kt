package com.example.bichimovil.home

import android.os.Bundle
import android.view.View
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
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)
    }

    override fun manageLoader(show: Boolean) {
        // Si quieres agregar un loader global aquí después, lo conectas aquí
    }
}