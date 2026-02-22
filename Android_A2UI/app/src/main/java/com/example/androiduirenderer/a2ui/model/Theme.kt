package com.example.androiduirenderer.a2ui.model

import android.graphics.Color
import com.google.gson.annotations.SerializedName

/**
 * Theme configuration for A2UI surfaces.
 * Based on HSBC design system analysis.
 */
data class Theme(
    @SerializedName("name") val name: String = "default",
    
    // Color palette
    @SerializedName("colors") val colors: ColorPalette = ColorPalette(),
    
    // Typography
    @SerializedName("typography") val typography: Typography = Typography(),
    
    // Spacing
    @SerializedName("spacing") val spacing: Spacing = Spacing(),
    
    // Component styling
    @SerializedName("components") val components: ComponentStyles = ComponentStyles(),
    
    // Elevation and shadows
    @SerializedName("elevation") val elevation: Elevation = Elevation()
) {
    companion object {
        /**
         * Create a theme from a map (parsed from JSON).
         * Supports both nested structure (with "colors", "typography", etc. keys)
         * and flat structure where properties are at root level.
         */
        fun fromMap(map: Map<String, Any>): Theme {
            // Handle alias for backward compatibility
            val processedMap = mutableMapOf<String, Any>().apply {
                putAll(map)
                // Support "primaryColor" alias for "primary"
                if (map.containsKey("primaryColor") && !map.containsKey("primary")) {
                    put("primary", map["primaryColor"]!!)
                }
            }
            
            // Check if map has nested structure
            val hasNestedColors = processedMap.containsKey("colors")
            val hasNestedTypography = processedMap.containsKey("typography")
            val hasNestedSpacing = processedMap.containsKey("spacing")
            val hasNestedComponents = processedMap.containsKey("components")
            val hasNestedElevation = processedMap.containsKey("elevation")
            
            return Theme(
                name = (processedMap["name"] as? String) ?: "default",
                colors = if (hasNestedColors) {
                    ColorPalette.fromMap(processedMap["colors"] as? Map<String, Any>)
                } else {
                    // Pass the entire map, ColorPalette.fromMap will pick up color properties
                    ColorPalette.fromMap(processedMap)
                },
                typography = if (hasNestedTypography) {
                    Typography.fromMap(processedMap["typography"] as? Map<String, Any>)
                } else {
                    // Pass the entire map, Typography.fromMap will pick up typography properties
                    Typography.fromMap(processedMap)
                },
                spacing = if (hasNestedSpacing) {
                    Spacing.fromMap(processedMap["spacing"] as? Map<String, Any>)
                } else {
                    // Pass the entire map, Spacing.fromMap will pick up spacing properties
                    Spacing.fromMap(processedMap)
                },
                components = if (hasNestedComponents) {
                    ComponentStyles.fromMap(processedMap["components"] as? Map<String, Any>)
                } else {
                    // Pass the entire map, ComponentStyles.fromMap will pick up component properties
                    ComponentStyles.fromMap(processedMap)
                },
                elevation = if (hasNestedElevation) {
                    Elevation.fromMap(processedMap["elevation"] as? Map<String, Any>)
                } else {
                    // Pass the entire map, Elevation.fromMap will pick up elevation properties
                    Elevation.fromMap(processedMap)
                }
            )
        }
        
        /**
         * Default HSBC theme based on website analysis.
         */
        val HSBC = Theme(
            name = "hsbc",
            colors = ColorPalette.HSBC,
            typography = Typography.HSBC,
            spacing = Spacing.HSBC,
            components = ComponentStyles.HSBC,
            elevation = Elevation.HSBC
        )
        
        /**
         * Light theme (default Android).
         */
        val LIGHT = Theme(
            name = "light",
            colors = ColorPalette.LIGHT,
            typography = Typography.LIGHT,
            spacing = Spacing.DEFAULT,
            components = ComponentStyles.LIGHT,
            elevation = Elevation.DEFAULT
        )
        
        /**
         * Dark theme.
         */
        val DARK = Theme(
            name = "dark",
            colors = ColorPalette.DARK,
            typography = Typography.DARK,
            spacing = Spacing.DEFAULT,
            components = ComponentStyles.DARK,
            elevation = Elevation.DEFAULT
        )
    }
}

/**
 * Color palette for the theme.
 */
