#!/usr/bin/env sh
set -eu
GRADLE_VERSION=9.2.1
GRADLE_HOME="${HOME}/.gradle/manual/gradle-${GRADLE_VERSION}"
if [ ! -x "${GRADLE_HOME}/bin/gradle" ]; then
  mkdir -p "${HOME}/.gradle/manual"
  ZIP="${TMPDIR:-/tmp}/gradle-${GRADLE_VERSION}-bin.zip"
  if command -v curl >/dev/null 2>&1; then
    curl -fL "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" -o "$ZIP"
  else
    wget -O "$ZIP" "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"
  fi
  unzip -q -o "$ZIP" -d "${HOME}/.gradle/manual"
  rm -f "$ZIP"
fi
exec "${GRADLE_HOME}/bin/gradle" "$@"
