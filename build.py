import os
import sys
import json
import glob
import subprocess
import shutil
import zipfile

root_dir = os.path.abspath(os.path.dirname(__file__))
src_dir = os.path.join(root_dir, "src", "main", "java")
res_dir = os.path.join(root_dir, "src", "main", "resources")
libs_dir = os.path.join(root_dir, "libs")
out_dir = os.path.join(root_dir, "build")
classes_dir = os.path.join(out_dir, "classes")
target_jar = os.path.join(out_dir, "PulseVisuals.jar")

os.makedirs(classes_dir, exist_ok=True)

print("=" * 50)
print("  Pulse - Direct Builder")
print("=" * 50)

# 1. Resolve classpath
appdata = os.environ.get('APPDATA')
mc = os.path.join(appdata, '.minecraft')
vers = os.path.join(mc, 'versions')
libs = os.path.join(mc, 'libraries')

profiles = sorted(glob.glob(os.path.join(vers, 'fabric-loader-*-1.21.11')), reverse=True)
if not profiles:
    print("[ERROR] Fabric profile not found in .minecraft/versions")
    sys.exit(1)

profile_dir = profiles[0]
profile_name = os.path.basename(profile_dir)
with open(os.path.join(profile_dir, profile_name + ".json"), "r", encoding="utf-8") as f:
    fab = json.load(f)

vanilla_id = fab.get("inheritsFrom", "1.21.11")
with open(os.path.join(vers, vanilla_id, vanilla_id + ".json"), "r", encoding="utf-8") as f:
    van = json.load(f)

def get_lib_path(lib):
    downloads = lib.get("downloads", {})
    artifact = downloads.get("artifact", {})
    if artifact.get("path"):
        return os.path.join(libs, artifact["path"].replace("/", os.sep))
    name = lib.get("name", "")
    ext = "jar"
    if "@" in name:
        name, ext = name.split("@")
    parts = name.split(":")
    group = parts[0].replace(".", os.sep)
    artifact_id = parts[1]
    version = parts[2]
    classifier = ("-" + parts[3]) if len(parts) >= 4 else ""
    filename = f"{artifact_id}-{version}{classifier}.{ext}"
    return os.path.join(libs, group, artifact_id, version, filename)

cp_jars = []
for lib in fab.get("libraries", []) + van.get("libraries", []):
    p = get_lib_path(lib)
    if os.path.exists(p) and p not in cp_jars:
        cp_jars.append(p)

client_jar = os.path.join(vers, vanilla_id, vanilla_id + ".jar")
if os.path.exists(client_jar):
    cp_jars.append(client_jar)

for pl in glob.glob(os.path.join(libs_dir, "*.jar")):
    if pl not in cp_jars:
        cp_jars.append(pl)

api_jar = os.path.join(mc, "mods", "fabric-api.jar")
if os.path.exists(api_jar):
    cp_jars.append(api_jar)

classpath_str = ";".join(cp_jars)
print(f"[1/3] Resolved {len(cp_jars)} libraries from .minecraft and libs/")

# 2. Collect Java source files
java_files = []
for r, d, files in os.walk(src_dir):
    for f in files:
        if f.endswith(".java"):
            java_files.append(os.path.join(r, f))

print(f"[2/3] Found {len(java_files)} Java source files")

# Write sources to argfile (use forward slashes for javac)
argfile = os.path.join(out_dir, "sources.txt")
with open(argfile, "w", encoding="utf-8") as f:
    for jf in java_files:
        f.write(f'"{jf.replace(os.sep, "/")}"\n')

# 3. Compile
cmd = [
    "javac",
    "-encoding", "UTF-8",
    "-cp", classpath_str,
    "-d", classes_dir,
    "-parameters",
    f"@{argfile}"
]

print("Compiling sources (this may take a few seconds)...")
res = subprocess.run(cmd)

# 4. Package JAR
print("[3/3] Packaging PulseVisuals.jar...")
with zipfile.ZipFile(target_jar, "w", zipfile.ZIP_DEFLATED) as z:
    # Add compiled classes
    for r, d, files in os.walk(classes_dir):
        for f in files:
            full_p = os.path.join(r, f)
            rel_p = os.path.relpath(full_p, classes_dir).replace(os.sep, "/")
            z.write(full_p, rel_p)
    # Add resources
    for r, d, files in os.walk(res_dir):
        for f in files:
            full_p = os.path.join(r, f)
            rel_p = os.path.relpath(full_p, res_dir).replace(os.sep, "/")
            z.write(full_p, rel_p)
    # Add embedded libs to META-INF/jars
    for pl in glob.glob(os.path.join(libs_dir, "*.jar")):
        z.write(pl, f"META-INF/jars/{os.path.basename(pl)}")

print(f"[SUCCESS] Built ready JAR: {target_jar}")
# Copy to Pulse run folder if exists
pulse_run_jar = r"c:\Users\user\Desktop\system 67\Pulse\run\PulseVisuals.jar"
if os.path.exists(os.path.dirname(pulse_run_jar)):
    shutil.copy2(target_jar, pulse_run_jar)
    print(f"[COPIED] Updated mod in Pulse/run/PulseVisuals.jar")
