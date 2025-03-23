package com.example.grouponproduceapp.models

data class CartItemWithDetails(
    val productId: String,
    var quantity: Int,
    val productName: String?,
    val productPrice: Double,
    val productImgUri: String?
)
