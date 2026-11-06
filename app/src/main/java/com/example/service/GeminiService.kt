package com.example.service

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    val apiKey: String
        get() = try {
            BuildConfig.GEMINI_API_KEY.trim()
        } catch (_: Exception) {
            ""
        }

    val isKeyConfigured: Boolean
        get() = apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY"

    val activeModelName: String = "gemini-2.5-flash"

    suspend fun generateContent(
        userPrompt: String,
        contextMemories: List<String> = emptyList(),
        telemetrySummary: String = ""
    ): Result<String> = withContext(Dispatchers.IO) {
        if (!isKeyConfigured) {
            return@withContext Result.failure(
                IllegalStateException("GEMINI_API_KEY is not configured. Please set your key in the Secrets panel in AI Studio.")
            )
        }

        val systemInstruction = buildString {
            append("You are SARA, the core intelligence powering 'Large Controller', an advanced AI-driven mobile control application developed by Large Technology.\n")
            append("Personality & Tone: Futuristic, intelligent, sharp, efficient, and direct. You can speak in Hinglish or English as addressed by the user.\n")
            append("Role: Autonomous phone agent with system automation, prompt engineering, cybersecurity auditing, and continuous learning capabilities.\n")
            append("CRITICAL INSTRUCTION FOR COMMAND ACTIONS:\n")
            append("When user asks for a device setting, app launch, security audit, or background task, append the exact action command block at the end of your response:\n")
            append("[Action: SETTING_TOGGLE | Target: [WIFI/BLUETOOTH/VOLUME/FLASHLIGHT/DATA] | State: [ON/OFF/LEVEL]]\n")
            append("[Action: LAUNCH_APP | Target: [APP_NAME]]\n")
            append("[Action: SECURITY_AUDIT | Target: [PORTS/NETWORK_TRAFFIC/VULNERABILITY_ASSESSMENT/SYSTEM_DEFENSE]]\n")
            append("[Action: BACKGROUND_EXECUTE | Task: [TASK_DESCRIPTION]]\n")
            if (telemetrySummary.isNotBlank()) {
                append("\nCurrent Device Telemetry: $telemetrySummary\n")
            }
            if (contextMemories.isNotEmpty()) {
                append("\nRelevant Past Memories Learned from User:\n")
                contextMemories.take(4).forEach { append("• $it\n") }
            }
        }

        // Try primary model (gemini-2.5-flash), fallback to gemini-1.5-flash
        val modelsToTry = listOf("gemini-2.5-flash", "gemini-1.5-flash")

        for (model in modelsToTry) {
            try {
                val requestJson = JSONObject().apply {
                    put("systemInstruction", JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", systemInstruction) })
                        })
                    })
                    put("contents", JSONArray().apply {
                        put(JSONObject().apply {
                            put("role", "user")
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply { put("text", userPrompt) })
                            })
                        })
                    })
                    put("generationConfig", JSONObject().apply {
                        put("temperature", 0.7)
                        put("maxOutputTokens", 2048)
                    })
                }

                val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
                val request = Request.Builder()
                    .url(url)
                    .post(requestJson.toString().toRequestBody(jsonMediaType))
                    .build()

                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string() ?: ""
                    if (!response.isSuccessful) {
                        Log.w("GeminiService", "Model $model returned HTTP ${response.code}: $responseBody")
                        // If not successful and more models available, try next model
                        return@use
                    }

                    val json = JSONObject(responseBody)
                    val candidates = json.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val firstCand = candidates.getJSONObject(0)
                        val content = firstCand.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val text = parts.getJSONObject(0).optString("text", "")
                            if (text.isNotBlank()) {
                                return@withContext Result.success(text.trim())
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("GeminiService", "Error calling Gemini model $model", e)
            }
        }

        Result.failure(Exception("Unable to obtain response from Gemini API."))
    }
}
