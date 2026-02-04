@echo off
setlocal enabledelayedexpansion

echo ======================================
echo   Text Style Converter - Launcher
echo ======================================
echo.

REM Check Java
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo X Java is not installed!
    echo   Please install Java 17 or later
    pause
    exit /b 1
)

echo [OK] Java is installed
echo.

REM Check Maven
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo X Maven is not installed!
    echo   Please install Maven 3.8 or later
    pause
    exit /b 1
)

echo [OK] Maven is installed
echo.

REM Build if needed
if not exist "target\TextStyleConverter.jar" (
    echo Building the project...
    call mvn clean package
    if %errorlevel% neq 0 (
        echo X Build failed
        pause
        exit /b 1
    )
)

echo.
echo Starting the application...
echo.

REM Run the application
call mvn javafx:run

echo.
echo Application closed.
pause
