#!/usr/bin/env python3
import json
import sys

with open("app/src/main/assets/config.a2ui.jsonl", "r") as f:
    lines = f.readlines()

# Find the line for page3 updateComponents (surfaceId page3)
for i, line in enumerate(lines):
    if '"surfaceId":"page3"' in line and '"updateComponents"' in line:
        data = json.loads(line.strip())
        components = data["updateComponents"]["components"]

        # Find root column component
        root = next(c for c in components if c["id"] == "root")
        # Add new child to root children list
        root["children"].append("templateDemoButton")

        # Create new button component
        button = {
            "component": "Button",
            "id": "templateDemoButton",
            "child": "templateDemoButtonText",
            "variant": "secondary",
            "action": {
                "functionCall": {"call": "navigateTo", "args": {"surfaceId": "page4"}}
            },
        }
        components.append(button)

        # Create text child for button
        text = {
            "component": "Text",
            "id": "templateDemoButtonText",
            "text": "Template Demo",
        }
        components.append(text)

        # Update line
        lines[i] = json.dumps(data) + "\n"
        break

with open("app/src/main/assets/config.a2ui.jsonl", "w") as f:
    f.writelines(lines)

print("Updated config")
