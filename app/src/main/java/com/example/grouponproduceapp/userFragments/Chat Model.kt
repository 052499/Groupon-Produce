package com.example.grouponproduceapp.userFragments

data class ChatModel(
    var id: String? = null,
    var message: String? = null,
    var date: Long,
    var senderId: String? = null,
    var senderName: String? = null,
    var receiverId: String? = null,
    var receiverName: String? = null,
    var readStatus: Map<String, Boolean>? = null
)
