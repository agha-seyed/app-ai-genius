package com.example.presentation.screens

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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import com.example.R
import com.example.data.ProjectEntity
import com.example.presentation.ContentViewModel
import com.example.presentation.UiState
import com.example.presentation.components.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    projectId: Int,
    viewModel: ContentViewModel,
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit
) {
    val context = LocalContext.current
    var project by remember { mutableStateOf<ProjectEntity?>(null) }
    var textContent by remember { mutableStateOf("") }
    var flowchartSteps by remember { mutableStateOf<List<String>>(emptyList()) }
    val snackbarHostState = remember { SnackbarHostState() }
    
    val parsedState by viewModel.parsedProjectState.collectAsStateWithLifecycle()
    
    LaunchedEffect(projectId) {
        viewModel.allProjects.collect { projects ->
            projects.find { it.id == projectId }?.let { 
                project = it
                viewModel.loadParsedProjectDetails(it)
            }
        }
    }
    
    LaunchedEffect(parsedState) {
        if (textContent.isBlank()) {
            textContent = parsedState.script
        }
        flowchartSteps = parsedState.flowchart
    }
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState) {
        when(uiState) {
            is UiState.RefinedSuccess -> {
                textContent = (uiState as UiState.RefinedSuccess).text
                viewModel.clearState()
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar((uiState as UiState.Error).message)
                viewModel.clearState()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.step2_title), color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                 actions = {
                    IconButton(onClick = {
                        val sendIntent: Intent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, textContent)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    }) {
                        Icon(Icons.Filled.Share, stringResource(R.string.share), tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Box(modifier = Modifier.padding(16.dp).background(MaterialTheme.colorScheme.background)) {
                Button(
                    onClick = onNavigateNext,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(stringResource(R.string.step3_title), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp)) {
            if (project != null) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                    Spacer(Modifier.height(8.dp))
                    // Refine Card
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(stringResource(R.string.gen_script), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            IconButton(onClick = { viewModel.refineContent(textContent) }, modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))) {
                                Icon(Icons.Filled.AutoFixHigh, "Refine", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            }
                        }
                        OutlinedTextField(
                            value = textContent,
                            onValueChange = { textContent = it },
                            modifier = Modifier.fillMaxWidth().height(280.dp),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground, lineHeight = 24.sp),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), 
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary, 
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                    
                    if (project!!.generateInfographic) {
                        Text(stringResource(R.string.flowchart_title), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Box(modifier = Modifier.fillMaxWidth().height(400.dp).clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)).border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))) {
                            FlowchartCanvas(flowchartSteps)
                        }
                    }
                    Spacer(modifier = Modifier.height(100.dp))
                }
            } else {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)
@Composable
fun FlowchartCanvas(nodes: List<String>) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val textMeasurer = rememberTextMeasurer()

    val displayNodes = if (nodes.isEmpty()) listOf("محتوایی وجود ندارد") else nodes.take(5)
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

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

        displayNodes.forEachIndexed { index, text ->
            val rectOffset = Offset(startX + offset.x * scale, currentY * scale + offset.y)
            
            if (index > 0) {
                val prevY = (currentY - verticalSpacing) * scale + offset.y + (nodeHeight * scale)
                drawLine(
                    color = primaryColor,
                    start = Offset(rectOffset.x + (nodeWidth * scale) / 2, prevY),
                    end = Offset(rectOffset.x + (nodeWidth * scale) / 2, rectOffset.y),
                    strokeWidth = 4f * scale
                )
            }

            drawRoundRect(
                color = surfaceColor,
                topLeft = rectOffset,
                size = Size(nodeWidth * scale, nodeHeight * scale),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f * scale)
            )
            drawRoundRect(
                color = primaryColor.copy(alpha=0.8f),
                topLeft = rectOffset,
                size = Size(nodeWidth * scale, nodeHeight * scale),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f * scale),
                style = Stroke(width = 2f * scale)
            )

            val snippet = if (text.length > 60) text.take(57) + "..." else text
            drawText(
                textMeasurer = textMeasurer,
                text = snippet,
                style = TextStyle(color = onSurfaceColor, fontSize = 16.sp * scale),
                topLeft = Offset(rectOffset.x + 30f * scale, rectOffset.y + 40f * scale)
            )

            currentY += verticalSpacing
        }
    }
}
