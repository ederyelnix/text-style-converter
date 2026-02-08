# Changelog

All changes to this project will be documented in this file.

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

## [2.0.0] - 2026-02-07

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
