package com.example.androiduirenderer.a2ui.model

import com.example.androiduirenderer.model.LayoutConfig
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

sealed class A2UIMessage {
     data class CreateSurface(
        @SerializedName("surfaceId") val surfaceId: String,
        @SerializedName("catalogId") val catalogId: String,
        @SerializedName("theme") val theme: Theme? = null,
        @SerializedName("sendDataModel") val sendDataModel: Boolean = false
    ) : A2UIMessage()

    data class UpdateComponents(
        @SerializedName("surfaceId") val surfaceId: String,
        @SerializedName("components") val components: List<A2UIComponent>
    ) : A2UIMessage()

    data class UpdateDataModel(
        @SerializedName("surfaceId") val surfaceId: String,
        @SerializedName("path") val path: String? = null,
        @SerializedName("value") val value: JsonElement? = null
    ) : A2UIMessage()

    data class DeleteSurface(
        @SerializedName("surfaceId") val surfaceId: String
    ) : A2UIMessage()
}

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

sealed class A2UIComponent {
    abstract val id: String
    abstract val accessibility: AccessibilityAttributes?
    abstract val weight: Float?

    data class Text(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("text") val text: DynamicValue,
        @SerializedName("variant") val variant: String? = null
    ) : A2UIComponent()

    data class Button(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("child") val child: String,
        @SerializedName("variant") val variant: String? = null,
        @SerializedName("action") val action: Action
    ) : A2UIComponent()

    data class CheckBox(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("label") val label: DynamicValue,
        @SerializedName("value") val value: DynamicValue
    ) : A2UIComponent()

    data class ChoicePicker(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("label") val label: DynamicValue? = null,
        @SerializedName("variant") val variant: String,
        @SerializedName("options") val options: List<ChoiceOption>,
        @SerializedName("value") val value: DynamicValue
    ) : A2UIComponent()

    data class Row(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("children") val children: ChildList,
        @SerializedName("justify") val justify: String? = null,
        @SerializedName("align") val align: String? = null
    ) : A2UIComponent()

    data class Column(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("children") val children: ChildList,
        @SerializedName("justify") val justify: String? = null,
        @SerializedName("align") val align: String? = null
    ) : A2UIComponent()

    data class A2UIList(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("children") val children: ChildList,
        @SerializedName("direction") val direction: String? = null,
        @SerializedName("align") val align: String? = null
    ) : A2UIComponent()

    data class TextField(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("label") val label: DynamicValue,
        @SerializedName("value") val value: DynamicValue,
        @SerializedName("variant") val variant: String? = null
    ) : A2UIComponent()

    data class Image(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("src") val src: DynamicValue,
        @SerializedName("alt") val alt: DynamicValue? = null,
        @SerializedName("fit") val fit: String? = null,
        @SerializedName("width") val width: DynamicValue? = null,
        @SerializedName("height") val height: DynamicValue? = null
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
        @SerializedName("src") val src: DynamicValue,
        @SerializedName("autoplay") val autoplay: DynamicValue? = null,
        @SerializedName("controls") val controls: DynamicValue? = null,
        @SerializedName("loop") val loop: DynamicValue? = null,
        @SerializedName("muted") val muted: DynamicValue? = null
    ) : A2UIComponent()

    data class ProgressBar(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("value") val value: DynamicValue,
        @SerializedName("variant") val variant: String? = null
    ) : A2UIComponent()

    data class Slider(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("value") val value: DynamicValue,
        @SerializedName("min") val min: DynamicValue? = null,
        @SerializedName("max") val max: DynamicValue? = null,
        @SerializedName("step") val step: DynamicValue? = null,
        @SerializedName("label") val label: DynamicValue? = null
    ) : A2UIComponent()

    data class BalanceDisplay(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("amount") val amount: DynamicValue,
        @SerializedName("currency") val currency: DynamicValue,
        @SerializedName("variant") val variant: String? = null
    ) : A2UIComponent()

    data class AccountCard(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("accountName") val accountName: DynamicValue,
        @SerializedName("accountNumber") val accountNumber: DynamicValue,
        @SerializedName("balance") val balance: DynamicValue,
        @SerializedName("currency") val currency: DynamicValue,
        @SerializedName("icon") val icon: String? = null,
        @SerializedName("isSelected") val isSelected: DynamicValue? = null,
        @SerializedName("action") val action: Action? = null
    ) : A2UIComponent()

