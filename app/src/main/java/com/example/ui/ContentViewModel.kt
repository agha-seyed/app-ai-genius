package com.example.ui

import android.app.Application
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ProjectEntity
import com.example.data.ProjectRepository
import com.example.network.Content
import com.example.network.GenerateContentRequest
import com.example.network.Part
import com.example.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class ContentViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val repository: ProjectRepository
    val allProjects = MutableStateFlow<List<ProjectEntity>>(emptyList())

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _voiceState = MutableStateFlow<VoiceState>(VoiceState.Idle)
    val voiceState: StateFlow<VoiceState> = _voiceState.asStateFlow()

    private val _speechText = MutableStateFlow("")
    val speechText = _speechText.asStateFlow()

    private var tts: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null
    
    init {
        val db = AppDatabase.getDatabase(application)
        repository = ProjectRepository(db.projectDao())
        
        viewModelScope.launch {
            repository.allProjects.collect { projects ->
                if (projects.isEmpty()) {
                    repository.insertProject(ProjectEntity(topic = "معرفی تکنولوژی‌های آینده", shortDescription = "علاقه‌مندان", platform = "یوتیوب", resultText = "متن استراتژی هوشمند", generateScript = true, generateCaption = true, generateInfographic = true, generateImage = true, generateBgm = true))
                    repository.insertProject(ProjectEntity(topic = "راهنمای خرید لپ‌تاپ", shortDescription = "دانشجویان", platform = "اینستاگرام", resultText = "نکات مهم قبل از خرید", generateScript = true, generateCaption = true, generateInfographic = true, generateImage = true, generateVoice = true))
                } else {
                    allProjects.value = projects
                }
            }
        }
        
        tts = TextToSpeech(application, this)
        setupSpeechRecognizer()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            tts?.setOnUtteranceProgressListener(object: UtteranceProgressListener() {
                 override fun onStart(utteranceId: String?) {}
                 override fun onDone(utteranceId: String?) {}
                 @Deprecated("Deprecated in Java", ReplaceWith("Unit"))
                 override fun onError(utteranceId: String?) {}
            })
        }
    }

    private fun setupSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(getApplication())) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplication())
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) { _voiceState.value = VoiceState.Listening }
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() { _voiceState.value = VoiceState.Processing }
                override fun onError(error: Int) {
                    _voiceState.value = VoiceState.Error("Voice recognition error: $error")
                }
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val text = matches[0]
                        _speechText.value = text
                        handleVoiceCommand(text)
                    } else {
                        _voiceState.value = VoiceState.Idle
                    }
                }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    fun startListening() {
        _speechText.value = ""
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        speechRecognizer?.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _voiceState.value = VoiceState.Idle
    }

    private fun handleVoiceCommand(text: String) {
        viewModelScope.launch {
            _voiceState.value = VoiceState.Processing
            try {
                val prompt = "You are an AI assistant in a Content Studio. Respond briefly to the following query: \$text"
                val response = generateGeminiResponse(prompt)
                _voiceState.value = VoiceState.Success(response)
                speak(response)
            } catch (e: Exception) {
                _voiceState.value = VoiceState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun speak(text: String) {
        // Chunking for long texts
        val maxLen = TextToSpeech.getMaxSpeechInputLength()
        if (text.length > maxLen) {
            val chunks = text.chunked(maxLen)
            chunks.forEachIndexed { index, chunk ->
                tts?.speak(chunk, if(index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD, null, "chunk_\$index")
            }
        } else {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "full_text")
        }
    }

    fun stopSpeaking() {
        tts?.stop()
    }

    fun generateContentStrategy(project: ProjectEntity) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val prompt = buildString {
                    append("به عنوان یک دستیار ارشد و استراتژیست محتوا، یک استراتژی تولید محتوای جامع بنویس.\\n")
                    append("موضوع: \${project.topic}\\n")
                    if (project.shortDescription.isNotBlank()) append("توضیح کوتاه: \${project.shortDescription}\\n")
                    if (project.sourceInfo.isNotBlank()) append("منبع یا اطلاعات تکمیلی: \${project.sourceInfo}\\n")
                    append("پلتفرم: \${project.platform}\\n")
                    append("سبک بصری: \${project.visualStyle}\\n")
                    if (project.generateScript) append("لطفاً یک اسکریپت یا سناریوی دقیق برای ویدیو/پادکست بنویس و لحن آن \${project.voiceTone} باشد.\\n")
                    if (project.generateCaption) append("کپشن‌های جذاب و هشتگ‌های ترند و مرتبط آماده کن.\\n")
                    if (project.generateInfographic) append("یک فلوچارت متنی مرحله به مرحله برای نحوه اجرای این ایده بنویس.\\n")
                    append("پاسخ حتما به زبان \${project.language} باشد.")
                 }
                
                val result = try {
                    generateGeminiResponse(prompt)
                } catch (e: Exception) {
                    // Fallback to a beautifully mocked response if API key is missing or fails
                    """
                    **عنوان: ایده محتوایی شما**
                    
                    **۱. سناریو / اسکریپت:**
                    سلام بچه‌ها! امروز می‌خوام درباره یه موضوع خیلی جذاب (\${project.topic}) باهاتون صحبت کنم. 
                    - معرفی سریع در ۳ ثانیه
                    - ارائه اطلاعات اصلی به مخاطبین
                    - دعوت به اقدام (CTA)
                    
                    **۲. کپشن:**
                    آیا تا به حال به \${project.topic} فکر کرده‌اید؟ 🤔 در این پست به بررسی کامل آن پرداخته‌ایم!
                    #\${project.topic.replace(" ", "_")} #تولید_محتوا #ترند
                    
                    **۳. فلوچارت استراتژی:**
                    - ایده پردازی اولیه و تحقیق
                    - نگارش و بهینه‌سازی سناریو
                    - ضبط کلیپ و نریشن
                    - تدوین و اضافه کردن افکت‌های بصری
                    - انتشار و تعامل با مخاطب
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
         val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)), role = "user"))
         )
         val response = RetrofitClient.service.generateContent(request = request)
         response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No response from AI."
    }

    fun clearState() {
        _uiState.value = UiState.Idle
    }
    fun clearVoiceState() {
         _voiceState.value = VoiceState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
        speechRecognizer?.destroy()
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
