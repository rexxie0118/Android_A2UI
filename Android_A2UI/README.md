# Android UI Renderer

A dynamic UI rendering engine for Android that builds user interfaces from JSONL configuration files.

## Features

- **Widget Registry System**: Extensible registry mapping widget types to Android View classes
- **JSONL Configuration**: Define pages, widgets, layout, and navigation in JSONL format
- **Dynamic Rendering**: Render UI at runtime from configuration
- **Step-by-Step Navigation**: Multi-page flows with back/next navigation
- **Built-in Widgets**: Button, Text, Dropdown, Radio, Checkbox, Image, List, and layout containers

## Architecture

### Core Components

1. **WidgetRegistry** - Central registry for widget type registration
2. **ConfigurationParser** - Parses JSONL configuration into PageConfig objects
3. **UIRenderer** - Renders widgets with layout parameters and attributes
4. **NavigationManager** - Manages page transitions and action routing
5. **Widgets** - Default widget implementations

### Configuration Format (JSONL)

Each line is a JSON object representing a page:

```json
{
  "id": "page1",
  "title": "Welcome",
  "widgets": [
    {
      "type": "text",
      "text": "Welcome to UI Renderer",
      "layout": {
        "width": "match_parent",
        "height": "wrap_content",
        "gravity": "center",
        "margin": {"top": 50}
      }
    },
    {
      "type": "button",
      "text": "Next",
      "id": "next_button",
      "action": "next",
      "layout": {
        "width": "200dp",
        "height": "wrap_content",
        "gravity": "center",
        "margin": {"top": 30}
      }
    }
  ],
  "nextPageId": "page2"
}
```

## Building the App

### Prerequisites

- Android SDK (API 34)
- Gradle 8.5+
- Java 11+

### Build Instructions

#### Option 1: Using Android Studio

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Build the project: `Build > Make Project`
4. Run on emulator or device: `Run > Run 'app'`

#### Option 2: Command Line

```bash
# Navigate to project directory
cd /Users/rex/android-ui-renderer

# Generate Gradle wrapper (if not present)
gradle wrapper --gradle-version 8.5 --distribution-type bin

# Build debug APK
./gradlew assembleDebug

# Install and run on connected device
./gradlew installDebug
```

### Troubleshooting

1. **Gradle sync errors**: Ensure Android SDK is properly installed
2. **Plugin compatibility**: Check Android Gradle Plugin and Gradle version compatibility
3. **Missing dependencies**: Internet connection required for first build

## Sample Configuration

The app includes a sample configuration in `app/src/main/assets/config.jsonl` with three pages:

1. **Welcome Page** - Introduction with Next button
2. **Form Page** - Input fields (text, dropdown, checkbox) with navigation buttons
3. **Summary Page** - List view with Finish button

## Extending the System

### Adding New Widget Types

1. Create a widget factory function:
```kotlin
registry.register("custom") { config, parent ->
    CustomView(context).apply {
        id = View.generateViewId()
        config.id?.let { tag = it }
    }
}
```

2. Register in `Widgets.kt`:
```kotlin
fun registerCustomWidgets(context: Context) {
    WidgetRegistry.getInstance().register("custom", customFactory)
}
```

### Custom Actions

Add action handlers in `MainActivity.handleAction()`:
```kotlin
when (action) {
    "custom_action" -> performCustomAction(widget)
    // ...
}
```

## Project Structure

```
android-ui-renderer/
├── app/
│   ├── src/main/java/com/example/androiduirenderer/
│   │   ├── MainActivity.kt              # Main entry point
│   │   ├── core/                        # Core rendering engine
│   │   │   ├── WidgetRegistry.kt        # Widget type registry
│   │   │   ├── ConfigurationParser.kt   # JSONL parser
│   │   │   ├── UIRenderer.kt           # UI renderer
│   │   │   ├── NavigationManager.kt    # Page navigation
│   │   │   └── Widgets.kt              # Default widget implementations
│   │   └── model/                       # Data models
│   │       └── Configuration.kt         # Configuration data classes
│   ├── src/main/assets/
│   │   └── config.jsonl                 # Sample configuration
│   └── build.gradle.kts                 # App build configuration
├── build.gradle.kts                     # Project build configuration
└── settings.gradle.kts                  # Project settings
```

## License

MIT License