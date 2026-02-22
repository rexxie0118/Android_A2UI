package com.example.androiduirenderer.a2ui.core

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.example.androiduirenderer.a2ui.model.*

/**
 * A2UI Parser following the v0.8 specification
 * Parses JSONL stream into A2UI messages
 */
class A2UIParser(private val gson: Gson = Gson()) {

    /**
     * Parse a single JSONL line into an A2UI message
     */
    fun parseMessage(json: String): A2UIResponse {
        val jsonObject = gson.fromJson(json, JsonObject::class.java)

        // Route by message type
        return when {
            jsonObject.has("beginRendering") -> {
                val beginRendering = parseBeginRendering(jsonObject.getAsJsonObject("beginRendering"))
                A2UIResponse(beginRendering = beginRendering)
            }
            jsonObject.has("surfaceUpdate") -> {
                val surfaceUpdate = parseSurfaceUpdate(jsonObject.getAsJsonObject("surfaceUpdate"))
                A2UIResponse(surfaceUpdate = surfaceUpdate)
            }
            jsonObject.has("dataModelUpdate") -> {
                val dataModelUpdate = parseDataModelUpdate(jsonObject.getAsJsonObject("dataModelUpdate"))
                A2UIResponse(dataModelUpdate = dataModelUpdate)
            }
            jsonObject.has("deleteSurface") -> {
                val deleteSurface = parseDeleteSurface(jsonObject.getAsJsonObject("deleteSurface"))
                A2UIResponse(deleteSurface = deleteSurface)
            }
            // Backward compatibility with old message types
            jsonObject.has("updateComponents") -> {
                val surfaceUpdate = parseSurfaceUpdateLegacy(jsonObject)
                A2UIResponse(surfaceUpdate = surfaceUpdate)
            }
            jsonObject.has("updateDataModel") -> {
                val dataModelUpdate = parseDataModelUpdateLegacy(jsonObject)
                A2UIResponse(dataModelUpdate = dataModelUpdate)
            }
            else -> throw JsonParseException("Unknown A2UI message type")
        }
    }

    private fun parseBeginRendering(json: JsonObject): A2UIMessage.BeginRendering {
        return A2UIMessage.BeginRendering(
            surfaceId = json.get("surfaceId").asString,
            rootComponentId = json.get("rootComponentId")?.asString,
            catalogId = json.get("catalogId")?.asString
        )
    }

    private fun parseSurfaceUpdate(json: JsonObject): A2UIMessage.SurfaceUpdate {
        val componentsArray = json.getAsJsonArray("components")
        val components = mutableListOf<A2UIComponent>()

        componentsArray.forEach { componentJson ->
            val component = parseComponent(componentJson.asJsonObject)
            components.add(component)
        }

        val globalStyles = json.get("globalStyles")?.let {
            gson.fromJson(it, GlobalStyles::class.java)
        }

        return A2UIMessage.SurfaceUpdate(
            surfaceId = json.get("surfaceId").asString,
            components = components,
            globalStyles = globalStyles
        )
    }

    private fun parseSurfaceUpdateLegacy(json: JsonObject): A2UIMessage.SurfaceUpdate {
        val componentsArray = json.getAsJsonArray("components")
        val components = mutableListOf<A2UIComponent>()

        componentsArray.forEach { componentJson ->
            val component = parseComponent(componentJson.asJsonObject)
            components.add(component)
        }

        return A2UIMessage.SurfaceUpdate(
            surfaceId = json.get("surfaceId").asString,
            components = components
        )
    }

    private fun parseDataModelUpdate(json: JsonObject): A2UIMessage.DataModelUpdate {
        return A2UIMessage.DataModelUpdate(
            surfaceId = json.get("surfaceId").asString,
            path = json.get("path").asString,
            value = json.get("value"),
            literal = json.get("literal")
        )
    }

    private fun parseDataModelUpdateLegacy(json: JsonObject): A2UIMessage.DataModelUpdate {
        return A2UIMessage.DataModelUpdate(
            surfaceId = json.get("surfaceId").asString,
            path = json.get("path")?.asString ?: "",
            value = json.get("value")
        )
    }

    private fun parseDeleteSurface(json: JsonObject): A2UIMessage.DeleteSurface {
        return A2UIMessage.DeleteSurface(
            surfaceId = json.get("surfaceId").asString
        )
    }

