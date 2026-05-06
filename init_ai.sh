#!/bin/bash
set -e

# Default parameters
CHANNEL="global"
VERSION="0.11.2"

# Usage function
usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -h, --help       Show this help message and exit"
    echo "  --channel        Specify the channel (cn or global). Default: global"
    echo "  --version        Specify the version. Default: 0.11.1"
    echo ""
    echo "Example:"
    echo "  $0 --channel cn --version 0.11.0"
}

while [[ "$#" -gt 0 ]]; do
    case $1 in
        -h|--help) usage; exit 0 ;;
        --channel) CHANNEL="$2"; shift ;;
        --version) VERSION="$2"; shift ;;
        *) echo "Unknown parameter passed: $1"; usage; exit 1 ;;
    esac
    shift
done

if [[ "$CHANNEL" != "cn" && "$CHANNEL" != "global" ]]; then
    echo "Error: Channel must be 'cn' or 'global'."
    usage
    exit 1
fi

# Extract major and minor version to use as directory name (e.g., 0.11.0 -> 0.11)
VERSION_DIR=$(echo "$VERSION" | cut -d. -f1,2)

# Set URL based on channel
if [[ "$CHANNEL" == "cn" ]]; then
    URL="https://lf-devtools.picoxr.com/obj/spatial-toolbox/online/aibundle/${VERSION_DIR}/aibundle-${VERSION}.zip"
else
    URL="https://lf-developer.picovr.com/obj/spatial-toolbox-mycis/oversea/aibundle/${VERSION_DIR}/aibundle-${VERSION}.zip"
fi

ZIP_FILE="/tmp/aibundle-${VERSION}.zip"
EXTRACT_DIR="/tmp/aibundle-${VERSION}-extract"

echo "Downloading AI assets from ${URL}..."
curl -L -o "${ZIP_FILE}" "${URL}"

if [[ ! -f "${ZIP_FILE}" ]]; then
    echo "Error: Failed to download the zip file."
    exit 1
fi

echo "Extracting assets..."
rm -rf "${EXTRACT_DIR}"
mkdir -p "${EXTRACT_DIR}"
unzip -q "${ZIP_FILE}" -d "${EXTRACT_DIR}"

echo "Copying contents to current directory ($(pwd))..."
# Copy all contents except AGENTS.md first
find "${EXTRACT_DIR}" -mindepth 1 -maxdepth 1 ! -name "AGENTS.md" -exec cp -r {} . \;

# Handle AGENTS.md specifically
if [[ -f "${EXTRACT_DIR}/AGENTS.md" ]]; then
    if [[ -f "AGENTS.md" ]]; then
        echo "AGENTS.md exists in current directory. Renaming downloaded file to SpatialSDK.md..."
        cp "${EXTRACT_DIR}/AGENTS.md" "SpatialSDK.md"
        
        # Check if the content is already there
        if ! grep -q "SpatialSDK.md" "AGENTS.md"; then
            echo "Appending Spatial SDK section to AGENTS.md..."
            echo -e "\n## Spatial SDK\nFor anything related to Spatial App development, check out \`./SpatialSDK.md\`." >> AGENTS.md
        else
            echo "Spatial SDK section already exists in AGENTS.md, skipping append."
        fi
    else
        echo "Copying AGENTS.md..."
        cp "${EXTRACT_DIR}/AGENTS.md" .
    fi
fi

echo "Cleaning up temporary files..."
rm -f "${ZIP_FILE}"
rm -rf "${EXTRACT_DIR}"

echo "Done!"
