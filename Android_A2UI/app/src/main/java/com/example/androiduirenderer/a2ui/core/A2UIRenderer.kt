package com.example.androiduirenderer.a2ui.core

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.androiduirenderer.a2ui.model.*

class A2UIRenderer(private val context: Context) {
    private var onNavigateListener: ((page: String) -> Unit)? = null

    // Store binding metadata for each component
    private val componentBindings = mutableMapOf<String, ViewBinding>()
    private val componentViews = mutableMapOf<String, View>()

    fun setOnNavigateListener(listener: (page: String) -> Unit) {
        onNavigateListener = listener
    }

    /**
     * Render a surface following A2UI practice:
     * STEP 1: Build widget tree from component definitions
     * STEP 2: Resolve data bindings
     * STEP 3: Components are already registered in ComponentCatalog (used during tree building)
     */
    fun renderSurface(components: List<A2UIComponent>, container: ViewGroup, theme: Theme? = null) {
        Log.d("A2UIRenderer", "Rendering ${components.size} components with theme ${theme?.name ?: "default"}")
        A2UIComponents.currentTheme = theme

        // Clear previous state
        container.removeAllViews()
        componentBindings.clear()
        componentViews.clear()

        // Build component map (mutable for template expansion)
        val componentMap = components.associateBy { it.id }.toMutableMap()

        // Find root component (must have id "root")
        val rootComponent = componentMap["root"]
            ?: throw IllegalArgumentException("No root component found")

        // STEP 1: Build widget tree - create all views and store binding metadata
        Log.d("A2UIRenderer", "STEP 1: Building widget tree")
        val rootBinding = buildWidgetTree(rootComponent, componentMap, container)
        componentBindings[rootComponent.id] = rootBinding
        componentViews[rootComponent.id] = rootBinding.view

        // Expand templates and build child relationships
        buildComponentTree(rootComponent, componentMap, rootBinding.view as? ViewGroup ?: container)

        // STEP 2: Resolve data bindings - separate pass after tree is built
        Log.d("A2UIRenderer", "STEP 2: Resolving data bindings")
        for (binding in componentBindings.values) {
            A2UIComponents.bindData(binding)
        }

        // STEP 3: Widget registry lookup already happened during tree building via ComponentCatalog
        Log.d("A2UIRenderer", "STEP 3: Widget registry lookup complete (done during tree building)")

        // Add root view to container
        container.addView(rootBinding.view)
    }

    private fun buildWidgetTree(
        component: A2UIComponent,
        componentMap: MutableMap<String, A2UIComponent>,
        parent: ViewGroup
    ): ViewBinding {
        // Build view using A2UIComponents (widget registry)
        val binding = A2UIComponents.buildWidgetTree(component, parent, context)
        componentViews[component.id] = binding.view
        componentBindings[component.id] = binding

        // Handle children for container components
        when (component) {
            is A2UIComponent.Row -> {
                handleContainerChildren(component, component.children, componentMap, binding.view as? ViewGroup ?: parent)
            }
            is A2UIComponent.Column -> {
                handleContainerChildren(component, component.children, componentMap, binding.view as? ViewGroup ?: parent)
            }
            is A2UIComponent.A2UIList -> {
                handleContainerChildren(component, component.children, componentMap, binding.view as? ViewGroup ?: parent)
            }
            is A2UIComponent.Card -> {
                handleContainerChildren(component, component.children, componentMap, binding.view as? ViewGroup ?: parent)
            }
            is A2UIComponent.Overlay -> {
                handleContainerChildren(component, component.children, componentMap, binding.view as? ViewGroup ?: parent)
            }
            is A2UIComponent.Button -> {
                component.child?.let { childId ->
                    componentMap[childId]?.let { childComponent ->
                        val childBinding = buildWidgetTree(childComponent, componentMap, binding.view as? ViewGroup ?: parent)
                        componentBindings[childId] = childBinding
                        componentViews[childId] = childBinding.view
                    }
                }
            }
            else -> {
                // Leaf components or components without children
            }
        }

        return binding
    }

