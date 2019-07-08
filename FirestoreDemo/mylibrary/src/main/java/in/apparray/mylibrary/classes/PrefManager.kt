package `in`.apparray.mylibrary.classes

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by umang on 6/21/2017.
 */

class PrefManager(// Context
        internal var _context: Context) {

    // Shared Preferences
    internal var pref: SharedPreferences

    // Editor for Shared preferences
    internal var editor: SharedPreferences.Editor

    // Shared pref mode
    internal var PRIVATE_MODE = 0


    init {
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    fun setSharedPref(key: String, value: Int) {
        editor.putInt(key, value)
        editor.commit()
    }

    fun setSharedPref(key: String, value: Long) {
        editor.putLong(key, value)
        editor.commit()
    }

    fun setSharedPref(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.commit()
    }

    fun getSharedPref(key: String, dValue: Int): Int {
        return pref.getInt(key, dValue)
    }

    fun getSharedPref(key: String, dValue: Long): Long {
        return pref.getLong(key, dValue)
    }

    fun getSharedPref(key: String, dValue: Boolean): Boolean {
        return pref.getBoolean(key, dValue)
    }

    fun setSharedPref(key: String, value: String) {
        editor.putString(key, value)
        editor.commit()
    }

    fun getSharedPref(key: String): String {
        return pref.getString(key, "")
    }

    companion object {

        // Shared preferences file name
        private val PREF_NAME = "PRFAaAF"
    }

}
