# Text Style Converter - JavaFX Edition

Professional Unicode text converter with 43+ styles, perfect for social media, messaging apps, and more. Fully multilingual (English, French, Spanish, Portuguese) and easily extensible.

## Features

### Available Styles (43+)

#### Serif (4 variants)
- Serif Normal
- Serif Bold
- Serif Italic  
- Serif Bold Italic

#### Sans-Serif (4 variants)
- Sans Serif Normal
- Sans Serif Bold
- Sans Serif Italic
- Sans Serif Bold Italic

#### Script (2 variants)
- Script Normal
- Script Bold

#### Fraktur (2 variants)
- Fraktur Normal (gothic)
- Fraktur Bold

#### Other Styles
- Monospace
- Double Struck (mathematical)
- Small Caps
- Superscript
- Subscript
- Circles (2 variants)
- Squares (2 variants)
- Parentheses
- Full Width
- Currency
- Medieval
- Asian Style
- Bubbles (2 variants)
- Regional Flags
- Math Bold (2 variants)
- Curly
- Lowercase
- Decorations (5 variants)
- Transformations (3 variants)
- Glitch/Zalgo (2 variants)

## Core Features

### Main Interface
- Input area with character counter
- Instant generation of 43+ styles
- Style search and filtering
- Customizable pagination (6, 12, 24, 48 results per page)
- One-click copying

### History
- Automatic conversion saving
- 50-entry limit
- Quick edit from history
- Direct regeneration
- Text file export
- Disk persistence

### Complete Unicode Support
- Optimized Unicode fonts (Noto, STIX)
- Proper rendering of all special characters
- Compatible with all systems

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- JavaFX 21+

## Installation

### With Maven

```bash
mvn clean install
mvn javafx:run
```

### Create an Executable JAR

```bash
mvn clean package
java -jar target/TextStyleConverter.jar
```

## Project Structure

```
text-style-converter/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/textstyle/
│       │       ├── TextStyleConverterApp.java
│       │       ├── controller/
│       │       │   └── TextStyleConverterController.java
│       │       └── util/
│       │           ├── I18N.java
│       │           ├── TextStyler.java
│       │           └── HistoryManager.java
│       └── resources/
│           ├── view/
│           │   └── main.fxml
│           ├── css/
│           │   └── styles.css
│           ├── polices/
│           │   ├── NotoSans-Regular.ttf
│           │   ├── NotoSansMath-Regular.ttf
│           │   ├── NotoSansSymbols-Regular.ttf
│           │   ├── NotoSansSymbols2-Regular.ttf
│           │   └── STIXTwoMath-Regular.ttf
│           ├── images/
│           │   ├── icon.png
│           │   └── icon.ico
│           └── i18n/
│               ├── messages_en.properties
│               ├── messages_fr.properties
│               ├── messages_es.properties
│               └── messages_pt.properties
└── pom.xml
```

## Project Classes

### Core Classes

#### **TextStyle.java** - Style data class
- Contains Unicode character mappings
- Stores style metadata (name, description, category)
- Handles text transformation logic

#### **HistoryEntry.java** - History entry class
- Stores conversion history with timestamps
- Manages entry data for persistence
- Includes original and transformed text

#### **main.fxml** - User interface layout
- Complete FXML layout file
- Defines UI components and structure

#### **styles.css** - Styling file
- Professional CSS styles for the application
- Theming and visual customization

### Controller Classes

#### **TextStyleConverterController.java** - Main controller
- Manages business logic and UI interactions
- Handles user events and data flow
- Coordinates between model and view

### Utility Classes

#### **TextStyler.java** - Style factory
- Creates 43+ Unicode text styles
- Contains character mapping dictionaries
- Handles text transformation for all styles

#### **HistoryManager.java** - History management
- Manages conversion history with disk persistence
- Handles save/load operations
- Limits entries to 50 maximum

#### **I18N.java** - Internationalization
- Manages multilingual support (EN/FR/ES/PT)
- Handles locale-specific text and formatting

## Unicode Mappings Implementation

