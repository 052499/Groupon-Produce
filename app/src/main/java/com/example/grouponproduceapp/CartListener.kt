package com.app.growceries



interface CartListener {
    fun showCartLayout(itemCount: Int)
    fun savingCartItemsCount(itemCount: Int)
}