package com.example.bodega_flow.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("bodega_session", Context.MODE_PRIVATE)

    fun saveSession(user: AuthResponse) {
        prefs.edit()
            .putLong("user_id", user.id)
            .putString("user_nombre", user.nombre)
            .putString("user_username", user.username)
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getLong("user_id", -1L) > 0
    }

    fun getUser(): AuthResponse? {
        val id = prefs.getLong("user_id", -1L)
        if (id <= 0) return null
        val nombre = prefs.getString("user_nombre", null) ?: return null
        val username = prefs.getString("user_username", null) ?: return null
        return AuthResponse(id = id, nombre = nombre, username = username)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
