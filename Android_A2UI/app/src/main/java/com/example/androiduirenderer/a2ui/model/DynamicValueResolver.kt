package com.example.androiduirenderer.a2ui.model

import android.util.Log

/**
 * Resolves DynamicValue instances to concrete values using a DataModel.
 */
class DynamicValueResolver(private val dataModel: DataModel) {
    
    /**
     * Resolve a DynamicValue to a concrete value.
     * @param dynamicValue The DynamicValue to resolve
     * @return The resolved value as the appropriate type (String, Number, Boolean, etc.)
     */
    fun resolve(dynamicValue: DynamicValue): Any? {
        return when (dynamicValue) {
            is DynamicValue.LiteralString -> dynamicValue.value
            is DynamicValue.LiteralNumber -> dynamicValue.value
            is DynamicValue.LiteralBoolean -> dynamicValue.value
            is DynamicValue.LiteralArray -> dynamicValue.value.map { resolve(it) }
            is DynamicValue.DataBinding -> resolveDataBinding(dynamicValue)
            is DynamicValue.FunctionCall -> resolveFunctionCall(dynamicValue)
        }
    }
    
    /**
     * Resolve a DynamicValue to a String.
     * Convenience method for text components.
     */
    fun resolveToString(dynamicValue: DynamicValue): String {
        return when (val resolved = resolve(dynamicValue)) {
            is String -> resolved
            else -> resolved?.toString() ?: ""
        }
    }
    
    /**
     * Resolve a DynamicValue to a Number (as Int).
     * Convenience method for numeric components.
     */
    fun resolveToInt(dynamicValue: DynamicValue): Int {
        return when (val resolved = resolve(dynamicValue)) {
            is Number -> resolved.toInt()
            is Boolean -> if (resolved) 1 else 0
            else -> 0
        }
    }
    
    /**
     * Resolve a DynamicValue to a Boolean.
     * Convenience method for checkbox components.
     */
    fun resolveToBoolean(dynamicValue: DynamicValue): Boolean {
        return when (val resolved = resolve(dynamicValue)) {
            is Boolean -> resolved
            is Number -> resolved.toInt() != 0
            is String -> resolved.lowercase() in setOf("true", "yes", "1", "on")
            else -> false
        }
    }
    
    /**
     * Add a listener for changes to a DynamicValue's underlying data.
     * Only works for DataBinding values; for literals, calls immediately.
     * @param dynamicValue The DynamicValue to listen to
     * @param listener Callback invoked when the value changes
     * @return A cleanup function to remove the listener
     */
    fun addChangeListener(dynamicValue: DynamicValue, listener: (Any?) -> Unit): () -> Unit {
        return when (dynamicValue) {
            is DynamicValue.DataBinding -> {
                dataModel.addListener(dynamicValue.path) { value ->
                    listener(value)
                }
                val cleanup: () -> Unit = { dataModel.removeListener(dynamicValue.path, listener) }
                cleanup
            }
            else -> {
                // For literal values, call once immediately
                listener(resolve(dynamicValue))
                val cleanup: () -> Unit = { }
                cleanup
            }
        }
    }
    
    /**
     * Set a value in the data model for a DataBinding DynamicValue.
     * If the DynamicValue is not a DataBinding, does nothing.
     * @param dynamicValue The DynamicValue representing the binding path
     * @param value The value to set
     */
    fun setValue(dynamicValue: DynamicValue, value: Any?) {
        when (dynamicValue) {
            is DynamicValue.DataBinding -> {
                dataModel.setValue(dynamicValue.path, value)
            }
            else -> {
                // Literal values cannot be updated
            }
        }
    }
    
    /**
     * Get the underlying data model (for advanced use cases).
     */
    fun getDataModel(): DataModel = dataModel
    
    private fun resolveDataBinding(dataBinding: DynamicValue.DataBinding): Any? {
        Log.d("DynamicValueResolver", "Resolving data binding: ${dataBinding.path}")
        return dataModel.getValue(dataBinding.path)
    }
    
    private fun resolveFunctionCall(functionCall: DynamicValue.FunctionCall): Any? {
        Log.d("DynamicValueResolver", "Resolving function call: ${functionCall.call}")
        // For now, return a placeholder value
        // In a real implementation, you would execute the function with args
        return when (functionCall.returnType) {
            "string" -> "function:${functionCall.call}"
            "number" -> 0
            "boolean" -> false
            else -> null
        }
    }
    
    companion object {
        /**
         * Create a DynamicValue from a concrete value.
         * Useful for creating literal values programmatically.
         */
        fun createDynamicValue(value: Any?): DynamicValue {
            return when (value) {
                is String -> DynamicValue.LiteralString(value)
                is Number -> DynamicValue.LiteralNumber(value)
                is Boolean -> DynamicValue.LiteralBoolean(value)
                is List<*> -> {
                    val list = value.map { createDynamicValue(it) }
                    DynamicValue.LiteralArray(list)
                }
                else -> DynamicValue.LiteralString(value?.toString() ?: "")
            }
        }
    }
}