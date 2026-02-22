#!/usr/bin/env python3
import json
import sys

lines = []
with open("app/src/main/assets/config.a2ui.jsonl", "r") as f:
    content = f.read()

# Split by '}{' but careful about nested braces. Simple: split by '\n' first
original_lines = content.split("\n")
fixed_lines = []
for line in original_lines:
    line = line.strip()
    if not line:
        continue
    # Check if line contains multiple JSON objects concatenated
    # Simple detection: count opening and closing braces
    start = 0
    depth = 0
    for i, ch in enumerate(line):
        if ch == "{":
            depth += 1
        elif ch == "}":
            depth -= 1
            if depth == 0:
                # End of a JSON object
                obj_str = line[start : i + 1]
                fixed_lines.append(obj_str)
                start = i + 1
    # If there is leftover (should not), add as is
    if start < len(line):
        # leftover characters (maybe whitespace)
        pass

# Now we have fixed_lines list of JSON strings.
# Ensure we have 9 messages.
print(f"Found {len(fixed_lines)} messages")
# Let's manually verify order and fix page2 updateComponents
# We'll rebuild the list with known correct messages.
# Actually we can just keep all messages except we need to replace the corrupted page2 updateComponents.
# Find index of page2 updateComponents (surfaceId page2)
for i, obj_str in enumerate(fixed_lines):
    try:
        obj = json.loads(obj_str)
        if (
            "updateComponents" in obj
            and obj["updateComponents"]["surfaceId"] == "page2"
        ):
            # Replace with original
            original = '{"version":"v0.10","updateComponents":{"surfaceId":"page2","components":[{"component":"Column","id":"root","children":["titleText","nameField","dropdown","agreeCheckbox","buttonRow"]},{"component":"Text","id":"titleText","text":"Enter your details","variant":"h2"},{"component":"TextField","id":"nameField","label":"Name","value":"","variant":"shortText"},{"component":"ChoicePicker","id":"dropdown","label":"Select option","variant":"mutuallyExclusive","options":[{"label":"Option 1","value":"opt1"},{"label":"Option 2","value":"opt2"},{"label":"Option 3","value":"opt3"}],"value":""},{"component":"CheckBox","id":"agreeCheckbox","label":"I agree","value":false},{"component":"Row","id":"buttonRow","children":["backButton","nextButton2"],"justify":"spaceBetween"},{"component":"Button","id":"backButton","child":"backButtonText","variant":"secondary","action":{"functionCall":{"call":"navigateTo","args":{"surfaceId":"page1"}}}},{"component":"Text","id":"backButtonText","text":"Back"},{"component":"Button","id":"nextButton2","child":"nextButton2Text","variant":"primary","action":{"functionCall":{"call":"navigateTo","args":{"surfaceId":"page3"}}}},{"component":"Text","id":"nextButton2Text","text":"Next"}]}}'
            fixed_lines[i] = original
            break
    except:
        continue

# Write back
with open("app/src/main/assets/config.a2ui.jsonl", "w") as f:
    for obj_str in fixed_lines:
        f.write(obj_str + "\n")

print("Fixed config file")
