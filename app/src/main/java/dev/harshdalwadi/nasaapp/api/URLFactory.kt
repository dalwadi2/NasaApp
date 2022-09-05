package dev.harshdalwadi.nasaapp.api

import dev.harshdalwadi.nasaapp.BuildConfig
import okhttp3.HttpUrl

object URLFactory {

    private var SCHEME = "https"
    private lateinit var HOST: String
    private lateinit var API_PATH: String
    const val URL_TESTING = "https://drive.google.com"
    const val URL_PROD = ""

    fun provideHttpUrl(): HttpUrl {
        val httpUrl = HttpUrl.Builder()
        when (BuildConfig.BUILD_TYPE) {
            "release" -> {
                SCHEME = "https"
                HOST = URL_PROD
                API_PATH = "a/obvious.in/file/d/"
            }
            else -> {
                SCHEME = "https"
                HOST = URL_TESTING
                API_PATH = ""
            }
        }
        return httpUrl
            .scheme(SCHEME)
            .host(HOST)
            .addPathSegments(API_PATH)
            .build()
    }

    /*------------------------- **************** ------------------------------------*/

    const val EP_FETCH_NASA_DATA = "18t-LzVG7bxu-oPxJQZg8P49I9UHcA552/view?usp=sharing"

    /*------------------------- **************** ------------------------------------*/
}

