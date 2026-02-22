package com.example.androiduirenderer.a2ui.model

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

/**
 * A2UI Message types following the v0.8 specification
 */
sealed class A2UIMessage {
    /**
     * beginRendering: Signal to start rendering with optional root component ID
     */
    data class BeginRendering(
        @SerializedName("surfaceId") val surfaceId: String,
        @SerializedName("rootComponentId") val rootComponentId: String? = null,
        @SerializedName("catalogId") val catalogId: String? = null
    ) : A2UIMessage()

    /**
     * surfaceUpdate: Update components for a surface (buffered until beginRendering)
     */
    data class SurfaceUpdate(
        @SerializedName("surfaceId") val surfaceId: String,
        @SerializedName("components") val components: List<A2UIComponent>,
        @SerializedName("globalStyles") val globalStyles: GlobalStyles? = null
    ) : A2UIMessage()

    /**
     * dataModelUpdate: Update data model values for a surface
     */
    data class DataModelUpdate(
        @SerializedName("surfaceId") val surfaceId: String,
        @SerializedName("path") val path: String,
        @SerializedName("value") val value: JsonElement? = null,
        @SerializedName("literal") val literal: JsonElement? = null
    ) : A2UIMessage()

    /**
     * deleteSurface: Remove a surface
     */
    data class DeleteSurface(
        @SerializedName("surfaceId") val surfaceId: String
    ) : A2UIMessage()

    /**
     * userAction: User interaction event to send to server
     */
    data class UserAction(
        @SerializedName("surfaceId") val surfaceId: String,
        @SerializedName("componentId") val componentId: String,
        @SerializedName("action") val action: Action,
        @SerializedName("context") val context: Map<String, Any>? = null
    ) : A2UIMessage()
}

/**
 * Global styles for a surface
 */
data class GlobalStyles(
    @SerializedName("font") val font: String? = null,
    @SerializedName("primaryColor") val primaryColor: String? = null,
    @SerializedName("backgroundColor") val backgroundColor: String? = null
)

/**
 * BoundValue: Can contain literal*, path, or both
 * - literal* only: Use literal value directly
 * - path only: Resolve against data model
 * - both: Update data model at path with literal, then bind to path
 */
data class BoundValue(
    @SerializedName("path") val path: String? = null,
    @SerializedName("literal") val literal: JsonElement? = null
)

/**
 * DynamicValue: Backward compatible type for component properties
 */
sealed class DynamicValue {
    data class LiteralString(val value: String) : DynamicValue()
    data class LiteralNumber(val value: Number) : DynamicValue()
    data class LiteralBoolean(val value: Boolean) : DynamicValue()
    data class LiteralArray(val value: List<DynamicValue>) : DynamicValue()
    data class DataBinding(val path: String) : DynamicValue()
    data class FunctionCall(
        val call: String,
        val args: Map<String, DynamicValue>,
        val returnType: String = "boolean"
    ) : DynamicValue()
}

/**
 * A2UI Components following the standard catalog
 */
sealed class A2UIComponent {
    abstract val id: String
    abstract val accessibility: AccessibilityAttributes?
    abstract val weight: Float?

    // === Basic Content Components ===
    data class Text(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("text") val text: DynamicValue,
        @SerializedName("usageHint") val usageHint: String? = null  // h1-h5, body, caption
    ) : A2UIComponent()

    data class Image(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("url") val url: DynamicValue,
        @SerializedName("fit") val fit: String? = null,  // cover, contain, fill
        @SerializedName("usageHint") val usageHint: String? = null  // avatar, hero
    ) : A2UIComponent()

    data class Icon(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("name") val name: String,
        @SerializedName("size") val size: DynamicValue? = null,
        @SerializedName("color") val color: DynamicValue? = null
    ) : A2UIComponent()

    data class Video(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("url") val url: DynamicValue,
        @SerializedName("autoplay") val autoplay: Boolean? = null,
        @SerializedName("controls") val controls: Boolean? = null,
        @SerializedName("loop") val loop: Boolean? = null
    ) : A2UIComponent()

