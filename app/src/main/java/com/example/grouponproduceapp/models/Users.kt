package com.example.grouponproduceapp.models



data class Users(
    var uid: String = "",
    val userName: String? = null,
    val userEmail: String? = null,
    val userPassword: String? =null,
    val userAddress: String? = null,
    val userType: String? = null
)
