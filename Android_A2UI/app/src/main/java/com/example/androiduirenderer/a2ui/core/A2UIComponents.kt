package com.example.androiduirenderer.a2ui.core

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.androiduirenderer.a2ui.model.*

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
    val srcBinding: DynamicValue? = null,
    val iconId: String? = null,
    val colorBinding: DynamicValue? = null,
    val children: List<ViewBinding> = emptyList()
)

object A2UIComponents {
    var dynamicValueResolver: DynamicValueResolver? = null
    var currentTheme: Theme? = null

    /**
     * STEP 1: Build widget tree from component definitions.
     * Creates all views and stores binding metadata for later resolution.
     * @return Root ViewBinding containing the view hierarchy with binding metadata
     */
    fun buildWidgetTree(component: A2UIComponent, parent: ViewGroup, context: Context): ViewBinding {
        return when (component) {
            is A2UIComponent.Text -> createText(component, context)
            is A2UIComponent.Button -> createButton(component, context)
            is A2UIComponent.CheckBox -> createCheckBox(component, context)
            is A2UIComponent.ChoicePicker -> createChoicePicker(component, context)
            is A2UIComponent.TextField -> createTextField(component, context)
            is A2UIComponent.Image -> createImage(component, context)
            is A2UIComponent.Icon -> createIcon(component, context)
            is A2UIComponent.Video -> createVideo(component, context)
            is A2UIComponent.ProgressBar -> createProgressBar(component, context)
            is A2UIComponent.Slider -> createSlider(component, context)
            is A2UIComponent.BalanceDisplay -> createBalanceDisplay(component, context)
            is A2UIComponent.AccountCard -> createAccountCard(component, context)
            is A2UIComponent.ActionButton -> createActionButton(component, context)
            is A2UIComponent.QuickActionCircle -> createQuickActionCircle(component, context)
            is A2UIComponent.Card -> createCard(component, context)
            is A2UIComponent.Tab -> createTab(component, context)
            is A2UIComponent.SegmentedTab -> createSegmentedTab(component, context)
            is A2UIComponent.ProductIcon -> createProductIcon(component, context)
            is A2UIComponent.View -> createView(component, context)
            is A2UIComponent.Divider -> createDivider(context)
            is A2UIComponent.Row -> createRow(component, parent, context)
            is A2UIComponent.Column -> createColumn(component, parent, context)
            is A2UIComponent.A2UIList -> createList(component, parent, context)
            is A2UIComponent.MenuItem,
            is A2UIComponent.MenuSection,
            is A2UIComponent.NavigationBar,
            is A2UIComponent.AppBar,
            is A2UIComponent.Overlay -> {
                throw NotImplementedError("Component ${component::class.simpleName} not yet implemented")
            }
        }
    }

    private fun createText(component: A2UIComponent.Text, context: Context): ViewBinding {
        return ViewBinding(
            view = TextView(context).apply { id = View.generateViewId() },
            textBinding = component.text
        )
    }

    private fun createButton(component: A2UIComponent.Button, context: Context): ViewBinding {
        return ViewBinding(
            view = Button(context).apply { id = View.generateViewId() }
        )
    }

    private fun createCheckBox(component: A2UIComponent.CheckBox, context: Context): ViewBinding {
        return ViewBinding(
            view = CheckBox(context).apply { id = View.generateViewId() },
            labelBinding = component.label,
            valueBinding = component.value
        )
    }

    private fun createChoicePicker(component: A2UIComponent.ChoicePicker, context: Context): ViewBinding {
        return ViewBinding(
            view = Spinner(context).apply { id = View.generateViewId() },
            labelBinding = component.label,
            valueBinding = component.value
        )
    }

    private fun createTextField(component: A2UIComponent.TextField, context: Context): ViewBinding {
        return ViewBinding(
            view = EditText(context).apply { id = View.generateViewId() },
            labelBinding = component.label,
            valueBinding = component.value
        )
    }