data class ColorPalette(
    @SerializedName("primary") val primary: String = "#2196F3",
    @SerializedName("primaryDark") val primaryDark: String = "#1976D2",
    @SerializedName("primaryLight") val primaryLight: String = "#BBDEFB",
    
    @SerializedName("secondary") val secondary: String = "#FF4081",
    @SerializedName("secondaryDark") val secondaryDark: String = "#F50057",
    @SerializedName("secondaryLight") val secondaryLight: String = "#FF80AB",
    
    @SerializedName("accent") val accent: String = "#FFC107",
    
    @SerializedName("background") val background: String = "#FFFFFF",
    @SerializedName("surface") val surface: String = "#FFFFFF",
    @SerializedName("error") val error: String = "#F44336",
    
    @SerializedName("onPrimary") val onPrimary: String = "#FFFFFF",
    @SerializedName("onSecondary") val onSecondary: String = "#FFFFFF",
    @SerializedName("onBackground") val onBackground: String = "#000000",
    @SerializedName("onSurface") val onSurface: String = "#000000",
    @SerializedName("onError") val onError: String = "#FFFFFF",
    
    @SerializedName("textPrimary") val textPrimary: String = "#000000",
    @SerializedName("textSecondary") val textSecondary: String = "#666666",
    @SerializedName("textDisabled") val textDisabled: String = "#9B9B9B",
    @SerializedName("textHint") val textHint: String = "#929292",
    
    @SerializedName("divider") val divider: String = "#E1E1E1",
    @SerializedName("border") val border: String = "#D2D2D2",
    
    @SerializedName("success") val success: String = "#4CAF50",
    @SerializedName("warning") val warning: String = "#FF9800",
    @SerializedName("info") val info: String = "#2196F3"
) {
    companion object {
        /**
         * Parse from map.
         */
        fun fromMap(map: Map<String, Any>?): ColorPalette {
            if (map == null) return ColorPalette()
            return ColorPalette(
                primary = (map["primary"] as? String) ?: "#2196F3",
                primaryDark = (map["primaryDark"] as? String) ?: "#1976D2",
                primaryLight = (map["primaryLight"] as? String) ?: "#BBDEFB",
                secondary = (map["secondary"] as? String) ?: "#FF4081",
                secondaryDark = (map["secondaryDark"] as? String) ?: "#F50057",
                secondaryLight = (map["secondaryLight"] as? String) ?: "#FF80AB",
                accent = (map["accent"] as? String) ?: "#FFC107",
                background = (map["background"] as? String) ?: "#FFFFFF",
                surface = (map["surface"] as? String) ?: "#FFFFFF",
                error = (map["error"] as? String) ?: "#F44336",
                onPrimary = (map["onPrimary"] as? String) ?: "#FFFFFF",
                onSecondary = (map["onSecondary"] as? String) ?: "#FFFFFF",
                onBackground = (map["onBackground"] as? String) ?: "#000000",
                onSurface = (map["onSurface"] as? String) ?: "#000000",
                onError = (map["onError"] as? String) ?: "#FFFFFF",
                textPrimary = (map["textPrimary"] as? String) ?: "#000000",
                textSecondary = (map["textSecondary"] as? String) ?: "#666666",
                textDisabled = (map["textDisabled"] as? String) ?: "#9B9B9B",
                textHint = (map["textHint"] as? String) ?: "#929292",
                divider = (map["divider"] as? String) ?: "#E1E1E1",
                border = (map["border"] as? String) ?: "#D2D2D2",
                success = (map["success"] as? String) ?: "#4CAF50",
                warning = (map["warning"] as? String) ?: "#FF9800",
                info = (map["info"] as? String) ?: "#2196F3"
            )
        }
        
        /**
         * HSBC color palette based on website analysis.
         */
        val HSBC = ColorPalette(
            primary = "#DA0011",        // HSBC Red
            primaryDark = "#A7000D",    // Dark Red (hover)
            primaryLight = "#BA1110",   // Medium Red
            
            secondary = "#1B6D85",      // HSBC Blue
            secondaryDark = "#245269",  // Dark Blue
            secondaryLight = "#5BC0DE", // Light Blue/Teal
            
            accent = "#269ABC",         // Accent Blue
            
            background = "#FFFFFF",     // White
            surface = "#F2F2F2",        // Light Gray
            error = "#A94442",          // Error Red
            
            onPrimary = "#FFFFFF",      // White text on red
            onSecondary = "#FFFFFF",    // White text on blue
            onBackground = "#000000",   // Black text on white
            onSurface = "#333333",      // Dark gray text on light gray
            onError = "#FFFFFF",        // White text on error
            
            textPrimary = "#000000",    // Black
            textSecondary = "#333333",  // Dark Gray
            textDisabled = "#9B9B9B",   // Medium Gray
            textHint = "#929292",       // Hint Gray
            
            divider = "#E1E1E1",        // Light Gray
            border = "#D2D2D2",         // Border Gray
            
            success = "#449D44",        // Success Green
            warning = "#FF9800",        // Warning Orange
            info = "#31B0D5"            // Info Blue
        )
        
        /**
         * Light theme palette (Material Design).
         */
        val LIGHT = ColorPalette()
        
        /**
         * Dark theme palette.
         */
        val DARK = ColorPalette(
            primary = "#BB86FC",
            primaryDark = "#3700B3",
            primaryLight = "#03DAC6",
            secondary = "#03DAC6",
            secondaryDark = "#018786",
            secondaryLight = "#03DAC6",
            accent = "#03DAC6",
            background = "#121212",
            surface = "#1E1E1E",
            error = "#CF6679",
            onPrimary = "#000000",
            onSecondary = "#000000",
            onBackground = "#FFFFFF",
            onSurface = "#FFFFFF",
            onError = "#000000",
            textPrimary = "#FFFFFF",
            textSecondary = "#B3B3B3",
            textDisabled = "#666666",
            textHint = "#808080",
            divider = "#2C2C2C",
            border = "#333333",
            success = "#4CAF50",
            warning = "#FF9800",
            info = "#2196F3"
        )
    }
    
    /**
     * Convert hex color string to Android Color int.
     */
    fun parseColor(colorString: String): Int {
        return try {
            Color.parseColor(colorString)
        } catch (e: Exception) {
            Color.BLACK
        }
    }
}

/**
 * Typography configuration.
 */
