package com.example.androiduirenderer.a2ui.core

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.*
import com.example.androiduirenderer.a2ui.model.*
import com.google.android.material.tabs.TabLayout

private fun Int.dpToPx(ctx: Context) = (this * ctx.resources.displayMetrics.density).toInt()

/**
 * Holds binding metadata for a view. Created during widget tree construction,
 * resolved later during the data binding pass.
 */
data class ViewBinding(
    val view: View,
    val textBinding: DynamicValue? = null,
    val labelBinding: DynamicValue? = null,
    val valueBinding: DynamicValue? = null,
    val checkedBinding: DynamicValue? = null,
    val srcBinding: DynamicValue? = null,
    val iconId: String? = null,
    val colorBinding: DynamicValue? = null,
    val children: List<ViewBinding> = emptyList()
)

object A2UIComponents {
    var dynamicValueResolver: DynamicValueResolver? = null
    var currentTheme: Theme? = null

    /**
     * STEP 5: Build widget tree from component definitions.
     * Creates all views and stores binding metadata for later resolution.
     * Uses ComponentCatalog for widget registry lookup.
     */
    fun buildWidgetTree(component: A2UIComponent, parent: ViewGroup, context: Context): ViewBinding {
        return when (component) {
            // Basic Content Components
            is A2UIComponent.Text -> createText(component, context)
            is A2UIComponent.Image -> createImage(component, context)
            is A2UIComponent.Icon -> createIcon(component, context)
            is A2UIComponent.Video -> createVideo(component, context)
            is A2UIComponent.AudioPlayer -> createAudioPlayer(component, context)
            is A2UIComponent.Divider -> createDivider(component, context)
            is A2UIComponent.ProgressBar -> createProgressBar(component, context)
            is A2UIComponent.Slider -> createSlider(component, context)

            // Layout & Container Components
            is A2UIComponent.Row -> createRow(component, context)
            is A2UIComponent.Column -> createColumn(component, context)
            is A2UIComponent.A2UIList -> createList(component, context)
            is A2UIComponent.Card -> createCard(component, context)
            is A2UIComponent.Tabs -> createTabs(component, context)
            is A2UIComponent.Modal -> createModal(component, context)

            // Interactive & Input Components
            is A2UIComponent.Button -> createButton(component, context)
            is A2UIComponent.CheckBox -> createCheckBox(component, context)
            is A2UIComponent.TextField -> createTextField(component, context)
            is A2UIComponent.DateTimeInput -> createDateTimeInput(component, context)
            is A2UIComponent.MultipleChoice -> createMultipleChoice(component, context)

            // Legacy/Other components - map to closest equivalent
            is A2UIComponent.ChoicePicker -> createMultipleChoice(component.toMultipleChoice(), context)
            is A2UIComponent.AccountCard,
            is A2UIComponent.ActionButton,
            is A2UIComponent.AppBar,
            is A2UIComponent.BalanceDisplay,
            is A2UIComponent.MenuItem,
            is A2UIComponent.MenuSection,
            is A2UIComponent.NavigationBar,
            is A2UIComponent.ProductIcon,
            is A2UIComponent.QuickActionCircle,
            is A2UIComponent.View,
            is A2UIComponent.Overlay,
            is A2UIComponent.SegmentedTab,
            is A2UIComponent.Tab -> {
                // Fallback: create a simple TextView placeholder
                ViewBinding(view = TextView(context).apply { 
                    id = View.generateViewId()
                    text = "Unsupported: ${component::class.simpleName}"
                })
            }
        }
    }

    // === Basic Content Components ===

    private fun createText(component: A2UIComponent.Text, context: Context): ViewBinding {
        return ViewBinding(
            view = TextView(context).apply { 
                id = View.generateViewId()
                // Apply usageHint (h1-h5, body, caption)
                component.usageHint?.let { hint ->
                    when (hint) {
                        "h1" -> textSize = 32f
                        "h2" -> textSize = 28f
                        "h3" -> textSize = 24f
                        "h4" -> textSize = 20f
                        "h5" -> textSize = 16f
                        "body" -> textSize = 14f
                        "caption" -> textSize = 12f
                    }
                }
            },
            textBinding = component.text
        )
    }

    private fun createImage(component: A2UIComponent.Image, context: Context): ViewBinding {
        return ViewBinding(
            view = ImageView(context).apply { 
                id = View.generateViewId()
                scaleType = when (component.fit) {
                    "cover" -> ImageView.ScaleType.CENTER_CROP
                    "contain" -> ImageView.ScaleType.FIT_CENTER
                    "fill" -> ImageView.ScaleType.FIT_XY
                    else -> ImageView.ScaleType.FIT_CENTER
                }
            },
            srcBinding = component.url
        )
    }

