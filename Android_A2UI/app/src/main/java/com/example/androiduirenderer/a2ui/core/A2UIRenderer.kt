package com.example.androiduirenderer.a2ui.core

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import com.example.androiduirenderer.a2ui.model.*

/**
 * A2UI Renderer following the v0.8 specification
 * 
 * Rendering Pipeline:
 * 1. Parse JSONL Stream
 * 2. Dispatch Messages (surfaceUpdate, dataModelUpdate, etc.)
 * 3. Buffer Components & Data (NO RENDERING YET)
 * 4. Receive beginRendering
 * 5. Build Widget Tree (lookup components in registry)
 * 6. Resolve Data Bindings (literal* vs path logic)
 * 7. Render Native Widgets
 * 8. Handle User Actions
 */
class A2UIRenderer(private val context: Context) {
    private val surfaces = mutableMapOf<String, SurfaceState>()
    private var onNavigateListener: ((surfaceId: String) -> Unit)? = null
    private var onActionListener: ((action: Action, context: Map<String, Any?>) -> Unit)? = null

    // Runtime view tracking
    private val componentViews = mutableMapOf<String, View>()
    private val viewBindings = mutableMapOf<View, ViewBinding>()

    fun setOnNavigateListener(listener: (surfaceId: String) -> Unit) {
        onNavigateListener = listener
    }

    fun setOnActionListener(listener: (action: Action, context: Map<String, Any?>) -> Unit) {
        onActionListener = listener
    }

    /**
     * Process an A2UI message - buffer updates, don't render yet
     */
    fun processMessage(response: A2UIResponse) {
        response.surfaceUpdate?.let { surfaceUpdate ->
            handleSurfaceUpdate(surfaceUpdate)
        }

        response.dataModelUpdate?.let { dataModelUpdate ->
            handleDataModelUpdate(dataModelUpdate)
        }

        response.deleteSurface?.let { deleteSurface ->
            handleDeleteSurface(deleteSurface)
        }

        response.beginRendering?.let { beginRendering ->
            // Progressive rendering: only render when beginRendering arrives
            // Container will be provided when actually rendering
        }
    }

    private fun handleSurfaceUpdate(update: A2UIMessage.SurfaceUpdate) {
        Log.d("A2UIRenderer", "Buffering surfaceUpdate for ${update.surfaceId}: ${update.components.size} components")
        
        val surface = surfaces.getOrPut(update.surfaceId) {
            SurfaceState(
                surfaceId = update.surfaceId,
                catalogId = update.globalStyles?.let { "default" } // Could use catalogId from message
            )
        }

        // Buffer components in adjacency list pattern
        update.components.forEach { component ->
            surface.componentBuffer[component.id] = component
        }

        // Apply global styles if present
        update.globalStyles?.let { styles ->
            // Apply font, primaryColor, backgroundColor etc.
        }
    }

    private fun handleDataModelUpdate(update: A2UIMessage.DataModelUpdate) {
        Log.d("A2UIRenderer", "Buffering dataModelUpdate for ${update.surfaceId}: ${update.path}")
        
        val surface = surfaces.getOrPut(update.surfaceId) {
            SurfaceState(surfaceId = update.surfaceId)
        }

        // Update data model
        val value = when {
            update.literal != null -> parseJsonValue(update.literal)
            update.value != null -> parseJsonValue(update.value)
            else -> null
        }
        surface.dataModel[update.path] = value
    }

    private fun handleDeleteSurface(delete: A2UIMessage.DeleteSurface) {
        Log.d("A2UIRenderer", "Deleting surface: ${delete.surfaceId}")
        surfaces.remove(delete.surfaceId)
        componentViews.clear()
        viewBindings.clear()
    }

    /**
     * STEP 4-7: Handle beginRendering signal
     * - Build widget tree from root
     * - Resolve data bindings
     * - Render to container
     */
    fun handleBeginRendering(beginRendering: A2UIMessage.BeginRendering, container: ViewGroup) {
        Log.d("A2UIRenderer", "beginRendering for ${beginRendering.surfaceId}, root: ${beginRendering.rootComponentId ?: "auto"}")
        
        val surface = surfaces[beginRendering.surfaceId]
            ?: throw IllegalStateException("Surface ${beginRendering.surfaceId} not found")

        // Clear previous views
        container.removeAllViews()
        componentViews.clear()
        viewBindings.clear()

        // Find root component
        val rootComponentId = beginRendering.rootComponentId 
            ?: surface.componentBuffer.keys.firstOrNull()
            ?: throw IllegalStateException("No components buffered for surface")

        val rootComponent = surface.componentBuffer[rootComponentId]
            ?: throw IllegalStateException("Root component $rootComponentId not found")

        // STEP 5: Build Widget Tree (lookup in registry via ComponentCatalog)
        Log.d("A2UIRenderer", "STEP 5: Building widget tree from root: $rootComponentId")
        val rootBinding = buildWidgetTree(rootComponent, surface, container)

        // STEP 6: Resolve Data Bindings (separate pass)
        Log.d("A2UIRenderer", "STEP 6: Resolving data bindings")
        resolveDataBinding(rootBinding, surface)

        // STEP 7: Render - add root view to container
        container.addView(rootBinding.view)

        Log.d("A2UIRenderer", "Rendering complete for surface ${beginRendering.surfaceId}")
    }

