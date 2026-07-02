import fitz
import os

pdf_path = r"d:\android-projeler\kpss arena\KPSS Arena.pdf"
doc = fitz.open(pdf_path)

full_text = ""
for page in doc:
    full_text += page.get_text() + "\n"

with open(r"d:\android-projeler\kpss arena\full_text_fitz.txt", "w", encoding="utf-8") as f:
    f.write(full_text)

print(f"Extracted {len(full_text)} characters.")
