#!/bin/sh
set -eu

manifest="app/src/main/AndroidManifest.xml"
for permission in android.permission.INTERNET android.permission.ACCESS_NETWORK_STATE android.permission.RECORD_AUDIO; do
    if grep -q "$permission" "$manifest"; then
        echo "Forbidden permission found: $permission" >&2
        exit 1
    fi
done
echo "Privacy check passed: no network or microphone permissions declared."

