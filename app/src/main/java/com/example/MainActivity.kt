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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import com.example.presentation.ContentViewModel
import com.example.presentation.VoiceState
import com.example.presentation.components.PulseEffect
import com.example.presentation.screens.CreateProjectScreen
import com.example.presentation.screens.DashboardScreen
import com.example.presentation.screens.DetailScreen
import com.example.presentation.screens.SettingsScreen
import com.example.presentation.theme.MyApplicationTheme

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
            val viewModel: ContentViewModel = hiltViewModel()
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
                    val snackbarHostState = remember { SnackbarHostState() }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                        containerColor = MaterialTheme.colorScheme.background
                    ) { innerPadding ->
                        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                            NavHost(
                                navController = navController, 
                                startDestination = "dashboard",
                                enterTransition = { androidx.compose.animation.slideInHorizontally(initialOffsetX = { 1000 }) + fadeIn() },
                                exitTransition = { fadeOut() },
                                popEnterTransition = { fadeIn() },
                                popExitTransition = { androidx.compose.animation.slideOutHorizontally(targetOffsetX = { 1000 }) + fadeOut() }
                            ) {
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
                                    com.example.presentation.screens.FinalAssetsScreen(
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
    val voiceState by viewModel.voiceState.collectAsStateWithLifecycle()
    val speechText by viewModel.speechText.collectAsStateWithLifecycle()
    
    Column(horizontalAlignment = Alignment.End, modifier = modifier) {
        AnimatedVisibility(
            visible = voiceState !is VoiceState.Idle,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                modifier = Modifier.padding(bottom = 16.dp).widthIn(max = 250.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = when (voiceState) {
                            is VoiceState.Listening -> "در حال شنیدن..."
                            is VoiceState.Processing -> "در حال پردازش..."
                            is VoiceState.Success -> (voiceState as VoiceState.Success).response
                            is VoiceState.Error -> androidx.compose.ui.res.stringResource(com.example.R.string.error_unknown)
                            else -> ""
                        },
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (speechText.isNotEmpty()) {
                        Spacer(Modifier.height(8.dp))
                        Text(text = "🗣️ $speechText", color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(64.dp)) {
            if (voiceState == VoiceState.Listening) {
                PulseEffect(color = MaterialTheme.colorScheme.error, modifier = Modifier.matchParentSize()) {}
            }
            FloatingActionButton(
                onClick = { 
                    if (voiceState == VoiceState.Idle) viewModel.startListening()
                    else viewModel.stopListening()
                },
                containerColor = if (voiceState == VoiceState.Listening) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                contentColor = if (voiceState == VoiceState.Listening) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Filled.Mic, contentDescription = "دستیار صوتی")
            }
        }
    }
}
