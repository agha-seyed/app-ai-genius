package com.example.domain.usecases

import com.example.data.preferences.UserSettings
import javax.inject.Inject

class HandleVoiceCommandUseCase @Inject constructor(
    private val generateAiResponseUseCase: GenerateAiResponseUseCase
) {
    suspend operator fun invoke(text: String, settings: UserSettings): String {
        val systemPrompt = settings.systemPrompt
        val prompt = "$systemPrompt\n\nRespond briefly to the following query: $text"
        return generateAiResponseUseCase(prompt, settings)
    }
}
