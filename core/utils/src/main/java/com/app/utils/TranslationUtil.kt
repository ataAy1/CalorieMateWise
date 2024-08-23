package com.app.utils

import android.util.Log
import com.app.data.dto.ApiResponse
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

object TranslationUtil {

    private val englishToTurkishTranslator = Translation.getClient(
        TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.TURKISH)
            .build()
    )

    private val turkishToEnglishTranslator = Translation.getClient(
        TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.TURKISH)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()
    )

    suspend fun ensureModelsDownloaded() {
        try {
            downloadModel(englishToTurkishTranslator, "English to Turkish")
            downloadModel(turkishToEnglishTranslator, "Turkish to English")
        } catch (e: Exception) {
            Log.e("TranslationUtil", "Error ensuring models are downloaded", e)
        }
    }

    private suspend fun downloadModel(translator: Translator, modelName: String) {
        try {
            translator.downloadModelIfNeeded().await()
            Log.d("ModelDownload", "$modelName model downloaded successfully.")
        } catch (e: Exception) {
            Log.e("ModelDownload", "Failed to download $modelName model.", e)
        }
    }

    suspend fun translateToTurkish(englishText: String): String {
        try {
            ensureModelsDownloaded()

            val translatedText = englishToTurkishTranslator.translate(englishText).await()
            return translatedText ?: englishText
        } catch (e: Exception) {
            Log.e("TranslationUtil", "Error translating text", e)
            return englishText
        }
    }

    private fun getDefaultImageUrl(): String {
        return ""
    }

    suspend fun translateApiResponse(response: ApiResponse): ApiResponse {
        response.parsed.forEach { parsed ->
            parsed.food.label = translateToTurkish(parsed.food.label ?: "")
            parsed.food.knownAs = translateToTurkish(parsed.food.knownAs ?: "")
            parsed.food.category = translateToTurkish(parsed.food.category ?: "")
            parsed.food.categoryLabel = translateToTurkish(parsed.food.categoryLabel ?: "")

            if (parsed.food.image.isNullOrEmpty()) {
                parsed.food.image = getDefaultImageUrl()
                Log.w("TranslationUtil", "Image for ${parsed.food.label} is empty. Using default image.")
            }
        }

        response.hints.forEach { hint ->
            hint.food.label = translateToTurkish(hint.food.label ?: "")
            hint.food.knownAs = translateToTurkish(hint.food.knownAs ?: "")
            hint.food.category = translateToTurkish(hint.food.category ?: "")
            hint.food.categoryLabel = translateToTurkish(hint.food.categoryLabel ?: "")

            if (hint.food.image.isNullOrEmpty()) {
                hint.food.image = getDefaultImageUrl()
                Log.w("TranslationUtil", "Image for hint ${hint.food.label} is empty. Using default image.")
            }

            hint.measures.forEach { measure ->
                measure.label = translateToTurkish(measure.label ?: "")
            }
        }

        return response
    }

    private val turkishToEnglishMapping = mapOf(
        "çorba" to "soup",
    )

    suspend fun translateToEnglish(turkishText: String): String {
        turkishToEnglishMapping[turkishText]?.let { return it }

        return try {
            val translatedText = turkishToEnglishTranslator.translate(turkishText).await()
            translatedText ?: turkishText
        } catch (e: Exception) {
            Log.e("TranslationUtil", "Error translating text", e)
            turkishText
        }
    }
}
