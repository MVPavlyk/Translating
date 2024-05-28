package com.example.translating.controller

import com.example.translating.service.DictionaryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/api/dictionary")
class DictionaryController {

    @Autowired
    lateinit var dictionaryService: DictionaryService

    @GetMapping("/translate/english/{word}")
    fun translateEnglish(@PathVariable word: String): ResponseEntity<String> {
        println(word)
        val translation = dictionaryService.getOrTranslateEnglish(word)
        println(translation)
        return if (translation?.contains("error") == false) {
            ResponseEntity.ok(translation)
        } else {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Translation failed")
        }
    }

    @GetMapping("/translate/french/{word}")
    fun translateUkrainian(@PathVariable word: String): ResponseEntity<String> {
        println(word)
        val translation = dictionaryService.getOrTranslateUkrainian(word)
        return if (translation?.contains("error") == false) {
            ResponseEntity.ok(translation)
        } else {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Translation failed")
        }
    }

    @GetMapping("/search/english/{keyword}")
    fun searchEnglish(@PathVariable keyword: String): ResponseEntity<List<String>> {
        val searchResults = dictionaryService.searchEnglish(keyword)
        return ResponseEntity.ok(searchResults)
    }

    @GetMapping("/search/french/{keyword}")
    fun searchUkrainian(@PathVariable keyword: String): ResponseEntity<List<String>> {
        val searchResults = dictionaryService.searchUkrainian(keyword)
        return ResponseEntity.ok(searchResults)
    }
}