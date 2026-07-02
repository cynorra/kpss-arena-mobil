import fitz # PyMuPDF
import re
import os

pdf_path = r"d:\android-projeler\kpss arena\KPSS Arena.pdf"
doc = fitz.open(pdf_path)

full_text = ""
for page in doc:
    full_text += page.get_text() + "\n"

files = {}
current_file = None
current_content = []

lines = full_text.split('\n')
for line in lines:
    match = re.search(r'===\s*DOSYA:\s*(.*?)\s*===', line)
    if match:
        if current_file:
            files[current_file] = '\n'.join(current_content)
        
        file_path = match.group(1).strip()
        # Clean up path
        file_path = file_path.replace("D:\\android-projeler\\kpssarena\\", "")
        # Also clean up from markdown comments
        if "-->" in file_path:
            file_path = file_path.replace("-->", "").strip()
        current_file = file_path
        current_content = []
    else:
        if current_file:
            current_content.append(line)

if current_file:
    files[current_file] = '\n'.join(current_content)

for file_path, content in files.items():
    # remove trailing --> from last line if xml comment, or other things
    # but the content itself is the code
    # Clean the content - some lines might have OCR artifacts, but hopefully it's mostly ok
    
    # We notice some XML comments `<!-- === DOSYA: ... ===` have trailing `-->` on the next line or same line?
    # In the PDF output: 
    # `<!-- === DOSYA: D:\android-projeler\kpssarena\app\src\main\AndroidManifest.xml === `
    # The OCR captured it as `<!-- === DOSYA: D:\android-projeler\kpssarena\app\src\main\AndroidManifest.xml === ` without closing tag
    
    full_dest = os.path.join(r"d:\android-projeler\kpss arena", file_path)
    os.makedirs(os.path.dirname(full_dest), exist_ok=True)
    with open(full_dest, 'w', encoding='utf-8') as f:
        f.write(content.strip() + "\n")

print(f"Extracted {len(files)} files.")