    data class ActionButton(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("icon") val icon: String,
        @SerializedName("label") val label: DynamicValue,
        @SerializedName("action") val action: Action,
        @SerializedName("variant") val variant: String? = null
    ) : A2UIComponent()

    data class QuickActionCircle(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("icon") val icon: String,
        @SerializedName("label") val label: DynamicValue,
        @SerializedName("action") val action: Action
    ) : A2UIComponent()

    data class NavigationBar(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("items") val items: List<NavigationItem>,
        @SerializedName("selectedItem") val selectedItem: DynamicValue
    ) : A2UIComponent()

    data class AppBar(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("title") val title: DynamicValue,
        @SerializedName("showBackButton") val showBackButton: DynamicValue? = null,
        @SerializedName("actions") val actions: List<AppBarAction>? = null
    ) : A2UIComponent()

    data class Divider(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("variant") val variant: String? = null
    ) : A2UIComponent()

    data class Card(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("children") val children: ChildList,
        @SerializedName("action") val action: Action? = null,
        @SerializedName("variant") val variant: String? = null
    ) : A2UIComponent()

    data class Tab(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("text") val text: DynamicValue,
        @SerializedName("selected") val selected: DynamicValue? = null,
        @SerializedName("action") val action: Action? = null
    ) : A2UIComponent()

    data class SegmentedTab(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("text") val text: DynamicValue,
        @SerializedName("selected") val selected: DynamicValue? = null,
        @SerializedName("action") val action: Action? = null
    ) : A2UIComponent()

    data class ProductIcon(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("icon") val icon: String,
        @SerializedName("label") val label: DynamicValue,
        @SerializedName("action") val action: Action? = null
    ) : A2UIComponent()

    data class View(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("backgroundColor") val backgroundColor: String? = null,
        @SerializedName("layout") val layout: LayoutConfig? = null
    ) : A2UIComponent()

    data class Overlay(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("children") val children: ChildList,
        @SerializedName("visible") val visible: DynamicValue? = null,
        @SerializedName("closeOnBackdrop") val closeOnBackdrop: Boolean? = null,
        @SerializedName("onClose") val onClose: Action? = null
    ) : A2UIComponent()

    data class MenuItem(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("icon") val icon: String,
        @SerializedName("label") val label: DynamicValue,
        @SerializedName("action") val action: Action? = null,
        @SerializedName("badge") val badge: DynamicValue? = null
    ) : A2UIComponent()

    data class MenuSection(
        override val id: String,
        override val accessibility: AccessibilityAttributes? = null,
        override val weight: Float? = null,
        @SerializedName("title") val title: DynamicValue,
        @SerializedName("items") val items: List<MenuItemConfig>
    ) : A2UIComponent()

    data class MenuItemConfig(
        @SerializedName("icon") val icon: String,
        @SerializedName("label") val label: String,
        @SerializedName("action") val action: String? = null
    )
}

data class AccessibilityAttributes(
    @SerializedName("label") val label: DynamicValue? = null,
    @SerializedName("description") val description: DynamicValue? = null
)

sealed class ChildList {
    data class Static(val children: List<String>) : ChildList()
    data class Template(
        @SerializedName("componentId") val componentId: String,
        @SerializedName("path") val path: String
    ) : ChildList()
}

data class ChoiceOption(
    @SerializedName("label") val label: DynamicValue,
    @SerializedName("value") val value: String
)

data class NavigationItem(
    @SerializedName("id") val id: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("label") val label: DynamicValue,
    @SerializedName("action") val action: Action? = null
)

data class AppBarAction(
    @SerializedName("icon") val icon: String,
    @SerializedName("action") val action: Action
)

sealed class Action {
    data class Event(
        @SerializedName("name") val name: String,
        @SerializedName("context") val context: Map<String, DynamicValue>? = null
    ) : Action()

    data class FunctionCallAction(
        @SerializedName("call") val call: String,
        @SerializedName("args") val args: Map<String, DynamicValue>,
        @SerializedName("returnType") val returnType: String = "boolean"
    ) : Action()
}

data class A2UIResponse(
    @SerializedName("version") val version: String = "v0.10",
    @SerializedName("createSurface") val createSurface: A2UIMessage.CreateSurface? = null,
    @SerializedName("updateComponents") val updateComponents: A2UIMessage.UpdateComponents? = null,
    @SerializedName("updateDataModel") val updateDataModel: A2UIMessage.UpdateDataModel? = null,
    @SerializedName("deleteSurface") val deleteSurface: A2UIMessage.DeleteSurface? = null
)