    /**
     * STEP 5: Build Widget Tree
     * Recursively create views and store binding metadata
     */
    private fun buildWidgetTree(
        component: A2UIComponent,
        surface: SurfaceState,
        parent: ViewGroup
    ): ViewBinding {
        // Create view using ComponentCatalog (widget registry lookup)
        val binding = A2UIComponents.buildWidgetTree(component, parent, context)
        componentViews[component.id] = binding.view
        viewBindings[binding.view] = binding

        // Handle children for container components
        when (component) {
            is A2UIComponent.Row -> {
                handleChildren(component.children, surface, binding.view as? ViewGroup ?: parent)
            }
            is A2UIComponent.Column -> {
                handleChildren(component.children, surface, binding.view as? ViewGroup ?: parent)
            }
            is A2UIComponent.A2UIList -> {
                handleChildren(component.children, surface, binding.view as? ViewGroup ?: parent)
            }
            is A2UIComponent.Card -> {
                handleChildren(component.children, surface, binding.view as? ViewGroup ?: parent)
            }
            is A2UIComponent.Button -> {
                component.child?.let { childId ->
                    surface.componentBuffer[childId]?.let { childComponent ->
                        val childBinding = buildWidgetTree(childComponent, surface, binding.view as? ViewGroup ?: parent)
                        if (binding.view is ViewGroup) {
                            binding.view.addView(childBinding.view)
                        }
                    }
                }
            }
            is A2UIComponent.Tabs -> {
                // Create tab views
                component.tabItems.forEachIndexed { index, tabItem ->
                    val tabComponent = surface.componentBuffer[tabItem.child]
                    tabComponent?.let { childComponent ->
                        val childBinding = buildWidgetTree(childComponent, surface, binding.view as? ViewGroup ?: parent)
                        if (binding.view is ViewGroup) {
                            binding.view.addView(childBinding.view)
                        }
                    }
                }
            }
            else -> {
                // Leaf components or components without children
            }
        }

        return binding
    }

    private fun handleChildren(
        children: ChildrenList,
        surface: SurfaceState,
        parentView: ViewGroup
    ) {
        val childIds = when (children) {
            is ChildrenList.ExplicitList -> children.children
            is ChildrenList.Child -> listOf(children.childId)
            is ChildrenList.ContentChild -> listOf(children.contentChildId)
            is ChildrenList.Template -> {
                // Dynamic list rendering
                expandTemplate(children, surface, parentView)
            }
        }

        childIds.forEach { childId ->
            surface.componentBuffer[childId]?.let { childComponent ->
                val childBinding = buildWidgetTree(childComponent, surface, parentView)
                if (parentView is ViewGroup) {
                    parentView.addView(childBinding.view)
                }
            }
        }
    }

    private fun expandTemplate(
        template: ChildrenList.Template,
        surface: SurfaceState,
        parentView: ViewGroup
    ): List<String> {
        // Get data list from data model
        val dataList = surface.dataModel[template.dataBinding] as? List<*> ?: return emptyList()
        
        val childIds = mutableListOf<String>()
        
        dataList.forEachIndexed { index, item ->
            val childId = "${template.componentId}_$index"
            
            // Clone template component with adjusted data binding
            val templateComponent = surface.componentBuffer[template.componentId]
            if (templateComponent != null) {
                val clonedComponent = cloneComponentForTemplate(templateComponent, childId, template.dataBinding, index, item)
                if (clonedComponent != null) {
                    surface.componentBuffer[childId] = clonedComponent
                    childIds.add(childId)
                }
            }
        }
        
        return childIds
    }

