package com.webstudio.nextext

import java.util.Date

data class ChatPreview(
    val userName: String = "",
    val mobile: String = "",
    val lastMessage: String = "",
    val timestamp: Date? = null,
    val profileImage: String? = null
)
