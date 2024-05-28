package com.example.translating.service

import com.example.translating.component.DatabaseConnector
import com.example.translating.component.MicrosoftTranslator
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import java.sql.SQLException

@Service
class DictionaryService {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var microsoftTranslator: MicrosoftTranslator

    fun getOrTranslateEnglish(word: String): String? {
        var listTranslation = translateEnglish(word)
        var translation: String?
        if (listTranslation.isEmpty()) {
            translation = microsoftTranslator.translateEnglishToUkrainian(word)
            if (translation?.contains("error") == false) {
                saveTranslationToDatabase(word, translation)
            }
            return translation
        }
        translation = transformToJSON(listTranslation, "fr")
        return translation
    }

    fun getOrTranslateUkrainian(word: String): String? {
        var listTranslation = translateEnglish(word)
        var translation: String?
        if (listTranslation.isEmpty()) {
            translation = microsoftTranslator.translateUkrainianToEnglish(word)
            if (translation?.contains("error") == false) {
                saveTranslationToDatabase(word, translation)
            }
            return translation
        }
        translation = transformToJSON(listTranslation, "en")
        return translation
    }

    private fun translateEnglish(word: String): List<String> {
        val sql = "SELECT translation FROM english_to_french WHERE english_word = ?"
        val translations = jdbcTemplate.query(sql, arrayOf(word)) { rs, _ ->
            rs.getString("translation")
        }
        println(translations)
        return translations
    }

    private fun translateUkrainian(word: String): List<String> {
        val sql = "SELECT translation FROM french_to_english WHERE french_word = ?"
        val translations = jdbcTemplate.query(sql, arrayOf(word)) { rs, _ ->
            rs.getString("translation")
        }
        println(translations)
        return translations
    }

    private fun saveTranslationToDatabase(word: String, translation: String) {
        // Парсимо результат перекладу з формату JSON
        val translationsJson = JsonParser.parseString(translation).asJsonArray
        val translations = mutableListOf<String>()

        // Ітеруємо через всі об'єкти перекладів та додаємо кожний переклад до списку
        translationsJson.forEach { translationObject ->
            val translatedText = translationObject.asJsonObject
                .getAsJsonArray("translations")[0]
                .asJsonObject
                .get("text").asString
            translations.add(translatedText)
        }

        // SQL-запит для збереження перекладу з англійської на українську
        val sqlEnglishToUkrainian = "INSERT INTO english_to_french (english_word, translation) VALUES (?, ?)"
        translations.forEach { translatedText ->
            jdbcTemplate.update(sqlEnglishToUkrainian, word, translatedText)
        }

        // SQL-запит для збереження перекладу з української на англійську
        val sqlUkrainianToEnglish = "INSERT INTO french_to_english (french_word, translation) VALUES (?, ?)"
        translations.forEach { translatedText ->
            jdbcTemplate.update(sqlUkrainianToEnglish, translatedText, word)
        }
    }


    fun searchEnglish(keyword: String): List<String> {
        val sql = "SELECT english_word FROM english_to_french WHERE english_word LIKE ?"
        return jdbcTemplate.queryForList(sql, arrayOf("%$keyword%"), String::class.java)
    }

    fun searchUkrainian(keyword: String): List<String> {
        val sql = "SELECT french_word FROM french_to_english WHERE french_word LIKE ?"
        return jdbcTemplate.queryForList(sql, arrayOf("%$keyword%"), String::class.java)
    }

    private fun transformToJSON(translations: List<String>, to: String): String {
        val jsonArray = JsonArray()
        translations.forEach { translation ->
            val translationObject = JsonObject()
            val translationArray = JsonArray()
            val translationItem = JsonObject()
            translationItem.addProperty("text", translation)
            translationItem.addProperty("to", to)
            translationArray.add(translationItem)
            translationObject.add("translations", translationArray)
            jsonArray.add(translationObject)
        }
        return jsonArray.toString()
    }
}