data class Typography(
    @SerializedName("fontFamily") val fontFamily: String = "sans-serif",
    @SerializedName("fontFamilyMedium") val fontFamilyMedium: String = "sans-serif-medium",
    @SerializedName("fontFamilyBold") val fontFamilyBold: String = "sans-serif",
    
    @SerializedName("h1Size") val h1Size: Float = 96f,  // sp
    @SerializedName("h2Size") val h2Size: Float = 60f,
    @SerializedName("h3Size") val h3Size: Float = 48f,
    @SerializedName("h4Size") val h4Size: Float = 34f,
    @SerializedName("h5Size") val h5Size: Float = 24f,
    @SerializedName("h6Size") val h6Size: Float = 20f,
    
    @SerializedName("subtitle1Size") val subtitle1Size: Float = 16f,
    @SerializedName("subtitle2Size") val subtitle2Size: Float = 14f,
    
    @SerializedName("body1Size") val body1Size: Float = 16f,
    @SerializedName("body2Size") val body2Size: Float = 14f,
    
    @SerializedName("buttonSize") val buttonSize: Float = 14f,
    @SerializedName("captionSize") val captionSize: Float = 12f,
    @SerializedName("overlineSize") val overlineSize: Float = 10f,
    
    @SerializedName("lineHeightMultiplier") val lineHeightMultiplier: Float = 1.42857f,
    
    @SerializedName("h1Weight") val h1Weight: Int = 300,  // 1-1000
    @SerializedName("h2Weight") val h2Weight: Int = 300,
    @SerializedName("h3Weight") val h3Weight: Int = 400,
    @SerializedName("h4Weight") val h4Weight: Int = 400,
    @SerializedName("h5Weight") val h5Weight: Int = 400,
    @SerializedName("h6Weight") val h6Weight: Int = 500,
    @SerializedName("subtitle1Weight") val subtitle1Weight: Int = 400,
    @SerializedName("subtitle2Weight") val subtitle2Weight: Int = 500,
    @SerializedName("body1Weight") val body1Weight: Int = 400,
    @SerializedName("body2Weight") val body2Weight: Int = 400,
    @SerializedName("buttonWeight") val buttonWeight: Int = 600,  // HSBC uses 600 for buttons
    @SerializedName("captionWeight") val captionWeight: Int = 400,
    @SerializedName("overlineWeight") val overlineWeight: Int = 400
) {
    companion object {
        /**
         * Parse from map.
         */
        fun fromMap(map: Map<String, Any>?): Typography {
            if (map == null) return Typography()
            return Typography(
                fontFamily = (map["fontFamily"] as? String) ?: "sans-serif",
                fontFamilyMedium = (map["fontFamilyMedium"] as? String) ?: "sans-serif-medium",
                fontFamilyBold = (map["fontFamilyBold"] as? String) ?: "sans-serif",
                h1Size = (map["h1Size"] as? Number)?.toFloat() ?: 96f,
                h2Size = (map["h2Size"] as? Number)?.toFloat() ?: 60f,
                h3Size = (map["h3Size"] as? Number)?.toFloat() ?: 48f,
                h4Size = (map["h4Size"] as? Number)?.toFloat() ?: 34f,
                h5Size = (map["h5Size"] as? Number)?.toFloat() ?: 24f,
                h6Size = (map["h6Size"] as? Number)?.toFloat() ?: 20f,
                subtitle1Size = (map["subtitle1Size"] as? Number)?.toFloat() ?: 16f,
                subtitle2Size = (map["subtitle2Size"] as? Number)?.toFloat() ?: 14f,
                body1Size = (map["body1Size"] as? Number)?.toFloat() ?: 16f,
                body2Size = (map["body2Size"] as? Number)?.toFloat() ?: 14f,
                buttonSize = (map["buttonSize"] as? Number)?.toFloat() ?: 14f,
                captionSize = (map["captionSize"] as? Number)?.toFloat() ?: 12f,
                overlineSize = (map["overlineSize"] as? Number)?.toFloat() ?: 10f,
                lineHeightMultiplier = (map["lineHeightMultiplier"] as? Number)?.toFloat() ?: 1.42857f,
                h1Weight = (map["h1Weight"] as? Number)?.toInt() ?: 300,
                h2Weight = (map["h2Weight"] as? Number)?.toInt() ?: 300,
                h3Weight = (map["h3Weight"] as? Number)?.toInt() ?: 400,
                h4Weight = (map["h4Weight"] as? Number)?.toInt() ?: 400,
                h5Weight = (map["h5Weight"] as? Number)?.toInt() ?: 400,
                h6Weight = (map["h6Weight"] as? Number)?.toInt() ?: 500,
                subtitle1Weight = (map["subtitle1Weight"] as? Number)?.toInt() ?: 400,
                subtitle2Weight = (map["subtitle2Weight"] as? Number)?.toInt() ?: 500,
                body1Weight = (map["body1Weight"] as? Number)?.toInt() ?: 400,
                body2Weight = (map["body2Weight"] as? Number)?.toInt() ?: 400,
                buttonWeight = (map["buttonWeight"] as? Number)?.toInt() ?: 600,
                captionWeight = (map["captionWeight"] as? Number)?.toInt() ?: 400,
                overlineWeight = (map["overlineWeight"] as? Number)?.toInt() ?: 400
            )
        }
        
        /**
         * HSBC typography based on website analysis.
         * HSBC uses rem units with base 10px = 1rem.
         * Converting to sp for Android (assuming 1rem ≈ 14sp).
         */
        val HSBC = Typography(
            fontFamily = "sans-serif",  // UniversNext equivalent
            fontFamilyMedium = "sans-serif-medium",
            fontFamilyBold = "sans-serif",
            
            // HSBC sizes: h1: 4.5rem (≈63sp), h2: 3.5rem (≈49sp), etc.
            // Scaling down for mobile: using Material defaults adjusted
            h1Size = 36f,   // 4.5rem → 36sp for mobile
            h2Size = 28f,   // 3.5rem → 28sp
            h3Size = 24f,   // 3.0rem → 24sp
            h4Size = 20f,   // 2.5rem → 20sp
            h5Size = 18f,   // 2.1rem → 18sp (HSBC body is 1.8rem)
            h6Size = 16f,   // 1.8rem → 16sp
            
            subtitle1Size = 16f,
            subtitle2Size = 14f,
            
            body1Size = 18f,  // HSBC base: 1.8rem
            body2Size = 16f,
            
            buttonSize = 18f,  // HSBC: 1.8rem for buttons
            captionSize = 14f, // 1.4rem
            overlineSize = 12f, // 1.2rem
            
            lineHeightMultiplier = 1.42857f, // HSBC line-height
            
            // Weights
            h1Weight = 300,
            h2Weight = 300,
            h3Weight = 400,
            h4Weight = 400,
            h5Weight = 400,
            h6Weight = 400,
            subtitle1Weight = 400,
            subtitle2Weight = 500,
            body1Weight = 400,
            body2Weight = 400,
            buttonWeight = 600,  // HSBC buttons use 600 weight
            captionWeight = 400,
            overlineWeight = 400
        )
        
        /**
         * Light theme typography.
         */
        val LIGHT = Typography()
        
        /**
         * Dark theme typography.
         */
        val DARK = Typography()
    }
}

/**
 * Spacing configuration.
 */
data class Spacing(
    @SerializedName("baseUnit") val baseUnit: Int = 4,  // dp
    @SerializedName("small") val small: Int = 4,        // 4dp
    @SerializedName("medium") val medium: Int = 8,      // 8dp
    @SerializedName("large") val large: Int = 16,       // 16dp
    @SerializedName("xlarge") val xlarge: Int = 24,     // 24dp
    @SerializedName("xxlarge") val xxlarge: Int = 32,   // 32dp
    
    @SerializedName("buttonPaddingVertical") val buttonPaddingVertical: Int = 10,   // dp (.6rem ≈ 10dp)
    @SerializedName("buttonPaddingHorizontal") val buttonPaddingHorizontal: Int = 20, // dp (1.2rem ≈ 20dp)
    
    @SerializedName("cardPadding") val cardPadding: Int = 16,     // dp
    @SerializedName("inputPadding") val inputPadding: Int = 12,   // dp
    @SerializedName("sectionPadding") val sectionPadding: Int = 24 // dp
) {
    companion object {
        /**
         * Parse from map.
         */
        fun fromMap(map: Map<String, Any>?): Spacing {
            if (map == null) return Spacing()
            return Spacing(
                baseUnit = (map["baseUnit"] as? Number)?.toInt() ?: 4,
                small = (map["small"] as? Number)?.toInt() ?: 4,
                medium = (map["medium"] as? Number)?.toInt() ?: 8,
                large = (map["large"] as? Number)?.toInt() ?: 16,
                xlarge = (map["xlarge"] as? Number)?.toInt() ?: 24,
                xxlarge = (map["xxlarge"] as? Number)?.toInt() ?: 32,
                buttonPaddingVertical = (map["buttonPaddingVertical"] as? Number)?.toInt() ?: 10,
                buttonPaddingHorizontal = (map["buttonPaddingHorizontal"] as? Number)?.toInt() ?: 20,
                cardPadding = (map["cardPadding"] as? Number)?.toInt() ?: 16,
                inputPadding = (map["inputPadding"] as? Number)?.toInt() ?: 12,
                sectionPadding = (map["sectionPadding"] as? Number)?.toInt() ?: 24
            )
        }
        
        /**
         * HSBC spacing based on website analysis.
         * HSBC uses rem units: .6rem ≈ 10dp, 1rem ≈ 16dp, 1.2rem ≈ 20dp
         */
        val HSBC = Spacing(
            baseUnit = 4,
            small = 4,    // .5rem ≈ 8dp, but using 4 for mobile
            medium = 8,   // .5rem ≈ 8dp
            large = 16,   // 1rem ≈ 16dp
            xlarge = 24,  // 1.5rem ≈ 24dp
            xxlarge = 32, // 2rem ≈ 32dp
            
            buttonPaddingVertical = 10,   // .6rem ≈ 10dp
            buttonPaddingHorizontal = 20, // 1.2rem ≈ 20dp
            
            cardPadding = 16,   // 1rem ≈ 16dp
            inputPadding = 12,  // .75rem ≈ 12dp
            sectionPadding = 24  // 1.5rem ≈ 24dp
        )
        
        /**
         * Default spacing.
         */
        val DEFAULT = Spacing()
    }
}

/**
 * Component-specific styling.
 */
