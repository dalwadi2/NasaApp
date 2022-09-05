package dev.harshdalwadi.nasaapp.viewModels

import android.app.Application
import dev.harshdalwadi.nasaapp.api.ApiServices
import dev.harshdalwadi.nasaapp.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val apiServices: ApiServices, context: Application) : BaseViewModel(context) {

}