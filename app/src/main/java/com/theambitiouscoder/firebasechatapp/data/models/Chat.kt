package com.theambitiouscoder.firebasechatapp.data.models

data class Chat(
    var senderId: String = "",
    var receiverId: String = "",
    var message: String = ""
)