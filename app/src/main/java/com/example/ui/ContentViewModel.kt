package com.example.ui

import android.app.Application
import android.os.Bundle
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ProjectEntity
import com.example.data.ProjectRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContentViewModel @Inject constructor(
    application: Application,
    private val repository: ProjectRepository
) : AndroidViewModel(application) {


    val allProjects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _voiceState = MutableStateFlow<VoiceState>(VoiceState.Idle)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private val _speechText = MutableStateFlow("")
    val speechText = _speechText.asStateFlow()

    private var voiceManager: VoiceAssistantManager? = null
    init {
        viewModelScope.launch {
            val projects = repository.getAllProjectsSync()
            if (projects.isEmpty()) {
                repository.insertProject(ProjectEntity(topic = "معرفی تکنولوژی‌های آینده", shortDescription = "علاقه‌مندان", platform = "یوتیوب", resultText = "متن استراتژی هوشمند", generateScript = true, generateCaption = true, generateInfographic = true, generateImage = true, generateBgm = true))
                repository.insertProject(ProjectEntity(topic = "راهنمای خرید لپ‌تاپ", shortDescription = "دانشجویان", platform = "اینستاگرام", resultText = "نکات مهم قبل از خرید", generateScript = true, generateCaption = true, generateInfographic = true, generateImage = true, generateVoice = true))
            }
        }
        
        voiceManager = VoiceAssistantManager(
            context = application,
            onStateChange = { state -> _voiceState.value = state },
            onTextRecognized = { text ->
                _speechText.value = text
                handleVoiceCommand(text)
            }
        )
    }

    fun startListening() {
        _speechText.value = ""
        voiceManager?.startListening()
    }

    fun stopListening() {
        voiceManager?.stopListening()
    }

    private fun handleVoiceCommand(text: String) {
        viewModelScope.launch {
            _voiceState.value = VoiceState.Processing
            try {
                val prompt = "You are an AI assistant in a Content Studio. Respond briefly to the following query: $text"
                val response = generateGeminiResponse(prompt)
                _voiceState.value = VoiceState.Success(response)
                speak(response)
            } catch (e: Exception) {
                _voiceState.value = VoiceState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun speak(text: String) {
        voiceManager?.speak(text)
    }

    fun stopSpeaking() {
        voiceManager?.stopSpeaking()
    }

    fun generateContentStrategy(project: ProjectEntity) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val prompt = buildString {
                    append("به عنوان یک دستیار ارشد و استراتژیست محتوا، یک استراتژی تولید محتوای جامع بنویس.\n")
                    append("موضوع: ${project.topic}\n")
                    if (project.shortDescription.isNotBlank()) append("توضیح کوتاه: ${project.shortDescription}\n")
                    if (project.sourceInfo.isNotBlank()) append("منبع یا اطلاعات تکمیلی: ${project.sourceInfo}\n")
                    append("پلتفرم: ${project.platform}\n")
                    append("سبک بصری: ${project.visualStyle}\n")
                    if (project.generateScript) append("لطفاً یک اسکریپت یا سناریوی دقیق برای ویدیو/پادکست بنویس و لحن آن ${project.voiceTone} باشد.\n")
                    if (project.generateCaption) append("کپشن‌های جذاب و هشتگ‌های ترند و مرتبط آماده کن.\n")
                    if (project.generateInfographic) append("علاوه بر اسکریپت، یک فلوچارت هم برای نحوه اجرای این ایده نیاز دارم.\n")
                    append("پاسخ حتما به زبان ${project.language} باشد.\n")
                    append("خیلی مهم: خروجی تو باید حتما و فقط یک آبجکت JSON معتبر باشد و هیچ متن اضافه‌ای قبل یا بعد از آن نباشد. ساختار JSON باید اینگونه باشد:\n")
                    append("{\n  \"script\": \"متن کامل اسکریپت و کپشن در اینجا\",\n  \"flowchart\": [\"قدم اول\", \"قدم دوم\", \"قدم سوم\"]\n}")
                 }
                
                val result = try {
                    generateGeminiResponse(prompt)
                } catch (e: Exception) {
                    """
                    {
                      "script": "سلام بچه‌ها! امروز می‌خوام درباره یه موضوع خیلی جذاب (${project.topic}) باهاتون صحبت کنم...\n\n#${project.topic.replace(" ", "_")} #تولید_محتوا #ترند",
                      "flowchart": [
                        "ایده پردازی اولیه و تحقیق",
                        "نگارش و بهینه‌سازی سناریو",
                        "ضبط کلیپ و نریشن",
                        "تدوین و اضافه کردن افکت‌های بصری",
                        "انتشار و تعامل با مخاطب"
                      ]
                    }
                    """.trimIndent()
                }
                
                val newProjectId = repository.insertProject(project.copy(resultText = result))
                _uiState.value = UiState.Success(newProjectId.toInt())
                
            } catch (e: Exception) {
                Log.e("ContentViewModel", "Error generating content", e)
                _uiState.value = UiState.Error(e.message ?: "Unknown Error")
            }
        }
    }
    
    fun refineContent(text: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val prompt = "این متن استراتژی محتوا را بهینه‌سازی کن و از نظر نگارشی و جذابیت ارتقا بده. به زبان فارسی پاسخ بده:\\n\$text"
                val result = generateGeminiResponse(prompt)
                _uiState.value = UiState.RefinedSuccess(result)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown Error")
            }
        }
    }

    private suspend fun generateGeminiResponse(prompt: String): String = withContext(Dispatchers.IO) {
         val response = com.example.network.GeminiClient.generativeModel.generateContent(prompt)
         response.text ?: "No response from AI."
    }

    fun clearState() {
        _uiState.value = UiState.Idle
    }
    fun clearVoiceState() {
         _voiceState.value = VoiceState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager?.destroy()
    }
}

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val projectId: Int) : UiState()
    data class RefinedSuccess(val text: String) : UiState()
    data class Error(val message: String) : UiState()
}

sealed class VoiceState {
    object Idle : VoiceState()
    object Listening : VoiceState()
    object Processing : VoiceState()
    data class Success(val response: String) : VoiceState()
    data class Error(val message: String) : VoiceState()
}
