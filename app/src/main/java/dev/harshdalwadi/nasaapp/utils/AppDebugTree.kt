package dev.harshdalwadi.nasaapp.utils

import timber.log.Timber

// Reference: https://stackoverflow.com/questions/38689399/log-method-name-and-line-number-in-timber

class AppDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String {
        return "(${element.fileName}:${element.lineNumber})#${element.methodName}"
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, LOG_PREFIX + tag, message, t)
    }

    companion object {
        private const val LOG_PREFIX = "myApp/"
    }
}