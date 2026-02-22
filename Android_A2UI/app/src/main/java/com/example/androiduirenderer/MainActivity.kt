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

/**
 * MainActivity demonstrating A2UI progressive rendering pattern
 * 
 * Flow:
 * 1. Parse JSONL stream
 * 2. Buffer all surfaceUpdate and dataModelUpdate messages (NO RENDERING)
 * 3. Wait for beginRendering signal
 * 4. On beginRendering: Build tree → Resolve bindings → Render
 */
class MainActivity : AppCompatActivity() {
    private lateinit var a2uiRenderer: A2UIRenderer
    private val parser = A2UIParser(Gson())
    private var currentSurfaceId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "onCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize renderer
        val dataModel = DataModel()
        val dynamicValueResolver = DynamicValueResolver(dataModel)
        A2UIComponents.dynamicValueResolver = dynamicValueResolver
        
        a2uiRenderer = A2UIRenderer(this)
        a2uiRenderer.setOnNavigateListener { surfaceId ->
            navigateToSurface(surfaceId)
        }
        a2uiRenderer.setOnActionListener { action, context ->
            handleUserAction(action, context)
        }

        val container = findViewById<LinearLayout>(R.id.container)

        try {
            // Load and parse JSONL configuration
            val inputStream = assets.open("config.a2ui.jsonl")
            val reader = BufferedReader(InputStreamReader(inputStream))

            // STEP 1-3: Parse JSONL and buffer all updates (NO RENDERING YET)
            Log.d("MainActivity", "STEP 1-3: Parsing JSONL and buffering updates")
            reader.useLines { lines ->
                lines.forEach { line ->
                    if (line.isNotBlank()) {
                        try {
                            val response = parser.parseMessage(line)
                            a2uiRenderer.processMessage(response)
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Error parsing A2UI message: ${e.message}", e)
                        }
                    }
                }
            }

            // STEP 4: Trigger rendering with beginRendering
            // In a real scenario, beginRendering comes from the server
            // Here we manually trigger it for the first surface
            Log.d("MainActivity", "STEP 4: Triggering beginRendering")
            val firstSurfaceId = a2uiRenderer.getSurface("page1")?.surfaceId 
                ?: a2uiRenderer.getSurface("surface1")?.surfaceId
                ?: throw IllegalStateException("No surfaces found")
            
            currentSurfaceId = firstSurfaceId
            val beginRendering = A2UIMessage.BeginRendering(
                surfaceId = firstSurfaceId,
                rootComponentId = "root" // Or let it auto-detect
            )
            a2uiRenderer.handleBeginRendering(beginRendering, container)

        } catch (e: Exception) {
            Toast.makeText(this, "Error loading A2UI configuration: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun navigateToSurface(surfaceId: String) {
        Log.d("MainActivity", "Navigating to surface: $surfaceId")
        val container = findViewById<LinearLayout>(R.id.container)
        
        currentSurfaceId = surfaceId
        val beginRendering = A2UIMessage.BeginRendering(
            surfaceId = surfaceId,
            rootComponentId = "root"
        )
        a2uiRenderer.handleBeginRendering(beginRendering, container)
    }

    private fun handleUserAction(action: Action, context: Map<String, Any?>) {
        Log.d("MainActivity", "User action: $action, context: $context")
        
        when (action) {
            is Action.Navigate -> {
                navigateToSurface(action.surfaceId)
            }
            is Action.Event -> {
                when (action.name) {
                    "next" -> {
                        // Navigate to next surface
                        currentSurfaceId?.let { current ->
                            val nextId = when (current) {
                                "page1" -> "page2"
                                "page2" -> "page3"
                                else -> null
                            }
                            nextId?.let { navigateToSurface(it) }
                        }
                    }
                    "back" -> {
                        // Navigate to previous surface
                        currentSurfaceId?.let { current ->
                            val prevId = when (current) {
                                "page3" -> "page2"
                                "page2" -> "page1"
                                else -> null
                            }
                            prevId?.let { navigateToSurface(it) }
                        }
                    }
                    "finish" -> {
                        finish()
                    }
                    else -> {
                        Toast.makeText(this, "Event: ${action.name}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            is Action.FunctionCall -> {
                when (action.call) {
                    "navigateTo" -> {
                        val surfaceId = action.args["surfaceId"]?.let { 
                            when (it) {
                                is DynamicValue.LiteralString -> it.value
                                else -> it.toString()
                            }
                        }
                        surfaceId?.let { navigateToSurface(it) }
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
        // Simple back navigation
        currentSurfaceId?.let { current ->
            val prevId = when (current) {
                "page2" -> "page1"
                "page3" -> "page2"
                else -> null
            }
            if (prevId != null) {
                navigateToSurface(prevId)
                return
            }
        }
        super.onBackPressed()
    }
}
