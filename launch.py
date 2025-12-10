import os
import subprocess
import platform
import time
import sys

class Getch:
    """Gets a single character from standard input.  Does not echo to the screen."""
    def __init__(self):
        try:
            self.impl = _GetchWindows()
        except ImportError:
            self.impl = _GetchUnix()

    def __call__(self): return self.impl()


class _GetchUnix:
    def __init__(self):
        import tty, termios
    def __call__(self):
        import sys, tty, termios
        fd = sys.stdin.fileno()
        old_settings = termios.tcgetattr(fd)
        try:
            tty.setraw(sys.stdin.fileno())
            ch = sys.stdin.read(1)
        finally:
            termios.tcsetattr(fd, termios.TCSADRAIN, old_settings)
        return ch


class _GetchWindows:
    def __init__(self):
        import msvcrt
    def __call__(self):
        import msvcrt
        # getch returns bytes, so we decode it to string
        return msvcrt.getch().decode('utf-8')


# --- HOW TO USE IT ---
getch = Getch()

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
    
    # List AVDs
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
        choice = int(input("\nSelect device: ")) - 1
        target_avd = avds[choice]
    except:
        target_avd = avds[0]

    print(f"🚀 Launching {target_avd}...")
    subprocess.Popen([emulator_bin, "-avd", target_avd], stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    
    print("⏳ Waiting for boot...")
    subprocess.run(["adb", "wait-for-device"])
    print("✅ Ready!")

def run_build_task(clean_install=False):
    cmd = get_gradle_command()
    
    if clean_install:
        print("\n🧹 Uninstalling old app first...")
        # Note: 'uninstallAll' is the gradle task, but sometimes 'adb uninstall' is faster if we know package name.
        # simpler to just use gradle's uninstallDebug
        try:
            subprocess.run([cmd, "uninstallDebug"], shell=(platform.system()=="Windows"), check=False)
        except:
            pass # Ignore errors if app wasn't installed

    print("\n🔨 Building and Updating App...")
    try:
        subprocess.run([cmd, "installDebug"], shell=(platform.system()=="Windows"), check=True)
        print("\n🎉 SUCCESS! App updated.")
    except subprocess.CalledProcessError:
        print("\n💥 BUILD FAILED.")

def main_menu():
    while True:
        print("\n" + "="*30)
        print("   🤖 ANDROID COMMAND CENTER")
        print("="*30)
        print(" [1] ⚡ Update App (Keep Data)   <-- Use this 99% of the time")
        print(" [2] 📲 Launch Emulator")
        print(" [3] ❌ Stop ADB Server")
        print(" [4] 🧹 Clean Install (Wipe Data) <-- Use if app crashes on launch")
        print(" [q] 🚪 Quit")
        
        choice = input("\n👉 Command: ").lower().strip()
        
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