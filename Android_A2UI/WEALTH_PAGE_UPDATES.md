# HSBC Banking App UI Components - Updates

## Overview
Updated the UI renderer to support the HSBC banking app's Wealth (财富) page and navigation system based on screenshots.

## New UI Components Added

### 1. Tab Component
- **Purpose**: Top-level navigation tabs (概览 / 财富远见)
- **Features**:
  - Selected/unselected states
  - Red underline indicator when selected
  - Click handling for tab switching
- **Styling**: 16sp text, color changes based on selection

### 2. SegmentedTab Component
- **Purpose**: Segmented control tabs (产品 / 服务)
- **Features**:
  - Pill-shaped background when selected
  - White text on red background when selected
  - Transparent background when unselected
- **Styling**: 14sp text, 20dp corner radius

### 3. ProductIcon Component
- **Purpose**: Grid of investment products (股票，IPO, 外汇，etc.)
- **Features**:
  - Circular icon container (56dp)
  - Icon with red tint color
  - Label text below icon
  - Click handling for product selection
- **Layout**: 3 columns in grid

### 4. Card Component (Enhanced)
- **Purpose**: Container for content sections
- **Features**:
  - Elevated variant support
  - Click action support
  - Configurable padding, border, corner radius
- **Variants**: Default (flat), Elevated (with shadow)

### 5. View Component
- **Purpose**: Simple colored divider/separator
- **Features**:
  - Custom background color
  - Configurable layout dimensions
- **Use**: Vertical dividers in navigation bar

## Updated Components

### BalanceDisplay
- Added `variant` property for different sizes
- Large variant for portfolio total value

### AccountCard  
- Updated to match HSBC design (no border, white background)
- Better spacing and padding

### QuickActionCircle
- Removed elevation for flat design
- Updated icon colors

## Navigation System

### Top Navigation Bar
```
[🏠] | 转账 | 银行卡 | 财富 | 保险 | [☰]
```
- **Home icon**: Navigates to home page
- **Tab texts**: Clickable, changes color when active
- **Active tab**: Red text (#DA0011) with underline
- **Inactive tabs**: Gray text (#666666)
- **Menu button**: Opens menu

### Navigation Logic
1. User taps navigation tab
2. `onNavigate` event triggered with page ID
3. Renderer loads corresponding config file
4. Updates navigation state (selected tab)
5. Re-renders UI with new page

### Navigation Configuration (`hsbc_navigation.json`)
```json
{
  "pages": {
    "home": {"configFile": "hsbc_home.a2ui.jsonl"},
    "wealth": {"configFile": "wealth_page.a2ui.jsonl"},
    ...
  },
  "navigation": {
    "tabs": [...],
    "selectedColor": "#DA0011",
    "unselectedColor": "#666666"
  }
}
```

## Wealth Page Structure

### Header Section
- Account selector dropdown (汇丰One 投资服务)
- Portfolio summary card with:
  - Total value (总市值): 0.00 HKD
  - Unrealized P/L (未变现利润／亏损)
  - Realized P/L (已变现利润／亏损)
  - Details link (查看详情)

### Tabs Section
- 概览 (Overview) - Active tab
- 财富远见 (Wealth Vision)

### Future Planner Card
- Title: "Future Planner"
- Question about retirement funds
- Required amount: HKD 10,885,019
- Call-to-action arrow

### Products & Services Section
- Segmented tabs: 产品 | 服务
- Product grid (3 columns):
  - 股票 (Stocks)
  - 新股认购 (IPO)
  - 外汇 (FX)
  - 灵活智投 (Smart Invest)
  - 单位信托基金 (Unit Trust)
  - 债券和存款证 (Bonds)
  - 股票挂钩投资 (Structured Products)
  - 高息投资存款 (High Interest Deposit)
  - 汇丰黄金代币 (HSBC Gold Token)
  - 汇财组合贷款 (Loan)
  - 保险 (Insurance)

## Theme Updates

### Colors
```json
{
  "primary": "#DA0011",
  "textPrimary": "#000000",
  "textSecondary": "#666666",
  "textHint": "#999999",
  "divider": "#E6E6E6"
}
```

### Typography
- h5: 18px, weight 500 (section titles)
- h6: 18px, weight 500 (card titles)
- body1: 16px (main content)
- caption: 12px (secondary text)
- balance: 24px, weight 500 (large amounts)

### Spacing
- Card padding: 16dp
- Section margin: 16dp
- Grid item spacing: 8dp

## Configuration Files

### wealth_page.a2ui.jsonl
Complete wealth page configuration with:
- Navigation bar with active state
- Portfolio summary
- Tab switching
- Future planner card
- Product grid

### hsbc_home.a2ui.jsonl
Updated home page with:
- Correct account list layout
- Cash flow card
- Investment card
- Rewards card
- Bottom chat bar

### hsbc_navigation.json
Navigation configuration with:
- Page routing
- Tab definitions
- Event handlers
- Data model structure

## Implementation Notes

### Data Binding
All dynamic values use data binding:
```json
{"type": "databinding", "path": "wealth.totalValue"}
```

### Event Handling
Events trigger navigation or actions:
```json
"action": {
  "type": "event",
  "name": "onNavigate",
  "context": {"page": "wealth"}
}
```

### Template Expansion
Sub-accounts use templates:
```json
"children": {
  "template": {
    "componentId": "subAccount",
    "path": "accounts.sub"
  }
}
```

## Usage

### Loading a Page
```kotlin
val renderer = A2UIRenderer(context)
renderer.setOnNavigateListener { page ->
    // Load page configuration
    val config = loadPageConfig(page)
    renderer.renderSurface(config.components, container, theme)
}

// Initial render
renderer.renderSurface(wealthComponents, container, hsbcTheme)
```

### Handling Navigation
```kotlin
// In component click handler
setOnClick {
    resolver?.triggerEvent("onNavigate", mapOf("page" to "wealth"))
}
```

## Next Steps

1. **Create missing pages**: cards_page.a2ui.jsonl, transfer_page.a2ui.jsonl, insurance_page.a2ui.jsonl
2. **Implement tab switching logic**: Handle onTabSelect events
3. **Add product detail pages**: For each product type
4. **Implement account details**: When tapping account cards
5. **Add bottom navigation**: If needed for mobile app

## Files Modified

### Kotlin Files
- `A2UIModels.kt`: Added Tab, SegmentedTab, ProductIcon, View, Card components
- `A2UIComponents.kt`: Implemented component rendering with theme styling
- `A2UIRenderer.kt`: Added navigation listener and page switching support

### Configuration Files
- `hsbc_home.a2ui.jsonl`: Updated with actual HSBC design
- `wealth_page.a2ui.jsonl`: New wealth management page
- `hsbc_navigation.json`: Navigation configuration
- `hsbc_banking_theme.json`: Updated colors and spacing
