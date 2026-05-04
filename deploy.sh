#!/bin/bash

PROJECT_NAME="CustomJoinLeave"
VERSION="2.4"
JAR="$PROJECT_NAME-$VERSION.jar"

BUILD_DIR="build/libs"

TARGET_DIR="$HOME/Ansible/EuropaVista/plugin_sets/survival"
# TARGET_DIR="$HOME/Testing/VE-Survival/plugins"
# TARGET_DIR="$HOME/VanillaEuropa/Survival/plugins"

UID_GID=100987

COMPILE_COMMAND="./gradlew shadowJar"

# If -c is passed, compile the project
if [[ $1 == "-c" ]]; then
  bash $COMPILE_COMMAND
fi

podman unshare rm -f "$TARGET_DIR/$PROJECT_NAME"*.jar
podman unshare cp $BUILD_DIR/$JAR $TARGET_DIR/
podman unshare chown 988:988 -R $TARGET_DIR/$JAR