    private fun parseComponent(json: JsonObject): A2UIComponent {
        val componentType = json.get("type")?.asString ?: json.get("component")?.asString
            ?: throw JsonParseException("Component type not found")
        val id = json.get("id").asString

        // Parse common fields
        val accessibility = if (json.has("accessibility")) {
            parseAccessibility(json.getAsJsonObject("accessibility"))
        } else null

        val weight = json.get("weight")?.asFloat

        return when (componentType) {
            // Basic Content Components
            "Text" -> {
                val text = parseDynamicValue(json.get("text"))
                val usageHint = json.get("usageHint")?.asString
                A2UIComponent.Text(id, accessibility, weight, text, usageHint)
            }
            "Image" -> {
                val url = parseDynamicValue(json.get("url"))
                val fit = json.get("fit")?.asString
                val usageHint = json.get("usageHint")?.asString
                A2UIComponent.Image(id, accessibility, weight, url, fit, usageHint)
            }
            "Icon" -> {
                val name = json.get("name").asString
                val size = json.get("size")?.let { parseDynamicValue(it) }
                val color = json.get("color")?.let { parseDynamicValue(it) }
                A2UIComponent.Icon(id, accessibility, weight, name, size, color)
            }
            "Video" -> {
                val url = parseDynamicValue(json.get("url"))
                val autoplay = json.get("autoplay")?.asBoolean
                val controls = json.get("controls")?.asBoolean
                val loop = json.get("loop")?.asBoolean
                A2UIComponent.Video(id, accessibility, weight, url, autoplay, controls, loop)
            }
            "AudioPlayer" -> {
                val url = parseDynamicValue(json.get("url"))
                val autoplay = json.get("autoplay")?.asBoolean
                val controls = json.get("controls")?.asBoolean
                A2UIComponent.AudioPlayer(id, accessibility, weight, url, autoplay, controls)
            }
            "Divider" -> {
                val orientation = json.get("orientation")?.asString
                A2UIComponent.Divider(id, accessibility, weight, orientation)
            }

            // Layout & Container Components
            "Row" -> {
                val children = parseChildrenList(json.get("children"))
                val distribution = json.get("distribution")?.asString
                val alignment = json.get("alignment")?.asString
                A2UIComponent.Row(id, accessibility, weight, children, distribution, alignment)
            }
            "Column" -> {
                val children = parseChildrenList(json.get("children"))
                val distribution = json.get("distribution")?.asString
                val alignment = json.get("alignment")?.asString
                A2UIComponent.Column(id, accessibility, weight, children, distribution, alignment)
            }
            "List" -> {
                val children = parseChildrenList(json.get("children"))
                val direction = json.get("direction")?.asString
                A2UIComponent.A2UIList(id, accessibility, weight, children, direction)
            }
            "Card" -> {
                val children = parseChildrenList(json.get("children"))
                val action = json.get("action")?.let { parseAction(it.asJsonObject) }
                A2UIComponent.Card(id, accessibility, weight, children, action)
            }
            "Tabs" -> {
                val tabItemsArray = json.getAsJsonArray("tabItems")
                val tabItems = tabItemsArray.map { parseTabItem(it.asJsonObject) }
                val selectedIndex = json.get("selectedIndex")?.let { parseDynamicValue(it) }
                A2UIComponent.Tabs(id, accessibility, weight, tabItems, selectedIndex)
            }
            "Modal" -> {
                val entryPointChild = json.get("entryPointChild")?.asString
                val contentChild = json.get("contentChild")?.asString
                val visible = json.get("visible")?.asBoolean
                A2UIComponent.Modal(id, accessibility, weight, entryPointChild, contentChild, visible)
            }

            // Interactive & Input Components
            "Button" -> {
                val child = json.get("child")?.asString
                val action = json.get("action")?.let { parseAction(it.asJsonObject) }
                val primary = json.get("primary")?.asBoolean
                A2UIComponent.Button(id, accessibility, weight, child, action, primary)
            }
            "CheckBox" -> {
                val label = parseDynamicValue(json.get("label"))
                val checked = parseDynamicValue(json.get("checked"))
                val value = json.get("value")?.let { parseDynamicValue(it) }
                val action = json.get("action")?.let { parseAction(it.asJsonObject) }
                A2UIComponent.CheckBox(id, accessibility, weight, label, checked, value, action)
            }
            "TextField" -> {
                val label = json.get("label")?.let { parseDynamicValue(it) }
                val text = parseDynamicValue(json.get("text"))
                val textFieldType = json.get("textFieldType")?.asString
                val validationRegexp = json.get("validationRegexp")?.asString
                A2UIComponent.TextField(id, accessibility, weight, label, text, textFieldType, validationRegexp)
            }
            "DateTimeInput" -> {
                val label = json.get("label")?.let { parseDynamicValue(it) }
                val value = parseDynamicValue(json.get("value"))
                val dateTimeType = json.get("dateTimeType")?.asString
                A2UIComponent.DateTimeInput(id, accessibility, weight, label, value, dateTimeType)
            }
            "MultipleChoice" -> {
                val label = json.get("label")?.let { parseDynamicValue(it) }
                val optionsArray = json.getAsJsonArray("options")
                val options = optionsArray.map { parseChoiceOption(it.asJsonObject) }
                val selectedValues = parseDynamicValue(json.get("selectedValues"))
                val maxAllowedSelections = json.get("maxAllowedSelections")?.asInt
                A2UIComponent.MultipleChoice(id, accessibility, weight, label, options, selectedValues, maxAllowedSelections)
            }
            "Slider" -> {
                val value = parseDynamicValue(json.get("value"))
                val minValue = json.get("minValue")?.let { parseDynamicValue(it) }
                val maxValue = json.get("maxValue")?.let { parseDynamicValue(it) }
                val step = json.get("step")?.let { parseDynamicValue(it) }
                A2UIComponent.Slider(id, accessibility, weight, value, minValue, maxValue, step)
            }

            else -> throw JsonParseException("Unknown component type: $componentType")
        }
    }

