#!/bin/bash

echo "========================================"
echo " verifying Spring Boot Learning Setup   "
echo "========================================"
echo ""

# 1. Check Java
echo "[1/3] Checking Java..."
if command -v java >/dev/null 2>&1; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo "✅ Java is installed."
    echo "   Version: $JAVA_VERSION"
    if [[ "$JAVA_VERSION" == *"21"* ]]; then
        echo "   ✅ Great! You have Java 21."
    else
        echo "   ⚠️ Note: Recommended version is Java 21."
    fi
else
    echo "❌ Java is NOT installed or not in PATH."
fi
echo ""

# 2. Check Maven
echo "[2/3] Checking Maven..."
if command -v mvn >/dev/null 2>&1; then
    MVN_VERSION=$(mvn -version 2>&1 | head -n 1)
    echo "✅ Maven is installed."
    echo "   $MVN_VERSION"
else
    echo "❌ Maven is NOT installed or not in PATH."
fi
echo ""

# 3. Check Docker
echo "[3/3] Checking Docker..."
if command -v docker >/dev/null 2>&1; then
    DOCKER_VERSION=$(docker --version)
    echo "✅ Docker is installed."
    echo "   $DOCKER_VERSION"

    # Check if Docker daemon is running
    if docker info >/dev/null 2>&1; then
        echo "   ✅ Docker daemon is running."
    else
        echo "   ❌ Docker is installed but the daemon is not running."
    fi
else
    echo "❌ Docker is NOT installed or not in PATH."
fi
echo ""

echo "========================================"
echo " Setup Verification Complete.           "
echo "========================================"
