import os

def fix_file(path, end_char):
    with open(path, "r", encoding="utf-8") as f:
        content = f.read()
    last_idx = content.rfind(end_char)
    if last_idx != -1:
        fixed_content = content[:last_idx+1]
        with open(path, "w", encoding="utf-8") as f:
            f.write(fixed_content)
        print(f"Fixed {path}")

fix_file("app/src/main/java/com/cynorra/kpssarena/presentation/base/BaseFragment.java", "}")
fix_file("app/src/main/java/com/cynorra/kpssarena/data/remote/FirebaseFirestoreSource.java", "}")
fix_file("app/src/main/java/com/cynorra/kpssarena/data/remote/FirebaseRealtimeSource.java", "}")
fix_file("app/src/main/java/com/cynorra/kpssarena/data/remote/FirebaseAuthSource.java", "}")
fix_file("app/src/main/res/layout/fragment_home.xml", ">")

