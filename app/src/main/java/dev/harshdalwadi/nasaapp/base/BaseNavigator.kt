package dev.harshdalwadi.nasaapp.base

import retrofit2.HttpException
import java.net.SocketTimeoutException

interface BaseNavigator {
    fun error(throwable: Throwable?) {
        if (throwable != null) {
            if (throwable is HttpException) {
                if (throwable.code() == 401) {
                    authError(throwable)
                    return
                } else if (throwable.code() > 499) {
                    serverError(throwable)
                    return
                }
            } else if (throwable is SocketTimeoutException) {
                networkError(throwable)
                return
            }
            networkError(throwable)
        }
    }

    fun serverError(throwable: HttpException)
    fun authError(throwable: Throwable?)
    fun networkError(throwable: Throwable? = null)
    fun handleErrorCode(errorCode: Int?)
    fun noInternetConnection()
//    fun updateApp(message: String, url: String)

}