package com.example.grouponproduceapp.models

import java.util.UUID


data class Product(
    var productId: String = UUID.randomUUID().toString(),
    var productTitle: String? = null,
    var productQty: Int? = null,
    var productUnit: String? = null,
    var productPrice: Int? = null,
    var productStock: Int? = null,
    var productCategory: String? = null,
//    var productType: String? = null,
    var itemCount: Int? = null,
    var adminUid: String? = null,
    var productImgsUri: ArrayList<String?>? = null,
    )

