package com.example.ai

import com.example.network.GeminiClient
import com.example.network.OpenAiCompatibleApi
import com.example.network.OpenAiMessage
import com.example.network.OpenAiRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

class ApiException(message: String) : Exception(message)

interface AiContentGenerator {
    suspend fun generateContent(prompt: String): String
}

@Singleton
class GeminiGeneratorImpl @Inject constructor() : AiContentGenerator {
    override suspend fun generateContent(prompt: String): String = withContext(Dispatchers.IO) {
        val response = GeminiClient.generativeModel.generateContent(prompt)
        response.text ?: throw ApiException("سرویس هوش مصنوعی (Gemini) پاسخی نداد. لطفاً مجدداً تلاش کنید.")
    }
}

class OpenAiCompatibleGeneratorImpl(
    private val api: OpenAiCompatibleApi,
    private val baseUrl: String,
    private val apiKey: String,
    private val modelName: String,
    private val systemPrompt: String
) : AiContentGenerator {
    override suspend fun generateContent(prompt: String): String = withContext(Dispatchers.IO) {
        val messages = mutableListOf<OpenAiMessage>()
        if (systemPrompt.isNotBlank()) {
            messages.add(OpenAiMessage(role = "system", content = systemPrompt))
        }
        messages.add(OpenAiMessage(role = "user", content = prompt))
        
        val request = OpenAiRequest(
            model = modelName,
            messages = messages
        )
        
        val response = api.generateContent(
            url = baseUrl,
            authHeader = "Bearer $apiKey",
            request = request
        )
        
        response.choices?.firstOrNull()?.message?.content 
            ?: throw ApiException("سرویس هوش مصنوعی ($modelName) پاسخی نداد. لطفاً کلید API را بررسی کرده یا مجدداً تلاش کنید.")
    }
}
