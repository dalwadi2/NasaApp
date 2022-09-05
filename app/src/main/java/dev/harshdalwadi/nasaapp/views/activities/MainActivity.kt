package dev.harshdalwadi.nasaapp.views.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.harshdalwadi.nasaapp.R
import dev.harshdalwadi.nasaapp.base.BaseActivity
import dev.harshdalwadi.nasaapp.databinding.ActivityMainBinding
import dev.harshdalwadi.nasaapp.viewModels.MainViewModel

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val mainViewModel: MainViewModel by viewModels()

    lateinit var mBinding: ActivityMainBinding

    override fun findContentView(): Int = R.layout.activity_main

    private lateinit var appBarConfiguration: AppBarConfiguration

    val navController by lazy {
        findNavController(R.id.nav_graph)
    }

    private val TAG = MainActivity::class.java.simpleName

    override fun onReady(savedInstanceState: Bundle?) {
        mBinding = getViewDataBinding()
        setViewModelObservers()
        setSupportActionBar(mBinding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

    }

    private fun setViewModelObservers() {

    }

    override fun networkError(throwable: Throwable?) {
        hideLoader()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

}