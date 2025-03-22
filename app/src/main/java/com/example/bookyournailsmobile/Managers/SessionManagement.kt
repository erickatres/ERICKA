package com.example.bookyournailsmobile.Managers

import android.content.Context
import android.content.SharedPreferences

class SessionManagement(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("SessionPref", Context.MODE_PRIVATE)

    // Save the session token and user ID
    fun saveSession(userId: String, sessionToken: String) {
        val editor = sharedPreferences.edit()
        editor.putString("user_id", userId)
        editor.putString("session_token", sessionToken)
        editor.apply()
    }

    // Retrieve the session token
    fun getSessionToken(): String? {
        return sharedPreferences.getString("session_token", null)
    }

    // Retrieve the user ID
    fun getUserId(): String? {
        return sharedPreferences.getString("user_id", null)
    }

    // Clear the session (for logout)
    fun clearSession() {
        val editor = sharedPreferences.edit()
        editor.remove("user_id")
        editor.remove("session_token")
        editor.apply()
    }
}