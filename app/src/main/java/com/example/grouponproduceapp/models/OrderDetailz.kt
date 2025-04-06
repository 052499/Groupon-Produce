package com.example.grouponproduceapp.models

import java.util.Date

data class OrderDetailz(
    val orderId: String = "",
    val userId: String = "",
    val adminId : String = "",
    val orderPrice: Int = 0,
    val orderDate: Long = 0L,
    val orderStatus: String = "processing",
    val orderDetails: List<OrderedItemDetails> = emptyList()
){
    fun getOrderDateAsDate(): Date {
        return Date(orderDate)  // Convert Long timestamp to Date
    }
}

data class OrderedItemDetails(
    val orderId: String = "",
    val productId: String = "",
    val adminId: String? = null,
    val productName: String? = "",
    val productQty: Int = 0,
    val productTotalPrice: Int = 0,
    val productImgUri: String? = null,
)
