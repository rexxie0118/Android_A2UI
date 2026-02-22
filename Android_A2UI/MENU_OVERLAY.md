# HSBC Banking App - Menu Overlay System

## Overview
Implemented a slide-out menu overlay that appears when tapping the hamburger/close icon in the top-right corner of the app.

## Menu Structure (from ham_combined.jpg)

### Header Section
```
┌─────────────────────────────┐
│ [×] 总览                    │
├─────────────────────────────┤
│ [💼] 产品 →                │
│      探索各式各样的产品。   │
└─────────────────────────────┘
```

### Quick Actions Grid
- 个人预算 (Budget)
- 保险 (Insurance)
- 安全和防诈骗 (Security)
- 转账 (Transfer)
- 银行卡 (Bank Cards)
- 财富 (Wealth)

### Rewards Section (您的奖赏)
- Well+ (Wellness Trophy)
- 您的任务 (Tasks)

### Security Section (保障您的账户安全)
- 管理设备 (Device Management)
- 管理安全设置 (Security Settings)
- 使用记录 (Usage History)
- 防诈骗和保安提示 (Anti-Fraud)

### Travel Section (准备外游)
- 外汇 (FX Currency)
- 旅游保险 (Travel Insurance)

### Planning Section (规划您的未来)
- Future Planner
- 保险产品 (Insurance Products)
- 强积金 (MPF)

### Loan Section (您的借贷选项)
- 银行卡和分期计划 (Card Loan)
- 贷款 (Personal Loan)

### Help Section (随时提供帮助)
- 常见问题 (FAQ)
- 联系我们 (Contact)
- 信息 (Info)
- 线上对话 (Online Chat)

### Footer
- Version: 3.66.30 (123845)
- 退出登录 (Logout)

## New UI Components

### 1. Overlay Component
```kotlin
data class Overlay(
    val id: String,
    val children: ChildList,
    val visible: DynamicValue?,
    val closeOnBackdrop: Boolean?,
    val onClose: Action?
)
```

**Features:**
- Dark backdrop (50% transparent black)
- Slide-in from left (85% screen width)
- Close on backdrop tap (configurable)
- Visibility controlled by data binding

**Implementation:**
```xml
<FrameLayout>
  <View          <!-- Backdrop -->
  <LinearLayout  <!-- Menu content -->
</FrameLayout>
```

### 2. MenuItem Component
```kotlin
data class MenuItem(
    val id: String,
    val icon: String,
    val label: DynamicValue,
    val action: Action?,
    val badge: DynamicValue?
)
```

**Features:**
- Icon (40x40dp) + Label layout
- Optional badge for notifications
- Click handling with actions
- Horizontal layout with padding

### 3. MenuSection Component
```kotlin
data class MenuSection(
    val id: String,
    val title: DynamicValue,
    val items: List<MenuItemConfig>
)
```

**Features:**
- Section title (gray, 14sp)
- Multiple menu items
- Vertical layout

## Icon Integration

All menu items use the extracted icons from screenshots:

| Menu Item | Icon Name | Source |
|-----------|-----------|--------|
| 产品 | `wallet_filled` | menu |
| 个人预算 | `budget` | menu |
| 保险 | `insurance_umbrella` | menu |
| 安全和防诈骗 | `security_shield` | menu |
| 转账 | `transfer` | menu |
| 银行卡 | `bank_card` | menu |
| 财富 | `wealth` | menu |
| Well+ | `wellness_trophy` | menu |
| 您的任务 | `tasks_checklist` | menu |
| 管理设备 | `device_management` | menu |
| 管理安全设置 | `security_settings` | menu |
| 使用记录 | `usage_history` | menu |
| 防诈骗和保安提示 | `anti_fraud` | menu |
| 外汇 | `fx_currency` | menu |
| 旅游保险 | `travel_insurance` | menu |
| Future Planner | `future_planner_flag` | menu |
| 保险产品 | `insurance_product` | menu |
| 强积金 | `mpf_chart` | menu |
| 银行卡和分期计划 | `wallet_card` | menu |
| 贷款 | `loan_hand` | menu |
| 常见问题 | `faq_question` | menu |
| 联系我们 | `contact_chat` | menu |
| 信息 | `info_locked` | menu |
| 线上对话 | `online_chat` | menu |
| 退出登录 | `logout` | menu |

