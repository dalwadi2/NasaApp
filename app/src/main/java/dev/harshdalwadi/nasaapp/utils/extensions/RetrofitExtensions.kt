package dev.harshdalwadi.nasaapp.utils.extensions

import dev.harshdalwadi.nasaapp.api.Resource
import dev.harshdalwadi.nasaapp.base.BaseFragment
import dev.harshdalwadi.nasaapp.utils.CoroutineDispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.withContext

suspend fun <T> apiCall(dispatchers: CoroutineDispatchers, apiCall: suspend () -> T): T {
    return withContext(dispatchers.network) {
        apiCall.invoke()
    }
}

suspend fun <T> resultApiCall(dispatchers: CoroutineDispatchers, apiCall: suspend () -> T): Result<T> {
    return runCatching { apiCall(dispatchers, apiCall) }
}

suspend fun <T> SharedFlow<Resource<T>>.customCollect(
    showLoaderAlways: Boolean = true,
    baseFragment: BaseFragment<*>,
    onSuccess: T.() -> Unit,
    onFailure: T.() -> Unit,
) {
    this.collect {
        when (it) {
            is Resource.Success -> {
                if (showLoaderAlways) {
                    baseFragment.hideLoader()
                }
                onSuccess(it.data!!)
            }
            is Resource.Error -> {
                if (showLoaderAlways) {
                    baseFragment.hideLoader()
                }
                baseFragment.networkError(it.throwable)
            }
            is Resource.Loading -> {
                if (showLoaderAlways) {
                    baseFragment.showLoader()
                }
            }
            Resource.Idle -> {}
        }
    }
}