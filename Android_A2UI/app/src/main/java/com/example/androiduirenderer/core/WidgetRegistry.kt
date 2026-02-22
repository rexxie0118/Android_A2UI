package com.example.androiduirenderer.core

import android.view.View
import android.view.ViewGroup

import com.example.androiduirenderer.model.WidgetConfig

typealias WidgetFactory = (WidgetConfig, ViewGroup) -> View

class WidgetRegistry private constructor() {
    private val registry = mutableMapOf<String, WidgetFactory>()

    fun register(type: String, factory: WidgetFactory) {
        registry[type] = factory
    }

    fun unregister(type: String) {
        registry.remove(type)
    }

    fun createWidget(config: WidgetConfig, parent: ViewGroup): View {
        val factory = registry[config.type]
            ?: throw IllegalArgumentException("Unknown widget type: ${config.type}")
        return factory(config, parent)
    }

    fun hasType(type: String): Boolean = registry.containsKey(type)

    companion object {
        private var instance: WidgetRegistry? = null

        fun getInstance(): WidgetRegistry {
            return instance ?: synchronized(this) {
                instance ?: WidgetRegistry().also { instance = it }
            }
        }
    }
}