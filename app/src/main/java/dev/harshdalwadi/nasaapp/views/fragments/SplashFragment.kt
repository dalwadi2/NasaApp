package dev.harshdalwadi.nasaapp.views.fragments

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.harshdalwadi.nasaapp.R
import dev.harshdalwadi.nasaapp.base.BaseFragment
import dev.harshdalwadi.nasaapp.databinding.FragmentSplashBinding

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {
    private var isLogin: Boolean = false
    override fun findContentView() = R.layout.fragment_splash

    lateinit var mBinding: FragmentSplashBinding

    override fun onReady() {
        mBinding = getViewDataBinding()


        Handler(Looper.getMainLooper()).postDelayed({
            redirectToHome()
        }, 2000)

    }

    private fun redirectToHome() {
        lifecycleScope.launchWhenResumed {
//            navigate(SplashFragmentDirections.actionSplashFragmentToHomeFragment())
        }
    }

    override fun networkError(throwable: Throwable?) {
        showErrorSnackBar()
    }

    override fun setViewModelObservers() {

    }
}