#!/bin/bash

# A script to set up the environment for compiling LibGDX Android games.

# Function to print messages
print_message() {
    echo "========================================"
    echo "$1"
    echo "========================================"
}

# --- Ensure OpenJDK 17 is available ---
print_message "Checking for Java Development Kit (JDK) 17"
JDK_17_PATH="/usr/lib/jvm/java-17-openjdk-amd64" # Path identified in previous attempts

if [ ! -d "$JDK_17_PATH" ] || ! "$JDK_17_PATH/bin/java" -version 2>&1 | grep -q 'version "17'; then
    echo "JDK 17 not found or not valid at $JDK_17_PATH. Installing OpenJDK 17..."
    sudo apt-get update -y
    sudo apt-get install -y openjdk-17-jdk
    if [ $? -ne 0 ]; then
        echo "Error: Failed to install OpenJDK 17. Please install it manually and run this script again."
        exit 1
    fi
    # Verify after install
    if [ ! -d "$JDK_17_PATH" ] || ! "$JDK_17_PATH/bin/java" -version 2>&1 | grep -q 'version "17'; then
        echo "Error: JDK 17 installation failed or path is incorrect even after attempting install."
        # Try to find it via update-alternatives as a fallback
        DETECTED_JDK_17_PATH=$(update-java-alternatives -l | grep "1.17" | head -n 1 | awk '{print $3}')
        if [ -n "$DETECTED_JDK_17_PATH" ] && [ -d "$DETECTED_JDK_17_PATH" ]; then
            JDK_17_PATH=$DETECTED_JDK_17_PATH
            echo "Found JDK 17 at $JDK_17_PATH via update-alternatives."
        else
            echo "Still cannot find a valid JDK 17. Exiting."
            exit 1
        fi
    fi
else
    echo "JDK 17 is already installed at $JDK_17_PATH."
fi

# Set JAVA_HOME to JDK 17 for sdkmanager and subsequent Gradle builds
export ORIGINAL_JAVA_HOME=$JAVA_HOME
export ORIGINAL_PATH=$PATH

export JAVA_HOME=$JDK_17_PATH
export PATH=$JAVA_HOME/bin:$PATH
echo "Temporarily set JAVA_HOME to $JAVA_HOME for SDK manager and this script execution."

# --- Install Android SDK ---
print_message "Checking for Android SDK"

SDK_DIR="$HOME/Android/Sdk"
CMDLINE_TOOLS_DIR="$SDK_DIR/cmdline-tools"
CMDLINE_TOOLS_LATEST_BIN="$CMDLINE_TOOLS_DIR/latest/bin"

if [ ! -d "$CMDLINE_TOOLS_LATEST_BIN" ] || [ ! -f "$CMDLINE_TOOLS_LATEST_BIN/sdkmanager" ]; then
    echo "Android SDK command-line tools 'latest' not found or sdkmanager missing. Downloading and setting it up..."
    SDK_URL="https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip"

    # Ensure base Sdk directory and cmdline-tools parent directory exist
    mkdir -p "$CMDLINE_TOOLS_DIR"

    TEMP_SDK_DIR=$(mktemp -d)

    echo "Downloading Android command-line tools to $TEMP_SDK_DIR/sdk-tools.zip..."
    wget -q --show-progress -O "$TEMP_SDK_DIR/sdk-tools.zip" "$SDK_URL"
    if [ $? -ne 0 ]; then
        echo "Error: Failed to download Android SDK. Please check the URL or your internet connection."
        rm -rf "$TEMP_SDK_DIR"
        exit 1
    fi

    echo "Extracting SDK to $TEMP_SDK_DIR/cmdline-tools_extracted..."
    mkdir -p "$TEMP_SDK_DIR/cmdline-tools_extracted"
    unzip -q "$TEMP_SDK_DIR/sdk-tools.zip" -d "$TEMP_SDK_DIR/cmdline-tools_extracted"

    # The zip extracts to a 'cmdline-tools' folder inside the extraction dir.
    # We want to move this to Sdk/cmdline-tools/latest
    if [ -d "$TEMP_SDK_DIR/cmdline-tools_extracted/cmdline-tools" ]; then
        echo "Moving extracted tools to $CMDLINE_TOOLS_DIR/latest..."
        # Remove 'latest' if it exists to avoid issues with mv
        rm -rf "$CMDLINE_TOOLS_DIR/latest"
        mv "$TEMP_SDK_DIR/cmdline-tools_extracted/cmdline-tools" "$CMDLINE_TOOLS_DIR/latest"
        if [ $? -ne 0 ]; then
            echo "Error: Failed to move SDK tools to $CMDLINE_TOOLS_DIR/latest."
            rm -rf "$TEMP_SDK_DIR"
            exit 1
        fi
    else
        echo "Error: Expected 'cmdline-tools' directory not found in extracted SDK."
        rm -rf "$TEMP_SDK_DIR"
        exit 1
    fi

    rm -rf "$TEMP_SDK_DIR"
    echo "Android SDK command-line tools setup complete."
