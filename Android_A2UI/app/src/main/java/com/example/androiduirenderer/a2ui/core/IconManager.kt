package com.example.androiduirenderer.a2ui.core

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import java.io.InputStreamReader

/**
 * Icon Manager for HSBC Banking App
 * Manages icon resources extracted from app screenshots
 */
data class IconManifest(
    val icons: List<IconEntry>,
    val total: Int
)

data class IconEntry(
    val name: String,
    val file: String,
    val source: String,
    val position: List<Int>
)

class IconManager private constructor(private val context: Context) {
    private val iconCache = mutableMapOf<String, Int>()
    private val manifest: IconManifest?
    
    init {
        manifest = loadManifest()
        populateIconCache()
    }
    
    /**
     * Load icon manifest from assets
     */
    private fun loadManifest(): IconManifest? {
        return try {
            context.assets.open("icons_manifest.json").use { inputStream ->
                val reader = InputStreamReader(inputStream)
                Gson().fromJson(reader, IconManifest::class.java)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load icon manifest", e)
            null
        }
    }
    
    /**
     * Populate icon cache with resource IDs
     */
    private fun populateIconCache() {
        manifest?.icons?.forEach { icon ->
            val resourceName = icon.file.replace(".png", "")
            val resourceId = context.resources.getIdentifier(
                resourceName,
                "drawable",
                context.packageName
            )
            if (resourceId != 0) {
                iconCache[icon.name] = resourceId
                Log.d(TAG, "Mapped icon '${icon.name}' -> R.drawable.$resourceName ($resourceId)")
            } else {
                Log.w(TAG, "Icon resource not found: ${icon.file}")
            }
        }
    }
    
    /**
     * Get drawable resource ID for an icon by name
     * @param iconName Name of the icon (e.g., "home", "transfer", "wealth")
     * @return Resource ID or 0 if not found
     */
    fun getIconResource(iconName: String): Int {
        return iconCache[iconName] ?: run {
            Log.w(TAG, "Icon not found: $iconName")
            0
        }
    }
    
    /**
     * Get Drawable object for an icon by name
     * @param iconName Name of the icon
     * @return Drawable or null if not found
     */
    fun getIconDrawable(iconName: String): Drawable? {
        val resourceId = getIconResource(iconName)
        return if (resourceId != 0) {
            ContextCompat.getDrawable(context, resourceId)
        } else {
            null
        }
    }
    
    /**
     * Check if an icon exists
     */
    fun hasIcon(iconName: String): Boolean {
        return iconCache.containsKey(iconName)
    }
    
    /**
     * Get all available icon names
     */
    fun getAvailableIcons(): List<String> {
        return iconCache.keys.toList()
    }
    
    /**
     * Get icon count
     */
    fun getIconCount(): Int {
        return iconCache.size
    }
    
    /**
     * Register a custom icon mapping at runtime
     */
    fun registerIcon(name: String, resourceId: Int) {
        iconCache[name] = resourceId
        Log.d(TAG, "Registered custom icon: $name -> $resourceId")
    }
    
    companion object {
        private const val TAG = "IconManager"
        private var instance: IconManager? = null
        
        /**
         * Get singleton instance
         * Must be initialized with context first
         */
        fun getInstance(): IconManager? {
            return instance
        }
        
        /**
         * Initialize the IconManager with application context
         * Should be called once during app initialization
         */
        fun initialize(context: Context): IconManager {
            if (instance == null) {
                instance = IconManager(context.applicationContext)
                Log.d(TAG, "IconManager initialized with ${instance?.getIconCount() ?: 0} icons")
            }
            return instance!!
        }
        
        /**
         * Clear singleton instance (for testing)
         */
        fun clearInstance() {
            instance = null
        }
    }
}
