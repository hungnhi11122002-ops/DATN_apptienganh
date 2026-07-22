package com.example.EnglishWithStork

import android.content.Context

class SessionManager(context: Context) {

    private val sharedPreferences =
        context.applicationContext.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

    fun saveUserId(userId: Int) {
        sharedPreferences.edit()
            .putInt(KEY_USER_ID, userId)
            .apply()
    }

    fun getUserId(): Int {
        return sharedPreferences.getInt(KEY_USER_ID, -1)
    }

    fun clearSession() {
        sharedPreferences.edit()
            .remove(KEY_USER_ID)
            .apply()
    }

    companion object {
        private const val PREF_NAME = "english_with_stork_session"
        private const val KEY_USER_ID = "logged_in_user_id"
    }
}