#!/bin/bash

echo "======================================"
echo "  Text Style Converter - Launcher"
echo "======================================"
echo ""

# Check Java version
if ! command -v java &> /dev/null; then
    echo "❌ Java n'est pas installé!"
    echo "   Installez Java 17 ou supérieur"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java 17+ requis (version détectée: $JAVA_VERSION)"
    exit 1
fi

echo "✓ Java version: $JAVA_VERSION"
echo ""

# Check Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven n'est pas installé!"
    echo "   Installez Maven 3.8 ou supérieur"
    exit 1
fi

echo "✓ Maven installé"
echo ""

# Build if needed
if [ ! -f "target/TextStyleConverter.jar" ]; then
    echo "📦 Compilation du projet..."
    mvn clean package
    if [ $? -ne 0 ]; then
        echo "❌ Erreur de compilation"
        exit 1
    fi
fi

echo ""
echo "🚀 Démarrage de l'application..."
echo ""

# Run the application
mvn javafx:run

echo ""
echo "Application fermée."
