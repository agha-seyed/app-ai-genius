package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val topic: String,
    val shortDescription: String = "",
    val sourceInfo: String = "",
    val platform: String = "Instagram",
    val visualStyle: String = "Cinematic",
    
    val generateScript: Boolean = true,
    val generateCaption: Boolean = true,
    val generateInfographic: Boolean = false,
    val language: String = "فارسی",
    
    val generateVoice: Boolean = false,
    val generateBgm: Boolean = false,
    val voiceGender: String = "آقا",
    val voiceTone: String = "حماسی",
    
    val generateImage: Boolean = false,
    val frameCount: Int = 4,
    val generateVideo: Boolean = false,
    val generateAnimatedTeaser: Boolean = false,
    val videoStyle: String = "Cinematic",
    
    val dateCreated: Long = System.currentTimeMillis(),
    val resultText: String? = null,
    val audioPath: String? = null
)