Each style uses precise Unicode character mappings in `TextStyler.java`:

```java
// Example: Serif Bold mapping implementation
private Map<Character, String> createSerifBoldMap() {
    Map<Character, String> map = new HashMap<>();
    int boldUpperStart = 0x1D400;  // 𝐀
    for (int i = 0; i < 26; i++) {
        map.put((char)('A' + i), new String(Character.toChars(boldUpperStart + i)));
    }
    // Additional mappings for lowercase, numbers, symbols...
    return map;
}
```

Used Unicode ranges in the implementation:
- Mathematical Alphanumeric Symbols (U+1D400–U+1D7FF)
- Enclosed Alphanumerics (U+2460–U+24FF)
- Letterlike Symbols (U+2100–U+214F)
- Combining Diacritical Marks (U+0300–U+036F)
- Additional ranges for specialized styles

## Required Fonts

**Note: All required fonts are already included in the project** - there's no need to download anything additional.

The following fonts are bundled with the application for optimal rendering:

1. **Noto Sans** - Base characters
2. **Noto Sans Math** - Mathematical symbols
3. **Noto Sans Symbols** - General symbols
4. **Noto Sans Symbols 2** - Extended symbols
5. **STIX Two Math** - Advanced mathematics
6. **Noto Color Emoji** - Emojis (optional)

### Font Location

All font files (.ttf) are located in:

```
src/main/resources/polices/
```

The application automatically loads these fonts at startup, ensuring consistent rendering across all systems.

## Usage

### Text Conversion

1. Enter your text in the input area
2. Click "Generate Styles"
3. Browse through 43+ generated styles
4. Click "Copy" for the desired style

### Search

Use the search bar to filter by:
- Style name
- Description
- Category

### History

- Access via the "History" button
- Edit, copy, or regenerate from history
- Export history to text file
- Clear history if needed

## Keyboard Shortcuts

- `Ctrl + Enter` in the text area: Generate styles

## Configuration

History is saved in:
```
~/.textstyle_history.txt
```

Limit: Maximum 50 entries

## Performance

- Instant generation (<100ms)
- Support for long texts
- Optimized pagination
- Real-time search

## Compatibility

### Operating Systems
- Windows 10/11
- macOS 10.15+
- Linux (Ubuntu 20.04+, Fedora, etc.)

### Compatible Applications
- All social networks (Twitter, Facebook, Instagram, etc.)
- Messaging apps (WhatsApp, Telegram, Discord, etc.)
- Text editors
- Web browsers
- Any application supporting Unicode

## Troubleshooting

### Startup Error

Check:
- Java 17+ installed
- JavaFX properly configured
- PATH environment variables

### Slow Performance

- Reduce results per page
- Close background applications
- Increase JVM memory: `java -Xmx512m -jar TextStyleConverter.jar`

## Development

### Adding a New Style

1. Create a new character mapping method in `TextStyler.java`:

```java
private Map<Character, String> createMyNewStyleMap() {
    Map<Character, String> map = new HashMap<>();
    // Add your mappings...
    return map;
}
```

2. Add the style to the collection:

```java
addStyle("myNewStyle", "My New Style", "ICON", 
         "Description", "Category", createMyNewStyleMap());
```

### Testing

Test all styles with the following sample text:
```
Hello World 123 !@# ABC xyz
```

## License

MIT License - see LICENSE file for details.

## Author

ederyelnix - Developed with JavaFX 21 and Unicode expertise.

## Version History

- **v2.0.0** - Complete JavaFX Desktop Application
  - 43+ Unicode text styles
  - Persistent conversion history
  - Modern user interface
  - Comprehensive font support

- **v1.0.0** - Initial Web Version
  - JavaScript/HTML/CSS implementation
  - 43 core text styles

## Support & Documentation

For technical questions about Unicode implementation:
- Unicode Character Charts: https://unicode.org/charts/
- File Format Information: https://www.fileformat.info/info/unicode/

---

 **Transform ordinary text into 43+ unique Unicode styles!**