    private fun parseDynamicValue(json: JsonElement?): DynamicValue {
        if (json == null || json.isJsonNull) {
            return DynamicValue.LiteralString("")
        }
        return when {
            json.isJsonPrimitive -> {
                val primitive = json.asJsonPrimitive
                when {
                    primitive.isString -> DynamicValue.LiteralString(primitive.asString)
                    primitive.isNumber -> DynamicValue.LiteralNumber(primitive.asNumber)
                    primitive.isBoolean -> DynamicValue.LiteralBoolean(primitive.asBoolean)
                    else -> throw JsonParseException("Unknown primitive type")
                }
            }
            json.isJsonArray -> {
                val array = json.asJsonArray.map { parseDynamicValue(it) }
                DynamicValue.LiteralArray(array)
            }
            json.isJsonObject -> {
                val obj = json.asJsonObject
                when {
                    obj.has("path") -> {
                        DynamicValue.DataBinding(obj.get("path").asString)
                    }
                    obj.has("call") -> {
                        val call = obj.get("call").asString
                        val args = if (obj.has("args")) {
                            val argsObj = obj.getAsJsonObject("args")
                            argsObj.entrySet().associate { (key, value) ->
                                key to parseDynamicValue(value)
                            }
                        } else emptyMap()
                        val returnType = obj.get("returnType")?.asString ?: "boolean"
                        DynamicValue.FunctionCall(call, args, returnType)
                    }
                    else -> throw JsonParseException("Unknown DynamicValue object type")
                }
            }
            else -> throw JsonParseException("Unknown DynamicValue type")
        }
    }

    private fun parseAccessibility(json: JsonObject): AccessibilityAttributes {
        return AccessibilityAttributes(
            label = if (json.has("label")) parseDynamicValue(json.get("label")) else null,
            description = if (json.has("description")) parseDynamicValue(json.get("description")) else null
        )
    }

    private fun parseChildrenList(json: JsonElement?): ChildrenList {
        if (json == null || json.isJsonNull) {
            return ChildrenList.ExplicitList(emptyList())
        }
        return when {
            json.isJsonArray -> {
                val children = json.asJsonArray.map { it.asString }
                ChildrenList.ExplicitList(children)
            }
            json.isJsonObject -> {
                val obj = json.asJsonObject
                when {
                    obj.has("componentId") && obj.has("dataBinding") -> {
                        ChildrenList.Template(
                            componentId = obj.get("componentId").asString,
                            dataBinding = obj.get("dataBinding").asString
                        )
                    }
                    obj.has("child") -> {
                        ChildrenList.Child(obj.get("child").asString)
                    }
                    obj.has("contentChild") -> {
                        ChildrenList.ContentChild(obj.get("contentChild").asString)
                    }
                    else -> ChildrenList.ExplicitList(emptyList())
                }
            }
            else -> ChildrenList.ExplicitList(emptyList())
        }
    }

    private fun parseTabItem(json: JsonObject): TabItem {
        return TabItem(
            title = json.get("title").asString,
            child = json.get("child").asString
        )
    }

    private fun parseChoiceOption(json: JsonObject): ChoiceOption {
        return ChoiceOption(
            label = parseDynamicValue(json.get("label")),
            value = json.get("value").asString
        )
    }

    private fun parseAction(json: JsonObject): Action {
        return when {
            json.has("surfaceId") -> {
                Action.Navigate(json.get("surfaceId").asString)
            }
            json.has("event") -> {
                val eventObj = json.getAsJsonObject("event")
                Action.Event(
                    name = eventObj.get("name").asString,
                    context = if (eventObj.has("context")) {
                        eventObj.getAsJsonObject("context").entrySet().associate { (key, value) ->
                            key to parseDynamicValue(value)
                        }
                    } else null
                )
            }
            json.has("call") -> {
                val call = json.get("call").asString
                val args = if (json.has("args")) {
                    json.getAsJsonObject("args").entrySet().associate { (key, value) ->
                        key to parseDynamicValue(value)
                    }
                } else emptyMap()
                val returnType = json.get("returnType")?.asString ?: "boolean"
                Action.FunctionCall(call, args, returnType)
            }
            json.has("name") -> {
                Action.Event(
                    name = json.get("name").asString,
                    context = if (json.has("context")) {
                        json.getAsJsonObject("context").entrySet().associate { (key, value) ->
                            key to parseDynamicValue(value)
                        }
                    } else null
                )
            }
            else -> throw JsonParseException("Unknown Action type")
        }
    }
}
