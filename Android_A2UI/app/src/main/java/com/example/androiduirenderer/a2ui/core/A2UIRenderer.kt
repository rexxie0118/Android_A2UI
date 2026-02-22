package com.example.androiduirenderer.a2ui.core

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.androiduirenderer.a2ui.model.*

class A2UIRenderer(private val context: Context) {
    private val componentCatalog = ComponentCatalog.getInstance()
    private val componentViews = mutableMapOf<String, View>()
    private var onNavigateListener: ((page: String) -> Unit)? = null
    
    fun setOnNavigateListener(listener: (page: String) -> Unit) {
        onNavigateListener = listener
    }
    
    fun renderSurface(components: List<A2UIComponent>, container: ViewGroup, theme: Theme? = null) {
        Log.d("A2UIRenderer", "Rendering ${components.size} components with theme ${theme?.name ?: "default"}")
        A2UIComponents.currentTheme = theme
        
        // Clear previous views
        container.removeAllViews()
        componentViews.clear()
        
        // Build component map (mutable for template expansion)
        val componentMap = components.associateBy { it.id }.toMutableMap()
        
        // Find root component (must have id "root")
        val rootComponent = componentMap["root"] 
            ?: throw IllegalArgumentException("No root component found")
        
        // Create all components first (including expanded templates)
        components.forEach { component ->
            createComponentView(component, componentMap, container)
        }
        
        // Build hierarchy
        val rootView = componentViews[rootComponent.id]
            ?: throw IllegalStateException("Root view not created")
        
        container.addView(rootView)
        
        // Apply layout and build child relationships
        buildComponentTree(rootComponent, componentMap, rootView as? ViewGroup ?: container)
    }
    
    private fun createComponentView(
        component: A2UIComponent,
        componentMap: MutableMap<String, A2UIComponent>,
        parent: ViewGroup
    ): View {
        // Return if already created
        componentViews[component.id]?.let { return it }
        
        Log.d("A2UIRenderer", "Creating component ${component.id} of type ${component::class.simpleName}")
        
        // Create the view
        val view = componentCatalog.createComponent(component, parent, context)
        componentViews[component.id] = view
        
        // Apply weight if specified
        component.weight?.let { weight ->
            if (view.layoutParams is LinearLayout.LayoutParams) {
                (view.layoutParams as LinearLayout.LayoutParams).weight = weight
            }
        }
        
        return view
    }
    
    private fun resolveTemplateChildren(
        template: ChildList.Template,
        componentMap: MutableMap<String, A2UIComponent>,
        parent: ViewGroup
    ): List<String> {
        val resolver = A2UIComponents.dynamicValueResolver
        if (resolver == null) {
            Log.w("A2UIRenderer", "No dynamic value resolver available for template ${template.componentId}")
            return emptyList()
        }
        
        // Get the template component
        val templateComponent = componentMap[template.componentId]
        if (templateComponent == null) {
            Log.w("A2UIRenderer", "Template component ${template.componentId} not found")
            return emptyList()
        }
        
        // Resolve the data path to get array
        val dataValue = resolver.getDataModel().getValue(template.path)
        if (dataValue !is List<*>) {
            Log.w("A2UIRenderer", "Data at path ${template.path} is not an array, got ${dataValue?.javaClass?.simpleName}")
            return emptyList()
        }
        
        val dataList = dataValue as List<Any?>
        Log.d("A2UIRenderer", "Expanding template ${template.componentId} with ${dataList.size} items")
        
        val childIds = mutableListOf<String>()
        
        dataList.forEachIndexed { index, item ->
            // Create a new component ID
            val childId = "${template.componentId}_${index}"
            
            // Clone the template component with new ID and adjust data bindings
            val childComponent = cloneComponentForTemplate(templateComponent, childId, template.path, index, item)
            if (childComponent != null) {
                // Add to component map
                componentMap[childId] = childComponent
                // Create view
                createComponentView(childComponent, componentMap, parent)
                childIds.add(childId)
            }
        }
        
        return childIds
    }
    