    private fun handleContainerChildren(
        component: A2UIComponent,
        children: ChildList,
        componentMap: MutableMap<String, A2UIComponent>,
        parentView: ViewGroup
    ) {
        val childIds = when (children) {
            is ChildList.Static -> children.children
            is ChildList.Template -> {
                resolveTemplateChildren(children, componentMap, parentView)
            }
            is ChildList.Empty -> emptyList()
        }

        childIds.forEach { childId ->
            componentMap[childId]?.let { childComponent ->
                val childBinding = buildWidgetTree(childComponent, componentMap, parentView)
                componentBindings[childId] = childBinding
                componentViews[childId] = childBinding.view
            }
        }
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

        val templateComponent = componentMap[template.componentId]
        if (templateComponent == null) {
            Log.w("A2UIRenderer", "Template component ${template.componentId} not found")
            return emptyList()
        }

        val dataValue = resolver.getDataModel().getValue(template.path)
        if (dataValue !is List<*>) {
            Log.w("A2UIRenderer", "Data at path ${template.path} is not an array")
            return emptyList()
        }

        val dataList = dataValue as List<Any?>
        Log.d("A2UIRenderer", "Expanding template ${template.componentId} with ${dataList.size} items")

        val childIds = mutableListOf<String>()

        dataList.forEachIndexed { index, item ->
            val childId = "${template.componentId}_${index}"
            val childComponent = cloneComponentForTemplate(templateComponent, childId, template.path, index, item)
            if (childComponent != null) {
                componentMap[childId] = childComponent
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
        fun adjust(dv: DynamicValue): DynamicValue = adjustDynamicValueForTemplate(dv, arrayPath, index, item)

        return when (original) {
            is A2UIComponent.Text -> original.copy(id = newId, text = adjust(original.text))
            is A2UIComponent.CheckBox -> original.copy(id = newId, label = adjust(original.label), value = adjust(original.value))
            is A2UIComponent.ChoicePicker -> {
                val adjustedOptions = original.options.map { option ->
                    option.copy(label = adjust(option.label))
                }
                original.copy(id = newId, label = original.label?.let { adjust(it) }, value = adjust(original.value), options = adjustedOptions)
            }
            is A2UIComponent.TextField -> original.copy(id = newId, label = adjust(original.label), value = adjust(original.value))
            is A2UIComponent.Image -> original.copy(id = newId, src = adjust(original.src), alt = original.alt?.let { adjust(it) })
            is A2UIComponent.Icon -> original.copy(id = newId, color = original.color?.let { adjust(it) })
            is A2UIComponent.Video -> original.copy(id = newId, src = adjust(original.src))
            is A2UIComponent.ProgressBar -> original.copy(id = newId, value = adjust(original.value))
            is A2UIComponent.Slider -> original.copy(id = newId, value = adjust(original.value), label = original.label?.let { adjust(it) })
            is A2UIComponent.BalanceDisplay -> original.copy(id = newId, amount = adjust(original.amount), currency = adjust(original.currency))
            is A2UIComponent.AccountCard -> original.copy(
                id = newId,
                accountName = adjust(original.accountName),
                accountNumber = adjust(original.accountNumber),
                balance = adjust(original.balance),
                currency = adjust(original.currency)
            )
            is A2UIComponent.ActionButton -> original.copy(id = newId, label = adjust(original.label))
            is A2UIComponent.QuickActionCircle -> original.copy(id = newId, label = adjust(original.label))
            is A2UIComponent.Tab -> original.copy(id = newId, text = adjust(original.text))
            is A2UIComponent.SegmentedTab -> original.copy(id = newId, text = adjust(original.text))
            is A2UIComponent.ProductIcon -> original.copy(id = newId, label = adjust(original.label))
            // Container components - clone children recursively
            is A2UIComponent.Row -> {
                val childList = original.children
                val newChildren = when (childList) {
                    is ChildList.Static -> {
                        val newChildIds = childList.children.mapIndexed { i, id -> "${id}_clone_${index}" }
                        ChildList.Static(newChildIds)
                    }
                    else -> childList
                }
                original.copy(id = newId, children = newChildren)
            }
            is A2UIComponent.Column -> {
                val childList = original.children
                val newChildren = when (childList) {
                    is ChildList.Static -> {
                        val newChildIds = childList.children.mapIndexed { i, id -> "${id}_clone_${index}" }
                        ChildList.Static(newChildIds)
                    }
                    else -> childList
                }
                original.copy(id = newId, children = newChildren)
            }
            is A2UIComponent.Card -> {
                val childList = original.children
                val newChildren = when (childList) {
                    is ChildList.Static -> {
                        val newChildIds = childList.children.mapIndexed { i, id -> "${id}_clone_${index}" }
                        ChildList.Static(newChildIds)
                    }
                    else -> childList
                }
                original.copy(id = newId, children = newChildren)
            }
            is A2UIComponent.A2UIList -> {
                val childList = original.children
                val newChildren = when (childList) {
                    is ChildList.Static -> {
                        val newChildIds = childList.children.mapIndexed { i, id -> "${id}_clone_${index}" }
                        ChildList.Static(newChildIds)
                    }
                    else -> childList
                }
                original.copy(id = newId, children = newChildren)
            }
            is A2UIComponent.Button -> {
                original.copy(id = newId, child = original.child?.let { "${it}_clone_${index}" })
            }
            is A2UIComponent.View -> original.copy(id = newId)
            is A2UIComponent.Divider -> original.copy(id = newId)
            is A2UIComponent.MenuItem,
            is A2UIComponent.MenuSection,
            is A2UIComponent.NavigationBar,
            is A2UIComponent.AppBar,
            is A2UIComponent.Overlay -> {
                Log.w("A2UIRenderer", "Template cloning for ${original::class.simpleName} not yet implemented")
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
                when {
                    originalPath == "@" -> {
                        when (item) {
                            is String -> DynamicValue.LiteralString(item)
                            is Number -> DynamicValue.LiteralNumber(item)
                            is Boolean -> DynamicValue.LiteralBoolean(item)
                            else -> DynamicValue.LiteralString(item?.toString() ?: "")
                        }
                    }
                    originalPath.startsWith("@.") -> {
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
                        val newPath = if (originalPath.contains(".") || originalPath.startsWith("$")) {
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
            is DynamicValue.FunctionCall -> dynamicValue
        }
    }

    private fun buildComponentTree(
        component: A2UIComponent,
        componentMap: MutableMap<String, A2UIComponent>,
        parentView: ViewGroup
    ) {
        val currentView = componentViews[component.id] ?: return
        val currentBinding = componentBindings[component.id] ?: return

        // Add child views to parent based on component type
        when (component) {
            is A2UIComponent.Row,
            is A2UIComponent.Column,
            is A2UIComponent.A2UIList,
            is A2UIComponent.Card,
            is A2UIComponent.Overlay -> {
                val children = when (component) {
                    is A2UIComponent.Row -> component.children
                    is A2UIComponent.Column -> component.children
                    is A2UIComponent.A2UIList -> component.children
                    is A2UIComponent.Card -> component.children
                    is A2UIComponent.Overlay -> component.children
                    else -> ChildList.Empty
                }

                val childIds = when (children) {
                    is ChildList.Static -> children.children
                    is ChildList.Template -> resolveTemplateChildren(children, componentMap, currentView as? ViewGroup ?: parentView)
                    is ChildList.Empty -> emptyList()
                }

                if (currentView is ViewGroup) {
                    childIds.forEach { childId ->
                        componentViews[childId]?.let { childView ->
                            currentView.addView(childView)
                        }
                    }
                }
            }
            is A2UIComponent.Button -> {
                component.child?.let { childId ->
                    componentViews[childId]?.let { childView ->
                        if (currentView is ViewGroup) {
                            currentView.addView(childView)
                        }
                    }
                }
            }
            else -> {
                // Leaf components don't have children
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
        componentBindings.clear()
    }
}
