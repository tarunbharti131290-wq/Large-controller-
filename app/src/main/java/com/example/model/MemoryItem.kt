package com.example.model

enum class MemoryType {
    KNOWLEDGE_TEACHING,
    PERSONAL_EVENT,
    USER_PREFERENCE,
    EMOTIONAL_CONTEXT,
    VOCABULARY_STYLE
}

data class MemoryItem(
    val id: String,
    val concept: String,
    val memoryType: MemoryType,
    val content: String,
    val learnedTimestamp: Long = System.currentTimeMillis(),
    val timesReferenced: Int = 1,
    val emotionalSentiment: String = "Neutral"
)