    private fun cloneComponentForTemplate(
        original: A2UIComponent,
        newId: String,
        arrayPath: String,
        index: Int,
        item: Any?
    ): A2UIComponent? {
        // Helper to adjust a DynamicValue field
        fun adjust(dv: DynamicValue): DynamicValue = adjustDynamicValueForTemplate(dv, arrayPath, index, item)
        
        return when (original) {
            is A2UIComponent.Text -> {
                original.copy(id = newId, text = adjust(original.text))
            }
            is A2UIComponent.CheckBox -> {
                original.copy(id = newId, label = adjust(original.label), value = adjust(original.value))
            }
            is A2UIComponent.ChoicePicker -> {
                // Adjust label, value, and each option's label
                val adjustedOptions = original.options.map { option ->
                    option.copy(label = adjust(option.label))
                }
                original.copy(id = newId, label = original.label?.let { adjust(it) }, value = adjust(original.value), options = adjustedOptions)
            }
            is A2UIComponent.TextField -> {
                original.copy(id = newId, label = adjust(original.label), value = adjust(original.value))
            }
            is A2UIComponent.Image -> {
                original.copy(id = newId, src = adjust(original.src), alt = original.alt?.let { adjust(it) })
            }
            is A2UIComponent.Icon -> {
                // Icon name is a string, not DynamicValue
                original.copy(id = newId, color = original.color?.let { adjust(it) })
            }
            is A2UIComponent.Video -> {
                original.copy(id = newId, src = adjust(original.src))
            }
            is A2UIComponent.ProgressBar -> {
                original.copy(id = newId, value = adjust(original.value))
            }
            is A2UIComponent.Slider -> {
                original.copy(id = newId, value = adjust(original.value), label = original.label?.let { adjust(it) })
            }
            // Container components need recursive cloning of children - not implemented yet
            is A2UIComponent.Button,
            is A2UIComponent.Row,
            is A2UIComponent.Column,
            is A2UIComponent.A2UIList -> {
                Log.w("A2UIRenderer", "Template cloning for container component ${original::class.simpleName} not yet implemented")
                null
            }
            // New banking components - not yet supported for template cloning
            is A2UIComponent.BalanceDisplay,
            is A2UIComponent.AccountCard,
            is A2UIComponent.ActionButton,
            is A2UIComponent.QuickActionCircle,
            is A2UIComponent.NavigationBar,
            is A2UIComponent.AppBar,
            is A2UIComponent.Card,
            is A2UIComponent.Tab,
            is A2UIComponent.SegmentedTab,
            is A2UIComponent.ProductIcon,
            is A2UIComponent.View,
            is A2UIComponent.Divider,
            is A2UIComponent.Overlay,
            is A2UIComponent.MenuItem,
            is A2UIComponent.MenuSection -> {
                Log.w("A2UIRenderer", "Template cloning for component ${original::class.simpleName} not yet implemented")
                null
            }
        }
    }
    
