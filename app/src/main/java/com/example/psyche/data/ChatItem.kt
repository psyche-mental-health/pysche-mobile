package com.example.psyche.data

data class ChatItem(
    val text: String,
    val isQuestion: Boolean,
    val isResultCard: Boolean = false
)