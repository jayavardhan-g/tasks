import os
import subprocess
import platform
import sys
import time

def get_gradle_command():
    return "gradlew.bat" if platform.system() == "Windows" else "./gradlew"

def get_sdk_path():
    sdk = os.environ.get("ANDROID_HOME")
    if not sdk:
        home = os.path.expanduser("~")
        sdk = os.path.join(home, "AppData", "Local", "Android", "Sdk")
    return sdk

def is_emulator_running():
    try:
        result = subprocess.run(["adb", "devices"], capture_output=True, text=True)
        return "emulator" in result.stdout and "device" in result.stdout
    except:
        return False

def launch_emulator_sequence():
    if is_emulator_running():
        print("\n✅ Emulator is already running.")
        return

    sdk = get_sdk_path()
    emulator_bin = os.path.join(sdk, "emulator", "emulator")
    
    # --- 1. Select AVD ---
    try:
        result = subprocess.run([emulator_bin, "-list-avds"], capture_output=True, text=True)
        avds = [d for d in result.stdout.strip().splitlines() if d]
    except FileNotFoundError:
        print("❌ Error: Emulator tool not found.")
        return

    if not avds:
        print("❌ No emulators found.")
        return

    print(f"\n📲 Found {len(avds)} devices:")
    for i, avd in enumerate(avds):
        print(f"[{i+1}] {avd}")
    
    try:
        choice = int(input("\nSelect device (default 1): ")) - 1
        target_avd = avds[choice]
    except:
        target_avd = avds[0]

    # --- 2. Select Boot Mode (NEW) ---
    print(f"\n⚙️  Boot Options for {target_avd}:")
    print(" [1] 🚀 Quick Boot (Standard)  <-- Fastest")
    print(" [2] ❄️  Cold Boot (No Snapshot) <-- Use if app is glitching")
    print(" [3] 🧨 Wipe Data (Factory Reset)<-- Use if app crashes on launch")
    
    boot_mode = input("\n👉 Select mode (default 1): ").strip()
    
    cmd = [emulator_bin, "-avd", target_avd]

    if boot_mode == '2':
        print(f"\n❄️  Cold booting {target_avd}...")
        cmd.append("-no-snapshot-load")
    elif boot_mode == '3':
        print(f"\n🧨 Wiping data for {target_avd}...")
        cmd.append("-wipe-data")
    else:
        print(f"\n🚀 Launching {target_avd}...")

    # --- 3. Launch ---
    subprocess.Popen(cmd, stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    
    print("⏳ Waiting for boot...")
    subprocess.run(["adb", "wait-for-device"])
    print("✅ Ready!")

def run_build_task(clean_install=False):
    cmd = get_gradle_command()
    
    if clean_install:
        print("\n🧹 Uninstalling old app first...")
        try:
            subprocess.run([cmd, "uninstallDebug"], shell=(platform.system()=="Windows"), check=False, stdin=subprocess.DEVNULL)
        except:
            pass 

    print("\n🔨 Building and Updating App...")
    try:
        subprocess.run([cmd, "installUniversalGmsDebug"], shell=(platform.system()=="Windows"), check=True, stdin=subprocess.DEVNULL)
        print("\n🎉 SUCCESS! App updated.")
    except subprocess.CalledProcessError:
        print("\n💥 BUILD FAILED.")

def main_menu():
    while True:
        print("\n" + "="*35)
        print("   🤖 ANDROID COMMAND CENTER")
        print("="*35)
        print(" [1] ⚡ Update App (Keep Data)")
        print(" [2] 📲 Launch Emulator")
        print(" [3] ❌ Stop ADB Server")
        print(" [4] 🧹 Clean Install (Wipe App Data)")
        print(" [q] 🚪 Quit")
        
        try:
            choice = input("\n👉 Command: ").lower().strip()
        except (EOFError, KeyboardInterrupt):
            print("\n👋 Exiting...")
            break
        
        if choice == '1':
            run_build_task(clean_install=False)
        elif choice == '2':
            launch_emulator_sequence()
        elif choice == '3':
            subprocess.run(["adb", "kill-server"])
            subprocess.run(["adb", "start-server"])
        elif choice == '4':
            run_build_task(clean_install=True)
        elif choice == 'q':
            break

if __name__ == "__main__":
    if not is_emulator_running():
        ans = input("⚠️ No emulator detected. Launch one? (y/n): ")
        if ans.lower() == 'y':
            launch_emulator_sequence()
    
    main_menu()