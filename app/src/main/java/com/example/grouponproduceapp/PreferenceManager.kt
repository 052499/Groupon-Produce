package com.example.grouponproduceapp

import android.content.SharedPreferences
import android.content.Context

class PreferenceManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    // Save userType (admin or user)
    fun saveUserType(userType: String) {
        sharedPreferences.edit().putString("USER_TYPE", userType).apply()
    }

    // Get userType
    fun getUserType(): String? {
        return sharedPreferences.getString("USER_TYPE", null)
    }

    // Clear userType
    fun clearUserType() {
        sharedPreferences.edit().remove("USER_TYPE").apply()
    }
}