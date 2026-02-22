package com.example.androiduirenderer.a2ui.model

import android.util.Log

/**
 * Simple data model for A2UI that stores values by path and notifies listeners of changes.
 * Supports nested paths like "user.name", "settings.theme.darkMode".
 */
class DataModel {
    private val data = mutableMapOf<String, Any?>()
    private val listeners = mutableMapOf<String, MutableList<(Any?) -> Unit>>()
    
    /**
     * Set a value at the given path.
     * @param path Dot-separated path (e.g., "user.name")
     * @param value The value to store (can be null)
     */
    fun setValue(path: String, value: Any?) {
        Log.d("DataModel", "Setting $path = $value")
        data[path] = value
        notifyListeners(path, value)
        // Also notify parent path listeners
        notifyParentPaths(path, value)
    }
    
    /**
     * Get a value at the given path.
     * @param path Dot-separated path
     * @return The value or null if not found
     */
    fun getValue(path: String): Any? {
        return data[path]
    }
    
    /**
     * Update multiple values from a map.
     * @param updates Map of path -> value
     */
    fun updateValues(updates: Map<String, Any?>) {
        updates.forEach { (path, value) ->
            setValue(path, value)
        }
    }
    
    /**
     * Add a listener for changes at a specific path.
     * @param path The path to listen to
     * @param listener Callback invoked when the path's value changes
     */
    fun addListener(path: String, listener: (Any?) -> Unit) {
        listeners.getOrPut(path) { mutableListOf() }.add(listener)
        // Immediately call with current value
        listener(getValue(path))
    }
    
    /**
     * Remove a listener.
     */
    fun removeListener(path: String, listener: (Any?) -> Unit) {
        listeners[path]?.remove(listener)
    }
    
    /**
     * Clear all data and listeners.
     */
    fun clear() {
        data.clear()
        listeners.clear()
    }
    
    private fun notifyListeners(path: String, value: Any?) {
        listeners[path]?.forEach { listener ->
            try {
                listener(value)
            } catch (e: Exception) {
                Log.e("DataModel", "Error in listener for $path", e)
            }
        }
    }
    
    private fun notifyParentPaths(fullPath: String, value: Any?) {
        val parts = fullPath.split(".")
        if (parts.size > 1) {
            // Notify parent paths (e.g., for "user.profile.name", notify "user.profile" and "user")
            for (i in 1 until parts.size) {
                val parentPath = parts.subList(0, i).joinToString(".")
                listeners[parentPath]?.forEach { listener ->
                    try {
                        // For parent paths, we might want to send the whole subtree
                        // For now, just send the value
                        listener(value)
                    } catch (e: Exception) {
                        Log.e("DataModel", "Error in parent listener for $parentPath", e)
                    }
                }
            }
        }
    }
    
    companion object {
        /**
         * Parse a JSON-like value from UpdateDataModel message.
         * This is a simplified implementation.
         */
        fun parseValue(value: Any?): Any? {
            return when (value) {
                is String -> value
                is Number -> value
                is Boolean -> value
                null -> null
                else -> value.toString()
            }
        }
    }
}