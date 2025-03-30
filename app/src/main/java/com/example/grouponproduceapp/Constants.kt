package com.example.grouponproduceapp

import android.content.Context
import android.content.SharedPreferences
import com.example.grouponproduceapp.R

object Constants {
    val allCategories = arrayOf(
        "vegetables",
        "fruits",
        "dairy",
        "meat",
    )
    val allUnits = arrayOf(
        "kg",
        "ltr",
        "pcs",
        "gm",
        "ml",

    )
//    val allTypes = arrayOf(
//        "type i",
//        "type ii",
//        "type iii",
//    )

    val allCategoriesIcons = arrayOf (
        R.drawable.vegetables,
        R.drawable.fruit,
        R.drawable.dairy,
        R.drawable.meal,
    )


    fun saveUserType(context: Context, userType: String) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("USER_TYPE", userType)
        editor.apply() // Asynchronously saves the data
    }

    fun getUserType(context: Context): String? {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("USER_TYPE", null) // Default is null if not found
    }
}