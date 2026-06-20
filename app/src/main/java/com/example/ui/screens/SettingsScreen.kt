package com.example.ui.screens

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
import com.example.data.preferences.AiProviderType
import com.example.ui.SettingsViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.userSettings.collectAsState()

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
                title = { Text("تنظیمات هوش مصنوعی") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E1E1E),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        if (settings == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF00FFCC))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Provider Selection
                Text("سرویس‌دهنده فعال", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ProviderChip(
                        name = "Google Gemini",
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

                // OpenRouter Settings
                if (settings!!.activeProvider == AiProviderType.OPEN_ROUTER) {
                    SettingSection("تنظیمات OpenRouter") {
                        OutlinedTextField(
                            value = openRouterApiKey,
                            onValueChange = { openRouterApiKey = it },
                            label = { Text("API Key") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.LightGray,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                        OutlinedTextField(
                            value = openRouterModel,
                            onValueChange = { openRouterModel = it },
                            label = { Text("نام مدل (مثل deepseek/deepseek-chat)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.LightGray,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                        Button(
                            onClick = { viewModel.updateOpenRouterSettings(openRouterApiKey, openRouterModel) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FFCC))
                        ) {
                            Text("ذخیره تنظیمات OpenRouter", color = Color.Black)
                        }
                    }
                }

                // Groq Settings
                if (settings!!.activeProvider == AiProviderType.GROQ) {
                    SettingSection("تنظیمات Groq") {
                        OutlinedTextField(
                            value = groqApiKey,
                            onValueChange = { groqApiKey = it },
                            label = { Text("API Key") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.LightGray,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                        OutlinedTextField(
                            value = groqModel,
                            onValueChange = { groqModel = it },
                            label = { Text("نام مدل (مثل llama3-8b-8192)") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.LightGray,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                        Button(
                            onClick = { viewModel.updateGroqSettings(groqApiKey, groqModel) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FFCC))
                        ) {
                            Text("ذخیره تنظیمات Groq", color = Color.Black)
                        }
                    }
                }

                // Global Prompt
                SettingSection("دستورالعمل سیستم (System Prompt)") {
                    OutlinedTextField(
                        value = systemPrompt,
                        onValueChange = { systemPrompt = it },
                        label = { Text("نحوه رفتار هوش مصنوعی") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.LightGray,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                    Button(
                        onClick = { viewModel.updateSystemPrompt(systemPrompt) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FFCC))
                    ) {
                        Text("ذخیره دستورالعمل", color = Color.Black)
                    }
                }

                // Voice Settings
                SettingSection("صدا و دستیار صوتی (TTS)") {
                    var expanded by remember { mutableStateOf(false) }
                    
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = selectedVoice.ifEmpty { "صدای پیش‌فرض سیستم" },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("صدای دستیار صوتی") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = true },
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.LightGray,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                disabledTextColor = Color.White
                            ),
                            enabled = false // to make it clickable only
                        )
                        Box(modifier = Modifier.matchParentSize().clickable { expanded = true })
                    }
                    
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color(0xFF2C2C2C))
                    ) {
                        DropdownMenuItem(
                            text = { Text("صدای پیش‌فرض سیستم", color = Color.White) },
                            onClick = {
                                selectedVoice = ""
                                viewModel.updateTtsVoice("")
                                expanded = false
                            }
                        )
                        ttsVoices.forEach { voice ->
                            DropdownMenuItem(
                                text = { Text("${voice.name} (${voice.locale.displayName})", color = Color.White) },
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
            .background(if (isSelected) Color(0xFF00FFCC) else Color(0xFF2C2C2C))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = name,
            color = if (isSelected) Color.Black else Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun SettingSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E1E1E))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(title, color = Color(0xFF00FFCC), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        content()
    }
}
