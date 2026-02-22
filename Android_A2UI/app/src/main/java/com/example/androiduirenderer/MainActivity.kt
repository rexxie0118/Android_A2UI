package com.example.androiduirenderer

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.androiduirenderer.a2ui.core.*
import com.example.androiduirenderer.a2ui.model.*
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    private lateinit var a2uiRenderer: A2UIRenderer
    private val surfaces = mutableMapOf<String, List<A2UIComponent>>()
    private val surfaceThemes = mutableMapOf<String, Theme>()
    private var currentSurfaceId: String? = null
    private val dataModel = DataModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up dynamic value resolver with data model
        val dynamicValueResolver = DynamicValueResolver(dataModel)
        A2UIComponents.dynamicValueResolver = dynamicValueResolver
        a2uiRenderer = A2UIRenderer(this)
        val container = findViewById<LinearLayout>(R.id.container)

        try {
            val inputStream = assets.open("config.a2ui.jsonl")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val parser = A2UIParser(Gson())
            
            reader.useLines { lines ->
                lines.forEach { line ->
                    if (line.isNotBlank()) {
                        try {
                            val response = parser.parseMessage(line)
                            processA2UIResponse(response, container)
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Error parsing A2UI message: ${e.message}", e)
                        }
                    }
                }
            }
            
            // Show first surface (page1)
            navigateToSurface("page1", container)
            
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading A2UI configuration: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun processA2UIResponse(response: A2UIResponse, container: ViewGroup) {
        response.createSurface?.let { createSurface ->
            Log.d("MainActivity", "Created surface: ${createSurface.surfaceId} with theme ${createSurface.theme?.name ?: "default"}")
            // Store theme for this surface
            createSurface.theme?.let { theme ->
                surfaceThemes[createSurface.surfaceId] = theme
            }
        }
        
        response.updateComponents?.let { updateComponents ->
            Log.d("MainActivity", "Updating components for surface: ${updateComponents.surfaceId}")
            surfaces[updateComponents.surfaceId] = updateComponents.components
        }
        
        response.updateDataModel?.let { updateDataModel ->
            Log.d("MainActivity", "Update data model for surface: ${updateDataModel.surfaceId}")
            // Handle data model updates
            updateDataModel.path?.let { path ->
                updateDataModel.value?.let { jsonValue ->
                    // Parse JSON value to actual value
                    val value = parseJsonValue(jsonValue)
                    dataModel.setValue(path, value)
                    Log.d("MainActivity", "Set data model $path = $value")
                }
            }
        }
        
        response.deleteSurface?.let { deleteSurface ->
            Log.d("MainActivity", "Delete surface: ${deleteSurface.surfaceId}")
            surfaces.remove(deleteSurface.surfaceId)
            if (currentSurfaceId == deleteSurface.surfaceId) {
                // Navigate to another surface if current is deleted
            }
        }
    }

    private fun navigateToSurface(surfaceId: String, container: ViewGroup) {
        val components = surfaces[surfaceId]
        if (components != null) {
            Log.d("MainActivity", "Navigating to surface: $surfaceId")
            currentSurfaceId = surfaceId
            val theme = surfaceThemes[surfaceId]
            a2uiRenderer.renderSurface(components, container, theme)
            setupActionHandlers(surfaceId, container)
        } else {
            Log.w("MainActivity", "Surface $surfaceId not found")
        }
    }

    private fun setupActionHandlers(surfaceId: String, container: ViewGroup) {
        val components = surfaces[surfaceId] ?: return
        
        components.forEach { component ->
            when (component) {
                is A2UIComponent.Button -> {
                    val view = a2uiRenderer.getViewForComponent(component.id)
                    if (view != null) {
                        Log.d("MainActivity", "Setting click listener for button ${component.id}")
                        view.setOnClickListener {
                            Log.d("MainActivity", "Button ${component.id} clicked")
                            handleA2UIAction(component.action, container)
                        }
                    } else {
                        Log.w("MainActivity", "Button view not found for ${component.id}")
                    }
                }
                else -> {
                    // Other interactive components
                }
            }
        }
    }

    private fun handleA2UIAction(action: Action, container: ViewGroup) {
        when (action) {
            is Action.Event -> {
                Log.d("MainActivity", "Event action: ${action.name}")
                Toast.makeText(this, "Event: ${action.name}", Toast.LENGTH_SHORT).show()
            }
            is Action.FunctionCallAction -> {
                Log.d("MainActivity", "Function call: ${action.call}")
                // Helper to resolve a DynamicValue argument
                fun resolveArg(value: DynamicValue?): Any? {
                    if (value == null) return null
                    val resolver = A2UIComponents.dynamicValueResolver
                    return if (resolver != null) {
                        resolver.resolve(value)
                    } else {
                        when (value) {
                            is DynamicValue.LiteralString -> value.value
                            is DynamicValue.LiteralNumber -> value.value
                            is DynamicValue.LiteralBoolean -> value.value
                            is DynamicValue.LiteralArray -> value.value.map { resolveArg(it) }
                            else -> value.toString()
                        }
                    }
                }
                when (action.call) {
                    "navigateTo" -> {
                        val surfaceIdArg = action.args["surfaceId"]
                        val surfaceId = when (val resolved = resolveArg(surfaceIdArg)) {
                            is String -> resolved
                            else -> surfaceIdArg?.toString() ?: ""
                        }
                        navigateToSurface(surfaceId, container)
                    }
                    "finishApp" -> {
                        finish()
                    }
                    else -> {
                        Toast.makeText(this, "Function: ${action.call}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        // Simple back navigation: go to previous surface
        when (currentSurfaceId) {
            "page2" -> {
                val container = findViewById<LinearLayout>(R.id.container)
                navigateToSurface("page1", container)
            }
            "page3" -> {
                val container = findViewById<LinearLayout>(R.id.container)
                navigateToSurface("page2", container)
            }
            else -> super.onBackPressed()
        }
    }
    
    private fun parseJsonValue(jsonElement: com.google.gson.JsonElement): Any? {
        return when {
            jsonElement.isJsonPrimitive -> {
                val primitive = jsonElement.asJsonPrimitive
                when {
                    primitive.isString -> primitive.asString
                    primitive.isNumber -> primitive.asNumber
                    primitive.isBoolean -> primitive.asBoolean
                    else -> null
                }
            }
            jsonElement.isJsonArray -> {
                jsonElement.asJsonArray.map { parseJsonValue(it) }
            }
            jsonElement.isJsonObject -> {
                val obj = mutableMapOf<String, Any?>()
                jsonElement.asJsonObject.entrySet().forEach { (key, value) ->
                    obj[key] = parseJsonValue(value)
                }
                obj
            }
            jsonElement.isJsonNull -> null
            else -> null
        }
    }
}