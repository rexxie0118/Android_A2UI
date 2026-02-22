#!/usr/bin/env python3
import json
import sys

with open("app/src/main/assets/config.a2ui.jsonl", "r") as f:
    lines = f.readlines()

# line index 5 (0-based) is page3 updateComponents
line = lines[5].strip()
data = json.loads(line)
components = data["updateComponents"]["components"]

# Find root column component
root = next(c for c in components if c["id"] == "root")
# Add new child to root children list before finishButton
if "finishButton" in root["children"]:
    idx = root["children"].index("finishButton")
    root["children"].insert(idx, "templateDemoButton")
else:
    root["children"].append("templateDemoButton")

# Create new button component
button = {
    "component": "Button",
    "id": "templateDemoButton",
    "child": "templateDemoButtonText",
    "variant": "secondary",
    "action": {"functionCall": {"call": "navigateTo", "args": {"surfaceId": "page4"}}},
}
components.append(button)

# Create text child for button
text = {"component": "Text", "id": "templateDemoButtonText", "text": "Template Demo"}
components.append(text)

# Update line
lines[5] = json.dumps(data) + "\n"

with open("app/src/main/assets/config.a2ui.jsonl", "w") as f:
    f.writelines(lines)

print("Updated page3")
