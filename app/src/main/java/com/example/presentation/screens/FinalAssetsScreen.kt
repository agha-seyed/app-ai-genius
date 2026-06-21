package com.example.presentation.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
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
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.data.ProjectEntity
import com.example.presentation.ContentViewModel
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
    
    val parsedState by viewModel.parsedProjectState.collectAsStateWithLifecycle()
    
    LaunchedEffect(projectId) {
        viewModel.allProjects.collect { projects ->
            projects.find { it.id == projectId }?.let { 
                project = it
                viewModel.loadParsedProjectDetails(it)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.final_result), color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back_btn), tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (project != null) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(32.dp)) {
                    val p = project!!
                    
                    if (p.generateImage || p.generateBgm || p.generateVoice) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            // Audio Player
                            if (p.generateBgm || p.generateVoice) {
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Text(stringResource(R.string.audio_player_title_fa), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                                    
                                    var isPlaying by remember { mutableStateOf(false) }
                                    val scriptText = parsedState.script
                                    
                                    DisposableEffect(Unit) {
                                        onDispose { viewModel.stopSpeaking() }
                                    }
                                    
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f))
                                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                                            .padding(24.dp),
                                        verticalArrangement = Arrangement.spacedBy(24.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                            Box(
                                                modifier = Modifier.size(56.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(Icons.Filled.MusicNote, stringResource(R.string.audio_icon), tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(28.dp))
                                            }
                                            Spacer(Modifier.width(16.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(stringResource(R.string.final_audio_output), color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                                Text(stringResource(R.string.tone_speaker, p.voiceTone, p.voiceGender), color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelMedium)
                                            }
                                        }
                                        
                                        Waveform(isPlaying)
                                        
                                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = { 
                                                    isPlaying = !isPlaying
                                                    if (isPlaying) {
                                                        viewModel.speak(scriptText)
                                                    } else {
                                                        viewModel.stopSpeaking()
                                                    }
                                                },
                                                modifier = Modifier.size(64.dp).background(if(isPlaying) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary, CircleShape).border(1.dp, if(isPlaying) MaterialTheme.colorScheme.outline else Color.Transparent, CircleShape)
                                            ) {
                                                Icon(if(isPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow, stringResource(R.string.play_stop), tint = if(isPlaying) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(36.dp))
                                            }
                                            Spacer(Modifier.width(24.dp))
                                            IconButton(
                                                onClick = {
                                                    val file = java.io.File(context.cacheDir, "audio_export.wav")
                                                    viewModel.exportAudio(scriptText, file)
                                                    android.widget.Toast.makeText(context, context.getString(R.string.audio_saved, file.absolutePath), android.widget.Toast.LENGTH_LONG).show()
                                                },
                                                modifier = Modifier.size(48.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                            ) {
                                                Icon(Icons.Filled.Download, stringResource(R.string.download_audio), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }
                            
                            // Image Gallery
                            if (p.generateImage || p.generateVideo) {
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Text(stringResource(R.string.gallery_title_fa), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                                    
                                    val imagePromptText = parsedState.imagePrompt.ifBlank { p.topic }
                                    
                                    val aspectRatioParams = when(p.platform.lowercase()) {
                                        "youtube" -> "width=1920&height=1080"
                                        "tiktok", "reels", "shorts" -> "width=1080&height=1920"
                                        else -> "width=1080&height=1080"
                                    }
                                    
                                    val posterHeight = if (aspectRatioParams.contains("1920&height=1080")) 160.dp else if (aspectRatioParams.contains("1080&height=1920")) 280.dp else 200.dp
                                    
                                    // Main Poster
                                    Box(modifier = Modifier.fillMaxWidth().height(posterHeight).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                                        val url = "https://image.pollinations.ai/prompt/${android.net.Uri.encode(imagePromptText + " high quality cinematic poster")}?${aspectRatioParams}"
                                        AsyncImage(
                                            model = url,
                                            contentDescription = stringResource(R.string.main_poster),
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
                                            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).background(MaterialTheme.colorScheme.surface.copy(alpha=0.8f), CircleShape).size(40.dp)
                                        ) {
                                            Icon(Icons.Filled.Share, stringResource(R.string.share), tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                    
                                    // Cover Image
                                    Box(modifier = Modifier.fillMaxWidth().height(posterHeight).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                                        val url2 = "https://image.pollinations.ai/prompt/${android.net.Uri.encode(imagePromptText + " youtube thumbnail cover")}?${aspectRatioParams}"
                                        AsyncImage(
                                            model = url2,
                                            contentDescription = stringResource(R.string.cover_image),
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
                                            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp).background(MaterialTheme.colorScheme.surface.copy(alpha=0.8f), CircleShape).size(40.dp)
                                        ) {
                                            Icon(Icons.Filled.Share, stringResource(R.string.share), tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(80.dp))
                }
            } else {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.primary)
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
            val color = if(i % 5 == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary.copy(alpha=0.6f)
            Box(modifier = Modifier.width(4.dp).height(height.dp).clip(CircleShape).background(color))
        }
    }
}
