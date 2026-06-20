package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.preferences.AiProviderType
import com.example.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val userSettings = preferencesRepository.userSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun updateActiveProvider(provider: AiProviderType) {
        viewModelScope.launch {
            preferencesRepository.updateActiveProvider(provider)
        }
    }

    fun updateOpenRouterSettings(apiKey: String, model: String) {
        viewModelScope.launch {
            preferencesRepository.updateOpenRouterSettings(apiKey, model)
        }
    }

    fun updateGroqSettings(apiKey: String, model: String) {
        viewModelScope.launch {
            preferencesRepository.updateGroqSettings(apiKey, model)
        }
    }

    fun updateSystemPrompt(prompt: String) {
        viewModelScope.launch {
            preferencesRepository.updateSystemPrompt(prompt)
        }
    }

    fun updateTtsVoice(voiceName: String) {
        viewModelScope.launch {
            preferencesRepository.updateTtsVoice(voiceName)
        }
    }
}
