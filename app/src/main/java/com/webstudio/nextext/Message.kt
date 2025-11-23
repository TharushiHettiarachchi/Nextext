package com.webstudio.nextext

import com.google.firebase.Timestamp

data class Message(
    val sender: String = "",
    val receiver: String = "",
    val message: String = "",
    val timestamp: Timestamp? = null
)