data class ComponentStyles(
    @SerializedName("button") val button: ButtonStyle = ButtonStyle(),
    @SerializedName("textField") val textField: TextFieldStyle = TextFieldStyle(),
    @SerializedName("checkbox") val checkbox: CheckboxStyle = CheckboxStyle(),
    @SerializedName("choicePicker") val choicePicker: ChoicePickerStyle = ChoicePickerStyle(),
    @SerializedName("card") val card: CardStyle = CardStyle(),
    @SerializedName("appBar") val appBar: AppBarStyle = AppBarStyle(),
    @SerializedName("progressBar") val progressBar: ProgressBarStyle = ProgressBarStyle(),
    @SerializedName("slider") val slider: SliderStyle = SliderStyle(),
    @SerializedName("image") val image: ImageStyle = ImageStyle(),
    @SerializedName("icon") val icon: IconStyle = IconStyle(),
    @SerializedName("video") val video: VideoStyle = VideoStyle(),
    @SerializedName("accountCard") val accountCard: AccountCardStyle = AccountCardStyle(),
    @SerializedName("balanceDisplay") val balanceDisplay: BalanceDisplayStyle = BalanceDisplayStyle(),
    @SerializedName("actionButton") val actionButton: ActionButtonStyle = ActionButtonStyle(),
    @SerializedName("quickActionCircle") val quickActionCircle: QuickActionCircleStyle = QuickActionCircleStyle(),
    @SerializedName("navigationBar") val navigationBar: NavigationBarStyle = NavigationBarStyle(),
    @SerializedName("divider") val divider: DividerStyle = DividerStyle()
) {
    companion object {
        /**
         * Parse from map.
         */
        fun fromMap(map: Map<String, Any>?): ComponentStyles {
            if (map == null) return ComponentStyles()
            return ComponentStyles(
                button = ButtonStyle.fromMap(map["button"] as? Map<String, Any>),
                textField = TextFieldStyle.fromMap(map["textField"] as? Map<String, Any>),
                checkbox = CheckboxStyle.fromMap(map["checkbox"] as? Map<String, Any>),
                choicePicker = ChoicePickerStyle.fromMap(map["choicePicker"] as? Map<String, Any>),
                card = CardStyle.fromMap(map["card"] as? Map<String, Any>),
                appBar = AppBarStyle.fromMap(map["appBar"] as? Map<String, Any>),
                progressBar = ProgressBarStyle.fromMap(map["progressBar"] as? Map<String, Any>),
                slider = SliderStyle.fromMap(map["slider"] as? Map<String, Any>),
                image = ImageStyle.fromMap(map["image"] as? Map<String, Any>),
                icon = IconStyle.fromMap(map["icon"] as? Map<String, Any>),
                video = VideoStyle.fromMap(map["video"] as? Map<String, Any>),
                accountCard = AccountCardStyle.fromMap(map["accountCard"] as? Map<String, Any>),
                balanceDisplay = BalanceDisplayStyle.fromMap(map["balanceDisplay"] as? Map<String, Any>),
                actionButton = ActionButtonStyle.fromMap(map["actionButton"] as? Map<String, Any>),
                quickActionCircle = QuickActionCircleStyle.fromMap(map["quickActionCircle"] as? Map<String, Any>),
                navigationBar = NavigationBarStyle.fromMap(map["navigationBar"] as? Map<String, Any>),
                divider = DividerStyle.fromMap(map["divider"] as? Map<String, Any>)
            )
        }
        
        /**
         * HSBC component styles.
         */
        val HSBC = ComponentStyles(
            button = ButtonStyle.HSBC,
            textField = TextFieldStyle.HSBC,
            checkbox = CheckboxStyle.HSBC,
            choicePicker = ChoicePickerStyle.HSBC,
            card = CardStyle.HSBC,
            appBar = AppBarStyle.HSBC,
            progressBar = ProgressBarStyle.HSBC,
            slider = SliderStyle.HSBC,
            image = ImageStyle.HSBC,
            icon = IconStyle.HSBC,
            video = VideoStyle.HSBC,
            accountCard = AccountCardStyle.HSBC,
            balanceDisplay = BalanceDisplayStyle.HSBC,
            actionButton = ActionButtonStyle.HSBC,
            quickActionCircle = QuickActionCircleStyle.HSBC,
            navigationBar = NavigationBarStyle.HSBC,
            divider = DividerStyle.HSBC
        )
        
        /**
         * Light theme component styles.
         */
        val LIGHT = ComponentStyles()
        
        /**
         * Dark theme component styles.
         */
        val DARK = ComponentStyles(
            button = ButtonStyle.DARK,
            textField = TextFieldStyle.DARK,
            checkbox = CheckboxStyle.DARK,
            choicePicker = ChoicePickerStyle.DARK,
            card = CardStyle.DARK,
            appBar = AppBarStyle.DARK,
            progressBar = ProgressBarStyle.DARK,
            slider = SliderStyle.DARK,
            image = ImageStyle.DARK,
            icon = IconStyle.DARK,
            video = VideoStyle.DARK,
            accountCard = AccountCardStyle.DARK,
            balanceDisplay = BalanceDisplayStyle.DARK,
            actionButton = ActionButtonStyle.DARK,
            quickActionCircle = QuickActionCircleStyle.DARK,
            navigationBar = NavigationBarStyle.DARK,
            divider = DividerStyle.DARK
        )
    }
}

/**
 * Button styling.
 */
data class ButtonStyle(
    @SerializedName("cornerRadius") val cornerRadius: Int = 3,  // dp (HSBC uses 3px = 3dp)
    @SerializedName("elevation") val elevation: Int = 0,        // dp (HSBC buttons are flat)
    @SerializedName("minHeight") val minHeight: Int = 48,       // dp
    @SerializedName("minWidth") val minWidth: Int = 64,         // dp
    
    // Primary button
    @SerializedName("primaryBackground") val primaryBackground: String? = null, // null = use theme primary
    @SerializedName("primaryText") val primaryText: String? = null,
    @SerializedName("primaryBorder") val primaryBorder: String? = null,
    
    // Secondary button
    @SerializedName("secondaryBackground") val secondaryBackground: String = "transparent",
    @SerializedName("secondaryText") val secondaryText: String? = null,
    @SerializedName("secondaryBorder") val secondaryBorder: String? = null,
    
    // Variants
    @SerializedName("borderlessBackground") val borderlessBackground: String = "transparent",
    @SerializedName("borderlessText") val borderlessText: String? = null
) {
    companion object {
        /**
         * Parse from map.
         */
        fun fromMap(map: Map<String, Any>?): ButtonStyle {
            if (map == null) return ButtonStyle()
            return ButtonStyle(
                cornerRadius = (map["cornerRadius"] as? Number)?.toInt() ?: 3,
                elevation = (map["elevation"] as? Number)?.toInt() ?: 0,
                minHeight = (map["minHeight"] as? Number)?.toInt() ?: 48,
                minWidth = (map["minWidth"] as? Number)?.toInt() ?: 64,
                primaryBackground = map["primaryBackground"] as? String,
                primaryText = map["primaryText"] as? String,
                primaryBorder = map["primaryBorder"] as? String,
                secondaryBackground = (map["secondaryBackground"] as? String) ?: "transparent",
                secondaryText = map["secondaryText"] as? String,
                secondaryBorder = map["secondaryBorder"] as? String,
                borderlessBackground = (map["borderlessBackground"] as? String) ?: "transparent",
                borderlessText = map["borderlessText"] as? String
            )
        }
        
        /**
         * HSBC button style.
         */
        val HSBC = ButtonStyle(
            cornerRadius = 3,     // 3px border radius
            elevation = 0,        // Flat buttons
            minHeight = 48,       // Minimum touch target
            minWidth = 64,
            
            // Primary: red background, white text
            primaryBackground = "#DA0011",
            primaryText = "#FFFFFF",
            primaryBorder = "#DA0011",
            
            // Secondary: transparent background, black border/text
            secondaryBackground = "transparent",
            secondaryText = "#000000",
            secondaryBorder = "#000000",
            
            // Borderless: transparent, black text
            borderlessBackground = "transparent",
            borderlessText = "#000000"
        )
        
        /**
         * Dark theme button style.
         */
        val DARK = ButtonStyle(
            cornerRadius = 3,
            elevation = 0,
            minHeight = 48,
            minWidth = 64,
            secondaryBackground = "transparent",
            secondaryText = "#FFFFFF",
            secondaryBorder = "#FFFFFF",
            borderlessBackground = "transparent",
            borderlessText = "#FFFFFF"
        )
    }
}

