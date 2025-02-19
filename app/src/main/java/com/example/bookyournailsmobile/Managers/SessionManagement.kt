package com.example.bookyournailsmobile.Managers

import android.content.Context
import android.content.SharedPreferences
class SessionManagement(context: Context) {
    private var sharedPreferences: SharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun saveSession(userId: String) { // Change parameter type to String
        editor.putString("USER_ID", userId).commit()
    }

    fun getSession(): String? { // Change return type to String?
        return sharedPreferences.getString("USER_ID", null)
    }

    fun clearSession() {
        editor.clear().commit()
    }
}