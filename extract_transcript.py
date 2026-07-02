import json
import re
import os

transcript_path = r"C:\Users\erens\.gemini\antigravity-ide\brain\57a9f4f8-eb44-4389-ad0c-0eab85c5bcff\.system_generated\logs\transcript_full.jsonl"

ocr_text = ""
with open(transcript_path, 'r', encoding='utf-8') as f:
    for line in f:
        try:
            data = json.loads(line)
            if data.get('type') == 'VIEW_FILE':
                content = data.get('content', '')
                if '==Start of OCR' in content:
                    ocr_text = content
        except Exception as e:
            pass

print("Found OCR text length:", len(ocr_text))

files = {}
current_file = None
current_content = []

lines = ocr_text.split('\n')
for line in lines:
    match = re.search(r'===\s*DOSYA:\s*(.*?)(?:\s*===|\s*$|-->)', line)
    if match:
        if current_file:
            files[current_file] = '\n'.join(current_content)
        
        file_path = match.group(1).strip()
        file_path = file_path.replace("D:\\android-projeler\\kpssarena\\", "")
        if file_path.endswith("-->"):
             file_path = file_path[:-3].strip()
        if file_path.endswith("==="):
             file_path = file_path[:-3].strip()
        if file_path.endswith("=="):
             file_path = file_path[:-2].strip()
        
        current_file = file_path
        current_content = []
    else:
        if current_file:
            if "==Start of OCR" in line or "==End of OCR" in line or "==Screenshot for page" in line or line.startswith("},:"):
                continue
            # Some lines are like `
