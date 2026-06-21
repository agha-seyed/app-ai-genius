package com.example.domain.usecases

import com.example.data.preferences.UserSettings
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

class HandleVoiceCommandUseCaseTest {

    @Test
    fun testHandleVoiceCommandUseCase() = runBlocking {
        // Arrange
        val mockGenerateAiResponseUseCase = mock(GenerateAiResponseUseCase::class.java)
        val useCase = HandleVoiceCommandUseCase(mockGenerateAiResponseUseCase)
        val settings = UserSettings(systemPrompt = "You are an assistant")
        val text = "Hello"
        val expectedPrompt = "You are an assistant\n\nRespond briefly to the following query: Hello"
        val expectedResponse = "Hi there"

        `when`(mockGenerateAiResponseUseCase(expectedPrompt, settings)).thenReturn(expectedResponse)

        // Act
        val result = useCase(text, settings)

        // Assert
        assertEquals(expectedResponse, result)
    }
}
