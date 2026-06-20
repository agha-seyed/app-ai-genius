package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

enum class AiProviderType {
    GOOGLE_GEMINI, OPEN_ROUTER, GROQ
}

data class UserSettings(
    val activeProvider: AiProviderType,
    val openRouterApiKey: String,
    val openRouterModel: String,
    val groqApiKey: String,
    val groqModel: String,
    val systemPrompt: String
)

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val ACTIVE_PROVIDER = stringPreferencesKey("active_provider")
    private val OPENROUTER_API_KEY = stringPreferencesKey("openrouter_api_key")
    private val OPENROUTER_MODEL = stringPreferencesKey("openrouter_model")
    private val GROQ_API_KEY = stringPreferencesKey("groq_api_key")
    private val GROQ_MODEL = stringPreferencesKey("groq_model")
    private val SYSTEM_PROMPT = stringPreferencesKey("system_prompt")

    val userSettingsFlow: Flow<UserSettings> = dataStore.data.map { preferences ->
        val providerStr = preferences[ACTIVE_PROVIDER] ?: AiProviderType.GOOGLE_GEMINI.name
        val provider = try {
            AiProviderType.valueOf(providerStr)
        } catch (e: Exception) {
            AiProviderType.GOOGLE_GEMINI
        }
        
        UserSettings(
            activeProvider = provider,
            openRouterApiKey = preferences[OPENROUTER_API_KEY] ?: "",
            openRouterModel = preferences[OPENROUTER_MODEL] ?: "deepseek/deepseek-chat:free",
            groqApiKey = preferences[GROQ_API_KEY] ?: "",
            groqModel = preferences[GROQ_MODEL] ?: "llama3-8b-8192",
            systemPrompt = preferences[SYSTEM_PROMPT] ?: "You are an AI assistant in a Content Studio."
        )
    }

    suspend fun updateActiveProvider(provider: AiProviderType) {
        dataStore.edit { it[ACTIVE_PROVIDER] = provider.name }
    }

    suspend fun updateOpenRouterSettings(apiKey: String, model: String) {
        dataStore.edit {
            it[OPENROUTER_API_KEY] = apiKey
            it[OPENROUTER_MODEL] = model
        }
    }

    suspend fun updateGroqSettings(apiKey: String, model: String) {
        dataStore.edit {
            it[GROQ_API_KEY] = apiKey
            it[GROQ_MODEL] = model
        }
    }

    suspend fun updateSystemPrompt(prompt: String) {
        dataStore.edit { it[SYSTEM_PROMPT] = prompt }
    }
}
