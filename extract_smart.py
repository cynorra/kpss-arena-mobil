import re
import os

with open(r'd:\android-projeler\kpss arena\full_text_fitz.txt', 'r', encoding='utf-8', errors='ignore') as f:
    lines = f.readlines()

blocks = []
current_block = []
current_heading = None

for line in lines:
    # Match a heading like "1. PROJE DİZİN YAPISI" or "10. BaseFragment.java"
    if re.match(r'^\d+\.\s+', line.strip()):
        if current_heading is not None:
            blocks.append((current_heading, current_block))
        current_heading = line.strip()
        current_block = []
    else:
        if current_heading is not None:
            current_block.append(line)

if current_heading is not None:
    blocks.append((current_heading, current_block))

extracted = 0

for heading, block_lines in blocks:
    content = '\n'.join(block_lines).strip()
    
    # 1. Look for DOSYA line
    dosya_match = re.search(r'DOSYA:\s*([^\s=]+)', content)
    filepath = ""
    if dosya_match:
        filepath = dosya_match.group(1).strip()
        filepath = filepath.replace("D:\\android-projeler\\kpssarena\\", "")
        filepath = filepath.replace("D:\\android-projeler\\kpssarena", "")
        filepath = filepath.replace("D:/android-projeler/kpssarena/", "")
        if filepath.endswith("-->"): filepath = filepath[:-3]
        if filepath.endswith("==="): filepath = filepath[:-3]
        if filepath.endswith("=="): filepath = filepath[:-2]
        filepath = filepath.strip()

    # 2. Extract explicit filename from heading if present, e.g., (fragment_home.xml)
    filename_from_heading = ""
    m_head = re.search(r'\((.*?\.xml)\)', heading)
    if m_head:
        filename_from_heading = m_head.group(1).strip()
    elif heading.endswith(".java") or ".java " in heading:
        m_head2 = re.search(r'([A-Za-z0-9_]+\.java)', heading)
        if m_head2:
            filename_from_heading = m_head2.group(1).strip()
    elif heading.endswith(".xml"):
        m_head2 = re.search(r'([A-Za-z0-9_]+\.xml)', heading)
        if m_head2:
            filename_from_heading = m_head2.group(1).strip()

    # 3. If it's a java file, get package
    if ".java" in filepath or filename_from_heading.endswith(".java"):
        pkg_match = re.search(r'package\s+([a-zA-Z0-9_\.]+);', content)
        if pkg_match:
            pkg_path = pkg_match.group(1).replace('.', '/')
            folder = f"app/src/main/java/{pkg_path}"
            fname = filename_from_heading
            if not fname and filepath.endswith(".java"):
                fname = filepath.split('\\')[-1].split('/')[-1]
            if fname:
                filepath = f"{folder}/{fname}"

    # 4. If it's XML and path is truncated
    if "res\\" in filepath or "res/" in filepath:
        if not filepath.endswith(".xml") and filename_from_heading:
            # Replace the truncated filename with the one from the heading
            parts = filepath.replace('\\', '/').split('/')
            # e.g., app/src/main/res/layout/fragment_hom
            parts[-1] = filename_from_heading
            filepath = '/'.join(parts)

    if not filepath:
        # fallback to heading name if nothing else
        if "gradle.properties" in heading:
            filepath = "gradle.properties"
        elif "local.properties" in heading:
            filepath = "local.properties"
        elif "build.gradle" in heading:
            if "Root" in heading:
                filepath = "build.gradle"
            else:
                filepath = "app/build.gradle"
        elif "gradle-wrapper" in heading:
            filepath = "gradle/wrapper/gradle-wrapper.properties"

    if filepath and len(content) > 10:
        # Remove DOSYA lines from content to clean it up
        clean_lines = []
        for l in block_lines:
            if "DOSYA:" not in l and not l.strip().startswith("<!-- ===") and not l.strip().startswith("// ==="):
                clean_lines.append(l)
        
        final_content = ''.join(clean_lines).strip()
        if len(final_content) > 0:
            full_dest = os.path.join(r"d:\android-projeler\kpss arena", filepath)
            os.makedirs(os.path.dirname(full_dest), exist_ok=True)
            with open(full_dest, 'w', encoding='utf-8') as f:
                f.write(final_content + "\n")
            extracted += 1

print(f"Extracted {extracted} files.")
