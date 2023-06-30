package com.rendersoncs.report.view.login.loginV2

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.rendersoncs.report.R
import com.rendersoncs.report.databinding.ActivityMainLoginBinding
import com.rendersoncs.report.common.util.viewModelFactory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginMainActivity: AppCompatActivity() {

    private lateinit var navHost: NavHostFragment
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val viewModel: LoginViewModel by viewModels {
        viewModelFactory { LoginViewModel(this.application) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel
        initViews(binding)
        observeNavElements(binding, navHost.navController)
    }

    private fun initViews(binding: ActivityMainLoginBinding) {
        setSupportActionBar(binding.toolbarLogin)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        navHost = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_login) as NavHostFragment?
            ?: return

        with(navHost.navController) {
            appBarConfiguration = AppBarConfiguration(graph)
            setupActionBarWithNavController(this, appBarConfiguration)
        }
    }

    private fun observeNavElements(
        binding: ActivityMainLoginBinding,
        navController: NavController
    ) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeLogin -> {
                    supportActionBar!!.setDisplayShowTitleEnabled(false)
                }
                R.id.signUp -> {
                    supportActionBar!!.setDisplayShowTitleEnabled(false)
                }
                R.id.recovery -> {
                    supportActionBar!!.setDisplayShowTitleEnabled(false)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navHost.navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}