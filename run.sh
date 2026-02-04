#!/bin/bash

echo "======================================"
echo "  Text Style Converter - Launcher"
echo "======================================"
echo ""

# Check Java version
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed!"
    echo "   Please install Java 17 or higher"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java 17+ is required (detected version: $JAVA_VERSION)"
    exit 1
fi

echo "✓ Java version: $JAVA_VERSION"
echo ""

# Check Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed!"
    echo "   Please install Maven 3.8 or higher"
    exit 1
fi

echo "✓ Maven is installed"
echo ""

# Build if needed
if [ ! -f "target/TextStyleConverter.jar" ]; then
    echo "📦 Building the project..."
    mvn clean package
    if [ $? -ne 0 ]; then
        echo "❌ Build failed"
        exit 1
    fi
fi

echo ""
echo "🚀 Launching the application..."
echo ""

# Run the application
mvn javafx:run

echo ""
echo "Application closed."
