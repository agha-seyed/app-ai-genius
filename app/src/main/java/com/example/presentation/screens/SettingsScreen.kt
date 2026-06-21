package com.example.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import android.speech.tts.TextToSpeech
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.data.preferences.AiProviderType
import com.example.presentation.SettingsViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()

    var openRouterApiKey by remember { mutableStateOf("") }
    var openRouterModel by remember { mutableStateOf("") }
    var groqApiKey by remember { mutableStateOf("") }
    var groqModel by remember { mutableStateOf("") }
    var systemPrompt by remember { mutableStateOf("") }
    var selectedVoice by remember { mutableStateOf("") }

    val context = LocalContext.current
    var ttsVoices by remember { mutableStateOf<List<android.speech.tts.Voice>>(emptyList()) }

    DisposableEffect(context) {
        var tts: TextToSpeech? = null
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                try {
                    val voices = tts?.voices?.toList()?.filter { it.locale.language == "en" || it.locale.language == "fa" }
                    if (voices != null) {
                        ttsVoices = voices
                    }
                } catch (e: Exception) {
                    // Ignore
                }
            }
        }
        onDispose {
            tts?.shutdown()
        }
    }

    // Sync state once settings are loaded
    LaunchedEffect(settings) {
        settings?.let {
            openRouterApiKey = it.openRouterApiKey
            openRouterModel = it.openRouterModel
            groqApiKey = it.groqApiKey
            groqModel = it.groqModel
            systemPrompt = it.systemPrompt
            selectedVoice = it.selectedTtsVoice
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title), color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back_btn), tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (settings == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Provider Selection
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(stringResource(R.string.active_provider), color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ProviderChip(
                            name = "Gemini",
                            isSelected = settings!!.activeProvider == AiProviderType.GOOGLE_GEMINI,
                            onClick = { viewModel.updateActiveProvider(AiProviderType.GOOGLE_GEMINI) }
                        )
                        ProviderChip(
                            name = "OpenRouter",
                            isSelected = settings!!.activeProvider == AiProviderType.OPEN_ROUTER,
                            onClick = { viewModel.updateActiveProvider(AiProviderType.OPEN_ROUTER) }
                        )
                        ProviderChip(
                            name = "Groq",
                            isSelected = settings!!.activeProvider == AiProviderType.GROQ,
                            onClick = { viewModel.updateActiveProvider(AiProviderType.GROQ) }
                        )
                    }
                }

                // OpenRouter Settings
                if (settings!!.activeProvider == AiProviderType.OPEN_ROUTER) {
                    SettingSection(stringResource(R.string.settings_openrouter)) {
                        OutlinedTextField(
                            value = openRouterApiKey,
                            onValueChange = { openRouterApiKey = it },
                            label = { Text(stringResource(R.string.api_key_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = appTextFieldColors()
                        )
                        OutlinedTextField(
                            value = openRouterModel,
                            onValueChange = { openRouterModel = it },
                            label = { Text(stringResource(R.string.openrouter_model_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = appTextFieldColors()
                        )
                        Button(
                            onClick = { viewModel.updateOpenRouterSettings(openRouterApiKey, openRouterModel) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(stringResource(R.string.save_openrouter), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Groq Settings
                if (settings!!.activeProvider == AiProviderType.GROQ) {
                    SettingSection(stringResource(R.string.settings_groq)) {
                        OutlinedTextField(
                            value = groqApiKey,
                            onValueChange = { groqApiKey = it },
                            label = { Text(stringResource(R.string.api_key_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = appTextFieldColors()
                        )
                        OutlinedTextField(
                            value = groqModel,
                            onValueChange = { groqModel = it },
                            label = { Text(stringResource(R.string.groq_model_label)) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = appTextFieldColors()
                        )
                        Button(
                            onClick = { viewModel.updateGroqSettings(groqApiKey, groqModel) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(stringResource(R.string.save_groq), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Global Prompt
                SettingSection(stringResource(R.string.system_prompt_title)) {
                    OutlinedTextField(
                        value = systemPrompt,
                        onValueChange = { systemPrompt = it },
                        label = { Text(stringResource(R.string.system_prompt_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = appTextFieldColors()
                    )
                    Button(
                        onClick = { viewModel.updateSystemPrompt(systemPrompt) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.save_prompt), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                // Voice Settings
                SettingSection(stringResource(R.string.voice_settings_title)) {
                    var expanded by remember { mutableStateOf(false) }
                    
                    val defaultVoiceString = stringResource(R.string.default_system_voice)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedVoice.ifEmpty { defaultVoiceString },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.voice_label)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = true },
                            colors = appTextFieldColors(),
                            enabled = false
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { expanded = true })
                    }
                    
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.default_system_voice), color = MaterialTheme.colorScheme.onSurface) },
                            onClick = {
                                selectedVoice = ""
                                viewModel.updateTtsVoice("")
                                expanded = false
                            }
                        )
                        ttsVoices.forEach { voice ->
                            DropdownMenuItem(
                                text = { Text("${voice.name} (${voice.locale.displayName})", color = MaterialTheme.colorScheme.onSurface) },
                                onClick = {
                                    selectedVoice = voice.name
                                    viewModel.updateTtsVoice(voice.name)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProviderChip(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = name,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun SettingSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(title, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        content()
    }
}

@Composable
fun appTextFieldColors() = TextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    disabledTextColor = MaterialTheme.colorScheme.onSurface,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,
    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
    unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
)
