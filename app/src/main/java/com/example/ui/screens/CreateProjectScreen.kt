package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.ProjectEntity
import com.example.ui.ContentViewModel
import com.example.ui.UiState
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectScreen(
    viewModel: ContentViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var topic by remember { mutableStateOf("") }
    var shortDescription by remember { mutableStateOf("") }
    var sourceInfo by remember { mutableStateOf("") }
    var platform by remember { mutableStateOf("Instagram") }
    var visualStyle by remember { mutableStateOf("Cinematic") }

    var genScript by remember { mutableStateOf(true) }
    var genCaption by remember { mutableStateOf(true) }

    var genVoice by remember { mutableStateOf(false) }
    var genBgm by remember { mutableStateOf(false) }
    var voiceGender by remember { mutableStateOf("آقا") }
    var voiceTone by remember { mutableStateOf("حماسی") }

    var genImage by remember { mutableStateOf(false) }
    var frameCount by remember { mutableStateOf("4") }
    var genVideo by remember { mutableStateOf(false) }
    var genAnimatedTeaser by remember { mutableStateOf(false) }
    var videoStyle by remember { mutableStateOf("Cinematic") }

    var genInfographic by remember { mutableStateOf(true) }
    var language by remember { mutableStateOf("فارسی") }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is UiState.Success -> {
                viewModel.clearState()
                onNavigateToDetail(state.projectId)
            }
            is UiState.Error -> {
                Toast.makeText(context, context.getString(R.string.error_unknown), Toast.LENGTH_LONG).show()
                viewModel.clearState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.step1_title), color = GeoTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = GeoAmberLight)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent,
        bottomBar = {
            Box(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = {
                        val project = ProjectEntity(
                            topic = topic,
                            shortDescription = shortDescription,
                            sourceInfo = sourceInfo,
                            platform = platform,
                            visualStyle = visualStyle,
                            generateScript = genScript,
                            generateCaption = genCaption,
                            generateVoice = genVoice,
                            generateBgm = genBgm,
                            voiceGender = voiceGender,
                            voiceTone = voiceTone,
                            generateImage = genImage,
                            frameCount = frameCount.toIntOrNull() ?: 4,
                            generateVideo = genVideo,
                            generateAnimatedTeaser = genAnimatedTeaser,
                            videoStyle = videoStyle,
                            generateInfographic = genInfographic,
                            language = language
                        )
                        viewModel.generateContentStrategy(project)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(GeoGold, GeoAmberLight))), contentAlignment = Alignment.Center) {
                        if (uiState is UiState.Loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(stringResource(R.string.generate_content), color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            // Section 0: Personas
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("پرسونای آماده (Quick Setup)", style = MaterialTheme.typography.titleMedium, color = GeoAmberLight, fontWeight = FontWeight.Bold)
                androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    val personas = listOf(
                        "بلاگر تکنولوژی" to { platform = "YouTube"; visualStyle = "Cinematic"; voiceTone = "پرانرژی"; genVoice = true },
                        "مربی انگیزشی" to { platform = "Instagram"; visualStyle = "Minimalist"; voiceTone = "حماسی"; genVoice = true },
                        "طنزپرداز" to { platform = "TikTok"; visualStyle = "Vibrant"; voiceTone = "شاد و خنده‌دار"; genVoice = true }
                    )
                    items(personas.size) { i ->
                        val persona = personas[i]
                        Surface(
                            modifier = Modifier.clickable { persona.second() },
                            color = GeoGlassBg,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GeoGlassBorder)
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Person, "Persona", tint = GeoGold, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(persona.first, color = GeoTextPrimary, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }

            // Section 1: Basic Info
            GlassCard(cornerRadius = 16.dp) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(stringResource(R.string.section_basic_info), style = MaterialTheme.typography.titleMedium, color = GeoAmberLight, fontWeight = FontWeight.Bold)
                    
                    AppTextField(value = topic, onValueChange = { topic = it }, label = stringResource(R.string.topic_label))
                    AppTextField(value = shortDescription, onValueChange = { shortDescription = it }, label = stringResource(R.string.short_desc_label))
                    AppTextField(value = sourceInfo, onValueChange = { sourceInfo = it }, label = stringResource(R.string.source_info_label))
                    AppTextField(value = platform, onValueChange = { platform = it }, label = stringResource(R.string.platform_label))
                    AppTextField(value = visualStyle, onValueChange = { visualStyle = it }, label = stringResource(R.string.visual_style_label))
                }
            }
            
            // Section 2: Studio & Audio
            GlassCard(cornerRadius = 16.dp) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(stringResource(R.string.section_studio_audio), style = MaterialTheme.typography.titleMedium, color = GeoAmberLight, fontWeight = FontWeight.Bold)
                    
                    SettingsSwitch(stringResource(R.string.gen_voice), genVoice) { genVoice = it }
                    SettingsSwitch(stringResource(R.string.gen_bgm), genBgm) { genBgm = it }
                    
                    if (genVoice) {
                        AppTextField(value = voiceGender, onValueChange = { voiceGender = it }, label = stringResource(R.string.voice_gender))
                        AppTextField(value = voiceTone, onValueChange = { voiceTone = it }, label = stringResource(R.string.voice_tone))
                    }
                }
            }
            
            // Section 3: Visual & Media
            GlassCard(cornerRadius = 16.dp) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(stringResource(R.string.section_visual_media), style = MaterialTheme.typography.titleMedium, color = GeoAmberLight, fontWeight = FontWeight.Bold)
                    
                    SettingsSwitch(stringResource(R.string.gen_image), genImage) { genImage = it }
                    if (genImage) {
                         AppTextField(value = frameCount, onValueChange = { frameCount = it }, label = stringResource(R.string.frame_count))
                    }
                    
                    SettingsSwitch(stringResource(R.string.gen_video), genVideo) { genVideo = it }
                    SettingsSwitch(stringResource(R.string.gen_animated_teaser), genAnimatedTeaser) { genAnimatedTeaser = it }
                    
                    if (genVideo || genAnimatedTeaser) {
                        AppTextField(value = videoStyle, onValueChange = { videoStyle = it }, label = stringResource(R.string.video_style))
                    }
                    
                    Text(stringResource(R.string.google_api_note), style = MaterialTheme.typography.labelSmall, color = GeoAmber)
                }
            }
            
            // Section 4: Infographic
            GlassCard(cornerRadius = 16.dp) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(stringResource(R.string.section_infographic), style = MaterialTheme.typography.titleMedium, color = GeoAmberLight, fontWeight = FontWeight.Bold)
                    
                    SettingsSwitch(stringResource(R.string.gen_infographic_smart), genInfographic) { genInfographic = it }
                    AppTextField(value = language, onValueChange = { language = it }, label = stringResource(R.string.lang_selection))
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun AppTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = GeoBlackTranslucent, unfocusedContainerColor = GeoBlackTranslucent,
            focusedIndicatorColor = GeoAmberLight, unfocusedIndicatorColor = GeoGlassBorder,
            focusedTextColor = GeoTextPrimary, unfocusedTextColor = GeoTextPrimary
        ),
        singleLine = true
    )
}

@Composable
fun SettingsSwitch(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(GeoGlassBg).padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(label, color = GeoTextPrimary, style = MaterialTheme.typography.bodyMedium)
        Switch(
            checked = checked, onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = GeoBackground, checkedTrackColor = GeoGold)
        )
    }
}
