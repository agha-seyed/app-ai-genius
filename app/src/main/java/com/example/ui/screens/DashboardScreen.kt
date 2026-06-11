package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ContentViewModel
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ContentViewModel,
    onNavigateToCreate: () -> Unit,
    onNavigateToDetail: (Int) -> Unit,
    onToggleLanguage: () -> Unit
) {
    val projects by viewModel.allProjects.collectAsState()

    Scaffold(
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(GeoSurface.copy(alpha = 0.8f))
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(Brush.linearGradient(listOf(GeoEmerald, GeoCyan))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("A", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                        }
                        Column {
                            Text("استودیو هوشمند", style = MaterialTheme.typography.titleMedium, color = GeoTextPrimary)
                            Text("AI Content Engine", style = MaterialTheme.typography.labelSmall, color = GeoEmerald)
                        }
                    }
                    IconButton(onClick = onToggleLanguage) {
                        Text("🌐", fontSize = 24.sp)
                    }
                }
                HorizontalDivider(color = GeoGlassBorder)
            }
        },
        containerColor = Color.Transparent,
        bottomBar = {
            Column(modifier = Modifier.background(GeoSurface.copy(alpha = 0.9f))) {
                HorizontalDivider(color = GeoGlassBorder)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavItem("🏠", "خانه", true) {}
                    BottomNavItem("✨", "تولید", false) { onNavigateToCreate() }
                    BottomNavItem("📊", "آمار", false) {}
                    BottomNavItem("⚙️", "تنظیمات", false) {}
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = GeoEmerald,
                contentColor = GeoBackground
            ) {
                Icon(Icons.Filled.Add, "New")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("پروژه‌های اخیر", style = MaterialTheme.typography.labelMedium, color = GeoTextSecondary)
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (projects.isEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    val templates = listOf(
                        Pair("IG", "سناریو اینستاگرام فروشگاهی"),
                        Pair("YT", "اسکریپت یوتیوب بررسی تکنولوژی"),
                        Pair("TK", "ایده ویدیوی وایرال تیک‌تاک")
                    )
                    items(templates) { template ->
                        GlassCard(
                            modifier = Modifier
                                .width(160.dp)
                                .clickable { onNavigateToCreate() },
                            cornerRadius = 16.dp
                        ) {
                            Box(modifier = Modifier.size(32.dp).clip(MaterialTheme.shapes.small).background(GeoCyan.copy(alpha=0.2f)), contentAlignment = Alignment.Center) {
                                Text(template.first, color = GeoCyan, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(template.second, style = MaterialTheme.typography.bodyMedium, color = GeoTextPrimary, maxLines = 2)
                            Spacer(Modifier.height(4.dp))
                            Text("همین حالا بسازید", style = MaterialTheme.typography.labelSmall, color = GeoEmerald)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(120.dp).clip(CircleShape).background(Brush.radialGradient(listOf(GeoCyan.copy(alpha=0.3f), Color.Transparent))), contentAlignment = Alignment.Center) {
                        Text("🤖", fontSize = 56.sp)
                    }
                    Spacer(Modifier.height(16.dp))
                    Text("به استودیو هوشمند خوش‌آمدید", style = MaterialTheme.typography.titleLarge, color = GeoTextPrimary, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("برای همگام‌سازی پروژه‌ها و امکانات پیشرفته، وارد شوید.", style = MaterialTheme.typography.bodyMedium, color = GeoTextSecondary)
                    Spacer(Modifier.height(32.dp))
                    
                    // Google Login Mock Button
                    Button(
                        onClick = { /* Mock login */ },
                        colors = ButtonDefaults.buttonColors(containerColor = GeoSurface.copy(alpha=0.9f)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.height(56.dp).border(1.dp, GeoGlassBorder, RoundedCornerShape(24.dp)),
                        contentPadding = PaddingValues(horizontal = 24.dp)
                    ) {
                        Text("G", color = GeoCyan, fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.padding(end = 12.dp))
                        Text("ورود با حساب گوگل", color = GeoTextPrimary, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(projects) { project ->
                        GlassCard(
                            modifier = Modifier
                                .width(160.dp)
                                .clickable { onNavigateToDetail(project.id) },
                            cornerRadius = 16.dp
                        ) {
                            Box(modifier = Modifier.size(32.dp).clip(MaterialTheme.shapes.small).background(GeoCyan.copy(alpha=0.2f)), contentAlignment = Alignment.Center) {
                                Text(project.platform.take(2).uppercase(), color = GeoCyan, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(project.topic, style = MaterialTheme.typography.bodyMedium, color = GeoTextPrimary, maxLines = 2)
                            Spacer(Modifier.height(4.dp))
                            Text(formatDate(project.dateCreated), style = MaterialTheme.typography.labelSmall, color = GeoTextSecondary)
                        }
                    }
                }
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@Composable
fun BottomNavItem(icon: String, label: String, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp, 32.dp)
                .clip(CircleShape)
                .background(if (isSelected) GeoCyan.copy(alpha = 0.2f) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 20.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) GeoCyan else GeoTextSecondary,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
