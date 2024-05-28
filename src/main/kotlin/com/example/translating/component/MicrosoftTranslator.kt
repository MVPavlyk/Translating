package com.example.translating.component

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class MicrosoftTranslator(
    @Value("\${microsoft.translate.api.key}") private val apiKey: String,
    @Value("\${microsoft.translate.api.location}") private val location: String
) {
    private val client = OkHttpClient()

    @Throws(IOException::class)
    fun translateEnglishToUkrainian(word: String): String? {
        val requestBody = "[{\"Text\": \"$word\"}]"
        val request = buildRequest("en", "fr", requestBody)
        val response = client.newCall(request).execute()
        return prettify(response.body?.string())
    }

    @Throws(IOException::class)
    fun translateUkrainianToEnglish(word: String): String? {
        val requestBody = "[{\"Text\": \"$word\"}]"
        val request = buildRequest("fr", "en", requestBody)
        val response = client.newCall(request).execute()
        return prettify(response.body?.string())
    }

    @Throws(IOException::class)
    private fun buildRequest(from: String, to: String, requestBody: String): Request {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val url = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&from=$from&to=$to"
        val body = requestBody.toRequestBody(mediaType)
        return Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Ocp-Apim-Subscription-Key", apiKey)
            .addHeader("Ocp-Apim-Subscription-Region", location)
            .addHeader("Content-type", "application/json")
            .build()
    }

    private fun prettify(json_text: String?): String {
        val parser = JsonParser()
        val json = parser.parse(json_text)
        val gson = GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(json)
    }
}