package com.example.androiduirenderer.a2ui.core

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.androiduirenderer.a2ui.model.*

private fun Int.dpToPx(ctx: Context) = (this * ctx.resources.displayMetrics.density).toInt()

object A2UIComponents {
    var dynamicValueResolver: DynamicValueResolver? = null
    var currentTheme: Theme? = null
    
    fun registerDefaultComponents(ctx: Context) {
        val cat = ComponentCatalog.getInstance()
        
        cat.register("Text") { c, p, c2 -> (c as? A2UIComponent.Text)?.run {
            TextView(c2).apply {
                id = View.generateViewId()
                text = dynamicValueResolver?.resolve(c.text)?.toString() ?: ""
            }
        } ?: throw IllegalArgumentException("Not Text") }
        
        cat.register("Button") { c, p, c2 -> (c as? A2UIComponent.Button)?.run {
            Button(c2).apply { id = View.generateViewId() }
        } ?: throw IllegalArgumentException("Not Button") }
        
        cat.register("Row") { c, p, c2 -> (c as? A2UIComponent.Row)?.run {
            LinearLayout(c2).apply { id = View.generateViewId(); orientation = LinearLayout.HORIZONTAL }
        } ?: throw IllegalArgumentException("Not Row") }
        
        cat.register("Column") { c, p, c2 -> (c as? A2UIComponent.Column)?.run {
            LinearLayout(c2).apply { id = View.generateViewId(); orientation = LinearLayout.VERTICAL }
        } ?: throw IllegalArgumentException("Not Column") }
        
        cat.register("List") { c, p, c2 -> (c as? A2UIComponent.A2UIList)?.run {
            LinearLayout(c2).apply { id = View.generateViewId(); orientation = LinearLayout.VERTICAL }
        } ?: throw IllegalArgumentException("Not List") }
        
        cat.register("Image") { c, p, c2 -> (c as? A2UIComponent.Image)?.run {
            ImageView(c2).apply { id = View.generateViewId() }
        } ?: throw IllegalArgumentException("Not Image") }
        
        cat.register("Icon") { c, p, c2 -> (c as? A2UIComponent.Icon)?.run {
            ImageView(c2).apply {
                id = View.generateViewId()
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                IconManager.getInstance()?.getIconResource(c.name.lowercase())?.let { setImageResource(it) }
                c.color?.let { setColorFilter(Color.parseColor("#DA0011"), android.graphics.PorterDuff.Mode.SRC_IN) }
            }
        } ?: throw IllegalArgumentException("Not Icon") }
        
        cat.register("Video") { c, p, c2 -> (c as? A2UIComponent.Video)?.run {
            VideoView(c2).apply { id = View.generateViewId() }
        } ?: throw IllegalArgumentException("Not Video") }
        
        cat.register("CheckBox") { c, p, c2 -> (c as? A2UIComponent.CheckBox)?.run {
            CheckBox(c2).apply { id = View.generateViewId() }
        } ?: throw IllegalArgumentException("Not CheckBox") }
        
        cat.register("TextField") { c, p, c2 -> (c as? A2UIComponent.TextField)?.run {
            EditText(c2).apply { id = View.generateViewId() }
        } ?: throw IllegalArgumentException("Not TextField") }
        
        cat.register("ChoicePicker") { c, p, c2 -> (c as? A2UIComponent.ChoicePicker)?.run {
            Spinner(c2).apply { id = View.generateViewId() }
        } ?: throw IllegalArgumentException("Not ChoicePicker") }
        
        cat.register("ProgressBar") { c, p, c2 -> (c as? A2UIComponent.ProgressBar)?.run {
            ProgressBar(c2).apply { id = View.generateViewId() }
        } ?: throw IllegalArgumentException("Not ProgressBar") }
        
        cat.register("Slider") { c, p, c2 -> (c as? A2UIComponent.Slider)?.run {
            SeekBar(c2).apply { id = View.generateViewId() }
        } ?: throw IllegalArgumentException("Not Slider") }
        
        cat.register("BalanceDisplay") { c, p, c2 -> (c as? A2UIComponent.BalanceDisplay)?.run {
            TextView(c2).apply {
                id = View.generateViewId()
                text = "${dynamicValueResolver?.resolve(currency)} ${dynamicValueResolver?.resolve(amount)}"
            }
        } ?: throw IllegalArgumentException("Not BalanceDisplay") }
        
        cat.register("AccountCard") { c, p, c2 -> (c as? A2UIComponent.AccountCard)?.run {
            LinearLayout(c2).apply {
                id = View.generateViewId()
                orientation = LinearLayout.VERTICAL
                addView(TextView(c2).apply { text = dynamicValueResolver?.resolve(accountName)?.toString() })
                addView(TextView(c2).apply { text = dynamicValueResolver?.resolve(accountNumber)?.toString() })
                addView(TextView(c2).apply { text = "${dynamicValueResolver?.resolve(balance)} ${dynamicValueResolver?.resolve(currency)}" })
            }
        } ?: throw IllegalArgumentException("Not AccountCard") }
        
        cat.register("ActionButton") { c, p, c2 -> (c as? A2UIComponent.ActionButton)?.run {
            LinearLayout(c2).apply {
                id = View.generateViewId()
                orientation = LinearLayout.VERTICAL
                gravity = android.view.Gravity.CENTER
                addView(ImageView(c2).apply {
                    layoutParams = LinearLayout.LayoutParams(32.dpToPx(c2), 32.dpToPx(c2))
                    IconManager.getInstance()?.getIconResource(c.icon)?.let { setImageResource(it) }
                })
                addView(TextView(c2).apply { text = dynamicValueResolver?.resolve(label)?.toString() })
            }
        } ?: throw IllegalArgumentException("Not ActionButton") }
        
        cat.register("QuickActionCircle") { c, p, c2 -> (c as? A2UIComponent.QuickActionCircle)?.run {
            LinearLayout(c2).apply {
                id = View.generateViewId()
                orientation = LinearLayout.VERTICAL
                gravity = android.view.Gravity.CENTER
                val btn = LinearLayout(c2).apply {
                    layoutParams = LinearLayout.LayoutParams(72.dpToPx(c2), 72.dpToPx(c2))
                    gravity = android.view.Gravity.CENTER
                    background = GradientDrawable().apply { cornerRadius = 36.dpToPx(c2).toFloat(); setColor(Color.WHITE) }
                }
                btn.addView(ImageView(c2).apply {
                    layoutParams = LinearLayout.LayoutParams(32.dpToPx(c2), 32.dpToPx(c2))
                    IconManager.getInstance()?.getIconResource(c.icon)?.let { setImageResource(it) }
                    setColorFilter(Color.parseColor("#DA0011"))
                })
                addView(btn)
                addView(TextView(c2).apply { text = dynamicValueResolver?.resolve(label)?.toString() })
            }
        } ?: throw IllegalArgumentException("Not QuickActionCircle") }
        
        cat.register("Divider") { c, p, c2 ->
            View(c2).apply {
                id = View.generateViewId()
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1.dpToPx(c2))
                setBackgroundColor(Color.parseColor("#E6E6E6"))
            }
        }
        
