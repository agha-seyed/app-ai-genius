package com.example.domain.usecases

import com.example.data.preferences.UserSettings
import javax.inject.Inject

class RefineContentUseCase @Inject constructor(
    private val generateAiResponseUseCase: GenerateAiResponseUseCase
) {
    suspend operator fun invoke(text: String, settings: UserSettings): String {
        val prompt = "این متن استراتژی محتوا را بهینه‌سازی کن و از نظر نگارشی و جذابیت ارتقا بده. به زبان فارسی پاسخ بده:\\n$text"
        return generateAiResponseUseCase(prompt, settings)
    }
}
