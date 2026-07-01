#!/bin/bash
set -euo pipefail

CRASHLYTICS_DIR="${BUILD_DIR%/Build/*}/SourcePackages/checkouts/firebase-ios-sdk/Crashlytics"
if [ ! -d "$CRASHLYTICS_DIR" ]; then
  CRASHLYTICS_DIR="${SHARED_PRECOMPS_DIR%/Build/*}/SourcePackages/checkouts/firebase-ios-sdk/Crashlytics"
fi

RUN_SCRIPT="${CRASHLYTICS_DIR}/run"
UPLOAD_SYMBOLS="${CRASHLYTICS_DIR}/upload-symbols"
GSP="${TARGET_BUILD_DIR}/${UNLOCALIZED_RESOURCES_FOLDER_PATH}/GoogleService-Info.plist"

if [ ! -f "$RUN_SCRIPT" ]; then
  echo "warning: Firebase Crashlytics run script not found. Resolve SPM packages first."
  exit 0
fi

echo "Uploading app dSYMs to Crashlytics (${CONFIGURATION})..."
"$RUN_SCRIPT"

SHARED_DSYM="${SRCROOT}/../composeApp/build/xcode-frameworks/${CONFIGURATION}/${SDK_NAME}/Shared.framework.dSYM"
if [ -f "$UPLOAD_SYMBOLS" ] && [ -d "$SHARED_DSYM" ] && [ -f "$GSP" ]; then
  echo "Uploading Kotlin Shared.framework dSYM to Crashlytics..."
  "$UPLOAD_SYMBOLS" -gsp "$GSP" -p ios "$SHARED_DSYM"
fi
