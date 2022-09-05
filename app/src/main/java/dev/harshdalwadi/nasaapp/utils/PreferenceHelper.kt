package dev.harshdalwadi.nasaapp.utils


import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class PreferenceHelper @Inject constructor(context: Context) {

    private var editor: SharedPreferences.Editor? = null
    private val prefs: SharedPreferences

    init {
        prefs = context.getSharedPreferences(Prefs.PREF_FILE, Context.MODE_PRIVATE)
    }

    fun clearAllPrefs() {
        prefs.edit().clear().apply()
    }

    fun clearPreference(string: String) {
        prefs.edit().remove(string).apply()
    }

    @SuppressLint("CommitPrefEdits")
    fun initPref() {
        editor = prefs.edit()
    }

    fun applyPref() {
        editor!!.apply()
    }

    fun saveStringPref(key: String, value: String) {
        editor!!.putString(key, value)
    }

    fun loadStringPref(key: String, defaultValue: String): String? {
        return prefs.getString(key, defaultValue)
    }

    fun loadStringPref(key: String): String? {
        return prefs.getString(key, "")
    }

    fun loadAuthKey(): String? {
        return prefs.getString(Prefs.AUTH, "")
    }

    @SuppressLint("CommitPrefEdits")
    fun saveAuthKey(string: String) {
        prefs.apply {
            edit()
                .putString(Prefs.AUTH, string)
                .apply()
        }
    }

    fun saveIntPref(key: String, value: Int) {
        editor!!.putInt(key, value)
    }

    fun saveLongPref(key: String, value: Long) {
        editor!!.putLong(key, value)
    }

    fun loadLongPref(key: String, defaultValue: Long): Long {
        return try {
            prefs.getLong(key, defaultValue)
        } catch (e: ClassCastException) {
            prefs.getInt(key, defaultValue.toInt()).toLong()
        }
    }

    fun loadLongPref(key: String): Long {
        return prefs.getLong(key, 0)
    }

    fun loadIntPref(key: String, defaultValue: Int): Int {
        return prefs.getInt(key, defaultValue)
    }

    fun loadIntPref(key: String): Int {
        return prefs.getInt(key, 0)
    }

    fun saveBooleanPref(key: String, value: Boolean) {
        editor!!.putBoolean(key, value)
    }

    fun loadBooleanPref(key: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    fun saveSetPref(key: String, value: Set<String>) {
        editor!!.putStringSet(key, value)
    }

    fun loadSetPref(key: String, value: Set<String>): Set<String>? {
        return prefs.getStringSet(key, value)
    }
}