## Configuration Files

### menu_overlay.a2ui.jsonl
Complete menu overlay configuration with:
- Overlay container with backdrop
- Header with product banner
- 6 menu sections with 24 menu items
- Version info and logout button

### hsbc_home.a2ui.jsonl (Updated)
- Added `menuButton` with `close_outline` icon
- Added `menuOverlay` component
- Menu visibility bound to `menu.visible` data path
- All icons using actual icon names

### wealth_page.a2ui.jsonl (Updated)
- Added `menuButton` with action
- All product icons using extracted icon names
- Menu visibility state

## Usage

### Toggle Menu Visibility
```kotlin
// Show menu
resolver.setValue("menu.visible", true)

// Hide menu
resolver.setValue("menu.visible", false)
```

### Handle Menu Events
```kotlin
when (event) {
    "onOpenMenu" -> {
        resolver.setValue("menu.visible", true)
    }
    "onCloseMenu" -> {
        resolver.setValue("menu.visible", false)
    }
    "onWealth" -> {
        navigateTo("wealth")
        resolver.setValue("menu.visible", false)
    }
    // ... handle other menu items
}
```

## Navigation Integration

```kotlin
renderer.setOnNavigateListener { page ->
    val config = when(page) {
        "home" -> loadConfig("hsbc_home.a2ui.jsonl")
        "wealth" -> loadConfig("wealth_page.a2ui.jsonl")
        "menu" -> loadConfig("menu_overlay.a2ui.jsonl")
    }
    renderer.renderSurface(config, container, theme)
}

// Handle menu open
fun onMenuClick() {
    resolver.setValue("menu.visible", true)
}

// Handle menu close
fun onMenuClose() {
    resolver.setValue("menu.visible", false)
}
```

## Styling

### Overlay
- Backdrop: `#80000000` (50% black)
- Menu width: 85% of screen
- Menu background: `#FFFFFF`
- Padding: 16dp

### MenuItem
- Height: 64dp (including padding)
- Icon: 40x40dp with 8dp padding
- Label: 14sp, `#000000`
- Icon-Label spacing: 16dp

### MenuSection
- Title: 14sp, `#666666`
- Top/Bottom margin: 16dp
- Divider between sections: `#E6E6E6`, 1dp

## Event Flow

```
User taps hamburger icon
    ↓
onOpenMenu event triggered
    ↓
menu.visible = true
    ↓
Overlay visibility updates
    ↓
Menu slides in from left
    ↓
User taps menu item
    ↓
Item action triggered (e.g., onWealth)
    ↓
Navigate to page + close menu
    ↓
menu.visible = false
    ↓
Overlay fades out
```

## Files Modified

### Kotlin Files
- `A2UIModels.kt`: Added Overlay, MenuItem, MenuSection components
- `A2UIComponents.kt`: Implemented rendering for new components
- `A2UIRenderer.kt`: Added Overlay and MenuSection to buildComponentTree

### Configuration Files
- `menu_overlay.a2ui.jsonl`: New menu overlay configuration
- `hsbc_home.a2ui.jsonl`: Added menu button and overlay
- `wealth_page.a2ui.jsonl`: Added menu button
- `icons_manifest.json`: 53 icons for menu items

## Next Steps

1. **Implement menu event handlers** in MainActivity
2. **Add smooth slide animation** for overlay
3. **Support menu item badges** for notifications
4. **Add user profile section** at top of menu
5. **Implement logout functionality**
6. **Add version check/update** logic
