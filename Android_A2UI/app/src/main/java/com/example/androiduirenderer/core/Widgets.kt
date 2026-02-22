package com.example.androiduirenderer.core

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.androiduirenderer.model.WidgetConfig

object Widgets {
    fun registerDefaultWidgets(context: Context) {
        val registry = WidgetRegistry.getInstance()
        registry.register("text") { config, parent ->
            TextView(context).apply {
                id = View.generateViewId()
                config.id?.let { tag = it }
            }
        }
        registry.register("button") { config, parent ->
            Button(context).apply {
                id = View.generateViewId()
                config.id?.let { tag = it }
            }
        }
        registry.register("checkbox") { config, parent ->
            CheckBox(context).apply {
                id = View.generateViewId()
                config.id?.let { tag = it }
            }
        }
        registry.register("radio") { config, parent ->
            RadioButton(context).apply {
                id = View.generateViewId()
                config.id?.let { tag = it }
            }
        }
        registry.register("dropdown") { config, parent ->
            Spinner(context).apply {
                id = View.generateViewId()
                config.id?.let { tag = it }
            }
        }
        registry.register("image") { config, parent ->
            ImageView(context).apply {
                id = View.generateViewId()
                config.id?.let { tag = it }
            }
        }
        registry.register("list") { config, parent ->
            ListView(context).apply {
                id = View.generateViewId()
                config.id?.let { tag = it }
            }
        }
        registry.register("linear") { config, parent ->
            LinearLayout(context).apply {
                id = View.generateViewId()
                config.id?.let { tag = it }
                config.layout?.orientation?.let { orientation ->
                    this.orientation = when (orientation.lowercase()) {
                        "vertical" -> LinearLayout.VERTICAL
                        else -> LinearLayout.HORIZONTAL
                    }
                }
            }
        }
        registry.register("frame") { config, parent ->
            FrameLayout(context).apply {
                id = View.generateViewId()
                config.id?.let { tag = it }
            }
        }
        registry.register("constraint") { config, parent ->
            ConstraintLayout(context).apply {
                id = View.generateViewId()
                config.id?.let { tag = it }
            }
        }
        registry.register("scroll") { config, parent ->
            ScrollView(context).apply {
                id = View.generateViewId()
                config.id?.let { tag = it }
            }
        }
    }
}