package com.example.androiduirenderer.core

import com.example.androiduirenderer.model.PageConfig
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.InputStream
import java.io.InputStreamReader

class ConfigurationParser(private val gson: Gson = Gson()) {
    fun parsePages(inputStream: InputStream): List<PageConfig> {
        val reader = InputStreamReader(inputStream)
        val pages = mutableListOf<PageConfig>()
        reader.useLines { lines ->
            lines.forEachIndexed { index, line ->
                if (line.isNotBlank()) {
                    try {
                        val page = gson.fromJson(line, PageConfig::class.java)
                        pages.add(page)
                    } catch (e: JsonSyntaxException) {
                        throw IllegalArgumentException("Invalid JSON at line ${index + 1}: ${e.message}")
                    }
                }
            }
        }
        return pages
    }

    fun parsePages(json: String): List<PageConfig> {
        return json.lines()
            .filter { it.isNotBlank() }
            .mapIndexed { index, line ->
                try {
                    gson.fromJson(line, PageConfig::class.java)
                } catch (e: JsonSyntaxException) {
                    throw IllegalArgumentException("Invalid JSON at line ${index + 1}: ${e.message}")
                }
            }
    }
}