/**
 * Text field styling.
 */
data class TextFieldStyle(
    @SerializedName("cornerRadius") val cornerRadius: Int = 4,      // dp
    @SerializedName("borderWidth") val borderWidth: Int = 1,        // dp
    @SerializedName("padding") val padding: Int = 12,               // dp
    @SerializedName("hintColor") val hintColor: String? = null,     // null = use theme textHint
    @SerializedName("textColor") val textColor: String? = null,     // null = use theme textPrimary
    @SerializedName("backgroundColor") val backgroundColor: String? = null, // null = use theme surface
    @SerializedName("borderColor") val borderColor: String? = null, // null = use theme border
    @SerializedName("focusColor") val focusColor: String? = null,   // null = use theme primary
    @SerializedName("errorColor") val errorColor: String? = null    // null = use theme error
) {
    companion object {
        /**
         * Parse from map.
         */
        fun fromMap(map: Map<String, Any>?): TextFieldStyle {
            if (map == null) return TextFieldStyle()
            return TextFieldStyle(
                cornerRadius = (map["cornerRadius"] as? Number)?.toInt() ?: 4,
                borderWidth = (map["borderWidth"] as? Number)?.toInt() ?: 1,
                padding = (map["padding"] as? Number)?.toInt() ?: 12,
                hintColor = map["hintColor"] as? String,
                textColor = map["textColor"] as? String,
                backgroundColor = map["backgroundColor"] as? String,
                borderColor = map["borderColor"] as? String,
                focusColor = map["focusColor"] as? String,
                errorColor = map["errorColor"] as? String
            )
        }
        
        /**
         * HSBC text field style.
         */
        val HSBC = TextFieldStyle(
            cornerRadius = 0,          // Sharp corners (HSBC style)
            borderWidth = 1,
            padding = 12,
            hintColor = "#929292",     // HSBC textHint
            textColor = "#000000",     // HSBC textPrimary
            backgroundColor = "#FFFFFF", // White
            borderColor = "#D2D2D2",   // HSBC border
            focusColor = "#DA0011",    // HSBC primary red
            errorColor = "#A94442"     // HSBC error
        )
        
        /**
         * Dark theme text field style.
         */
        val DARK = TextFieldStyle(
            cornerRadius = 4,
            borderWidth = 1,
            padding = 12,
            hintColor = "#808080",
            textColor = "#FFFFFF",
            backgroundColor = "#1E1E1E",
            borderColor = "#333333",
            focusColor = "#BB86FC",
            errorColor = "#CF6679"
        )
    }
}

/**
 * Checkbox styling.
 */
data class CheckboxStyle(
    @SerializedName("checkedColor") val checkedColor: String? = null,     // null = use theme primary
    @SerializedName("uncheckedColor") val uncheckedColor: String? = null, // null = use theme border
    @SerializedName("checkColor") val checkColor: String? = null,         // null = use theme onPrimary
    @SerializedName("textColor") val textColor: String? = null            // null = use theme textPrimary
) {
    companion object {
        /**
         * Parse from map.
         */
        fun fromMap(map: Map<String, Any>?): CheckboxStyle {
            if (map == null) return CheckboxStyle()
            return CheckboxStyle(
                checkedColor = map["checkedColor"] as? String,
                uncheckedColor = map["uncheckedColor"] as? String,
                checkColor = map["checkColor"] as? String,
                textColor = map["textColor"] as? String
            )
        }
        
        /**
         * HSBC checkbox style.
         */
        val HSBC = CheckboxStyle(
            checkedColor = "#DA0011",   // HSBC primary red
            uncheckedColor = "#D2D2D2", // HSBC border
            checkColor = "#FFFFFF",     // White check
            textColor = "#000000"       // Black text
        )
        
        /**
         * Dark theme checkbox style.
         */
        val DARK = CheckboxStyle(
            checkedColor = "#BB86FC",
            uncheckedColor = "#333333",
            checkColor = "#000000",
            textColor = "#FFFFFF"
        )
    }
}

/**
 * Choice picker styling.
 */
data class ChoicePickerStyle(
    @SerializedName("cornerRadius") val cornerRadius: Int = 4,          // dp
    @SerializedName("borderWidth") val borderWidth: Int = 1,            // dp
    @SerializedName("borderColor") val borderColor: String? = null,     // null = use theme border
    @SerializedName("backgroundColor") val backgroundColor: String? = null, // null = use theme surface
    @SerializedName("textColor") val textColor: String? = null,         // null = use theme textPrimary
    @SerializedName("selectedColor") val selectedColor: String? = null, // null = use theme primary
    @SerializedName("selectedTextColor") val selectedTextColor: String? = null // null = use theme onPrimary
) {
    companion object {
        /**
         * Parse from map.
         */
        fun fromMap(map: Map<String, Any>?): ChoicePickerStyle {
            if (map == null) return ChoicePickerStyle()
            return ChoicePickerStyle(
                cornerRadius = (map["cornerRadius"] as? Number)?.toInt() ?: 4,
                borderWidth = (map["borderWidth"] as? Number)?.toInt() ?: 1,
                borderColor = map["borderColor"] as? String,
                backgroundColor = map["backgroundColor"] as? String,
                textColor = map["textColor"] as? String,
                selectedColor = map["selectedColor"] as? String,
                selectedTextColor = map["selectedTextColor"] as? String
            )
        }
        
        /**
         * HSBC choice picker style.
         */
        val HSBC = ChoicePickerStyle(
            cornerRadius = 0,          // Sharp corners
            borderWidth = 1,
            borderColor = "#D2D2D2",   // HSBC border
            backgroundColor = "#FFFFFF", // White
            textColor = "#000000",     // Black text
            selectedColor = "#DA0011", // HSBC primary red
            selectedTextColor = "#FFFFFF" // White text on selection
        )
        
        /**
         * Dark theme choice picker style.
         */
        val DARK = ChoicePickerStyle(
            cornerRadius = 4,
            borderWidth = 1,
            borderColor = "#333333",
            backgroundColor = "#1E1E1E",
            textColor = "#FFFFFF",
            selectedColor = "#BB86FC",
            selectedTextColor = "#000000"
        )
    }
}

