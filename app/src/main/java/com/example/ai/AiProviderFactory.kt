package com.example.ai

import com.example.data.preferences.AiProviderType
import com.example.data.preferences.UserSettings
import com.example.network.OpenAiCompatibleApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiProviderFactory @Inject constructor(
    private val geminiGeneratorImpl: GeminiGeneratorImpl,
    private val openAiCompatibleApi: OpenAiCompatibleApi
) {
    fun getGenerator(settings: UserSettings): AiContentGenerator {
        return when (settings.activeProvider) {
            AiProviderType.GOOGLE_GEMINI -> geminiGeneratorImpl
            AiProviderType.OPEN_ROUTER -> {
                OpenAiCompatibleGeneratorImpl(
                    api = openAiCompatibleApi,
                    baseUrl = "https://openrouter.ai/api/v1/chat/completions",
                    apiKey = settings.openRouterApiKey,
                    modelName = settings.openRouterModel,
                    systemPrompt = settings.systemPrompt
                )
            }
            AiProviderType.GROQ -> {
                OpenAiCompatibleGeneratorImpl(
                    api = openAiCompatibleApi,
                    baseUrl = "https://api.groq.com/openai/v1/chat/completions",
                    apiKey = settings.groqApiKey,
                    modelName = settings.groqModel,
                    systemPrompt = settings.systemPrompt
                )
            }
        }
    }
}
