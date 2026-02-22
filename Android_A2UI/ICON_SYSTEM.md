# HSBC Banking App - Icon System

## Overview
Extracted 53 icons from HSBC app screenshots and implemented an IconManager for centralized icon management.

## Icon Extraction Sources

| Source File | Icons Extracted | Description |
|-------------|----------------|-------------|
| `s_combined.jpg` | 14 | Home page icons (quick actions, account cards, bottom bar) |
| `wealth_combined.jpg` | 11 | Wealth page icons (product grid, portfolio) |
| `ham_combined.jpg` | 28 | Menu page icons (navigation, security, loans, help) |

## Extracted Icons

### Navigation Icons
| Icon Name | File | Size | Source |
|-----------|------|------|--------|
| `home` | `ic_home_outline.png` | 120x120 | menu |
| `bell_outline` | `ic_bell_outline.png` | 120x120 | menu |
| `person_outline` | `ic_person_outline.png` | 120x120 | menu |
| `close_outline` | `ic_close_outline.png` | 120x120 | menu |
| `arrow_forward` | `ic_arrow_forward.png` | 60x60 | home |
| `expand_more` | `ic_expand_more.png` | 50x50 | wealth |

### Main Feature Icons
| Icon Name | File | Size | Source |
|-----------|------|------|--------|
| `transfer` | `ic_transfer.png` | 100x100 | menu |
| `bank_card` | `ic_bank_card.png` | 100x100 | menu |
| `wealth` | `ic_wealth.png` | 100x100 | menu |
| `wallet_filled` | `ic_wallet_filled.png` | 120x120 | menu |
| `transfer_local` | `ic_transfer_local.png` | 60x60 | home |
| `transfer_hsbc` | `ic_transfer_hsbc.png` | 60x60 | home |
| `deposit` | `ic_deposit.png` | 60x60 | home |

### Investment Product Icons
| Icon Name | File | Size | Source |
|-----------|------|------|--------|
| `trending_up` | `ic_trending_up.png` | 60x60 | home |
| `ipo_flag` | `ic_ipo_flag.png` | 50x50 | wealth |
| `currency_exchange` | `ic_currency_exchange.png` | 50x50 | wealth |
| `pie_chart` | `ic_pie_chart.png` | 50x50 | wealth |
| `fund` | `ic_fund.png` | 50x50 | wealth |
| `bond` | `ic_bond.png` | 50x50 | wealth |
| `puzzle` | `ic_puzzle.png` | 50x50 | wealth |
| `percent` | `ic_percent.png` | 50x50 | wealth |
| `hexagon` | `ic_hexagon.png` | 60x60 | home |

### Security Icons
| Icon Name | File | Size | Source |
|-----------|------|------|--------|
| `security_shield` | `ic_security_shield.png` | 100x100 | menu |
| `device_management` | `ic_device_management.png` | 80x80 | menu |
| `security_settings` | `ic_security_settings.png` | 80x80 | menu |
| `usage_history` | `ic_usage_history.png` | 80x80 | menu |
| `anti_fraud` | `ic_anti_fraud.png` | 80x80 | menu |
| `info_locked` | `ic_info_locked.png` | 80x80 | menu |

### Insurance Icons
| Icon Name | File | Size | Source |
|-----------|------|------|--------|
| `insurance_umbrella` | `ic_insurance_umbrella.png` | 100x100 | menu |
| `travel_insurance` | `ic_travel_insurance.png` | 80x80 | menu |
| `insurance_product` | `ic_insurance_product.png` | 80x80 | menu |

### Financial Planning Icons
| Icon Name | File | Size | Source |
|-----------|------|------|--------|
| `future_planner_flag` | `ic_future_planner_flag.png` | 80x80 | menu |
| `mpf_chart` | `ic_mpf_chart.png` | 80x80 | menu |
| `budget` | `ic_budget.png` | 100x100 | menu |

### Loan Icons
| Icon Name | File | Size | Source |
|-----------|------|------|--------|
| `wallet_card` | `ic_wallet_card.png` | 80x80 | menu |
| `loan_hand` | `ic_loan_hand.png` | 80x80 | menu |
| `loan` | `ic_loan.png` | 50x50 | wealth |

