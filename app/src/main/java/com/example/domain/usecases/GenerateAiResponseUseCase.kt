package com.example.domain.usecases

import com.example.ai.AiProviderFactory
import com.example.data.preferences.UserSettings
import javax.inject.Inject

class GenerateAiResponseUseCase @Inject constructor(
    private val aiProviderFactory: AiProviderFactory
) {
    suspend operator fun invoke(prompt: String, settings: UserSettings): String {
         val generator = aiProviderFactory.getGenerator(settings)
         return generator.generateContent(prompt)
    }
}
