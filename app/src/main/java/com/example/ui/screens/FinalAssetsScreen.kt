package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.ProjectEntity
import com.example.ui.ContentViewModel
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import coil.compose.AsyncImage
import androidx.compose.ui.platform.LocalContext
import android.content.Intent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinalAssetsScreen(
    projectId: Int,
    viewModel: ContentViewModel,
    onNavigateBack: () -> Unit
) {
    var project by remember { mutableStateOf<ProjectEntity?>(null) }
    val context = LocalContext.current
    
    LaunchedEffect(projectId) {
        viewModel.allProjects.collect { projects ->
            projects.find { it.id == projectId }?.let { project = it }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.step3_title), color = GeoTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = GeoAmberLight)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(GeoBackground)) {
            if (project != null) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
                    val p = project!!
                    
                    if (p.generateImage || p.generateBgm || p.generateVoice) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            // Column 1 (Left): Audio Player
                            if (p.generateBgm || p.generateVoice) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(stringResource(R.string.audio_player_title), style = MaterialTheme.typography.titleMedium, color = GeoAmberLight)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    var isPlaying by remember { mutableStateOf(false) }
                                    
                                    val scriptText = remember(p.resultText) {
                                        val rawText = p.resultText ?: ""
                                        val cleanText = rawText.removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
                                        try {
                                            org.json.JSONObject(cleanText).optString("script", cleanText)
                                        } catch(e: Exception) {
                                            cleanText
                                        }
                                    }
                                    
                                    DisposableEffect(Unit) {
                                        onDispose { viewModel.stopSpeaking() }
                                    }
                                    
                                    GlassCard(cornerRadius = 32.dp) {
                                        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                                Box(
                                                    modifier = Modifier.size(64.dp).clip(CircleShape).background(Brush.linearGradient(listOf(GeoAmber, GeoGold))),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(Icons.Filled.MusicNote, "Audio", tint = GeoBackground, modifier = Modifier.size(32.dp))
                                                }
                                                Spacer(Modifier.width(16.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text("خروجی صوتی نهایی", color = GeoTextPrimary, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                                    Text("لحن: \${p.voiceTone} | گوینده: \${p.voiceGender}", color = GeoAmber, style = MaterialTheme.typography.labelSmall)
                                                }
                                            }
                                            Spacer(Modifier.height(24.dp))
                                            Waveform(isPlaying)
                                            Spacer(Modifier.height(24.dp))
                                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                                IconButton(
                                                    onClick = { 
                                                        isPlaying = !isPlaying
                                                        if (isPlaying) {
                                                            viewModel.speak(scriptText)
                                                        } else {
                                                            viewModel.stopSpeaking()
                                                        }
                                                    },
                                                    modifier = Modifier.size(72.dp).background(if(isPlaying) GeoGlassBorder else GeoAmberLight, CircleShape)
                                                ) {
                                                    Icon(if(isPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow, "Play/Stop", tint = if(isPlaying) GeoTextPrimary else GeoBackground, modifier = Modifier.size(40.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            
                            // Column 2 (Right): Image Gallery
                            if (p.generateImage || p.generateVideo) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(stringResource(R.string.gallery_title), style = MaterialTheme.typography.titleMedium, color = GeoAmberLight)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    val imagePromptText = remember(p.resultText) {
                                        val rawText = p.resultText ?: ""
                                        val cleanText = rawText.removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
                                        try {
                                            org.json.JSONObject(cleanText).optString("image_prompt", p.topic)
                                        } catch(e: Exception) {
                                            p.topic
                                        }
                                    }
                                    
                                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                                        // Main Poster
                                        Box(modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(32.dp)).background(Brush.linearGradient(listOf(GeoBlackTranslucent, GeoGlassBg))), contentAlignment = Alignment.Center) {
                                            val url = "https://image.pollinations.ai/prompt/${android.net.Uri.encode(imagePromptText + " high quality cinematic poster")}"
                                            AsyncImage(
                                                model = url,
                                                contentDescription = "Main Poster",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                            )
                                            IconButton(
                                                onClick = {
                                                    val sendIntent = Intent().apply {
                                                        action = Intent.ACTION_SEND
                                                        putExtra(Intent.EXTRA_TEXT, url)
                                                        type = "text/plain"
                                                    }
                                                    context.startActivity(Intent.createChooser(sendIntent, null))
                                                }, 
                                                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).background(GeoAmberLight, CircleShape).size(48.dp)
                                            ) {
                                                Icon(Icons.Filled.Share, "Share", tint = GeoBackground, modifier = Modifier.size(24.dp))
                                            }
                                        }
                                        
                                        // Cover Image
                                        Box(modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(32.dp)).background(Brush.linearGradient(listOf(GeoBlackTranslucent, GeoGlassBg))), contentAlignment = Alignment.Center) {
                                            val url2 = "https://image.pollinations.ai/prompt/${android.net.Uri.encode(imagePromptText + " youtube thumbnail cover")}"
                                            AsyncImage(
                                                model = url2,
                                                contentDescription = "Cover Image",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                            )
                                            IconButton(
                                                onClick = {
                                                    val sendIntent = Intent().apply {
                                                        action = Intent.ACTION_SEND
                                                        putExtra(Intent.EXTRA_TEXT, url2)
                                                        type = "text/plain"
                                                    }
                                                    context.startActivity(Intent.createChooser(sendIntent, null))
                                                }, 
                                                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).background(GeoGold, CircleShape).size(48.dp)
                                            ) {
                                                Icon(Icons.Filled.Share, "Share", tint = GeoBackground, modifier = Modifier.size(24.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(80.dp))
                }
            } else {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = GeoAmberLight)
            }
        }
    }
}

@Composable
fun Waveform(isPlaying: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        for (i in 0 until 40) {
            val infiniteTransition = rememberInfiniteTransition()
            val height by infiniteTransition.animateFloat(
                initialValue = 8f,
                targetValue = if (isPlaying) (16..64).random().toFloat() else 8f,
                animationSpec = infiniteRepeatable(
                    animation = tween((300..800).random(), easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ), label = ""
            )
            Box(modifier = Modifier.width(4.dp).height(height.dp).clip(CircleShape).background(if(i % 5 == 0) GeoAmberLight else GeoAmber.copy(alpha=0.6f)))
        }
    }
}
