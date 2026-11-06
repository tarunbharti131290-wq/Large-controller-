package com.example.model

enum class MessageSender {
    USER,
    SARA
}

data class ChatMessage(
    val id: String,
    val sender: MessageSender,
    val text: String,
    val action: CommandAction? = null,
    val actions: List<CommandAction> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false,
    val isActionExecuted: Boolean = false
) {
    fun getAllActions(): List<CommandAction> {
        return if (actions.isNotEmpty()) actions else listOfNotNull(action)
    }
}
