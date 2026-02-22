package com.example.androiduirenderer.a2ui.core

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.example.androiduirenderer.a2ui.model.A2UIComponent

typealias ComponentFactory = (A2UIComponent, ViewGroup, Context) -> View

class ComponentCatalog private constructor() {
    private val registry = mutableMapOf<String, ComponentFactory>()

    fun register(type: String, factory: ComponentFactory) {
        registry[type] = factory
    }

    fun unregister(type: String) {
        registry.remove(type)
    }

    fun createComponent(component: A2UIComponent, parent: ViewGroup, context: Context): View {
        val factory = registry[component::class.simpleName]
            ?: throw IllegalArgumentException("Unknown component type: ${component::class.simpleName}")
        return factory(component, parent, context)
    }

    fun hasType(type: String): Boolean = registry.containsKey(type)

    companion object {
        private var instance: ComponentCatalog? = null

        fun getInstance(): ComponentCatalog {
            return instance ?: synchronized(this) {
                instance ?: ComponentCatalog().also { instance = it }
            }
        }
    }
}