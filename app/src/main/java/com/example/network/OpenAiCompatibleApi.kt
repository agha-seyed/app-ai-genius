package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

@JsonClass(generateAdapter = true)
data class OpenAiRequest(
    @Json(name = "model") val model: String,
    @Json(name = "messages") val messages: List<OpenAiMessage>
)

@JsonClass(generateAdapter = true)
data class OpenAiMessage(
    @Json(name = "role") val role: String,
    @Json(name = "content") val content: String
)

@JsonClass(generateAdapter = true)
data class OpenAiResponse(
    @Json(name = "choices") val choices: List<OpenAiChoice>?
)

@JsonClass(generateAdapter = true)
data class OpenAiChoice(
    @Json(name = "message") val message: OpenAiMessage?
)

interface OpenAiCompatibleApi {
    @POST
    suspend fun generateContent(
        @Url url: String,
        @Header("Authorization") authHeader: String,
        @Body request: OpenAiRequest
    ): OpenAiResponse
}
