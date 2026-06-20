package com.example.data.sync

import com.example.data.ProjectEntity
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudSyncRepository @Inject constructor() {
    
    // In a real app, this would use Firebase Firestore or Realtime Database
    suspend fun syncProjects(projects: List<ProjectEntity>) {
        // Simulate network delay
        delay(1000)
    }

    suspend fun fetchCloudProjects(): List<ProjectEntity> {
        delay(1000)
        return emptyList()
    }
}