/**
 * Card styling.
 */
data class CardStyle(
    @SerializedName("cornerRadius") val cornerRadius: Int = 0,          // dp (HSBC uses sharp corners)
    @SerializedName("elevation") val elevation: Int = 0,                // dp (HSBC cards are flat)
    @SerializedName("backgroundColor") val backgroundColor: String? = null, // null = use theme surface
    @SerializedName("padding") val padding: Int = 16                    // dp
) {
    companion object {
        /**
         * Parse from map.
         */
        fun fromMap(map: Map<String, Any>?): CardStyle {
            if (map == null) return CardStyle()
            return CardStyle(
                cornerRadius = (map["cornerRadius"] as? Number)?.toInt() ?: 0,
                elevation = (map["elevation"] as? Number)?.toInt() ?: 0,
                backgroundColor = map["backgroundColor"] as? String,
                padding = (map["padding"] as? Number)?.toInt() ?: 16
            )
        }
        
        /**
         * HSBC card style.
         */
        val HSBC = CardStyle(
            cornerRadius = 0,          // Sharp corners
            elevation = 0,             // Flat design
            backgroundColor = "#FFFFFF", // White
            padding = 16               // 1rem ≈ 16dp
        )
        
        /**
         * Dark theme card style.
         */
        val DARK = CardStyle(
            cornerRadius = 4,
            elevation = 2,
            backgroundColor = "#1E1E1E",
            padding = 16
        )
    }
}

/**
 * App bar styling.
 */
data class AppBarStyle(
    @SerializedName("backgroundColor") val backgroundColor: String? = null, // null = use theme primary
    @SerializedName("textColor") val textColor: String? = null,            // null = use theme onPrimary
    @SerializedName("elevation") val elevation: Int = 0,                   // dp
    @SerializedName("height") val height: Int = 56                         // dp
) {
    companion object {
        /**
         * Parse from map.
         */
        fun fromMap(map: Map<String, Any>?): AppBarStyle {
            if (map == null) return AppBarStyle()
            return AppBarStyle(
                backgroundColor = map["backgroundColor"] as? String,
                textColor = map["textColor"] as? String,
                elevation = (map["elevation"] as? Number)?.toInt() ?: 0,
                height = (map["height"] as? Number)?.toInt() ?: 56
            )
        }
        
        /**
         * HSBC app bar style.
         */
        val HSBC = AppBarStyle(
            backgroundColor = "#FFFFFF", // White (HSBC header is white with red logo)
            textColor = "#000000",       // Black text
            elevation = 0,               // Flat
            height = 56
        )
        
        /**
         * Dark theme app bar style.
         */
        val DARK = AppBarStyle(
            backgroundColor = "#1E1E1E",
            textColor = "#FFFFFF",
            elevation = 4,
            height = 56
        )
    }
}

/**
 * Progress bar styling.
 */
data class ProgressBarStyle(
    @SerializedName("trackColor") val trackColor: String? = null,         // null = use theme surface
    @SerializedName("progressColor") val progressColor: String? = null,   // null = use theme primary
    @SerializedName("indeterminateColor") val indeterminateColor: String? = null, // null = use theme primary
    @SerializedName("cornerRadius") val cornerRadius: Int = 0,            // dp (HSBC uses sharp corners)
    @SerializedName("height") val height: Int = 4                         // dp (thickness)
) {
    companion object {
        /**
         * Parse from map.
         */
        fun fromMap(map: Map<String, Any>?): ProgressBarStyle {
            if (map == null) return ProgressBarStyle()
            return ProgressBarStyle(
                trackColor = map["trackColor"] as? String,
                progressColor = map["progressColor"] as? String,
                indeterminateColor = map["indeterminateColor"] as? String,
                cornerRadius = (map["cornerRadius"] as? Number)?.toInt() ?: 0,
                height = (map["height"] as? Number)?.toInt() ?: 4
            )
        }
        
        /**
         * HSBC progress bar style.
         */
        val HSBC = ProgressBarStyle(
            trackColor = "#F2F2F2",        // HSBC surface light gray
            progressColor = "#DA0011",     // HSBC primary red
            indeterminateColor = "#DA0011",
            cornerRadius = 0,              // Sharp corners
            height = 4
        )
        
        /**
         * Dark theme progress bar style.
         */
        val DARK = ProgressBarStyle(
            trackColor = "#2C2C2C",
            progressColor = "#BB86FC",
            indeterminateColor = "#BB86FC",
            cornerRadius = 2,
            height = 4
        )
    }
}

/**
 * Slider styling.
 */
data class SliderStyle(
    @SerializedName("trackColor") val trackColor: String? = null,         // null = use theme surface
    @SerializedName("progressColor") val progressColor: String? = null,   // null = use theme primary
    @SerializedName("thumbColor") val thumbColor: String? = null,         // null = use theme primary
    @SerializedName("cornerRadius") val cornerRadius: Int = 0,            // dp
    @SerializedName("trackHeight") val trackHeight: Int = 4,              // dp
    @SerializedName("thumbRadius") val thumbRadius: Int = 8               // dp
) {
    companion object {
        /**
         * Parse from map.
         */
        fun fromMap(map: Map<String, Any>?): SliderStyle {
            if (map == null) return SliderStyle()
            return SliderStyle(
                trackColor = map["trackColor"] as? String,
                progressColor = map["progressColor"] as? String,
                thumbColor = map["thumbColor"] as? String,
                cornerRadius = (map["cornerRadius"] as? Number)?.toInt() ?: 0,
                trackHeight = (map["trackHeight"] as? Number)?.toInt() ?: 4,
                thumbRadius = (map["thumbRadius"] as? Number)?.toInt() ?: 8
            )
        }
        
        /**
         * HSBC slider style.
         */
        val HSBC = SliderStyle(
            trackColor = "#F2F2F2",        // HSBC surface light gray
            progressColor = "#DA0011",     // HSBC primary red
            thumbColor = "#DA0011",        // HSBC primary red
            cornerRadius = 0,              // Sharp corners
            trackHeight = 4,
            thumbRadius = 8
        )
        
        /**
         * Dark theme slider style.
         */
        val DARK = SliderStyle(
            trackColor = "#2C2C2C",
            progressColor = "#BB86FC",
            thumbColor = "#BB86FC",
            cornerRadius = 2,
            trackHeight = 4,
            thumbRadius = 8
        )
    }
}

/**
 * Image styling.
 */