    private fun adjustDynamicValueForTemplate(
        dynamicValue: DynamicValue,
        arrayPath: String,
        index: Int,
        item: Any?
    ): DynamicValue {
        return when (dynamicValue) {
            is DynamicValue.DataBinding -> {
                val originalPath = dynamicValue.path
                // Special handling for template relative paths
                when {
                    originalPath == "@" -> {
                        // Replace with literal value of the current item
                        when (item) {
                            is String -> DynamicValue.LiteralString(item)
                            is Number -> DynamicValue.LiteralNumber(item)
                            is Boolean -> DynamicValue.LiteralBoolean(item)
                            else -> DynamicValue.LiteralString(item?.toString() ?: "")
                        }
                    }
                    originalPath.startsWith("@.") -> {
                        // Relative path within the item (if item is a map)
                        val property = originalPath.substring(2)
                        val propValue = when (item) {
                            is Map<*, *> -> item[property]
                            else -> null
                        }
                        when (propValue) {
                            is String -> DynamicValue.LiteralString(propValue)
                            is Number -> DynamicValue.LiteralNumber(propValue)
                            is Boolean -> DynamicValue.LiteralBoolean(propValue)
                            else -> DynamicValue.LiteralString(propValue?.toString() ?: "")
                        }
                    }
                    else -> {
                        // Assume path is relative to array item, construct absolute path
                        // Using dot notation: arrayPath.index.property
                        val newPath = if (originalPath.contains(".") || originalPath.startsWith("$")) {
                            // Already absolute or contains dots, keep as is
                            originalPath
                        } else {
                            "$arrayPath.$index.$originalPath"
                        }
                        DynamicValue.DataBinding(newPath)
                    }
                }
            }
            is DynamicValue.LiteralString,
            is DynamicValue.LiteralNumber,
            is DynamicValue.LiteralBoolean,
            is DynamicValue.LiteralArray,
            is DynamicValue.FunctionCall -> {
                // Literals and function calls stay the same
                dynamicValue
            }
        }
    }
    
