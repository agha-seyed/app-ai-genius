package com.example.domain.usecases

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.inject.Inject

data class ParsedProjectResult(
    val script: String = "",
    val flowchart: List<String> = emptyList(),
    val imagePrompt: String = ""
)

class ParseProjectResultUseCase @Inject constructor() {
    suspend operator fun invoke(resultText: String?, defaultTopic: String = ""): ParsedProjectResult = withContext(Dispatchers.Default) {
        if (resultText.isNullOrBlank()) return@withContext ParsedProjectResult(script = "", flowchart = emptyList(), imagePrompt = defaultTopic)
        
        val cleanText = resultText.removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
        
        try {
            val json = JSONObject(cleanText)
            val script = json.optString("script", cleanText)
            
            val array = json.optJSONArray("flowchart")
            val steps = mutableListOf<String>()
            if (array != null) {
                for (i in 0 until array.length()) steps.add(array.getString(i))
            }
            
            val imagePrompt = json.optString("image_prompt", defaultTopic)
            
            ParsedProjectResult(script, steps, imagePrompt)
        } catch (e: Exception) {
            val fallbackScript = resultText.replace(Regex("[\"\\{\\}\\[\\]]"), "").trim()
            ParsedProjectResult(fallbackScript, emptyList(), defaultTopic)
        }
    }
}