data class ImageStyle(
    @SerializedName("cornerRadius") val cornerRadius: Int = 0,            // dp (HSBC uses sharp corners)
    @SerializedName("backgroundColor") val backgroundColor: String? = null, // null = transparent
    @SerializedName("tintColor") val tintColor: String? = null            // null = no tint
) {
    companion object {
        /**
         * Parse from map.
         */
        fun fromMap(map: Map<String, Any>?): ImageStyle {
            if (map == null) return ImageStyle()
            return ImageStyle(
                cornerRadius = (map["cornerRadius"] as? Number)?.toInt() ?: 0,
                backgroundColor = map["backgroundColor"] as? String,
                tintColor = map["tintColor"] as? String
            )
        }
        
        /**
         * HSBC image style.
         */
        val HSBC = ImageStyle(
            cornerRadius = 0,              // Sharp corners
            backgroundColor = null,        // Transparent
            tintColor = null               // No tint
        )
        
        /**
         * Dark theme image style.
         */
        val DARK = ImageStyle(
            cornerRadius = 0,
            backgroundColor = null,
            tintColor = null
        )
    }
}

/**
 * Icon styling.
 */
data class IconStyle(
    @SerializedName("tintColor") val tintColor: String? = null,           // null = use theme textPrimary
    @SerializedName("size") val size: Int? = null                         // null = use component size
) {
    companion object {
        /**
         * Parse from map.
         */
        fun fromMap(map: Map<String, Any>?): IconStyle {
            if (map == null) return IconStyle()
            return IconStyle(
                tintColor = map["tintColor"] as? String,
                size = (map["size"] as? Number)?.toInt()
            )
        }
        
        /**
         * HSBC icon style.
         */
        val HSBC = IconStyle(
            tintColor = "#000000",         // HSBC textPrimary black
            size = null                    // Use component size
        )
        
        /**
         * Dark theme icon style.
         */
        val DARK = IconStyle(
            tintColor = "#FFFFFF",
            size = null
        )
    }
}

/**
 * Video styling.
 */
data class VideoStyle(
    @SerializedName("cornerRadius") val cornerRadius: Int = 0,            // dp (HSBC uses sharp corners)
    @SerializedName("backgroundColor") val backgroundColor: String? = null, // null = use theme surface
    @SerializedName("controlsColor") val controlsColor: String? = null    // null = default controls
) {
    companion object {
        /**
         * Parse from map.
         */
        fun fromMap(map: Map<String, Any>?): VideoStyle {
            if (map == null) return VideoStyle()
            return VideoStyle(
                cornerRadius = (map["cornerRadius"] as? Number)?.toInt() ?: 0,
                backgroundColor = map["backgroundColor"] as? String,
                controlsColor = map["controlsColor"] as? String
            )
        }
        
        /**
         * HSBC video style.
         */
        val HSBC = VideoStyle(
            cornerRadius = 0,              // Sharp corners
            backgroundColor = "#F2F2F2",   // HSBC surface light gray
            controlsColor = null           // Default controls
        )
        
        /**
         * Dark theme video style.
         */
        val DARK = VideoStyle(
            cornerRadius = 0,
            backgroundColor = "#1E1E1E",
            controlsColor = null
        )
    }
}

/**
 * Account card styling.
 */
data class AccountCardStyle(
    @SerializedName("cornerRadius") val cornerRadius: Int = 0,
    @SerializedName("elevation") val elevation: Int = 0,
    @SerializedName("backgroundColor") val backgroundColor: String? = null,
    @SerializedName("padding") val padding: Int = 16,
    @SerializedName("borderWidth") val borderWidth: Int = 1,
    @SerializedName("borderColor") val borderColor: String? = null,
    @SerializedName("selectedBorderColor") val selectedBorderColor: String? = null,
    @SerializedName("selectedBackgroundColor") val selectedBackgroundColor: String? = null
) {
    companion object {
        fun fromMap(map: Map<String, Any>?): AccountCardStyle {
            if (map == null) return AccountCardStyle()
            return AccountCardStyle(
                cornerRadius = (map["cornerRadius"] as? Number)?.toInt() ?: 0,
                elevation = (map["elevation"] as? Number)?.toInt() ?: 0,
                backgroundColor = map["backgroundColor"] as? String,
                padding = (map["padding"] as? Number)?.toInt() ?: 16,
                borderWidth = (map["borderWidth"] as? Number)?.toInt() ?: 1,
                borderColor = map["borderColor"] as? String,
                selectedBorderColor = map["selectedBorderColor"] as? String,
                selectedBackgroundColor = map["selectedBackgroundColor"] as? String
            )
        }
        
        val HSBC = AccountCardStyle(
            cornerRadius = 0,
            elevation = 0,
            backgroundColor = "#FFFFFF",
            padding = 16,
            borderWidth = 1,
            borderColor = "#E1E1E1",
            selectedBorderColor = "#DA0011",
            selectedBackgroundColor = "#FFF5F5"
        )
        
        val DARK = AccountCardStyle(
            cornerRadius = 0,
            elevation = 0,
            backgroundColor = "#1E1E1E",
            padding = 16,
            borderWidth = 1,
            borderColor = "#333333",
            selectedBorderColor = "#BB86FC",
            selectedBackgroundColor = "#2D2D2D"
        )
    }
}

/**
 * Balance display styling.
 */
data class BalanceDisplayStyle(
    @SerializedName("textColor") val textColor: String? = null,
    @SerializedName("currencySize") val currencySize: Int = 14,
    @SerializedName("amountSize") val amountSize: Int = 28,
    @SerializedName("positiveColor") val positiveColor: String? = null,
    @SerializedName("negativeColor") val negativeColor: String? = null
) {
    companion object {
        fun fromMap(map: Map<String, Any>?): BalanceDisplayStyle {
            if (map == null) return BalanceDisplayStyle()
            return BalanceDisplayStyle(
                textColor = map["textColor"] as? String,
                currencySize = (map["currencySize"] as? Number)?.toInt() ?: 14,
                amountSize = (map["amountSize"] as? Number)?.toInt() ?: 28,
                positiveColor = map["positiveColor"] as? String,
                negativeColor = map["negativeColor"] as? String
            )
        }
        
        val HSBC = BalanceDisplayStyle(
            textColor = "#000000",
            currencySize = 14,
            amountSize = 28,
            positiveColor = "#449D44",
            negativeColor = "#A94442"
        )
        
        val DARK = BalanceDisplayStyle(
            textColor = "#FFFFFF",
            currencySize = 14,
            amountSize = 28,
            positiveColor = "#4CAF50",
            negativeColor = "#CF6679"
        )
    }
}

/**
 * Action button styling.
 */
