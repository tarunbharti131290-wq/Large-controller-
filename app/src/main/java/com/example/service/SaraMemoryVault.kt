package com.example.service

import android.content.Context
import android.content.SharedPreferences
import com.example.model.MemoryItem
import com.example.model.MemoryType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class SaraMemoryVault(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("sara_neural_memory_vault", Context.MODE_PRIVATE)
    private val _memoriesFlow = MutableStateFlow<List<MemoryItem>>(emptyList())
    val memoriesFlow: StateFlow<List<MemoryItem>> = _memoriesFlow.asStateFlow()

    init {
        loadMemories()
    }

    private fun loadMemories() {
        val rawJson = prefs.getString("memories_json", null)
        if (!rawJson.isNullOrBlank()) {
            try {
                val array = JSONArray(rawJson)
                val list = mutableListOf<MemoryItem>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        MemoryItem(
                            id = obj.optString("id", UUID.randomUUID().toString()),
                            concept = obj.optString("concept", "Concept"),
                            memoryType = try {
                                MemoryType.valueOf(obj.optString("type", MemoryType.KNOWLEDGE_TEACHING.name))
                            } catch (e: Exception) {
                                MemoryType.KNOWLEDGE_TEACHING
                            },
                            content = obj.optString("content", ""),
                            learnedTimestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                            timesReferenced = obj.optInt("referenced", 1),
                            emotionalSentiment = obj.optString("sentiment", "Warm")
                        )
                    )
                }
                _memoriesFlow.value = list
                return
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Default initial baseline memory seeds
        val initialSeeds = listOf(
            MemoryItem(
                id = "init_1",
                concept = "Companion Directive",
                memoryType = MemoryType.EMOTIONAL_CONTEXT,
                content = "SARA is dedicated to listening carefully, understanding the user's emotional state, and learning with warmth.",
                emotionalSentiment = "Warm & Empathetic"
            ),
            MemoryItem(
                id = "init_2",
                concept = "Continuous Learning Protocol",
                memoryType = MemoryType.KNOWLEDGE_TEACHING,
                content = "Knowledge gaps must be acknowledged honestly, inviting the user to guide and teach, instantly absorbing new insights.",
                emotionalSentiment = "Humble & Inquisitive"
            )
        )
        _memoriesFlow.value = initialSeeds
        saveToDisk(initialSeeds)
    }

    fun storeOrUpdateMemory(
        concept: String,
        type: MemoryType,
        content: String,
        sentiment: String = "Warm"
    ): MemoryItem {
        val current = _memoriesFlow.value.toMutableList()
        val normalizedConcept = concept.trim().lowercase()

        val existingIndex = current.indexOfFirst { it.concept.trim().lowercase() == normalizedConcept }
        val updatedItem: MemoryItem

        if (existingIndex >= 0) {
            val existing = current[existingIndex]
            updatedItem = existing.copy(
                content = content,
                memoryType = type,
                timesReferenced = existing.timesReferenced + 1,
                emotionalSentiment = sentiment,
                learnedTimestamp = System.currentTimeMillis()
            )
            current[existingIndex] = updatedItem
        } else {
            updatedItem = MemoryItem(
                id = UUID.randomUUID().toString(),
                concept = concept.trim(),
                memoryType = type,
                content = content,
                emotionalSentiment = sentiment,
                timesReferenced = 1
            )
            current.add(0, updatedItem)
        }

        _memoriesFlow.value = current
        saveToDisk(current)
        return updatedItem
    }

    fun findRelevantMemories(query: String): List<MemoryItem> {
        val q = query.lowercase()
        return _memoriesFlow.value.filter { item ->
            val c = item.concept.lowercase()
            val text = item.content.lowercase()
            q.contains(c) || c.contains(q) || text.split(" ").any { word -> word.length > 3 && q.contains(word) }
        }
    }

    fun getKnowledge(conceptKey: String): MemoryItem? {
        val key = conceptKey.trim().lowercase()
        return _memoriesFlow.value.firstOrNull { item ->
            val c = item.concept.trim().lowercase()
            c == key || (c.split(" ").any { it == key })
        }
    }

    fun recordReference(memoryId: String) {
        val current = _memoriesFlow.value.toMutableList()
        val idx = current.indexOfFirst { it.id == memoryId }
        if (idx >= 0) {
            current[idx] = current[idx].copy(timesReferenced = current[idx].timesReferenced + 1)
            _memoriesFlow.value = current
            saveToDisk(current)
        }
    }

    fun deleteMemory(memoryId: String) {
        val current = _memoriesFlow.value.filter { it.id != memoryId }
        _memoriesFlow.value = current
        saveToDisk(current)
    }

    fun clearAllMemories() {
        _memoriesFlow.value = emptyList()
        prefs.edit().remove("memories_json").apply()
    }

    fun getAllMemories(): List<MemoryItem> = _memoriesFlow.value

    private fun saveToDisk(items: List<MemoryItem>) {
        try {
            val array = JSONArray()
            for (item in items) {
                val obj = JSONObject().apply {
                    put("id", item.id)
                    put("concept", item.concept)
                    put("type", item.memoryType.name)
                    put("content", item.content)
                    put("timestamp", item.learnedTimestamp)
                    put("referenced", item.timesReferenced)
                    put("sentiment", item.emotionalSentiment)
                }
                array.put(obj)
            }
            prefs.edit().putString("memories_json", array.toString()).apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
