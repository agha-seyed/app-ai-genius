package com.example.presentation

import android.app.Application
import android.os.Bundle
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ProjectEntity
import com.example.domain.repository.ProjectRepository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow

import com.example.ai.AiProviderFactory
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.preferences.UserSettings
import kotlinx.coroutines.flow.first

import com.example.domain.usecases.GenerateContentStrategyUseCase
import com.example.presentation.util.toUserFriendlyMessage
import com.example.domain.usecases.RefineContentUseCase
import com.example.domain.usecases.HandleVoiceCommandUseCase
import com.example.domain.usecases.ParseProjectResultUseCase
import com.example.domain.usecases.ParsedProjectResult

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContentViewModel @Inject constructor(
    application: Application,
    private val repository: ProjectRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val generateContentStrategyUseCase: GenerateContentStrategyUseCase,
    private val refineContentUseCase: RefineContentUseCase,
    private val handleVoiceCommandUseCase: HandleVoiceCommandUseCase,
    private val parseProjectResultUseCase: ParseProjectResultUseCase
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

    private val _parsedProjectState = MutableStateFlow(ParsedProjectResult())
    val parsedProjectState = _parsedProjectState.asStateFlow()

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

    fun loadParsedProjectDetails(project: ProjectEntity) {
        viewModelScope.launch {
            val result = parseProjectResultUseCase(project.resultText, project.topic)
            _parsedProjectState.value = result
        }
    }

    private fun handleVoiceCommand(text: String) {
        viewModelScope.launch {
            _voiceState.value = VoiceState.Processing
            try {
                val settings = preferencesRepository.userSettingsFlow.first()
                if (settings.selectedTtsVoice.isNotEmpty()) {
                    voiceManager?.setVoice(settings.selectedTtsVoice)
                }
                
                val response = handleVoiceCommandUseCase(text, settings)
                _voiceState.value = VoiceState.Success(response)
                speak(response)
            } catch (e: Exception) {
                _voiceState.value = VoiceState.Error(e.toUserFriendlyMessage())
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
                val settings = preferencesRepository.userSettingsFlow.first()
                val result = generateContentStrategyUseCase(project, settings)
                
                val newProjectId = repository.insertProject(project.copy(resultText = result))
                _uiState.value = UiState.Success(newProjectId.toInt())
                
            } catch (e: Exception) {
                Log.e("ContentViewModel", "Error generating content", e)
                _uiState.value = UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }
    
    fun refineContent(text: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val settings = preferencesRepository.userSettingsFlow.first()
                val result = refineContentUseCase(text, settings)
                _uiState.value = UiState.RefinedSuccess(result)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.toUserFriendlyMessage())
            }
        }
    }

    fun clearState() {
        _uiState.value = UiState.Idle
    }

    fun exportAudio(text: String, outputFile: java.io.File) {
        voiceManager?.exportAudio(text, outputFile)
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
