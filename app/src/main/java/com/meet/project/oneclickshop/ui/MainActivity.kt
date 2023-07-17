package com.meet.project.oneclickshop.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.meet.project.oneclickshop.R
import com.meet.project.oneclickshop.databinding.ActivityMainBinding
import com.meet.project.oneclickshop.utils.BaseFragment

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private var navHostFragment: NavHostFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupNavigation()
    }

    private fun setupNavigation() {
        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment?
        navController = navHostFragment?.navController!!
    }

    override fun onBackPressed() {
        navHostFragment?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let { fragment ->
                if (fragment is BaseFragment && fragment.onBackPressed())
                    return
            }
        }

        if (!navController.navigateUp())
            super.onBackPressed()
    }

}