    private fun buildComponentTree(
        component: A2UIComponent,
        componentMap: MutableMap<String, A2UIComponent>,
        parentView: ViewGroup
    ) {
        val currentView = componentViews[component.id] ?: return
        
        // Handle children based on component type
        when (component) {
            is A2UIComponent.Row -> {
                val childList = component.children
                val childIds: List<String> = when (childList) {
                    is ChildList.Static -> childList.children
                    is ChildList.Template -> {
                        resolveTemplateChildren(childList, componentMap, currentView as? ViewGroup ?: parentView)
                    }
                    else -> emptyList<String>()
                }
                
                childIds.forEach { childId ->
                    val childComponent = componentMap[childId]
                    if (childComponent != null) {
                        val childView = componentViews[childId]
                        if (childView != null && currentView is ViewGroup) {
                            Log.d("A2UIRenderer", "Adding child $childId to ${component::class.simpleName} ${component.id}")
                            currentView.addView(childView)
                            buildComponentTree(childComponent, componentMap, currentView)
                        }
                    } else {
                        Log.w("A2UIRenderer", "Child component $childId not found for ${component.id}")
                    }
                }
            }
            
            is A2UIComponent.Column -> {
                val childList = component.children
                val childIds: List<String> = when (childList) {
                    is ChildList.Static -> childList.children
                    is ChildList.Template -> {
                        resolveTemplateChildren(childList, componentMap, currentView as? ViewGroup ?: parentView)
                    }
                    else -> emptyList<String>()
                }
                
                childIds.forEach { childId ->
                    val childComponent = componentMap[childId]
                    if (childComponent != null) {
                        val childView = componentViews[childId]
                        if (childView != null && currentView is ViewGroup) {
                            Log.d("A2UIRenderer", "Adding child $childId to ${component::class.simpleName} ${component.id}")
                            currentView.addView(childView)
                            buildComponentTree(childComponent, componentMap, currentView)
                        }
                    } else {
                        Log.w("A2UIRenderer", "Child component $childId not found for ${component.id}")
                    }
                }
            }
            
            is A2UIComponent.A2UIList -> {
                val listComponent = component
                val childList = listComponent.children
                val childIds: List<String> = when (childList) {
                    is ChildList.Static -> childList.children
                    is ChildList.Template -> {
                        resolveTemplateChildren(childList, componentMap, currentView as? ViewGroup ?: parentView)
                    }
                    else -> emptyList<String>()
                }
                
                if (currentView is ViewGroup) {
                    childIds.forEach { childId ->
                        val childComponent = componentMap[childId]
                        if (childComponent != null) {
                            val childView = componentViews[childId]
                            if (childView != null) {
                                currentView.addView(childView)
                                buildComponentTree(childComponent, componentMap, currentView)
                            }
                        }
                    }
                }
            }
            
            is A2UIComponent.Button -> {
                Log.d("A2UIRenderer", "Button ${component.id} child id: ${component.child}")
                val childComponent = componentMap[component.child]
                if (childComponent != null) {
                    val childView = componentViews[component.child]
                    if (childView != null && currentView is ViewGroup) {
                        Log.d("A2UIRenderer", "Adding child view ${component.child} to button ${component.id}")
                        currentView.addView(childView)
                        buildComponentTree(childComponent, componentMap, currentView)
                    } else {
                        Log.w("A2UIRenderer", "Child view not found or button not ViewGroup for ${component.id}")
                    }
                } else {
                    Log.w("A2UIRenderer", "Child component ${component.child} not found for button ${component.id}")
                }
            }
            
            is A2UIComponent.Card -> {
                val childList = component.children
                val childIds: List<String> = when (childList) {
                    is ChildList.Static -> childList.children
                    is ChildList.Template -> {
                        resolveTemplateChildren(childList, componentMap, currentView as? ViewGroup ?: parentView)
                    }
                    else -> emptyList<String>()
                }
                
                childIds.forEach { childId ->
                    val childComponent = componentMap[childId]
                    if (childComponent != null) {
                        val childView = componentViews[childId]
                        if (childView != null && currentView is ViewGroup) {
                            Log.d("A2UIRenderer", "Adding child $childId to ${component::class.simpleName} ${component.id}")
                            currentView.addView(childView)
                            buildComponentTree(childComponent, componentMap, currentView)
                        }
                    } else {
                        Log.w("A2UIRenderer", "Child component $childId not found for ${component.id}")
                    }
                }
            }
            
            is A2UIComponent.Overlay -> {
                val childList = component.children
                val childIds: List<String> = when (childList) {
                    is ChildList.Static -> childList.children
                    is ChildList.Template -> {
                        resolveTemplateChildren(childList, componentMap, currentView as? ViewGroup ?: parentView)
                    }
                    else -> emptyList<String>()
                }
                
                childIds.forEach { childId ->
                    val childComponent = componentMap[childId]
                    if (childComponent != null) {
                        val childView = componentViews[childId]
                        if (childView != null && currentView is ViewGroup) {
                            Log.d("A2UIRenderer", "Adding child $childId to ${component::class.simpleName} ${component.id}")
                            currentView.addView(childView)
                            buildComponentTree(childComponent, componentMap, currentView)
                        }
                    } else {
                        Log.w("A2UIRenderer", "Child component $childId not found for ${component.id}")
                    }
                }
            }
            
            is A2UIComponent.MenuItem,
            is A2UIComponent.MenuSection,
            is A2UIComponent.Text,
            is A2UIComponent.CheckBox,
            is A2UIComponent.ChoicePicker,
            is A2UIComponent.TextField,
            is A2UIComponent.Image,
            is A2UIComponent.Icon,
            is A2UIComponent.Video,
            is A2UIComponent.ProgressBar,
            is A2UIComponent.Slider,
            is A2UIComponent.Tab,
            is A2UIComponent.SegmentedTab,
            is A2UIComponent.ProductIcon,
            is A2UIComponent.View,
            is A2UIComponent.Divider,
            is A2UIComponent.BalanceDisplay,
            is A2UIComponent.AccountCard,
            is A2UIComponent.ActionButton,
            is A2UIComponent.QuickActionCircle,
            is A2UIComponent.NavigationBar,
            is A2UIComponent.AppBar,
            is A2UIComponent.Card -> {
                // Leaf components or components that handle their own children
            }
        }
    }
    
    fun getViewForComponent(componentId: String): View? {
        return componentViews[componentId]
    }
    
    fun handleNavigation(page: String) {
        onNavigateListener?.invoke(page)
    }
    
    fun clear() {
        componentViews.clear()
    }
}