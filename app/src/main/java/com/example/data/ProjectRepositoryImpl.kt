package com.example.data

import com.example.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProjectRepositoryImpl @Inject constructor(private val projectDao: ProjectDao) : ProjectRepository {
    override val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()

    override suspend fun getAllProjectsSync(): List<ProjectEntity> {
        return projectDao.getAllProjectsSync()
    }

    override suspend fun getProjectById(id: Int): ProjectEntity? {
        return projectDao.getProjectById(id)
    }

    override suspend fun insertProject(project: ProjectEntity): Long {
        return projectDao.insertProject(project)
    }

    override suspend fun updateProject(project: ProjectEntity) {
        projectDao.updateProject(project)
    }
    
    override suspend fun deleteProject(id: Int) {
        projectDao.deleteProjectById(id)
    }
}
