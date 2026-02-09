# Changelog

All changes to this project will be documented in this file.

## [2.0.4] - 2026-02-09

### Added
- Interactive Tutorial : Step-by-step guided tour on first launch
- Visual Highlights : Yellow highlights show exactly what to do
- Contextual Explanations : Learn each feature when you need it
- Copy Feedback : Visual confirmation when text is copied to clipboard
- Help Menu : Choose between tutorial or user guide
- Tutorial Persistence : Runs once, can be restarted anytime

### Fixed
- Version number updated in footer (v2.0.4)
- All translation files synchronized
- Help button properly styled and positioned

### bugs fixed

## [2.0.3] - 2026-02-09

### Added
- Help button in application header (yellow ❓ button)
- Comprehensive help dialog system
- Multilingual help content in all 4 languages (EN, FR, ES, PT)
- User guide with step-by-step instructions
- Feature overview section in help
- Keyboard shortcuts reference
- Compatibility information display
- Scrollable help dialog for better readability

### Fixed
- Version number updated in footer (v2.0.3)
- All translation files synchronized
- Help button properly styled and positioned

### Technical
- New `showHelpDialog()` method in controller
- New `createHelpSection()` helper method
- Help button CSS styling with hover effects
- Translation keys for help content in all languages

## [2.0.2] - 2026-02-08

### bugs fixed

## [2.0.1] - 2026-02-08

### Added
- Locale preference persistence across application restarts
- Preferences saved to ~/.textstyle_preferences.txt
- Language selection now remembered between sessions

### Fixed
- Card responsiveness improved with flexible widths
  - Min width: 280px
  - Preferred width: 320px
  - Max width: 400px
- Cards now adapt naturally to window resizing

### Changed
- Result ordering now groups styles by font families
- Family order: Serif → Sans-Serif → Script → Fraktur → Mathematical → Circled → Squared → Decorations → Transforms → Special → Glitch
- Better visual coherence and navigation experience

### Improved
- Code structure in TextStyleConverterController
- Card creation logic simplified
- Better separation of concerns

## [2.0.0] - 2026-02-04

### Added
- Complete JavaFX desktop application
- 43+ Unicode text styles
- Persistent conversion history (50 entries max)
- Modern user interface with Material Design principles
- Comprehensive font support (Noto, STIX)
- Multi-language support (EN, FR, ES, PT)
- Pagination system (6, 12, 24, 48 results per page)
- Search and filtering
- History export to text file
- One-click copy to clipboard
- Full text preview dialog

### Technical
- JavaFX 21
- Java 17
- Maven build system
- FXML-based UI
- Resource bundle internationalization
- Unicode character mapping system

## [1.0.0] - 2025-12-15

### Added
- Initial web version (JavaScript/HTML/CSS)
- 43 core text styles
- Basic Unicode transformation
- Web-based interface