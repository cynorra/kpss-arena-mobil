import re
import os

with open(r'd:\android-projeler\kpss arena\full_text_fitz.txt', 'r', encoding='utf-8') as f:
    full_text = f.read()

files = {}
current_file = None
current_content = []

lines = full_text.split('\n')
for line in lines:
    match = re.search(r'DOSYA:\s*([^\s=]+)', line)
    if match:
        filepath_matched = match.group(1).strip()
        if "kpssarena" in filepath_matched.lower():
            if current_file:
                files[current_file] = '\n'.join(current_content)
            
            file_path = filepath_matched
            file_path = file_path.replace("D:\\android-projeler\\kpssarena\\", "")
            file_path = file_path.replace("D:\\android-projeler\\kpssarena", "")
            file_path = file_path.replace("D:/android-projeler/kpssarena/", "")
            
            if file_path.endswith("-->"): file_path = file_path[:-3]
            if file_path.endswith("==="): file_path = file_path[:-3]
            if file_path.endswith("=="): file_path = file_path[:-2]
            
            current_file = file_path.strip()
            current_content = []
            continue
    
    if current_file:
        if "==Start of OCR" in line or "==End of OCR" in line or "==Screenshot for page" in line or line.startswith("},:"):
            continue
        current_content.append(line)

if current_file:
    files[current_file] = '\n'.join(current_content)

extracted_count = 0
for file_path, content in files.items():
    if len(file_path) > 0 and len(file_path) < 200:
        full_dest = os.path.join(r"d:\android-projeler\kpss arena", file_path)
        os.makedirs(os.path.dirname(full_dest), exist_ok=True)
        with open(full_dest, 'w', encoding='utf-8') as f:
            f.write(content.strip() + "\n")
        extracted_count += 1

print(f"Extracted {extracted_count} files.")
