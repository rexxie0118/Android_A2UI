package com.example.androiduirenderer.core

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.androiduirenderer.model.LayoutConfig
import com.example.androiduirenderer.model.WidgetConfig

class UIRenderer(private val context: Context) {
    private val widgetRegistry = WidgetRegistry.getInstance()

    fun renderPage(widgets: List<WidgetConfig>, container: ViewGroup) {
        container.removeAllViews()
        widgets.forEach { config ->
            val view = createWidget(config, container)
            container.addView(view)
        }
    }

    fun createWidget(config: WidgetConfig, parent: ViewGroup): View {
        Log.d("UIRenderer", "createWidget type=${config.type} id=${config.id}")
        val view = widgetRegistry.createWidget(config, parent)
        config.id?.let { view.tag = it }
        applyLayoutParams(config, view, parent)
        applyAttributes(config, view)
        config.children?.forEach { childConfig ->
            val childView = createWidget(childConfig, view as? ViewGroup ?: parent)
            (view as? ViewGroup)?.addView(childView)
        }
        return view
    }

    private fun generateLayoutParams(parent: ViewGroup, config: WidgetConfig): ViewGroup.LayoutParams {
        return when (parent) {
            is LinearLayout -> LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            is FrameLayout -> FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            is ConstraintLayout -> ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            else -> ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun applyLayoutParams(config: WidgetConfig, view: View, parent: ViewGroup) {
        val layoutParams = generateLayoutParams(parent, config)
        config.layout?.let { layout ->
            layout.width?.let { widthStr ->
                layoutParams.width = parseDimension(widthStr, parent.context)
            }
            layout.height?.let { heightStr ->
                layoutParams.height = parseDimension(heightStr, parent.context)
            }
            layout.weight?.let { weight ->
                Log.d("UIRenderer", "Weight $weight for ${config.id}, parent=${parent::class.simpleName}")
                if (layoutParams is LinearLayout.LayoutParams) {
                    layoutParams.weight = weight
                    Log.d("UIRenderer", "Set weight=$weight for ${config.id}")
                } else {
                    Log.d("UIRenderer", "LayoutParams not LinearLayout.LayoutParams, actual=${layoutParams::class.simpleName}")
                }
            }
            layout.gravity?.let { gravityStr ->
                val gravity = parseGravity(gravityStr)
                Log.d("UIRenderer", "Gravity $gravityStr -> $gravity")
                when (layoutParams) {
                    is LinearLayout.LayoutParams -> layoutParams.gravity = gravity
                    is FrameLayout.LayoutParams -> layoutParams.gravity = gravity
                    is ConstraintLayout.LayoutParams -> {
                        // For ConstraintLayout, gravity is not applicable
                    }
                }
            }
            layout.margin?.let { margin ->
                if (layoutParams is ViewGroup.MarginLayoutParams) {
                    margin.left?.let { layoutParams.leftMargin = it }
                    margin.top?.let { layoutParams.topMargin = it }
                    margin.right?.let { layoutParams.rightMargin = it }
                    margin.bottom?.let { layoutParams.bottomMargin = it }
                }
            }
        }
        Log.d("UIRenderer", "Widget ${config.id}: width=${layoutParams.width}, height=${layoutParams.height}, parent=${parent::class.simpleName}")
        view.layoutParams = layoutParams
    }

    private fun applyAttributes(config: WidgetConfig, view: View) {
        Log.d("UIRenderer", "applyAttributes id=${config.id} type=${config.type}")
        config.attributes?.forEach { (key, value) ->
            Log.d("UIRenderer", "Attribute $key=$value")
            when (key) {
                "backgroundColor" -> if (value is String) view.setBackgroundColor(parseColor(value))
                "textColor" -> if (value is String && view is TextView) view.setTextColor(parseColor(value))
                "textSize" -> if (value is Number && view is TextView) view.textSize = value.toFloat()
                "enabled" -> if (value is Boolean) view.isEnabled = value
                "visibility" -> if (value is String) view.visibility = parseVisibility(value)
            }
        }
        when (view) {
            is TextView -> {
                config.text?.let { view.text = it }
                config.hint?.let { view.hint = it }
            }
            is Button -> {
                config.text?.let { view.text = it }
            }
            is CheckBox -> {
                config.text?.let { view.text = it }
                config.checked?.let { view.isChecked = it }
            }
            is RadioButton -> {
                config.text?.let { view.text = it }
                config.checked?.let { view.isChecked = it }
            }
            is ImageView -> {
                config.src?.let { src ->
                    val resourceId = context.resources.getIdentifier(src, "drawable", context.packageName)
                    if (resourceId != 0) {
                        view.setImageResource(resourceId)
                    }
                }
            }
            is Spinner -> {
                config.options?.let { options ->
                    val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, options)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    view.adapter = adapter
                }
            }
            is ListView -> {
                config.items?.let { items ->
                    val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, items)
                    view.adapter = adapter
                }
            }
        }
    }

    private fun parseDimension(dimension: String, context: Context): Int {
        return when {
            dimension == "match_parent" -> ViewGroup.LayoutParams.MATCH_PARENT
            dimension == "wrap_content" -> ViewGroup.LayoutParams.WRAP_CONTENT
            dimension.endsWith("dp") -> {
                val dpValue = dimension.removeSuffix("dp").toFloatOrNull() ?: 0f
                (dpValue * context.resources.displayMetrics.density).toInt()
            }
            dimension.endsWith("px") -> {
                dimension.removeSuffix("px").toIntOrNull() ?: 0
            }
            else -> dimension.toIntOrNull() ?: ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }

    private fun parseGravity(gravity: String): Int {
        return when (gravity.lowercase()) {
            "center" -> android.view.Gravity.CENTER
            "center_horizontal" -> android.view.Gravity.CENTER_HORIZONTAL
            "center_vertical" -> android.view.Gravity.CENTER_VERTICAL
            "start" -> android.view.Gravity.START
            "end" -> android.view.Gravity.END
            "top" -> android.view.Gravity.TOP
            "bottom" -> android.view.Gravity.BOTTOM
            else -> android.view.Gravity.START
        }
    }

    private fun parseColor(colorStr: String): Int {
        return try {
            android.graphics.Color.parseColor(colorStr)
        } catch (e: IllegalArgumentException) {
            android.graphics.Color.BLACK
        }
    }

    private fun parseVisibility(visibility: String): Int {
        return when (visibility.lowercase()) {
            "gone" -> View.GONE
            "invisible" -> View.INVISIBLE
            else -> View.VISIBLE
        }
    }
}