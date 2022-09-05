package dev.harshdalwadi.nasaapp.di.modules

import dev.harshdalwadi.nasaapp.di.components.AppComponent
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module(
    includes = [
        AppComponent::class,
        DataUtilityModule::class,
    ]
)
@InstallIn(SingletonComponent::class)
abstract class AppModule