    private fun createImage(component: A2UIComponent.Image, context: Context): ViewBinding {
        return ViewBinding(
            view = ImageView(context).apply { id = View.generateViewId() },
            srcBinding = component.src
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

    private fun createProgressBar(component: A2UIComponent.ProgressBar, context: Context): ViewBinding {
        return ViewBinding(
            view = ProgressBar(context).apply { id = View.generateViewId() },
            valueBinding = component.value
        )
    }

    private fun createSlider(component: A2UIComponent.Slider, context: Context): ViewBinding {
        return ViewBinding(
            view = SeekBar(context).apply { id = View.generateViewId() },
            valueBinding = component.value,
            labelBinding = component.label
        )
    }

    private fun createBalanceDisplay(component: A2UIComponent.BalanceDisplay, context: Context): ViewBinding {
        return ViewBinding(
            view = TextView(context).apply { id = View.generateViewId() },
            textBinding = component.amount,
            valueBinding = component.currency
        )
    }

    private fun createAccountCard(component: A2UIComponent.AccountCard, context: Context): ViewBinding {
        val containerView = LinearLayout(context).apply {
            id = View.generateViewId()
            orientation = LinearLayout.VERTICAL
        }
        val nameView = TextView(context)
        val numberView = TextView(context)
        val balanceView = TextView(context)
        containerView.addView(nameView)
        containerView.addView(numberView)
        containerView.addView(balanceView)

        return ViewBinding(
            view = containerView,
            labelBinding = component.accountName,
            valueBinding = component.accountNumber,
            children = listOf(
                ViewBinding(view = nameView, textBinding = component.accountName),
                ViewBinding(view = numberView, textBinding = component.accountNumber),
                ViewBinding(view = balanceView, textBinding = component.balance)
            )
        )
    }

    private fun createActionButton(component: A2UIComponent.ActionButton, context: Context): ViewBinding {
        val containerView = LinearLayout(context).apply {
            id = View.generateViewId()
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
        }
        val iconView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(32.dpToPx(context), 32.dpToPx(context))
        }
        val labelView = TextView(context)
        containerView.addView(iconView)
        containerView.addView(labelView)

        return ViewBinding(
            view = containerView,
            labelBinding = component.label,
            children = listOf(
                ViewBinding(view = iconView, iconId = component.icon)
            )
        )
    }

    private fun createQuickActionCircle(component: A2UIComponent.QuickActionCircle, context: Context): ViewBinding {
        val containerView = LinearLayout(context).apply {
            id = View.generateViewId()
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
        }
        val btnContainer = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(72.dpToPx(context), 72.dpToPx(context))
            gravity = android.view.Gravity.CENTER
        }
        val iconView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(32.dpToPx(context), 32.dpToPx(context))
        }
        val labelView = TextView(context)
        btnContainer.addView(iconView)
        containerView.addView(btnContainer)
        containerView.addView(labelView)

        return ViewBinding(
            view = containerView,
            labelBinding = component.label,
            children = listOf(
                ViewBinding(view = iconView, iconId = component.icon)
            )
        )
    }

    private fun createCard(component: A2UIComponent.Card, context: Context): ViewBinding {
        return ViewBinding(
            view = LinearLayout(context).apply {
                id = View.generateViewId()
                orientation = LinearLayout.VERTICAL
                setPadding(16.dpToPx(context), 16.dpToPx(context), 16.dpToPx(context), 16.dpToPx(context))
            }
        )
    }

    private fun createTab(component: A2UIComponent.Tab, context: Context): ViewBinding {
        return ViewBinding(
            view = TextView(context).apply {
                id = View.generateViewId()
                gravity = android.view.Gravity.CENTER
                setPadding(24.dpToPx(context), 12.dpToPx(context), 24.dpToPx(context), 12.dpToPx(context))
            },
            textBinding = component.text
        )
    }

    private fun createSegmentedTab(component: A2UIComponent.SegmentedTab, context: Context): ViewBinding {
        return ViewBinding(
            view = TextView(context).apply {
                id = View.generateViewId()
                gravity = android.view.Gravity.CENTER
                setPadding(20.dpToPx(context), 8.dpToPx(context), 20.dpToPx(context), 8.dpToPx(context))
            },
            textBinding = component.text
        )
    }

    private fun createProductIcon(component: A2UIComponent.ProductIcon, context: Context): ViewBinding {
        val containerView = LinearLayout(context).apply {
            id = View.generateViewId()
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER
            setPadding(8.dpToPx(context), 8.dpToPx(context), 8.dpToPx(context), 8.dpToPx(context))
        }
        val iconContainer = LinearLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(56.dpToPx(context), 56.dpToPx(context))
            gravity = android.view.Gravity.CENTER
        }
        val iconView = ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(32.dpToPx(context), 32.dpToPx(context))
        }
        val labelView = TextView(context).apply {
            textSize = 12f
            setPadding(0, 8.dpToPx(context), 0, 0)
        }
        iconContainer.addView(iconView)
        containerView.addView(iconContainer)
        containerView.addView(labelView)

        return ViewBinding(
            view = containerView,
            labelBinding = component.label,
            children = listOf(
                ViewBinding(view = iconView, iconId = component.icon)
            )
        )
    }

    private fun createView(component: A2UIComponent.View, context: Context): ViewBinding {
        return ViewBinding(
            view = View(context).apply { id = View.generateViewId() }
        )
    }

    private fun createDivider(context: Context): ViewBinding {
        return ViewBinding(
            view = View(context).apply {
                id = View.generateViewId()
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1.dpToPx(context))
                setBackgroundColor(Color.parseColor("#E6E6E6"))
            }
        )
    }

    private fun createRow(component: A2UIComponent.Row, parent: ViewGroup, context: Context): ViewBinding {
        val rowView = LinearLayout(context).apply {
            id = View.generateViewId()
            orientation = LinearLayout.HORIZONTAL
        }

        return ViewBinding(
            view = rowView,
            children = emptyList()
        )
    }

    private fun createColumn(component: A2UIComponent.Column, parent: ViewGroup, context: Context): ViewBinding {
        val columnView = LinearLayout(context).apply {
            id = View.generateViewId()
            orientation = LinearLayout.VERTICAL
        }

        return ViewBinding(
            view = columnView,
            children = emptyList()
        )
    }

    private fun createList(component: A2UIComponent.A2UIList, parent: ViewGroup, context: Context): ViewBinding {
        val listView = LinearLayout(context).apply {
            id = View.generateViewId()
            orientation = LinearLayout.VERTICAL
        }

        return ViewBinding(
            view = listView,
            children = emptyList()
        )
    }

    /**
     * STEP 2: Resolve data bindings for the entire widget tree.
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
