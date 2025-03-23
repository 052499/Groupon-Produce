package com.example.grouponproduceapp.models

//data class CartItem(
//    val userId: String,
//    val itemId: String,
//    var quantity: Int
//)

// CartItem data class for easy storage in SharedPreferences
data class CartItem(val productId: String, var quantity: Int)

//data class Cart(
//    val items: MutableMap<String, Int> // itemId -> quantity
//)

