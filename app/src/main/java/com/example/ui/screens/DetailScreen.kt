package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import com.example.R
import com.example.data.ProjectEntity
import com.example.ui.ContentViewModel
import com.example.ui.UiState
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    projectId: Int,
    viewModel: ContentViewModel,
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit
) {
    var project by remember { mutableStateOf<ProjectEntity?>(null) }
    var textContent by remember { mutableStateOf("") }
    
    // Actually we should listen to the flow if it's updated, but simple state is fine
    LaunchedEffect(projectId) {
        viewModel.allProjects.collect { projects ->
            projects.find { it.id == projectId }?.let { 
                project = it
                textContent = it.resultText ?: ""
            }
        }
    }
    
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(uiState) {
        when(uiState) {
            is UiState.RefinedSuccess -> {
                textContent = (uiState as UiState.RefinedSuccess).text
                viewModel.clearState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.step2_title), color = GeoTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = GeoCyan)
                    }
                },
                 actions = {
                    IconButton(onClick = {  }) {
                        Icon(Icons.Filled.AutoFixHigh, "Refine", tint = GeoAmber)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent,
        bottomBar = {
            Box(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = onNavigateNext,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(GeoEmerald, GeoCyan))), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.step3_title), color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (project != null) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
                    // Refine Card
                    GlassCard(cornerRadius = 16.dp) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            Text(stringResource(R.string.gen_script), color = GeoEmerald, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { viewModel.refineContent(textContent) }) {
                                Icon(Icons.Filled.AutoFixHigh, "Refine", tint = GeoAmber)
                            }
                        }
                        TextField(
                            value = textContent,
                            onValueChange = { textContent = it },
                            modifier = Modifier.fillMaxWidth().height(250.dp),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = GeoTextPrimary),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    if (project!!.generateInfographic) {
                        Text(stringResource(R.string.flowchart_title), color = GeoCyan, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(400.dp).clip(RoundedCornerShape(16.dp)).background(GeoBlackTranslucent)) {
                            FlowchartCanvas(textContent)
                        }
                    }
                    Spacer(modifier = Modifier.height(80.dp))
                }
            } else {
                CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center), color = GeoCyan)
            }
        }
    }
}

@OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)
@Composable
fun FlowchartCanvas(content: String) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val textMeasurer = rememberTextMeasurer()

    val nodes = remember(content) {
        val lines = content.split("\\n").filter { it.isNotBlank() && it.length > 5 }
        if (lines.isEmpty()) listOf("No Content") else lines.take(5)
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(0.5f, 3f)
                    offset += pan
                }
            }
    ) {
        val nodeWidth = 350f
        val nodeHeight = 140f
        val verticalSpacing = 220f
        val startX = (size.width - nodeWidth) / 2
        var currentY = 100f

        nodes.forEachIndexed { index, text ->
            val rectOffset = Offset(startX + offset.x * scale, currentY * scale + offset.y)
            
            if (index > 0) {
                val prevY = (currentY - verticalSpacing) * scale + offset.y + (nodeHeight * scale)
                drawLine(
                    color = GeoCyan,
                    start = Offset(rectOffset.x + (nodeWidth * scale) / 2, prevY),
                    end = Offset(rectOffset.x + (nodeWidth * scale) / 2, rectOffset.y),
                    strokeWidth = 4f * scale
                )
            }

            drawRoundRect(
                color = GeoSurface.copy(alpha=0.9f),
                topLeft = rectOffset,
                size = Size(nodeWidth * scale, nodeHeight * scale),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f * scale)
            )
            drawRoundRect(
                color = GeoAmber.copy(alpha=0.5f),
                topLeft = rectOffset,
                size = Size(nodeWidth * scale, nodeHeight * scale),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f * scale),
                style = Stroke(width = 3f * scale)
            )

            val snippet = if (text.length > 60) text.take(57) + "..." else text
            drawText(
                textMeasurer = textMeasurer,
                text = snippet,
                style = TextStyle(color = Color.White, fontSize = 16.sp * scale),
                topLeft = Offset(rectOffset.x + 30f * scale, rectOffset.y + 40f * scale)
            )

            currentY += verticalSpacing
        }
    }
}
