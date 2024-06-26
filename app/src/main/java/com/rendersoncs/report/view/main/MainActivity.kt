package com.rendersoncs.report.view.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.rendersoncs.report.R
import com.rendersoncs.report.common.constants.ReportConstants
import com.rendersoncs.report.common.extension.spaceToNewLine
import com.rendersoncs.report.databinding.ActivityMainBinding
import com.rendersoncs.report.view.fragment.ChooseThemeDialogFragment.SingleChoiceListener
import com.rendersoncs.report.view.login.LoginActivity
import com.rendersoncs.report.view.login.util.LibraryClass
import com.rendersoncs.report.view.viewmodel.ReportViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SingleChoiceListener {

    lateinit var binding: ActivityMainBinding

    private var uiStateJobName: Job? = null
    private var uiStateJobEmail: Job? = null
    private var uiStateJobPhoto: Job? = null

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var prefTheme: SharedPreferences

    private lateinit var mFireBaseAnalytics: FirebaseAnalytics
    private var authStateListener: FirebaseAuth.AuthStateListener? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    private val viewModel: ReportViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkLoggedUser()

        prefTheme = getSharedPreferences(ReportConstants.THEME.MY_PREFERENCE_THEME, MODE_PRIVATE)

        val user = initFireBaseUser()
        user?.let {
            viewModel.getUserUid(it)
            viewModel.getInfoUserFireBase(it, databaseReference)
        }

        initViews(binding)
        getInfoUser(binding)
        observeNavElements(binding, navHostFragment.navController)
    }

    private fun checkLoggedUser() {
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            if (firebaseAuth.currentUser == null) {
                Intent(this, LoginActivity::class.java).apply {
                    startActivity(this)
                    finish()
                }
            }
        }
    }

    private fun initFireBaseUser(): FirebaseUser? {
        authStateListener?.let { auth ->
            mFireBaseAnalytics = FirebaseAnalytics.getInstance(applicationContext)
            firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.addAuthStateListener(auth)
            databaseReference = LibraryClass.getFirebase()
            return firebaseAuth.currentUser
        }
        return null
    }

    private fun observeNavElements(
            binding: ActivityMainBinding,
            navController: NavController
    ) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            when (destination.id) {
                R.id.reportActivity -> {
                    supportActionBar?.setDisplayShowTitleEnabled(true)
                    supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    binding.homeMain.toolbar.title = getString(R.string.label_menu_new_report)
                }
                R.id.addNewReport -> {
                    binding.homeMain.toolbar.navigationIcon =
                        ContextCompat.getDrawable(this, R.drawable.ic_back_black)
                    supportActionBar?.setDisplayShowTitleEnabled(true)
                }
                R.id.resumeReport -> {
                    binding.homeMain.toolbar.navigationIcon =
                        ContextCompat.getDrawable(this, R.drawable.ic_back_black)
                    supportActionBar?.setDisplayShowTitleEnabled(true)
                }
                R.id.updatePassword -> {
                    supportActionBar?.setDisplayShowTitleEnabled(false)
                }
                R.id.removeUser -> {
                    supportActionBar?.setDisplayShowTitleEnabled(false)
                }
                R.id.about -> {
                    supportActionBar?.setDisplayShowTitleEnabled(true)
                    supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    binding.homeMain.toolbar.title = getString(R.string.label_menu_about)
                }
                else -> {
                    supportActionBar?.setDisplayShowTitleEnabled(true)
                }
            }
        }
    }

    private fun initViews(binding: ActivityMainBinding) {
        setSupportActionBar(binding.homeMain.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
                ?: return

        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.dashboardFragment), binding.drawerLayout)
        with(navHostFragment.navController) {
            setupActionBarWithNavController(this, appBarConfiguration)
            binding.navView.setupWithNavController(this)
        }

        binding.navView.menu.findItem(R.id.login).setOnMenuItemClickListener {
            FirebaseAuth.getInstance().signOut()
            viewModel.deletePreference()
            true
        }
    }

    private fun getInfoUser(binding: ActivityMainBinding) {
        val header = binding.navView.getHeaderView(0)

        uiStateJobPhoto = lifecycleScope.launchWhenStarted {
            viewModel.photo.collect {
                val photo = header.findViewById<ImageView>(R.id.img_profile)
                Glide.with(applicationContext).load(it).into(photo)
            }
        }

        uiStateJobName = lifecycleScope.launchWhenStarted {
            viewModel.name.collect {
                val name = header.findViewById<TextView>(R.id.txt_profile_name)
                val partName = it.split(" ")

                val firstName = partName.firstOrNull()
                val secondName = if (partName.size > 1) partName.lastOrNull() else ""
                val fullName = "$firstName $secondName"
                name.text = fullName.spaceToNewLine()
            }
        }

        uiStateJobEmail = lifecycleScope.launchWhenStarted {
            viewModel.email.collect {
                val email = header.findViewById<TextView>(R.id.txt_profile_mail)
                email.text = it
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navHostFragment.navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START))
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        else {
            super.onBackPressed()
        }
    }

    override fun onPositiveButtonClicked(list: Array<String>, position: Int) {
        if (position == 0)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        recreate()
        viewModel.saveThemeState(prefTheme, position)
    }

    override fun onNegativeButtonClicked() {}

    override fun onDestroy() {
        super.onDestroy()
        destroyState()
    }

    private fun destroyState() {
        uiStateJobName?.cancel()
        uiStateJobPhoto?.cancel()
        uiStateJobEmail?.cancel()
    }
}