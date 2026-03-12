import os

# Root directory to scan
root_dir = r"./"   # change this to your target directory
output_file = "collected_files.txt"

extensions = (".html", ".java")

with open(output_file, "w", encoding="utf-8", errors="ignore") as out:
    for root, dirs, files in os.walk(root_dir):
        for file in files:
            if file.lower().endswith(extensions):
                full_path = os.path.join(root, file)

                try:
                    with open(full_path, "r", encoding="utf-8", errors="ignore") as f:
                        content = f.read()

                    out.write(f"{file} - {full_path}\n")
                    out.write("*content*\n")
                    out.write(content)
                    out.write("\n\n" + "-"*60 + "\n\n")

                except Exception as e:
                    print(f"Failed to read {full_path}: {e}")

print(f"Done. Output written to {output_file}")