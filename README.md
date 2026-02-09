# Text Style Converter - JavaFX Edition v2.0.4

Professional Unicode text converter with 43+ styles, featuring an interactive tutorial system that guides users through all features.

## Version 2.0.4 - Interactive Tutorial System

### New Features
- **Interactive Tutorial** - Step-by-step guided tour on first launch
- **Visual Highlights** - Yellow highlights show exactly what to do
- **Contextual Explanations** - Learn each feature when you need it
- **Copy Feedback** - Visual confirmation when text is copied to clipboard
- **Help Menu** - Choose between tutorial or user guide
- **Tutorial Persistence** - Runs once, can be restarted anytime


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
- Responsive card layout

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
- Language preference saved between sessions

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

## Usage

### Text Conversion

1. Enter your text in the input area
2. Click "Generate Styles"
3. Browse through 43+ generated styles (best ones on page 1)
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

Application data is saved in your home directory:

**History file:**
```
~/.textstyle_history.txt
```
Limit: Maximum 50 entries

**Preferences file:**
```
~/.textstyle_preferences.txt
```
Stores: Selected language preference

## Performance

- Instant generation (<100ms)
- Support for long texts
- Optimized pagination
- Real-time search
- Responsive card layout

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
addStyle("myNewStyle", "ICON", "Category", createMyNewStyleMap());
```

3. Add to priority list if it should appear first:

```java
// In TextStyleConverterController.java
private static final List<String> PRIORITY_STYLES = Arrays.asList(
    "myNewStyle", // Add here
    "serifBold", "sansSerifBold", ...
);
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

- **v2.0.4** - Interactive Tutorial System
  - Step-by-step guided tour
  - Visual element highlighting
  - Copy button feedback
  - Help menu with options
  - Tutorial persistence

- **v2.0.3** - Help System
  - Help button and user guide
  - Comprehensive documentation


- **v2.0.2** bugs fixed

- **v2.0.1** - Responsive Cards & Better Ordering
  - Responsive card layout (min 280px, pref 320px, max 400px)
  - Priority-based result ordering (best styles first)
  - Improved code structure and maintainability

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
