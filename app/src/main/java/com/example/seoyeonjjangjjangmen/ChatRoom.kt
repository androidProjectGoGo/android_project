package com.example.seoyeonjjangjjangmen

import java.io.Serializable

data class ChatRoom(
    val users: Map<String, Boolean>? = HashMap(),
    var messages: Map<String,Message>? = HashMap()
) : Serializable {
}

data class Message(
    var senderUid: String = "",
    var sended_date: String = "",
    var content: String = "",
    var confirmed:Boolean=false
) : Serializable {
}