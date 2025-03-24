package com.example.grouponproduceapp



interface CartListener {
    fun showCartLayout(itemCount: Int)
    fun savingCartItemsCount(itemCount: Int)
}