    data class AudioPlayer(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("url") val url: DynamicValue,
        @SerializedName("autoplay") val autoplay: Boolean? = null,
        @SerializedName("controls") val controls: Boolean? = null
    ) : A2UIComponent()

    data class Divider(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("orientation") val orientation: String? = null  // horizontal, vertical
    ) : A2UIComponent()

    // === Layout & Container Components ===
    data class Row(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("children") val children: ChildrenList,
        @SerializedName("distribution") val distribution: String? = null,  // start, center, end, spaceBetween
        @SerializedName("alignment") val alignment: String? = null  // start, center, end, stretch
    ) : A2UIComponent()

    data class Column(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("children") val children: ChildrenList,
        @SerializedName("distribution") val distribution: String? = null,
        @SerializedName("alignment") val alignment: String? = null
    ) : A2UIComponent()

    data class A2UIList(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("children") val children: ChildrenList,
        @SerializedName("direction") val direction: String? = null  // vertical, horizontal
    ) : A2UIComponent()

    data class Card(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("children") val children: ChildrenList,
        @SerializedName("action") val action: Action? = null
    ) : A2UIComponent()

    data class Tabs(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("tabItems") val tabItems: List<TabItem>,
        @SerializedName("selectedIndex") val selectedIndex: DynamicValue? = null
    ) : A2UIComponent()

    data class Modal(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("entryPointChild") val entryPointChild: String? = null,
        @SerializedName("contentChild") val contentChild: String? = null,
        @SerializedName("visible") val visible: Boolean? = null
    ) : A2UIComponent()

    // === Interactive & Input Components ===
    data class Button(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("child") val child: String? = null,
        @SerializedName("action") val action: Action? = null,
        @SerializedName("primary") val primary: Boolean? = null
    ) : A2UIComponent()

    data class CheckBox(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("label") val label: DynamicValue,
        @SerializedName("checked") val checked: DynamicValue,
        @SerializedName("value") val value: DynamicValue? = null,
        @SerializedName("action") val action: Action? = null
    ) : A2UIComponent()

    data class TextField(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("label") val label: DynamicValue? = null,
        @SerializedName("text") val text: DynamicValue,
        @SerializedName("textFieldType") val textFieldType: String? = null,  // text, password, email, number
        @SerializedName("validationRegexp") val validationRegexp: String? = null
    ) : A2UIComponent()

    data class DateTimeInput(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("label") val label: DynamicValue? = null,
        @SerializedName("value") val value: DynamicValue,
        @SerializedName("dateTimeType") val dateTimeType: String? = null  // date, time, dateTime
    ) : A2UIComponent()

    data class MultipleChoice(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("label") val label: DynamicValue? = null,
        @SerializedName("options") val options: List<ChoiceOption>,
        @SerializedName("selectedValues") val selectedValues: DynamicValue,
        @SerializedName("maxAllowedSelections") val maxAllowedSelections: Int? = null
    ) : A2UIComponent()

    data class Slider(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("value") val value: DynamicValue,
        @SerializedName("minValue") val minValue: DynamicValue? = null,
        @SerializedName("maxValue") val maxValue: DynamicValue? = null,
        @SerializedName("step") val step: DynamicValue? = null
    ) : A2UIComponent()

    // === Custom Catalog Components ===
    data class ChoicePicker(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("label") val label: DynamicValue,
        @SerializedName("value") val value: DynamicValue,
        @SerializedName("options") val options: List<ChoiceOption>
    ) : A2UIComponent()

    data class ProgressBar(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("value") val value: DynamicValue,
        @SerializedName("label") val label: DynamicValue? = null
    ) : A2UIComponent()

    data class BalanceDisplay(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("amount") val amount: DynamicValue,
        @SerializedName("currency") val currency: DynamicValue? = null
    ) : A2UIComponent()

    data class AccountCard(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("accountName") val accountName: DynamicValue,
        @SerializedName("accountNumber") val accountNumber: DynamicValue,
        @SerializedName("balance") val balance: DynamicValue,
        @SerializedName("currency") val currency: DynamicValue? = null
    ) : A2UIComponent()

