package dev.harshdalwadi.nasaapp.di.modules

import android.app.Application
import dev.harshdalwadi.nasaapp.BuildConfig
import dev.harshdalwadi.nasaapp.utils.AppDebugTree
import dev.harshdalwadi.nasaapp.utils.PreferenceHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class DataUtilityModule {

    init {
        initTimber(BuildConfig.DEBUG)
    }

    @Singleton
    @Provides
    fun providePreference(application: Application): PreferenceHelper {
        return PreferenceHelper(application.baseContext)
    }


    private fun initTimber(showLogs: Boolean) {
        Timber.uprootAll()
        if (showLogs) {
            Timber.plant(AppDebugTree())
        }
    }
}