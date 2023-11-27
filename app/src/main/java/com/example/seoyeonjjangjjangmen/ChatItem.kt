package com.example.seoyeonjjangjjangmen

import com.google.firebase.Timestamp

data class ChatItem(
    val sender: String? = null,
    val content: String? = null,
    val time: String? = null
) {
    constructor() : this("", "", null)
}