    data class ActionButton(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("label") val label: DynamicValue,
        @SerializedName("icon") val icon: String,
        @SerializedName("action") val action: Action? = null
    ) : A2UIComponent()

    data class QuickActionCircle(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("label") val label: DynamicValue,
        @SerializedName("icon") val icon: String,
        @SerializedName("action") val action: Action? = null
    ) : A2UIComponent()

    data class Tab(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("text") val text: DynamicValue,
        @SerializedName("selected") val selected: Boolean? = null
    ) : A2UIComponent()

    data class SegmentedTab(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("text") val text: DynamicValue,
        @SerializedName("selected") val selected: Boolean? = null
    ) : A2UIComponent()

    data class ProductIcon(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("label") val label: DynamicValue,
        @SerializedName("icon") val icon: String
    ) : A2UIComponent()

    data class View(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("children") val children: ChildrenList? = null
    ) : A2UIComponent()

    data class MenuItem(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("label") val label: DynamicValue,
        @SerializedName("icon") val icon: String? = null,
        @SerializedName("action") val action: Action? = null
    ) : A2UIComponent()

    data class MenuSection(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("title") val title: DynamicValue,
        @SerializedName("items") val items: List<MenuItem>
    ) : A2UIComponent()

    data class NavigationBar(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("items") val items: List<MenuItem>,
        @SerializedName("selectedIndex") val selectedIndex: DynamicValue? = null
    ) : A2UIComponent()

    data class AppBar(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("title") val title: DynamicValue,
        @SerializedName("actions") val actions: List<Action>? = null
    ) : A2UIComponent()

    data class Overlay(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("children") val children: ChildrenList,
        @SerializedName("visible") val visible: Boolean? = null
    ) : A2UIComponent()
}

/**
 * Children list: Can be explicit list, template, or reference
 */
sealed class ChildrenList {
    /**
     * explicitList: Direct list of component IDs
     */
    data class ExplicitList(val children: List<String>) : ChildrenList()

    /**
     * child: Single child component ID
     */
    data class Child(val childId: String) : ChildrenList()

    /**
     * contentChild: Content child for Modal etc.
     */
    data class ContentChild(val contentChildId: String) : ChildrenList()

    /**
     * template: Dynamic list rendering
     */
    data class Template(
        @SerializedName("componentId") val componentId: String,
        @SerializedName("dataBinding") val dataBinding: String
    ) : ChildrenList()
}

data class TabItem(
    @SerializedName("title") val title: String,
    @SerializedName("child") val child: String
)

data class ChoiceOption(
    @SerializedName("label") val label: DynamicValue,
    @SerializedName("value") val value: String
)

data class AccessibilityAttributes(
    @SerializedName("label") val label: DynamicValue? = null,
    @SerializedName("description") val description: DynamicValue? = null
)

sealed class Action {
    data class Navigate(
        @SerializedName("surfaceId") val surfaceId: String
    ) : Action()

    data class Event(
        @SerializedName("name") val name: String,
        @SerializedName("context") val context: Map<String, DynamicValue>? = null
    ) : Action()

    data class FunctionCall(
        @SerializedName("call") val call: String,
        @SerializedName("args") val args: Map<String, DynamicValue>,
        @SerializedName("returnType") val returnType: String = "boolean"
    ) : Action()
}

/**
 * A2UI Response from parser
 */
data class A2UIResponse(
    val beginRendering: A2UIMessage.BeginRendering? = null,
    val surfaceUpdate: A2UIMessage.SurfaceUpdate? = null,
    val dataModelUpdate: A2UIMessage.DataModelUpdate? = null,
    val deleteSurface: A2UIMessage.DeleteSurface? = null
)

/**
 * Surface state: Holds component buffer and data model for a surface
 */
data class SurfaceState(
    val surfaceId: String,
    val componentBuffer: MutableMap<String, A2UIComponent> = mutableMapOf(),
    val dataModel: MutableMap<String, Any?> = mutableMapOf(),
    val globalStyles: GlobalStyles? = null,
    val catalogId: String? = null
)