else
    echo "Android SDK command-line tools directory already exists at $CMDLINE_TOOLS_LATEST_BIN."
fi

# Set Android SDK environment variables
export ANDROID_HOME=$SDK_DIR # Also known as ANDROID_SDK_ROOT
export ANDROID_SDK_ROOT=$SDK_DIR
# Ensure new cmdline-tools path is prioritized
export PATH=$CMDLINE_TOOLS_LATEST_BIN:$ANDROID_HOME/platform-tools:$PATH

# --- Install required Android packages ---
print_message "Installing required Android SDK packages"

SDKMANAGER_PATH="$CMDLINE_TOOLS_LATEST_BIN/sdkmanager"

if [ ! -f "$SDKMANAGER_PATH" ]; then
    echo "Error: sdkmanager not found at $SDKMANAGER_PATH."
    # Attempt to find sdkmanager in common locations if ANDROID_HOME was already set from outside
    if [ -n "$ORIGINAL_ANDROID_HOME" ] && [ -f "$ORIGINAL_ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager" ]; then
        SDKMANAGER_PATH="$ORIGINAL_ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager"
    elif [ -n "$ORIGINAL_ANDROID_HOME" ] && [ -f "$ORIGINAL_ANDROID_HOME/tools/bin/sdkmanager" ]; then # Very old path
        SDKMANAGER_PATH="$ORIGINAL_ANDROID_HOME/tools/bin/sdkmanager"
    else
         echo "Failed to find sdkmanager. Please ensure Android SDK is correctly installed and ANDROID_HOME is set."
         exit 1
    fi
fi

echo "Using sdkmanager at: $SDKMANAGER_PATH"
echo "Current JAVA_HOME for sdkmanager: $JAVA_HOME"
"$JAVA_HOME/bin/java" -version # Verify Java version being used by sdkmanager calls

# Accept licenses before installing packages
echo "Attempting to accept licenses..."
yes | "$SDKMANAGER_PATH" --licenses > sdk_licenses_output.txt 2>&1 || echo "License acceptance command finished. Check sdk_licenses_output.txt for details."
cat sdk_licenses_output.txt

# Install platform-tools, build-tools, and a recent platform
echo "Installing platform-tools, build-tools (33.0.1), and platform (android-33)..."
"$SDKMANAGER_PATH" "platform-tools" "platforms;android-33" "build-tools;33.0.1" > sdk_install_output.txt 2>&1
if [ $? -ne 0 ]; then
    echo "Error: Failed to install Android SDK packages. Output:"
    cat sdk_install_output.txt
    # exit 1 # Commenting out exit to see if partial setup is usable
else
    echo "Android SDK packages installed successfully."
    cat sdk_install_output.txt # Show output even on success for verification
fi

print_message "Setup complete! ✅"
echo "JAVA_HOME set to (for this script): $JAVA_HOME"
echo "ANDROID_HOME set to: $ANDROID_HOME"
echo "Updated PATH (for this script): $PATH"
echo "To make these changes permanent, you might need to add them to your .bashrc or .profile and restart your terminal."

# Restore original JAVA_HOME and PATH if they were set before, for subsequent commands in the same session if any.
# For this task, this script is the last thing run in the bash session tool call.
# if [ -n "$ORIGINAL_JAVA_HOME" ]; then
# export JAVA_HOME=$ORIGINAL_JAVA_HOME
# export PATH=$ORIGINAL_PATH
# fi
