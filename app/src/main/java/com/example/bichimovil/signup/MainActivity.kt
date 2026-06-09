package com.example.bichimovil.signup

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import com.example.bichimovil.R
import com.example.bichimovil.core.FragmentCommunicator
import com.example.bichimovil.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), FragmentCommunicator, AuthFragmentCommunicator {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializar NavGraph si es primera vez
        if (savedInstanceState == null) {
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
            val navController = navHostFragment.navController
            navController.navigate(R.id.signInFragment)
        }
    }

    override fun manageLoader(isVisible: Boolean) {
        binding.loaderView.isVisible = isVisible
    }

    override fun goToSignUp() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(R.id.signUpFragment)
    }

    override fun goToSignIn() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        // Limpiar back stack y volver a Sign In
        navController.popBackStack(R.id.signInFragment, false)
        if (navController.currentDestination?.id != R.id.signInFragment) {
            navController.navigate(R.id.signInFragment)
        }
    }

    override fun goToForgotPassword() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(R.id.forgotPasswordFragment)
    }

    override fun goToPersonalInfo(email: String, password: String) {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        val bundle = Bundle().apply {
            putString("email", email)
            putString("password", password)
        }
        navController.navigate(R.id.personalInfoFragment, bundle)
    }
}