    private fun cloneComponentForTemplate(
        original: A2UIComponent,
        newId: String,
        dataPath: String,
        index: Int,
        item: Any?
    ): A2UIComponent? {
        // Handle component cloning for templates
        // Note: Data binding adjustment is currently disabled due to compiler type inference issue
        return if (original is A2UIComponent.Text) {
            original.copy(id = newId, text = original.text)
        } else if (original is A2UIComponent.CheckBox) {
            original.copy(id = newId, label = original.label, checked = original.checked, value = null)
        } else if (original is A2UIComponent.TextField) {
            original.copy(id = newId, label = original.label, text = original.text)
        } else if (original is A2UIComponent.A2UIList) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.Row) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.Column) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.Card) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.Slider) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.ProgressBar) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.Image) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.Icon) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.Video) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.AudioPlayer) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.Divider) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.Tabs) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.Modal) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.Button) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.DateTimeInput) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.MultipleChoice) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.ChoicePicker) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.BalanceDisplay) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.AccountCard) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.ActionButton) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.QuickActionCircle) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.NavigationBar) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.AppBar) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.ProductIcon) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.View) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.Overlay) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.SegmentedTab) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.Tab) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.MenuItem) {
            original.copy(id = newId)
        } else if (original is A2UIComponent.MenuSection) {
            original.copy(id = newId)
        } else {
            // Unknown component type
            null
        }
    }

    /**
     * STEP 6: Resolve Data Bindings
     * Walk the view tree and resolve all bindings using the data model
     * 
     * Resolution Logic:
     * - literal* only: Use literal value directly
     * - path only: Resolve against data model
     * - both: Update data model at path with literal, then bind
     */
    private fun resolveDataBinding(binding: ViewBinding, surface: SurfaceState) {
        val resolver = A2UIComponents.dynamicValueResolver ?: return

        // Resolve text binding
        binding.textBinding?.let { dv ->
            val value = resolveBoundValue(dv, surface)
            (binding.view as? TextView)?.text = value?.toString() ?: ""
        }

        // Resolve label binding
        binding.labelBinding?.let { dv ->
            val value = resolveBoundValue(dv, surface)
            when (binding.view) {
                is CheckBox -> binding.view.text = value?.toString() ?: ""
                is EditText -> binding.view.hint = value?.toString() ?: ""
                is TextView -> binding.view.text = value?.toString() ?: ""
            }
        }

        // Resolve value binding
        binding.valueBinding?.let { dv ->
            val value = resolveBoundValue(dv, surface)
            when (binding.view) {
                is CheckBox -> binding.view.isChecked = value as? Boolean ?: false
                is SeekBar -> binding.view.progress = (value as? Number)?.toInt() ?: 0
                is EditText -> binding.view.setText(value?.toString() ?: "")
                is TextView -> binding.view.text = value?.toString() ?: ""
            }
        }

        // Resolve checked binding
        binding.checkedBinding?.let { dv ->
            val value = resolveBoundValue(dv, surface)
            (binding.view as? CheckBox)?.isChecked = value as? Boolean ?: false
        }

        // Resolve icon/src bindings
        binding.iconId?.let { iconId ->
            (binding.view as? ImageView)?.let { imageView ->
                IconManager.getInstance()?.getIconResource(iconId.lowercase())?.let {
                    imageView.setImageResource(it)
                }
            }
        }

        // Resolve color binding
        binding.colorBinding?.let { dv ->
            (binding.view as? ImageView)?.let { imageView ->
                val color = resolveBoundValue(dv, surface)?.toString() ?: ""
                if (color.isNotEmpty()) {
                    try {
                        imageView.setColorFilter(android.graphics.Color.parseColor(color))
                    } catch (e: Exception) {
                        // Invalid color format
                    }
                }
            }
        }

        // Recursively resolve children
        binding.children.forEach { child ->
            resolveDataBinding(child, surface)
        }
    }

    /**
     * Resolve a DynamicValue following A2UI spec:
     * - literal* only: Use literal value directly
     * - path only: Resolve against data model
     * - both: Update data model at path with literal, then bind to path
     */
    private fun resolveBoundValue(dv: DynamicValue, surface: SurfaceState): Any? {
        return when (dv) {
            is DynamicValue.LiteralString,
            is DynamicValue.LiteralNumber,
            is DynamicValue.LiteralBoolean -> {
                // literal* only: Use directly
                when (dv) {
                    is DynamicValue.LiteralString -> dv.value
                    is DynamicValue.LiteralNumber -> dv.value
                    is DynamicValue.LiteralBoolean -> dv.value
                    else -> null
                }
            }
            is DynamicValue.LiteralArray -> dv.value.map { resolveBoundValue(it, surface) }
            is DynamicValue.DataBinding -> {
                // path only: Resolve against data model
                surface.dataModel[dv.path]
            }
            is DynamicValue.FunctionCall -> {
                // Function calls (not fully implemented)
                null
            }
        }
    }

    fun getViewForComponent(componentId: String): View? {
        return componentViews[componentId]
    }

    fun getSurface(surfaceId: String): SurfaceState? {
        return surfaces[surfaceId]
    }

    fun handleUserAction(surfaceId: String, componentId: String, action: Action) {
        val surface = surfaces[surfaceId] ?: return
        
        // Resolve action context bindings
        val resolvedContext = when (action) {
            is Action.Event -> action.context?.mapValues { resolveBoundValue(it.value, surface) }
            else -> null
        }

        onActionListener?.invoke(action, resolvedContext ?: emptyMap())
    }

    fun clear() {
        surfaces.clear()
        componentViews.clear()
        viewBindings.clear()
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
