package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import com.example.ui.ContentViewModel
import com.example.ui.VoiceState
import com.example.ui.components.PulseEffect
import com.example.ui.screens.CreateProjectScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DetailScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme

import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection
import android.content.res.Configuration
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            var currentLang by remember { mutableStateOf("fa") }
            
            val locale = java.util.Locale(currentLang)
            val localizedContext = remember(currentLang) {
                val config = Configuration(context.resources.configuration)
                config.setLocale(locale)
                config.setLayoutDirection(locale)
                context.createConfigurationContext(config)
            }
            
            val direction = if (localizedContext.resources.configuration.layoutDirection == android.view.View.LAYOUT_DIRECTION_RTL) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides localizedContext.resources.configuration,
                LocalLayoutDirection provides direction
            ) {
                MyApplicationTheme {
                    val navController = rememberNavController()
                    val viewModel: ContentViewModel = hiltViewModel()

                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            NavHost(navController = navController, startDestination = "dashboard") {
                                composable("dashboard") {
                                    DashboardScreen(
                                        viewModel = viewModel,
                                        onNavigateToCreate = { navController.navigate("create") },
                                        onNavigateToDetail = { id -> navController.navigate("detail/$id") },
                                        onNavigateToSettings = { navController.navigate("settings") },
                                        onToggleLanguage = { currentLang = if (currentLang == "fa") "en" else "fa" }
                                    )
                                }
                            composable("settings") {
                                SettingsScreen(
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                            composable("create") {
                                CreateProjectScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    onNavigateToDetail = { id ->
                                        navController.popBackStack()
                                        navController.navigate("detail/$id")
                                    }
                                )
                            }
                            composable("detail/{id}") { backStackEntry ->
                                val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                                if (id != null) {
                                    DetailScreen(
                                        projectId = id,
                                        viewModel = viewModel,
                                        onNavigateBack = { navController.popBackStack() },
                                        onNavigateNext = { navController.navigate("assets/$id") }
                                    )
                                }
                            }
                            composable("assets/{id}") { backStackEntry ->
                                val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                                if (id != null) {
                                    com.example.ui.screens.FinalAssetsScreen(
                                        projectId = id,
                                        viewModel = viewModel,
                                        onNavigateBack = { navController.popBackStack() }
                                    )
                                }
                            }
                        }

                        // Floating Voice Assistant Overlay
                        VoiceAssistantOverlay(viewModel, modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp))
                    }
                }
            }
            }
        }
    }
}

@Composable
fun VoiceAssistantOverlay(viewModel: ContentViewModel, modifier: Modifier = Modifier) {
    val voiceState by viewModel.voiceState.collectAsState()
    val speechText by viewModel.speechText.collectAsState()
    
    Column(horizontalAlignment = Alignment.End, modifier = modifier) {
        AnimatedVisibility(
            visible = voiceState !is VoiceState.Idle,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                modifier = Modifier.padding(bottom = 16.dp).widthIn(max = 250.dp),
                color = com.example.ui.theme.GlassBackground,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = when (voiceState) { // Exhaustive when to avoid missing branches initially
                            is VoiceState.Listening -> "Listening..."
                            is VoiceState.Processing -> "Thinking..."
                            is VoiceState.Success -> (voiceState as VoiceState.Success).response
                            is VoiceState.Error -> androidx.compose.ui.res.stringResource(com.example.R.string.error_unknown)
                            else -> ""
                        },
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (speechText.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Text(text = "🗣️ \$speechText", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(64.dp)) {
            if (voiceState == VoiceState.Listening) {
                PulseEffect(color = Color.Red, modifier = Modifier.matchParentSize()) {}
            }
            FloatingActionButton(
                onClick = { 
                    if (voiceState == VoiceState.Idle) viewModel.startListening()
                    else viewModel.stopListening()
                },
                containerColor = if (voiceState == VoiceState.Listening) Color.Red else MaterialTheme.colorScheme.tertiary,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Mic, contentDescription = "Voice Assistant", tint = Color.White)
            }
        }
    }
}