### Rewards & Wellness Icons
| Icon Name | File | Size | Source |
|-----------|------|------|--------|
| `wellness_trophy` | `ic_wellness_trophy.png` | 80x80 | menu |
| `tasks_checklist` | `ic_tasks_checklist.png` | 80x80 | menu |
| `rewards` | `ic_rewards.png` | 60x60 | home |

### Help & Support Icons
| Icon Name | File | Size | Source |
|-----------|------|------|--------|
| `faq_question` | `ic_faq_question.png` | 80x80 | menu |
| `contact_chat` | `ic_contact_chat.png` | 80x80 | menu |
| `online_chat` | `ic_online_chat.png` | 80x80 | menu |
| `chat` | `ic_chat.png` | 60x60 | home |
| `mic` | `ic_mic.png` | 60x60 | home |

### Utility Icons
| Icon Name | File | Size | Source |
|-----------|------|------|--------|
| `edit` | `ic_edit.png` | 60x60 | home |
| `visibility` | `ic_visibility.png` | 60x60 | home |
| `bank` | `ic_bank.png` | 60x60 | home |
| `add_circle` | `ic_add_circle.png` | 60x60 | home |
| `logout` | `ic_logout.png` | 80x80 | menu |
| `fx_currency` | `ic_fx_currency.png` | 80x80 | menu |
| `info` | `ic_info.png` | 50x50 | wealth |

## IconManager Implementation

### Usage

```kotlin
// Initialize during app startup (in Application class or MainActivity.onCreate)
IconManager.initialize(applicationContext)

// Get icon resource ID
val iconManager = IconManager.getInstance()
val homeIconRes = iconManager?.getIconResource("home")

// Get drawable directly
val homeDrawable = iconManager?.getIconDrawable("home")

// Check if icon exists
if (iconManager?.hasIcon("wealth") == true) {
    // Use the icon
}

// Register custom icon at runtime
iconManager?.registerIcon("custom_icon", R.drawable.my_custom_icon)
```

### In Components

```kotlin
// Icon component automatically uses IconManager
catalog.register("Icon") { component, parent, ctx ->
    val iconManager = IconManager.getInstance()
    val iconResId = iconManager?.getIconResource(component.name)
    
    ImageView(ctx).apply {
        if (iconResId != null && iconResId != 0) {
            setImageResource(iconResId)
        }
        // Apply tint, size, etc.
    }
}
```

## File Locations

```
android-ui-renderer/
├── app/src/main/res/drawable/
│   ├── ic_home_outline.png
│   ├── ic_transfer.png
│   ├── ic_wealth.png
│   └── ... (53 icon files)
├── app/src/main/assets/
│   └── icons_manifest.json
└── app/src/main/java/.../a2ui/core/
    └── IconManager.kt
```

## Icon Naming Convention

Icons follow Android naming conventions:
- Prefix: `ic_`
- Name: snake_case (e.g., `transfer_local`, `future_planner_flag`)
- Format: PNG
- Sizes: 50x50, 60x60, 80x80, 100x100, 120x120 (extracted from screenshots)

## Icon Manifest

The `icons_manifest.json` file contains:
```json
{
  "icons": [
    {
      "name": "home_outline",
      "file": "ic_home_outline.png",
      "source": "menu",
      "position": [100, 180, 120, 120]
    },
    ...
  ],
  "total": 53
}
```

## Integration with Theme

Icons can be styled using the theme system:

```json
{
  "components": {
    "icon": {
      "tintColor": "#DA0011",
      "size": 24
    },
    "quickActionCircle": {
      "iconColor": "#DA0011",
      "iconSize": 32
    }
  }
}
```

## Color Tinting

All extracted icons support color tinting via `setColorFilter()`:

```kotlin
// Apply HSBC red tint
iconView.setColorFilter(
    Color.parseColor("#DA0011"),
    PorterDuff.Mode.SRC_IN
)
```

## Benefits

1. **Consistency**: All icons extracted from actual HSBC app
2. **Centralized Management**: Single source of truth for icon resources
3. **Runtime Flexibility**: Can register custom icons dynamically
4. **Theme Support**: Icons can be tinted to match theme colors
5. **Performance**: Resource IDs cached for quick lookup
6. **Maintainability**: Manifest tracks all icons and their sources

## Future Enhancements

1. Add vector drawable versions for scalability
2. Implement icon categories for better organization
3. Add icon preview tool for developers
4. Support icon animation (Lottie)
5. Add icon fallback mechanism
