#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
PROJECT_ID="${FIREBASE_PROJECT_ID:-kmp-base-kira}"
DISPLAY_NAME="${FIREBASE_DISPLAY_NAME:-KMP Base}"

ANDROID_APPS=(
  "develop:com.kira.kmpbase.develop"
  "staging:com.kira.kmpbase.staging"
  "product:com.kira.kmpbase"
)

IOS_APPS=(
  "develop:com.kira.kmpbase.develop"
  "staging:com.kira.kmpbase.staging"
  "product:com.kira.kmpbase"
)

if ! command -v npx >/dev/null 2>&1; then
  echo "npx is required."
  exit 1
fi

FIREBASE="npx --yes firebase-tools@latest"

echo "Checking Firebase authentication..."
if ! $FIREBASE projects:list >/dev/null 2>&1; then
  echo "Run: npx firebase-tools@latest login"
  exit 1
fi

if ! $FIREBASE projects:list | rg -q "\\b${PROJECT_ID}\\b"; then
  echo "Creating Firebase project: ${PROJECT_ID}"
  $FIREBASE projects:create "${PROJECT_ID}" --display-name "${DISPLAY_NAME}"
else
  echo "Using existing Firebase project: ${PROJECT_ID}"
fi

echo "Enabling Crashlytics API (if needed)..."
# Best-effort; requires billing/API access on some accounts.
$FIREBASE apps:list --project "${PROJECT_ID}" >/dev/null 2>&1 || true

for entry in "${ANDROID_APPS[@]}"; do
  flavor="${entry%%:*}"
  package="${entry##*:}"
  dest="${ROOT_DIR}/androidApp/src/${flavor}/google-services.json"
  mkdir -p "$(dirname "${dest}")"

  echo "Downloading Android config (${flavor}) for ${package}"
  $FIREBASE apps:sdkconfig ANDROID \
    --project "${PROJECT_ID}" \
    --package-name "${package}" \
    --out "${dest}" || {
      echo "Registering Android app ${package}"
      app_id="$($FIREBASE apps:create ANDROID "${DISPLAY_NAME} ${flavor}" \
        --package-name "${package}" \
        --project "${PROJECT_ID}" \
        --json | python3 -c 'import json,sys; print(json.load(sys.stdin)["result"]["appId"])')"
      $FIREBASE apps:sdkconfig ANDROID \
        --project "${PROJECT_ID}" \
        --app-id "${app_id}" \
        --out "${dest}"
    }
done

for entry in "${IOS_APPS[@]}"; do
  env_name="${entry%%:*}"
  bundle_id="${entry##*:}"
  dest="${ROOT_DIR}/iosApp/Firebase/${env_name}/GoogleService-Info.plist"
  mkdir -p "$(dirname "${dest}")"

  echo "Downloading iOS config (${env_name}) for ${bundle_id}"
  $FIREBASE apps:sdkconfig IOS \
    --project "${PROJECT_ID}" \
    --bundle-id "${bundle_id}" \
    --out "${dest}" || {
      echo "Registering iOS app ${bundle_id}"
      app_id="$($FIREBASE apps:create IOS "${DISPLAY_NAME} iOS ${env_name}" \
        --bundle-id "${bundle_id}" \
        --project "${PROJECT_ID}" \
        --json | python3 -c 'import json,sys; print(json.load(sys.stdin)["result"]["appId"])')"
      $FIREBASE apps:sdkconfig IOS \
        --project "${PROJECT_ID}" \
        --app-id "${app_id}" \
        --out "${dest}"
    }
done

cat > "${ROOT_DIR}/firebase/.firebaserc" <<EOF
{
  "projects": {
    "default": "${PROJECT_ID}"
  }
}
EOF

echo ""
echo "Firebase setup complete for project '${PROJECT_ID}'."
echo "Android configs: androidApp/src/{develop,staging,product}/google-services.json"
echo "iOS configs: iosApp/Firebase/{develop,staging,product}/GoogleService-Info.plist"
echo ""
echo "Next: rebuild the app and send a test crash from a release build to verify Crashlytics."