        cat.register("Card") { c, p, c2 -> (c as? A2UIComponent.Card)?.run {
            LinearLayout(c2).apply {
                id = View.generateViewId()
                orientation = LinearLayout.VERTICAL
                background = GradientDrawable().apply { setColor(Color.WHITE) }
                setPadding(16.dpToPx(c2), 16.dpToPx(c2), 16.dpToPx(c2), 16.dpToPx(c2))
            }
        } ?: throw IllegalArgumentException("Not Card") }
        
        cat.register("Tab") { c, p, c2 -> (c as? A2UIComponent.Tab)?.run {
            TextView(c2).apply {
                id = View.generateViewId()
                gravity = android.view.Gravity.CENTER
                setPadding(24.dpToPx(c2), 12.dpToPx(c2), 24.dpToPx(c2), 12.dpToPx(c2))
                text = dynamicValueResolver?.resolve(c.text)?.toString() ?: ""
            }
        } ?: throw IllegalArgumentException("Not Tab") }
        
        cat.register("SegmentedTab") { c, p, c2 -> (c as? A2UIComponent.SegmentedTab)?.run {
            TextView(c2).apply {
                id = View.generateViewId()
                gravity = android.view.Gravity.CENTER
                setPadding(20.dpToPx(c2), 8.dpToPx(c2), 20.dpToPx(c2), 8.dpToPx(c2))
                text = dynamicValueResolver?.resolve(c.text)?.toString() ?: ""
            }
        } ?: throw IllegalArgumentException("Not SegmentedTab") }
        
        cat.register("ProductIcon") { c, p, c2 -> (c as? A2UIComponent.ProductIcon)?.run {
            LinearLayout(c2).apply {
                id = View.generateViewId()
                orientation = LinearLayout.VERTICAL
                gravity = android.view.Gravity.CENTER
                setPadding(8.dpToPx(c2), 8.dpToPx(c2), 8.dpToPx(c2), 8.dpToPx(c2))
                val iconCont = LinearLayout(c2).apply {
                    layoutParams = LinearLayout.LayoutParams(56.dpToPx(c2), 56.dpToPx(c2))
                    gravity = android.view.Gravity.CENTER
                    background = GradientDrawable().apply { cornerRadius = 28.dpToPx(c2).toFloat(); setColor(Color.parseColor("#F2F2F2")) }
                }
                iconCont.addView(ImageView(c2).apply {
                    layoutParams = LinearLayout.LayoutParams(32.dpToPx(c2), 32.dpToPx(c2))
                    IconManager.getInstance()?.getIconResource(c.icon)?.let { setImageResource(it) }
                    setColorFilter(Color.parseColor("#DA0011"))
                })
                addView(iconCont)
                addView(TextView(c2).apply {
                    textSize = 12f
                    setPadding(0, 8.dpToPx(c2), 0, 0)
                    text = dynamicValueResolver?.resolve(label)?.toString() ?: ""
                })
            }
        } ?: throw IllegalArgumentException("Not ProductIcon") }
        
        cat.register("View") { c, p, c2 -> (c as? A2UIComponent.View)?.run {
            View(c2).apply {
                id = View.generateViewId()
                backgroundColor?.let { setBackgroundColor(Color.parseColor(it)) }
            }
        } ?: throw IllegalArgumentException("Not View") }
    }
}
