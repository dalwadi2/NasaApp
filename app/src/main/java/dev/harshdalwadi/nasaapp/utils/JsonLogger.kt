package dev.harshdalwadi.nasaapp.utils

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.io.IOException

class JsonLogger : HttpLoggingInterceptor.Logger {

    override fun log(message: String) {
        if (message.startsWith("{")) {
            val mapper = ObjectMapper()
            val json: Any

            try {
                json = mapper.readValue(message, Any::class.java)
                val prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json)
                Timber.d(prettyJson)
            } catch (e: IOException) {
                // Non-JSON logging
                Timber.e(e, message)
            }
        } else {
            // Non-JSON logging
            Timber.d(message)
        }
    }
}