data class ActionButtonStyle(
    @SerializedName("cornerRadius") val cornerRadius: Int = 0,
    @SerializedName("size") val size: Int = 72,
    @SerializedName("iconSize") val iconSize: Int = 32,
    @SerializedName("labelSize") val labelSize: Int = 12,
    @SerializedName("backgroundColor") val backgroundColor: String? = null,
    @SerializedName("iconColor") val iconColor: String? = null,
    @SerializedName("labelColor") val labelColor: String? = null,
    @SerializedName("elevation") val elevation: Int = 0
) {
    companion object {
        fun fromMap(map: Map<String, Any>?): ActionButtonStyle {
            if (map == null) return ActionButtonStyle()
            return ActionButtonStyle(
                cornerRadius = (map["cornerRadius"] as? Number)?.toInt() ?: 0,
                size = (map["size"] as? Number)?.toInt() ?: 72,
                iconSize = (map["iconSize"] as? Number)?.toInt() ?: 32,
                labelSize = (map["labelSize"] as? Number)?.toInt() ?: 12,
                backgroundColor = map["backgroundColor"] as? String,
                iconColor = map["iconColor"] as? String,
                labelColor = map["labelColor"] as? String,
                elevation = (map["elevation"] as? Number)?.toInt() ?: 0
            )
        }
        
        val HSBC = ActionButtonStyle(
            cornerRadius = 0,
            size = 72,
            iconSize = 32,
            labelSize = 12,
            backgroundColor = "#FFFFFF",
            iconColor = "#DA0011",
            labelColor = "#333333",
            elevation = 0
        )
        
        val DARK = ActionButtonStyle(
            cornerRadius = 0,
            size = 72,
            iconSize = 32,
            labelSize = 12,
            backgroundColor = "#2D2D2D",
            iconColor = "#BB86FC",
            labelColor = "#FFFFFF",
            elevation = 0
        )
    }
}

/**
 * Quick action circle button styling.
 */
data class QuickActionCircleStyle(
    @SerializedName("cornerRadius") val cornerRadius: Int = 36,
    @SerializedName("size") val size: Int = 72,
    @SerializedName("iconSize") val iconSize: Int = 32,
    @SerializedName("backgroundColor") val backgroundColor: String? = null,
    @SerializedName("iconColor") val iconColor: String? = null,
    @SerializedName("labelColor") val labelColor: String? = null,
    @SerializedName("elevation") val elevation: Int = 2
) {
    companion object {
        fun fromMap(map: Map<String, Any>?): QuickActionCircleStyle {
            if (map == null) return QuickActionCircleStyle()
            return QuickActionCircleStyle(
                cornerRadius = (map["cornerRadius"] as? Number)?.toInt() ?: 36,
                size = (map["size"] as? Number)?.toInt() ?: 72,
                iconSize = (map["iconSize"] as? Number)?.toInt() ?: 32,
                backgroundColor = map["backgroundColor"] as? String,
                iconColor = map["iconColor"] as? String,
                labelColor = map["labelColor"] as? String,
                elevation = (map["elevation"] as? Number)?.toInt() ?: 2
            )
        }
        
        val HSBC = QuickActionCircleStyle(
            cornerRadius = 36,
            size = 72,
            iconSize = 32,
            backgroundColor = "#FFFFFF",
            iconColor = "#DA0011",
            labelColor = "#333333",
            elevation = 2
        )
        
        val DARK = QuickActionCircleStyle(
            cornerRadius = 36,
            size = 72,
            iconSize = 32,
            backgroundColor = "#2D2D2D",
            iconColor = "#BB86FC",
            labelColor = "#FFFFFF",
            elevation = 2
        )
    }
}

/**
 * Navigation bar styling.
 */
data class NavigationBarStyle(
    @SerializedName("backgroundColor") val backgroundColor: String? = null,
    @SerializedName("iconColor") val iconColor: String? = null,
    @SerializedName("selectedIconColor") val selectedIconColor: String? = null,
    @SerializedName("selectedTextColor") val selectedTextColor: String? = null,
    @SerializedName("unselectedTextColor") val unselectedTextColor: String? = null,
    @SerializedName("height") val height: Int = 56,
    @SerializedName("elevation") val elevation: Int = 8
) {
    companion object {
        fun fromMap(map: Map<String, Any>?): NavigationBarStyle {
            if (map == null) return NavigationBarStyle()
            return NavigationBarStyle(
                backgroundColor = map["backgroundColor"] as? String,
                iconColor = map["iconColor"] as? String,
                selectedIconColor = map["selectedIconColor"] as? String,
                selectedTextColor = map["selectedTextColor"] as? String,
                unselectedTextColor = map["unselectedTextColor"] as? String,
                height = (map["height"] as? Number)?.toInt() ?: 56,
                elevation = (map["elevation"] as? Number)?.toInt() ?: 8
            )
        }
        
        val HSBC = NavigationBarStyle(
            backgroundColor = "#FFFFFF",
            iconColor = "#333333",
            selectedIconColor = "#DA0011",
            selectedTextColor = "#DA0011",
            unselectedTextColor = "#666666",
            height = 56,
            elevation = 8
        )
        
        val DARK = NavigationBarStyle(
            backgroundColor = "#1E1E1E",
            iconColor = "#B3B3B3",
            selectedIconColor = "#BB86FC",
            selectedTextColor = "#BB86FC",
            unselectedTextColor = "#999999",
            height = 56,
            elevation = 8
        )
    }
}

/**
 * Divider styling.
 */
data class DividerStyle(
    @SerializedName("color") val color: String? = null,
    @SerializedName("thickness") val thickness: Int = 1,
    @SerializedName("inset") val inset: Int = 0
) {
    companion object {
        fun fromMap(map: Map<String, Any>?): DividerStyle {
            if (map == null) return DividerStyle()
            return DividerStyle(
                color = map["color"] as? String,
                thickness = (map["thickness"] as? Number)?.toInt() ?: 1,
                inset = (map["inset"] as? Number)?.toInt() ?: 0
            )
        }
        
        val HSBC = DividerStyle(
            color = "#E1E1E1",
            thickness = 1,
            inset = 0
        )
        
        val DARK = DividerStyle(
            color = "#2C2C2C",
            thickness = 1,
            inset = 0
        )
    }
}

/**
 * Elevation and shadow configuration.
 */
data class Elevation(
    @SerializedName("level0") val level0: Int = 0,    // dp
    @SerializedName("level1") val level1: Int = 2,    // dp
    @SerializedName("level2") val level2: Int = 4,    // dp
    @SerializedName("level3") val level3: Int = 6,    // dp
    @SerializedName("level4") val level4: Int = 8,    // dp
    @SerializedName("level5") val level5: Int = 10    // dp
) {
    companion object {
        /**
         * Parse from map.
         */
        fun fromMap(map: Map<String, Any>?): Elevation {
            if (map == null) return Elevation()
            return Elevation(
                level0 = (map["level0"] as? Number)?.toInt() ?: 0,
                level1 = (map["level1"] as? Number)?.toInt() ?: 2,
                level2 = (map["level2"] as? Number)?.toInt() ?: 4,
                level3 = (map["level3"] as? Number)?.toInt() ?: 6,
                level4 = (map["level4"] as? Number)?.toInt() ?: 8,
                level5 = (map["level5"] as? Number)?.toInt() ?: 10
            )
        }
        
        /**
         * HSBC elevation (mostly flat design).
         */
        val HSBC = Elevation(
            level0 = 0,
            level1 = 0,  // HSBC uses minimal shadows
            level2 = 2,
            level3 = 4,
            level4 = 6,
            level5 = 8
        )
        
        /**
         * Default elevation.
         */
        val DEFAULT = Elevation()
    }
}