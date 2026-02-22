package com.example.androiduirenderer.model

import com.google.gson.annotations.SerializedName

data class PageConfig(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String? = null,
    @SerializedName("widgets") val widgets: List<WidgetConfig>,
    @SerializedName("nextPageId") val nextPageId: String? = null,
    @SerializedName("previousPageId") val previousPageId: String? = null
)

data class WidgetConfig(
    @SerializedName("type") val type: String,
    @SerializedName("id") val id: String? = null,
    @SerializedName("text") val text: String? = null,
    @SerializedName("hint") val hint: String? = null,
    @SerializedName("options") val options: List<String>? = null,
    @SerializedName("checked") val checked: Boolean? = null,
    @SerializedName("src") val src: String? = null,
    @SerializedName("items") val items: List<String>? = null,
    @SerializedName("action") val action: String? = null,
    @SerializedName("layout") val layout: LayoutConfig? = null,
    @SerializedName("children") val children: List<WidgetConfig>? = null,
    @SerializedName("attributes") val attributes: Map<String, Any>? = null
)

data class LayoutConfig(
    @SerializedName("width") val width: String? = null,
    @SerializedName("height") val height: String? = null,
    @SerializedName("orientation") val orientation: String? = null,
    @SerializedName("gravity") val gravity: String? = null,
    @SerializedName("weight") val weight: Float? = null,
    @SerializedName("margin") val margin: MarginConfig? = null,
    @SerializedName("padding") val padding: PaddingConfig? = null
)

data class MarginConfig(
    @SerializedName("left") val left: Int? = null,
    @SerializedName("top") val top: Int? = null,
    @SerializedName("right") val right: Int? = null,
    @SerializedName("bottom") val bottom: Int? = null
)

data class PaddingConfig(
    @SerializedName("left") val left: Int? = null,
    @SerializedName("top") val top: Int? = null,
    @SerializedName("right") val right: Int? = null,
    @SerializedName("bottom") val bottom: Int? = null
)