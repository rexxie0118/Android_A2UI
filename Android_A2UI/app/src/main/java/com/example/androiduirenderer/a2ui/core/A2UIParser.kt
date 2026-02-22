package com.example.androiduirenderer.a2ui.core

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.example.androiduirenderer.a2ui.model.*

class A2UIParser(private val gson: Gson = Gson()) {
    
    fun parseMessage(json: String): A2UIResponse {
        val jsonObject = gson.fromJson(json, JsonObject::class.java)
        
        return when {
            jsonObject.has("createSurface") -> {
                val createSurface = parseCreateSurface(jsonObject.getAsJsonObject("createSurface"))
                A2UIResponse(createSurface = createSurface)
            }
            jsonObject.has("updateComponents") -> {
                val updateComponents = parseUpdateComponents(jsonObject.getAsJsonObject("updateComponents"))
                A2UIResponse(updateComponents = updateComponents)
            }
            jsonObject.has("updateDataModel") -> {
                val updateDataModel = parseUpdateDataModel(jsonObject.getAsJsonObject("updateDataModel"))
                A2UIResponse(updateDataModel = updateDataModel)
            }
            jsonObject.has("deleteSurface") -> {
                val deleteSurface = parseDeleteSurface(jsonObject.getAsJsonObject("deleteSurface"))
                A2UIResponse(deleteSurface = deleteSurface)
            }
            else -> throw JsonParseException("Unknown A2UI message type")
        }
    }
    
    private fun parseCreateSurface(json: JsonObject): A2UIMessage.CreateSurface {
        return A2UIMessage.CreateSurface(
            surfaceId = json.get("surfaceId").asString,
            catalogId = json.get("catalogId").asString,
            theme = if (json.has("theme")) {
                val themeMap = gson.fromJson(json.get("theme"), Map::class.java) as Map<String, Any>
                Theme.fromMap(themeMap)
            } else null,
            sendDataModel = json.get("sendDataModel")?.asBoolean ?: false
        )
    }
    
    private fun parseUpdateComponents(json: JsonObject): A2UIMessage.UpdateComponents {
        val surfaceId = json.get("surfaceId").asString
        val componentsArray = json.getAsJsonArray("components")
        val components = mutableListOf<A2UIComponent>()
        
        componentsArray.forEach { componentJson ->
            val component = parseComponent(componentJson.asJsonObject)
            components.add(component)
        }
        
        return A2UIMessage.UpdateComponents(surfaceId, components)
    }
    
    private fun parseUpdateDataModel(json: JsonObject): A2UIMessage.UpdateDataModel {
        return A2UIMessage.UpdateDataModel(
            surfaceId = json.get("surfaceId").asString,
            path = json.get("path")?.asString,
            value = json.get("value")
        )
    }
    
    private fun parseDeleteSurface(json: JsonObject): A2UIMessage.DeleteSurface {
        return A2UIMessage.DeleteSurface(
            surfaceId = json.get("surfaceId").asString
        )
    }
    
    private fun parseComponent(json: JsonObject): A2UIComponent {
        val componentType = json.get("component").asString
        val id = json.get("id").asString
        
        // Parse common fields
        val accessibility = if (json.has("accessibility")) {
            parseAccessibility(json.getAsJsonObject("accessibility"))
        } else null
        
        val weight = json.get("weight")?.asFloat
        
        return when (componentType) {
            "Text" -> {
                val text = parseDynamicValue(json.get("text"))
                val variant = json.get("variant")?.asString
                A2UIComponent.Text(id, accessibility, weight, text, variant)
            }
            "Button" -> {
                val child = json.get("child").asString
                val variant = json.get("variant")?.asString
                val action = parseAction(json.getAsJsonObject("action"))
                A2UIComponent.Button(id, accessibility, weight, child, variant, action)
            }
            "CheckBox" -> {
                val label = parseDynamicValue(json.get("label"))
                val value = parseDynamicValue(json.get("value"))
                A2UIComponent.CheckBox(id, accessibility, weight, label, value)
            }
            "ChoicePicker" -> {
                val label = if (json.has("label")) parseDynamicValue(json.get("label")) else null
                val variant = json.get("variant").asString
                val options = json.getAsJsonArray("options").map { optionJson ->
                    parseChoiceOption(optionJson.asJsonObject)
                }
                val value = parseDynamicValue(json.get("value"))
                A2UIComponent.ChoicePicker(id, accessibility, weight, label, variant, options, value)
            }
            "Row" -> {
                val children = parseChildList(json.get("children"))
                val justify = json.get("justify")?.asString
                val align = json.get("align")?.asString
                A2UIComponent.Row(id, accessibility, weight, children, justify, align)
            }
            "Column" -> {
                val children = parseChildList(json.get("children"))
                val justify = json.get("justify")?.asString
                val align = json.get("align")?.asString
                A2UIComponent.Column(id, accessibility, weight, children, justify, align)
            }
            "List" -> {
                val children = parseChildList(json.get("children"))
                val direction = json.get("direction")?.asString
                val align = json.get("align")?.asString
                A2UIComponent.A2UIList(id, accessibility, weight, children, direction, align)
            }
            "TextField" -> {
                val label = parseDynamicValue(json.get("label"))
                val value = parseDynamicValue(json.get("value"))
                val variant = json.get("variant")?.asString
                A2UIComponent.TextField(id, accessibility, weight, label, value, variant)
            }
            else -> throw JsonParseException("Unknown component type: $componentType")
        }
    }
    
    private fun parseDynamicValue(json: JsonElement): DynamicValue {
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
    
    private fun parseChildList(json: JsonElement): ChildList {
        return when {
            json.isJsonArray -> {
                val children = json.asJsonArray.map { it.asString }
                ChildList.Static(children)
            }
            json.isJsonObject -> {
                val obj = json.asJsonObject
                ChildList.Template(
                    componentId = obj.get("componentId").asString,
                    path = obj.get("path").asString
                )
            }
            else -> throw JsonParseException("Invalid ChildList")
        }
    }
    
    private fun parseChoiceOption(json: JsonObject): ChoiceOption {
        return ChoiceOption(
            label = parseDynamicValue(json.get("label")),
            value = json.get("value").asString
        )
    }
    
    private fun parseAction(json: JsonObject): Action {
        return when {
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
            json.has("functionCall") -> {
                val funcObj = json.getAsJsonObject("functionCall")
                Action.FunctionCallAction(
                    call = funcObj.get("call").asString,
                    args = if (funcObj.has("args")) {
                        funcObj.getAsJsonObject("args").entrySet().associate { (key, value) ->
                            key to parseDynamicValue(value)
                        }
                    } else emptyMap(),
                    returnType = funcObj.get("returnType")?.asString ?: "boolean"
                )
            }
            else -> throw JsonParseException("Unknown Action type")
        }
    }
}