    private fun createIcon(component: A2UIComponent.Icon, context: Context): ViewBinding {
        return ViewBinding(
            view = ImageView(context).apply {
                id = View.generateViewId()
                scaleType = ImageView.ScaleType.CENTER_INSIDE
            },
            iconId = component.name,
            colorBinding = component.color
        )
    }

    private fun createVideo(component: A2UIComponent.Video, context: Context): ViewBinding {
        return ViewBinding(
            view = VideoView(context).apply { id = View.generateViewId() }
        )
    }

    private fun createAudioPlayer(component: A2UIComponent.AudioPlayer, context: Context): ViewBinding {
        return ViewBinding(
            view = LinearLayout(context).apply { 
                id = View.generateViewId()
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER
                addView(ImageView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(48.dpToPx(context), 48.dpToPx(context))
                    setImageResource(android.R.drawable.ic_media_play)
                })
                addView(TextView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    text = "Audio Player"
                })
            }
        )
    }

    private fun createDivider(component: A2UIComponent.Divider, context: Context): ViewBinding {
        val orientation = component.orientation ?: "horizontal"
        return ViewBinding(
            view = View(context).apply {
                id = View.generateViewId()
                layoutParams = if (orientation == "horizontal") {
                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1.dpToPx(context))
                } else {
                    LinearLayout.LayoutParams(1.dpToPx(context), ViewGroup.LayoutParams.MATCH_PARENT)
                }
                setBackgroundColor(Color.parseColor("#E6E6E6"))
            }
        )
    }

    private fun createProgressBar(component: A2UIComponent.ProgressBar, context: Context): ViewBinding {
        return ViewBinding(
            view = ProgressBar(context).apply { 
                id = View.generateViewId()
            },
            valueBinding = component.value
        )
    }

    // === Layout & Container Components ===

    private fun createRow(component: A2UIComponent.Row, context: Context): ViewBinding {
        return ViewBinding(
            view = LinearLayout(context).apply {
                id = View.generateViewId()
                orientation = LinearLayout.HORIZONTAL
                // Apply distribution and alignment
                component.distribution?.let { dist ->
                    gravity = when (dist) {
                        "center" -> android.view.Gravity.CENTER_HORIZONTAL
                        "end" -> android.view.Gravity.END
                        "spaceBetween" -> android.view.Gravity.FILL_HORIZONTAL
                        else -> android.view.Gravity.START
                    }
                }
            },
            children = emptyList()
        )
    }

    private fun createColumn(component: A2UIComponent.Column, context: Context): ViewBinding {
        return ViewBinding(
            view = LinearLayout(context).apply {
                id = View.generateViewId()
                orientation = LinearLayout.VERTICAL
                // Apply distribution and alignment
                component.distribution?.let { dist ->
                    gravity = when (dist) {
                        "center" -> android.view.Gravity.CENTER_VERTICAL
                        "end" -> android.view.Gravity.BOTTOM
                        "spaceBetween" -> android.view.Gravity.FILL_VERTICAL
                        else -> android.view.Gravity.TOP
                    }
                }
            },
            children = emptyList()
        )
    }

    private fun createList(component: A2UIComponent.A2UIList, context: Context): ViewBinding {
        return ViewBinding(
            view = LinearLayout(context).apply {
                id = View.generateViewId()
                orientation = if (component.direction == "horizontal") {
                    LinearLayout.HORIZONTAL
                } else {
                    LinearLayout.VERTICAL
                }
            },
            children = emptyList()
        )
    }

    private fun createCard(component: A2UIComponent.Card, context: Context): ViewBinding {
        return ViewBinding(
            view = LinearLayout(context).apply {
                id = View.generateViewId()
                orientation = LinearLayout.VERTICAL
                setPadding(16.dpToPx(context), 16.dpToPx(context), 16.dpToPx(context), 16.dpToPx(context))
                // Apply card styling
                background = android.graphics.drawable.GradientDrawable().apply {
                    setColor(Color.WHITE)
                    cornerRadius = 8.dpToPx(context).toFloat()
                }
                // Set elevation for API 21+
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    elevation = 4.dpToPx(context).toFloat()
                }
            },
            children = emptyList()
        )
    }

    private fun createTabs(component: A2UIComponent.Tabs, context: Context): ViewBinding {
        return ViewBinding(
            view = TabLayout(context).apply {
                id = View.generateViewId()
                // Add tabs
                component.tabItems.forEach { tabItem ->
                    addTab(newTab().setText(tabItem.title))
                }
            },
            children = emptyList()
        )
    }

    private fun createModal(component: A2UIComponent.Modal, context: Context): ViewBinding {
        // Modal is represented as a FrameLayout overlay
        return ViewBinding(
            view = FrameLayout(context).apply {
                id = View.generateViewId()
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                // Background will be set when visible
            },
            children = emptyList()
        )
    }

    // === Interactive & Input Components ===

    private fun createButton(component: A2UIComponent.Button, context: Context): ViewBinding {
        return ViewBinding(
            view = Button(context).apply { 
                id = View.generateViewId()
                // Apply primary style
                if (component.primary == true) {
                    setTextColor(Color.WHITE)
                    setBackgroundColor(Color.parseColor("#DA0011"))
                }
            }
        )
    }

    private fun createCheckBox(component: A2UIComponent.CheckBox, context: Context): ViewBinding {
        return ViewBinding(
            view = CheckBox(context).apply { id = View.generateViewId() },
            labelBinding = component.label,
            checkedBinding = component.checked
        )
    }

    private fun createTextField(component: A2UIComponent.TextField, context: Context): ViewBinding {
        return ViewBinding(
            view = EditText(context).apply { 
                id = View.generateViewId()
                component.textFieldType?.let { type ->
                    when (type) {
                        "password" -> inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                        "email" -> inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        "number" -> inputType = android.text.InputType.TYPE_CLASS_NUMBER
                        else -> inputType = android.text.InputType.TYPE_CLASS_TEXT
                    }
                }
            },
            labelBinding = component.label,
            valueBinding = component.text
        )
    }

    private fun createDateTimeInput(component: A2UIComponent.DateTimeInput, context: Context): ViewBinding {
        return ViewBinding(
            view = EditText(context).apply { 
                id = View.generateViewId()
                component.dateTimeType?.let { type ->
                    when (type) {
                        "date" -> inputType = android.text.InputType.TYPE_CLASS_DATETIME or android.text.InputType.TYPE_DATETIME_VARIATION_DATE
                        "time" -> inputType = android.text.InputType.TYPE_CLASS_DATETIME or android.text.InputType.TYPE_DATETIME_VARIATION_TIME
                        else -> inputType = android.text.InputType.TYPE_CLASS_DATETIME
                    }
                }
            },
            labelBinding = component.label,
            valueBinding = component.value
        )
    }

    private fun createMultipleChoice(component: A2UIComponent.MultipleChoice, context: Context): ViewBinding {
        return ViewBinding(
            view = Spinner(context).apply { id = View.generateViewId() },
            labelBinding = component.label,
            valueBinding = component.selectedValues
        )
    }

    private fun createSlider(component: A2UIComponent.Slider, context: Context): ViewBinding {
        return ViewBinding(
            view = SeekBar(context).apply { id = View.generateViewId() },
            valueBinding = component.value
        )
    }

    /**
     * STEP 6: Resolve data bindings for the entire widget tree.
     * Walks the tree and resolves all stored bindings using the DynamicValueResolver.
     */
    fun bindData(binding: ViewBinding) {
        val resolver = dynamicValueResolver ?: return

        // Resolve text binding
        binding.textBinding?.let { dv ->
            (binding.view as? TextView)?.text = resolver.resolveToString(dv)
        }

        // Resolve label binding
        binding.labelBinding?.let { dv ->
            when (binding.view) {
                is CheckBox -> binding.view.text = resolver.resolveToString(dv)
                is EditText -> binding.view.hint = resolver.resolveToString(dv)
                is TextView -> binding.view.text = resolver.resolveToString(dv)
            }
        }

        // Resolve value binding
        binding.valueBinding?.let { dv ->
            when (binding.view) {
                is CheckBox -> binding.view.isChecked = resolver.resolveToBoolean(dv)
                is SeekBar -> binding.view.progress = resolver.resolveToInt(dv)
                is EditText -> binding.view.setText(resolver.resolveToString(dv))
                is TextView -> binding.view.text = resolver.resolveToString(dv)
            }
        }

        // Resolve checked binding
        binding.checkedBinding?.let { dv ->
            (binding.view as? CheckBox)?.isChecked = resolver.resolveToBoolean(dv)
        }

        // Resolve src/icon bindings
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
                val color = resolver.resolveToString(dv)
                if (color.isNotEmpty()) {
                    try {
                        imageView.setColorFilter(Color.parseColor(color))
                    } catch (e: Exception) {
                        // Invalid color format
                    }
                }
            }
        }

        // Recursively bind children
        binding.children.forEach { child ->
            bindData(child)
        }
    }
}

// Extension to convert legacy ChoicePicker to MultipleChoice
fun A2UIComponent.ChoicePicker.toMultipleChoice(): A2UIComponent.MultipleChoice {
    return A2UIComponent.MultipleChoice(
        id = id,
        accessibility = accessibility,
        weight = weight,
        label = label,
        options = options,
        selectedValues = value,
        maxAllowedSelections = null
    )
}
