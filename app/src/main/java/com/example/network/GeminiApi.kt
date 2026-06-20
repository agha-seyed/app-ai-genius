package com.example.network

import com.example.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel

object GeminiClient {
    val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-3.1-pro",
            apiKey = BuildConfig.GEMINI_API_KEY
        )
    }
}
