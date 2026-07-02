import os
for root, dirs, files in os.walk("app/src/main/java"):
    for file in files:
        if file.endswith(".java"):
            path = os.path.join(root, file)
            with open(path, "r", encoding="utf-8") as f:
                content = f.read().strip()
                if not content.endswith("}"):
                    print(f"Truncated Java file: {path}")

for root, dirs, files in os.walk("app/src/main/res/layout"):
    for file in files:
        if file.endswith(".xml"):
            path = os.path.join(root, file)
            with open(path, "r", encoding="utf-8") as f:
                content = f.read().strip()
                if not content.endswith(">"):
                    print(f"Truncated XML file: {path}")

