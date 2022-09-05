package dev.harshdalwadi.nasaapp.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import dev.harshdalwadi.nasaapp.utils.SingleLiveEvent


abstract class BaseViewModel constructor(context: Application) : AndroidViewModel(context) {

    val msgObserver = MutableLiveData<String>()
    val isLoadingObserver = MutableLiveData<Boolean>()

    val error = MutableLiveData<Throwable>()

    val backPressedObserver = SingleLiveEvent<Boolean>()
    fun onBackClick() {
        backPressedObserver.value = true
    }

    val navClickObserver = SingleLiveEvent<Boolean>()
    fun onNavClick() {
        navClickObserver.value = true
    }

    protected fun setLoading(isLoading: Boolean) {
        this.isLoadingObserver.postValue(isLoading)
    }

}
