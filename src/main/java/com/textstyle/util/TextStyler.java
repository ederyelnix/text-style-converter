package com.textstyle.util;

import com.textstyle.model.TextStyle;
import java.util.*;

/**
 * Central factory for creating all Unicode text styles.
 * Contains mappings for 43+ different Unicode styles including Serif, Sans-Serif,
 * Script, Fraktur, Circled, Squared, and many decorative variations.
 */
public class TextStyler {
    private final Map<String, TextStyle> styles;
    private final Map<String, List<String>> categories;

    public TextStyler() {
        this.styles = new LinkedHashMap<>();
        this.categories = new HashMap<>();
        initializeAllStyles();
    }

    private void initializeAllStyles() {
        // SERIF STYLES (4 variants)
        addStyle("serifNormal", "FONT", "Serif", createSerifNormalMap());
        addStyle("serifBold", "BOLD", "Serif", createBoldSerifMap());
        addStyle("serifItalic", "ITALIC", "Serif", createItalicMap());
        addStyle("serifBoldItalic", "BOLD", "Serif", createBoldItalicMap());

        // SANS SERIF STYLES (4 variants)
        addStyle("sansSerifNormal", "FONT", "Sans-Serif", createSansSerifMap());
        addStyle("sansSerifBold", "BOLD", "Sans-Serif", createSansSerifBoldMap());
        addStyle("sansSerifItalic", "ITALIC", "Sans-Serif", createSansSerifItalicMap());
        addStyle("sansSerifBoldItalic", "BOLD", "Sans-Serif", createSansSerifBoldItalicMap());

        // SCRIPT STYLES (2 variants)
        addStyle("scriptNormal", "PEN", "Script", createScriptMap());
        addStyle("scriptBold", "PEN", "Script", createBoldScriptMap());

        // FRAKTUR STYLES (2 variants)
        addStyle("frakturNormal", "SCROLL", "Fraktur", createFrakturMap());
        addStyle("frakturBold", "SCROLL", "Fraktur", createBoldFrakturMap());

        // MONOSPACE
        addStyle("monospace", "TERMINAL", "Monospace", createMonospaceMap());

        // DOUBLE STRUCK
        addStyle("doubleStruck", "INFINITY", "Mathematical", createDoubleStruckMap());

        // CIRCLED STYLES (2 variants)
        addStyle("circled", "CIRCLE", "Circled", createCircledMap());
        addStyle("circledNegative", "DOT_CIRCLE", "Circled", createCircledNegativeMap());

        // SQUARED STYLES (2 variants)
        addStyle("squared", "SQUARE", "Squared", createSquaredMap());
        addStyle("squaredNegative", "STOP", "Squared", createSquaredNegativeMap());

        // PARENTHESIZED
        addStyle("parenthesized", "CODE", "Decorative", createParenthesizedMap());

        // FULLWIDTH
        addStyle("fullwidth", "TEXT_WIDTH", "Fullwidth", createFullwidthMap());

        // SMALL CAPS
        addStyle("smallCaps", "FONT", "Caps", createSmallCapsMap());

        // SUPERSCRIPT & SUBSCRIPT
        addStyle("superscript", "SUPERSCRIPT", "Mathematical", createSuperscriptMap());
        addStyle("subscript", "SUBSCRIPT", "Mathematical", createSubscriptMap());

        // SPECIAL STYLES
        addStyle("currency", "DOLLAR", "Special", createCurrencyMap());
        addStyle("medieval", "CHESS_ROOK", "Decorative", createMedievalMap());
        addStyle("asianStyle", "LANGUAGE", "Fullwidth", createAsianStyleMap());

        // BUBBLE STYLES (2 variants)
        addStyle("bubble", "CIRCLE", "Circled", createBubbleMap());
        addStyle("bubbleNegative", "ADJUST", "Circled", createBubbleNegativeMap());

        // REGIONAL FLAGS
        addStyle("regionalFlags", "FLAG", "Special", createRegionalFlagsMap());

        // MATH STYLES (2 variants)
        addStyle("mathBold", "CALCULATOR", "Mathematical", createMathBoldMap());
        addStyle("mathBoldItalic", "CALCULATOR", "Mathematical", createMathBoldItalicMap());

        // CURLY
        addStyle("curly", "SIGNATURE", "Script", createCurlyMap());

        // TINY
        addStyle("tiny", "COMPRESS", "Mathematical", createTinyMap());

        // DECORATIONS - using custom converters
        addStyleWithConverter("strikethrough", "STRIKETHROUGH", "Decoration",
                text -> applyDiacritic(text, '\u0336'));

        addStyleWithConverter("underline", "UNDERLINE", "Decoration",
                text -> applyDiacritic(text, '\u0332'));

        addStyleWithConverter("overline", "MINUS", "Decoration",
                text -> applyDiacritic(text, '\u0305'));

        addStyleWithConverter("doubleUnderline", "UNDERLINE", "Decoration",
                text -> applyDiacritic(text, '\u0333'));

        addStyleWithConverter("slashed", "SLASH", "Decoration",
                text -> applyDiacritic(text, '\u0338'));

        // UPSIDE DOWN
        addStyleWithConverter("upsideDown", "UNDO", "Transform",
                this::toUpsideDown);

        // REVERSED
        addStyleWithConverter("reversed", "EXCHANGE", "Transform",
                text -> new StringBuilder(text).reverse().toString());

        // WIDE
        addStyleWithConverter("wide", "ARROWS_H", "Transform",
                text -> String.join(" ", text.split("")));

        // CUTE/KAWAII
        addStyleWithConverter("cute", "HEART", "Decorative", text -> {
            String[] decorations = {"✧", "♡", "✿", "❀", "⊹", "˚", "✩", "★", "☆"};
            Random rand = new Random();
            String deco1 = decorations[rand.nextInt(decorations.length)];
            String deco2 = decorations[rand.nextInt(decorations.length)];
            return deco1 + " " + text + " " + deco2;
        });

        // ZALGO (2 variants)
        addStyleWithConverter("zalgoLight", "GHOST", "Glitch",
                text -> toZalgo(text, false));

        addStyleWithConverter("zalgoHeavy", "GHOST", "Glitch",
                text -> toZalgo(text, true));
    }

    private void addStyle(String id, String icon, String category, Map<Character, String> charMap) {
        TextStyle style = new TextStyle(id, icon, category, charMap);
        styles.put(id, style);
        categories.computeIfAbsent(category, k -> new ArrayList<>()).add(id);
    }

    private void addStyleWithConverter(String id, String icon, String category, 
                                       TextStyle.StyleConverter converter) {
        TextStyle style = new TextStyle(id, icon, category, converter);
        styles.put(id, style);
        categories.computeIfAbsent(category, k -> new ArrayList<>()).add(id);
    }

    // ===== CHARACTER MAP CREATORS =====

    private Map<Character, String> createSerifNormalMap() {
        Map<Character, String> map = new HashMap<>();
        for (int i = 0; i < 26; i++) {
            map.put((char) ('a' + i), String.valueOf((char) ('a' + i)));
            map.put((char) ('A' + i), String.valueOf((char) ('A' + i)));
        }
        for (int i = 0; i < 10; i++) {
            map.put((char) ('0' + i), String.valueOf((char) ('0' + i)));
        }
        return map;
    }

    private Map<Character, String> createBoldSerifMap() {
        Map<Character, String> map = new HashMap<>();
        int boldLowerStart = 0x1D41A;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('a' + i), new String(Character.toChars(boldLowerStart + i)));
        }
        int boldUpperStart = 0x1D400;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), new String(Character.toChars(boldUpperStart + i)));
        }
        int boldDigitStart = 0x1D7CE;
        for (int i = 0; i < 10; i++) {
            map.put((char) ('0' + i), new String(Character.toChars(boldDigitStart + i)));
        }
        return map;
    }

    private Map<Character, String> createItalicMap() {
        Map<Character, String> map = new HashMap<>();
        String[] lower = {"𝑎","𝑏","𝑐","𝑑","𝑒","𝑓","𝑔","ℎ","𝑖","𝑗","𝑘","𝑙","𝑚","𝑛","𝑜","𝑝","𝑞","𝑟","𝑠","𝑡","𝑢","𝑣","𝑤","𝑥","𝑦","𝑧"};
        for (int i = 0; i < 26; i++) {
            map.put((char) ('a' + i), lower[i]);
        }
        int italicUpperStart = 0x1D434;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), new String(Character.toChars(italicUpperStart + i)));
        }
        return map;
    }

    private Map<Character, String> createBoldItalicMap() {
        Map<Character, String> map = new HashMap<>();
        int boldItalicLowerStart = 0x1D482;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('a' + i), new String(Character.toChars(boldItalicLowerStart + i)));
        }
        int boldItalicUpperStart = 0x1D468;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), new String(Character.toChars(boldItalicUpperStart + i)));
        }
        return map;
    }

    private Map<Character, String> createSansSerifMap() {
        Map<Character, String> map = new HashMap<>();
        int sansLowerStart = 0x1D5BA;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('a' + i), new String(Character.toChars(sansLowerStart + i)));
        }
        int sansUpperStart = 0x1D5A0;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), new String(Character.toChars(sansUpperStart + i)));
        }
        int sansDigitStart = 0x1D7E2;
        for (int i = 0; i < 10; i++) {
            map.put((char) ('0' + i), new String(Character.toChars(sansDigitStart + i)));
        }
        return map;
    }

    private Map<Character, String> createSansSerifBoldMap() {
        Map<Character, String> map = new HashMap<>();
        int sansBoldLowerStart = 0x1D5EE;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('a' + i), new String(Character.toChars(sansBoldLowerStart + i)));
        }
        int sansBoldUpperStart = 0x1D5D4;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), new String(Character.toChars(sansBoldUpperStart + i)));
        }
        int sansBoldDigitStart = 0x1D7EC;
        for (int i = 0; i < 10; i++) {
            map.put((char) ('0' + i), new String(Character.toChars(sansBoldDigitStart + i)));
        }
        return map;
    }

    private Map<Character, String> createSansSerifItalicMap() {
        Map<Character, String> map = new HashMap<>();
        int sansItalicLowerStart = 0x1D622;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('a' + i), new String(Character.toChars(sansItalicLowerStart + i)));
        }
        int sansItalicUpperStart = 0x1D608;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), new String(Character.toChars(sansItalicUpperStart + i)));
        }
        return map;
    }

    private Map<Character, String> createSansSerifBoldItalicMap() {
        Map<Character, String> map = new HashMap<>();
        int sansBoldItalicLowerStart = 0x1D656;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('a' + i), new String(Character.toChars(sansBoldItalicLowerStart + i)));
        }
        int sansBoldItalicUpperStart = 0x1D63C;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), new String(Character.toChars(sansBoldItalicUpperStart + i)));
        }
        return map;
    }

    private Map<Character, String> createScriptMap() {
        Map<Character, String> map = new HashMap<>();
        String[] lower = {"𝒶","𝒷","𝒸","𝒹","ℯ","𝒻","ℊ","𝒽","𝒾","𝒿","𝓀","𝓁","𝓂","𝓃","ℴ","𝓅","𝓆","𝓇","𝓈","𝓉","𝓊","𝓋","𝓌","𝓍","𝓎","𝓏"};
        for (int i = 0; i < 26; i++) {
            map.put((char) ('a' + i), lower[i]);
        }
        String[] upper = {"𝒜","ℬ","𝒞","𝒟","ℰ","ℱ","𝒢","ℋ","ℐ","𝒥","𝒦","ℒ","ℳ","𝒩","𝒪","𝒫","𝒬","ℛ","𝒮","𝒯","𝒰","𝒱","𝒲","𝒳","𝒴","𝒵"};
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), upper[i]);
        }
        return map;
    }

    private Map<Character, String> createBoldScriptMap() {
        Map<Character, String> map = new HashMap<>();
        int boldScriptLowerStart = 0x1D4EA;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('a' + i), new String(Character.toChars(boldScriptLowerStart + i)));
        }
        int boldScriptUpperStart = 0x1D4D0;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), new String(Character.toChars(boldScriptUpperStart + i)));
        }
        return map;
    }

    private Map<Character, String> createFrakturMap() {
        Map<Character, String> map = new HashMap<>();
        int frakturLowerStart = 0x1D51E;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('a' + i), new String(Character.toChars(frakturLowerStart + i)));
        }
        String[] upper = {"𝔄","𝔅","ℭ","𝔇","𝔈","𝔉","𝔊","ℌ","ℑ","𝔍","𝔎","𝔏","𝔐","𝔑","𝔒","𝔓","𝔔","ℜ","𝔖","𝔗","𝔘","𝔙","𝔚","𝔛","𝔜","ℨ"};
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), upper[i]);
        }
        return map;
    }

    private Map<Character, String> createBoldFrakturMap() {
        Map<Character, String> map = new HashMap<>();
        int boldFrakturLowerStart = 0x1D586;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('a' + i), new String(Character.toChars(boldFrakturLowerStart + i)));
        }
        int boldFrakturUpperStart = 0x1D56C;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), new String(Character.toChars(boldFrakturUpperStart + i)));
        }
        return map;
    }

    private Map<Character, String> createMonospaceMap() {
        Map<Character, String> map = new HashMap<>();
        int monoLowerStart = 0x1D68A;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('a' + i), new String(Character.toChars(monoLowerStart + i)));
        }
        int monoUpperStart = 0x1D670;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), new String(Character.toChars(monoUpperStart + i)));
        }
        int monoDigitStart = 0x1D7F6;
        for (int i = 0; i < 10; i++) {
            map.put((char) ('0' + i), new String(Character.toChars(monoDigitStart + i)));
        }
        return map;
    }

    private Map<Character, String> createDoubleStruckMap() {
        Map<Character, String> map = new HashMap<>();
        int dsLowerStart = 0x1D552;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('a' + i), new String(Character.toChars(dsLowerStart + i)));
        }
        String[] upper = {"𝔸","𝔹","ℂ","𝔻","𝔼","𝔽","𝔾","ℍ","𝕀","𝕁","𝕂","𝕃","𝕄","ℕ","𝕆","ℙ","ℚ","ℝ","𝕊","𝕋","𝕌","𝕍","𝕎","𝕏","𝕐","ℤ"};
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), upper[i]);
        }
        int dsDigitStart = 0x1D7D8;
        for (int i = 0; i < 10; i++) {
            map.put((char) ('0' + i), new String(Character.toChars(dsDigitStart + i)));
        }
        return map;
    }

    private Map<Character, String> createCircledMap() {
        Map<Character, String> map = new HashMap<>();
        int circledLowerStart = 0x24D0;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('a' + i), new String(Character.toChars(circledLowerStart + i)));
        }
        int circledUpperStart = 0x24B6;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), new String(Character.toChars(circledUpperStart + i)));
        }
        map.put('0', "⓪");
        int circledDigitStart = 0x2460;
        for (int i = 1; i <= 9; i++) {
            map.put((char) ('0' + i), new String(Character.toChars(circledDigitStart + i - 1)));
        }
        return map;
    }

    private Map<Character, String> createCircledNegativeMap() {
        Map<Character, String> map = new HashMap<>();
        int circledNegUpperStart = 0x1F150;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), new String(Character.toChars(circledNegUpperStart + i)));
        }
        map.put('0', "⓿");
        int circledNegDigitStart = 0x2776;
        for (int i = 1; i <= 9; i++) {
            map.put((char) ('0' + i), new String(Character.toChars(circledNegDigitStart + i - 1)));
        }
        for (int i = 0; i < 26; i++) {
            map.put((char) ('a' + i), String.valueOf((char) ('a' + i)));
        }
        return map;
    }

    private Map<Character, String> createSquaredMap() {
        Map<Character, String> map = new HashMap<>();
        int squaredUpperStart = 0x1F130;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), new String(Character.toChars(squaredUpperStart + i)));
        }
        for (int i = 0; i < 26; i++) {
            map.put((char) ('a' + i), String.valueOf((char) ('a' + i)));
        }
        for (int i = 0; i < 10; i++) {
            map.put((char) ('0' + i), String.valueOf((char) ('0' + i)));
        }
        return map;
    }

    private Map<Character, String> createSquaredNegativeMap() {
        Map<Character, String> map = new HashMap<>();
        int squaredNegUpperStart = 0x1F170;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), new String(Character.toChars(squaredNegUpperStart + i)));
        }
        for (int i = 0; i < 26; i++) {
            map.put((char) ('a' + i), String.valueOf((char) ('a' + i)));
        }
        for (int i = 0; i < 10; i++) {
            map.put((char) ('0' + i), String.valueOf((char) ('0' + i)));
        }
        return map;
    }

    private Map<Character, String> createParenthesizedMap() {
        Map<Character, String> map = new HashMap<>();
        int parenLowerStart = 0x249C;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('a' + i), new String(Character.toChars(parenLowerStart + i)));
        }
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), String.valueOf((char) ('A' + i)));
        }
        int parenDigitStart = 0x2474;
        for (int i = 1; i <= 9; i++) {
            map.put((char) ('0' + i), new String(Character.toChars(parenDigitStart + i - 1)));
        }
        map.put('0', "0");
        return map;
    }

    private Map<Character, String> createFullwidthMap() {
        Map<Character, String> map = new HashMap<>();
        int fullwidthLowerStart = 0xFF41;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('a' + i), new String(Character.toChars(fullwidthLowerStart + i)));
        }
        int fullwidthUpperStart = 0xFF21;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), new String(Character.toChars(fullwidthUpperStart + i)));
        }
        int fullwidthDigitStart = 0xFF10;
        for (int i = 0; i < 10; i++) {
            map.put((char) ('0' + i), new String(Character.toChars(fullwidthDigitStart + i)));
        }
        return map;
    }

    private Map<Character, String> createSmallCapsMap() {
        Map<Character, String> map = new HashMap<>();
        Map<Character, String> smallCapsMap = Map.ofEntries(
            Map.entry('a', "ᴀ"), Map.entry('b', "ʙ"), Map.entry('c', "ᴄ"), Map.entry('d', "ᴅ"),
            Map.entry('e', "ᴇ"), Map.entry('f', "ꜰ"), Map.entry('g', "ɢ"), Map.entry('h', "ʜ"),
            Map.entry('i', "ɪ"), Map.entry('j', "ᴊ"), Map.entry('k', "ᴋ"), Map.entry('l', "ʟ"),
            Map.entry('m', "ᴍ"), Map.entry('n', "ɴ"), Map.entry('o', "ᴏ"), Map.entry('p', "ᴘ"),
            Map.entry('q', "ǫ"), Map.entry('r', "ʀ"), Map.entry('s', "s"), Map.entry('t', "ᴛ"),
            Map.entry('u', "ᴜ"), Map.entry('v', "ᴠ"), Map.entry('w', "ᴡ"), Map.entry('x', "x"),
            Map.entry('y', "ʏ"), Map.entry('z', "ᴢ")
        );
        map.putAll(smallCapsMap);
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), String.valueOf((char) ('A' + i)));
        }
        for (int i = 0; i < 10; i++) {
            map.put((char) ('0' + i), String.valueOf((char) ('0' + i)));
        }
        return map;
    }

    private Map<Character, String> createSuperscriptMap() {
        return Map.ofEntries(
            Map.entry('0', "⁰"), Map.entry('1', "¹"), Map.entry('2', "²"), Map.entry('3', "³"),
            Map.entry('4', "⁴"), Map.entry('5', "⁵"), Map.entry('6', "⁶"), Map.entry('7', "⁷"),
            Map.entry('8', "⁸"), Map.entry('9', "⁹"),
            Map.entry('a', "ᵃ"), Map.entry('b', "ᵇ"), Map.entry('c', "ᶜ"), Map.entry('d', "ᵈ"),
            Map.entry('e', "ᵉ"), Map.entry('f', "ᶠ"), Map.entry('g', "ᵍ"), Map.entry('h', "ʰ"),
            Map.entry('i', "ⁱ"), Map.entry('j', "ʲ"), Map.entry('k', "ᵏ"), Map.entry('l', "ˡ"),
            Map.entry('m', "ᵐ"), Map.entry('n', "ⁿ"), Map.entry('o', "ᵒ"), Map.entry('p', "ᵖ"),
            Map.entry('r', "ʳ"), Map.entry('s', "ˢ"), Map.entry('t', "ᵗ"), Map.entry('u', "ᵘ"),
            Map.entry('v', "ᵛ"), Map.entry('w', "ʷ"), Map.entry('x', "ˣ"), Map.entry('y', "ʸ"),
            Map.entry('z', "ᶻ"),
            Map.entry('A', "ᴬ"), Map.entry('B', "ᴮ"), Map.entry('D', "ᴰ"), Map.entry('E', "ᴱ"),
            Map.entry('G', "ᴳ"), Map.entry('H', "ᴴ"), Map.entry('I', "ᴵ"), Map.entry('J', "ᴶ"),
            Map.entry('K', "ᴷ"), Map.entry('L', "ᴸ"), Map.entry('M', "ᴹ"), Map.entry('N', "ᴺ"),
            Map.entry('O', "ᴼ"), Map.entry('P', "ᴾ"), Map.entry('R', "ᴿ"), Map.entry('T', "ᵀ"),
            Map.entry('U', "ᵁ"), Map.entry('V', "ⱽ"), Map.entry('W', "ᵂ"),
            Map.entry('+', "⁺"), Map.entry('-', "⁻"), Map.entry('=', "⁼"),
            Map.entry('(', "⁽"), Map.entry(')', "⁾")
        );
    }

    private Map<Character, String> createSubscriptMap() {
        return Map.ofEntries(
            Map.entry('0', "₀"), Map.entry('1', "₁"), Map.entry('2', "₂"), Map.entry('3', "₃"),
            Map.entry('4', "₄"), Map.entry('5', "₅"), Map.entry('6', "₆"), Map.entry('7', "₇"),
            Map.entry('8', "₈"), Map.entry('9', "₉"),
            Map.entry('a', "ₐ"), Map.entry('e', "ₑ"), Map.entry('h', "ₕ"), Map.entry('i', "ᵢ"),
            Map.entry('j', "ⱼ"), Map.entry('k', "ₖ"), Map.entry('l', "ₗ"), Map.entry('m', "ₘ"),
            Map.entry('n', "ₙ"), Map.entry('o', "ₒ"), Map.entry('p', "ₚ"), Map.entry('r', "ᵣ"),
            Map.entry('s', "ₛ"), Map.entry('t', "ₜ"), Map.entry('u', "ᵤ"), Map.entry('v', "ᵥ"),
            Map.entry('x', "ₓ"),
            Map.entry('+', "₊"), Map.entry('-', "₋"), Map.entry('=', "₌"),
            Map.entry('(', "₍"), Map.entry(')', "₎")
        );
    }

    private Map<Character, String> createCurrencyMap() {
        return Map.ofEntries(
            Map.entry('a', "₳"), Map.entry('b', "฿"), Map.entry('c', "₵"), Map.entry('d', "đ"),
            Map.entry('e', "€"), Map.entry('f', "ƒ"), Map.entry('l', "£"), Map.entry('n', "₦"),
            Map.entry('p', "₱"), Map.entry('r', "₹"), Map.entry('s', "$"), Map.entry('t', "₮"),
            Map.entry('w', "₩"), Map.entry('y', "¥"),
            Map.entry('A', "₳"), Map.entry('B', "฿"), Map.entry('C', "₵"), Map.entry('D', "Đ"),
            Map.entry('E', "€"), Map.entry('F', "Ƒ"), Map.entry('L', "£"), Map.entry('N', "₦"),
            Map.entry('P', "₱"), Map.entry('R', "₹"), Map.entry('S', "$"), Map.entry('T', "₮"),
            Map.entry('W', "₩"), Map.entry('Y', "¥")
        );
    }

    private Map<Character, String> createMedievalMap() {
        return Map.ofEntries(
            Map.entry('a', "α"), Map.entry('b', "ϐ"), Map.entry('c', "¢"), Map.entry('d', "∂"),
            Map.entry('e', "ε"), Map.entry('f', "ƒ"), Map.entry('g', "ց"), Map.entry('h', "հ"),
            Map.entry('i', "ì"), Map.entry('j', "ʝ"), Map.entry('k', "ҝ"), Map.entry('l', "ӏ"),
            Map.entry('m', "ʍ"), Map.entry('n', "ղ"), Map.entry('o', "σ"), Map.entry('p', "ρ"),
            Map.entry('q', "φ"), Map.entry('r', "ɾ"), Map.entry('s', "ร"), Map.entry('t', "τ"),
            Map.entry('u', "մ"), Map.entry('v', "ѵ"), Map.entry('w', "ա"), Map.entry('x', "×"),
            Map.entry('y', "ყ"), Map.entry('z', "ʐ"),
            Map.entry('A', "Ⱥ"), Map.entry('B', "Ᏸ"), Map.entry('C', "Ꮯ"), Map.entry('D', "Ꭰ"),
            Map.entry('E', "Ɛ"), Map.entry('F', "Ƒ"), Map.entry('G', "Ɠ"), Map.entry('H', "Ƕ"),
            Map.entry('I', "Ꭵ"), Map.entry('J', "Ʝ"), Map.entry('K', "Ҡ"), Map.entry('L', "Ꝉ"),
            Map.entry('M', "Ɱ"), Map.entry('N', "Ɲ"), Map.entry('O', "Ơ"), Map.entry('P', "Ᵽ"),
            Map.entry('Q', "Ҩ"), Map.entry('R', "Ɍ"), Map.entry('S', "Ꞩ"), Map.entry('T', "Ⱦ"),
            Map.entry('U', "Ա"), Map.entry('V', "Ꮙ"), Map.entry('W', "Ꮤ"), Map.entry('X', "Ӿ"),
            Map.entry('Y', "Ƴ"), Map.entry('Z', "Ȥ")
        );
    }

    private Map<Character, String> createAsianStyleMap() {
        return Map.ofEntries(
            Map.entry('a', "ﾑ"), Map.entry('b', "乃"), Map.entry('c', "ᄃ"), Map.entry('d', "り"),
            Map.entry('e', "乇"), Map.entry('f', "ｷ"), Map.entry('g', "ム"), Map.entry('h', "ん"),
            Map.entry('i', "ﾉ"), Map.entry('j', "ﾌ"), Map.entry('k', "ズ"), Map.entry('l', "ﾚ"),
            Map.entry('m', "ﾶ"), Map.entry('n', "刀"), Map.entry('o', "の"), Map.entry('p', "ｱ"),
            Map.entry('q', "ゐ"), Map.entry('r', "尺"), Map.entry('s', "丂"), Map.entry('t', "ｲ"),
            Map.entry('u', "ひ"), Map.entry('v', "ｳ"), Map.entry('w', "W"), Map.entry('x', "ﾒ"),
            Map.entry('y', "ﾘ"), Map.entry('z', "乙"),
            Map.entry('A', "ﾑ"), Map.entry('B', "乃"), Map.entry('C', "ᄃ"), Map.entry('D', "り"),
            Map.entry('E', "乇"), Map.entry('F', "ｷ"), Map.entry('G', "ム"), Map.entry('H', "ん"),
            Map.entry('I', "ﾉ"), Map.entry('J', "ﾌ"), Map.entry('K', "ズ"), Map.entry('L', "ﾚ"),
            Map.entry('M', "ﾶ"), Map.entry('N', "刀"), Map.entry('O', "の"), Map.entry('P', "ｱ"),
            Map.entry('Q', "ゐ"), Map.entry('R', "尺"), Map.entry('S', "丂"), Map.entry('T', "ｲ"),
            Map.entry('U', "ひ"), Map.entry('V', "ｳ"), Map.entry('W', "W"), Map.entry('X', "ﾒ"),
            Map.entry('Y', "ﾘ"), Map.entry('Z', "乙")
        );
    }

    private Map<Character, String> createBubbleMap() {
        Map<Character, String> map = new HashMap<>();
        String lowerChars = "ⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓞⓟⓠⓡⓢⓣⓤⓥⓦⓧⓨⓩ";
        String upperChars = "ⒶⒷⒸⒹⒺⒻⒼⒽⒾⒿⓀⓁⓂⓃⓄⓅⓆⓇⓈⓉⓊⓋⓌⓍⓎⓏ";
        for (int i = 0; i < 26; i++) {
            map.put((char) ('a' + i), String.valueOf(lowerChars.charAt(i)));
            map.put((char) ('A' + i), String.valueOf(upperChars.charAt(i)));
        }
        String digitChars = "⓪①②③④⑤⑥⑦⑧⑨";
        for (int i = 0; i < 10; i++) {
            map.put((char) ('0' + i), String.valueOf(digitChars.charAt(i)));
        }
        return map;
    }

    private Map<Character, String> createBubbleNegativeMap() {
        Map<Character, String> map = new HashMap<>();
        int bubbleNegStart = 0x1F150;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), new String(Character.toChars(bubbleNegStart + i)));
        }
        String digitChars = "⓿❶❷❸❹❺❻❼❽❾";
        for (int i = 0; i < 10; i++) {
            map.put((char) ('0' + i), String.valueOf(digitChars.charAt(i)));
        }
        return map;
    }

    private Map<Character, String> createRegionalFlagsMap() {
        Map<Character, String> map = new HashMap<>();
        int regionalStart = 0x1F1E6;
        for (int i = 0; i < 26; i++) {
            String flag = new String(Character.toChars(regionalStart + i));
            map.put((char) ('A' + i), flag);
            map.put((char) ('a' + i), flag);
        }
        return map;
    }

    private Map<Character, String> createMathBoldMap() {
        Map<Character, String> map = new HashMap<>();
        int mathBoldLowerStart = 0x1D41A;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('a' + i), new String(Character.toChars(mathBoldLowerStart + i)));
        }
        int mathBoldUpperStart = 0x1D400;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), new String(Character.toChars(mathBoldUpperStart + i)));
        }
        return map;
    }

    private Map<Character, String> createMathBoldItalicMap() {
        Map<Character, String> map = new HashMap<>();
        int mathBoldItalicLowerStart = 0x1D482;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('a' + i), new String(Character.toChars(mathBoldItalicLowerStart + i)));
        }
        int mathBoldItalicUpperStart = 0x1D468;
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), new String(Character.toChars(mathBoldItalicUpperStart + i)));
        }
        return map;
    }

    private Map<Character, String> createCurlyMap() {
        return Map.ofEntries(
            Map.entry('a', "𝒶"), Map.entry('b', "𝒷"), Map.entry('c', "𝒸"), Map.entry('d', "𝒹"),
            Map.entry('e', "𝑒"), Map.entry('f', "𝒻"), Map.entry('g', "𝑔"), Map.entry('h', "𝒽"),
            Map.entry('i', "𝒾"), Map.entry('j', "𝒿"), Map.entry('k', "𝓀"), Map.entry('l', "𝓁"),
            Map.entry('m', "𝓂"), Map.entry('n', "𝓃"), Map.entry('o', "𝑜"), Map.entry('p', "𝓅"),
            Map.entry('q', "𝓆"), Map.entry('r', "𝓇"), Map.entry('s', "𝓈"), Map.entry('t', "𝓉"),
            Map.entry('u', "𝓊"), Map.entry('v', "𝓋"), Map.entry('w', "𝓌"), Map.entry('x', "𝓍"),
            Map.entry('y', "𝓎"), Map.entry('z', "𝓏"),
            Map.entry('A', "𝒜"), Map.entry('B', "𝐵"), Map.entry('C', "𝒞"), Map.entry('D', "𝒟"),
            Map.entry('E', "𝐸"), Map.entry('F', "𝐹"), Map.entry('G', "𝒢"), Map.entry('H', "𝐻"),
            Map.entry('I', "𝐼"), Map.entry('J', "𝒥"), Map.entry('K', "𝒦"), Map.entry('L', "𝐿"),
            Map.entry('M', "𝑀"), Map.entry('N', "𝒩"), Map.entry('O', "𝒪"), Map.entry('P', "𝒫"),
            Map.entry('Q', "𝒬"), Map.entry('R', "𝑅"), Map.entry('S', "𝒮"), Map.entry('T', "𝒯"),
            Map.entry('U', "𝒰"), Map.entry('V', "𝒱"), Map.entry('W', "𝒲"), Map.entry('X', "𝒳"),
            Map.entry('Y', "𝒴"), Map.entry('Z', "𝒵")
        );
    }

    private Map<Character, String> createTinyMap() {
        return Map.ofEntries(
            Map.entry('a', "ᵃ"), Map.entry('b', "ᵇ"), Map.entry('c', "ᶜ"), Map.entry('d', "ᵈ"),
            Map.entry('e', "ᵉ"), Map.entry('f', "ᶠ"), Map.entry('g', "ᵍ"), Map.entry('h', "ʰ"),
            Map.entry('i', "ⁱ"), Map.entry('j', "ʲ"), Map.entry('k', "ᵏ"), Map.entry('l', "ˡ"),
            Map.entry('m', "ᵐ"), Map.entry('n', "ⁿ"), Map.entry('o', "ᵒ"), Map.entry('p', "ᵖ"),
            Map.entry('r', "ʳ"), Map.entry('s', "ˢ"), Map.entry('t', "ᵗ"), Map.entry('u', "ᵘ"),
            Map.entry('v', "ᵛ"), Map.entry('w', "ʷ"), Map.entry('x', "ˣ"), Map.entry('y', "ʸ"),
            Map.entry('z', "ᶻ"),
            Map.entry('A', "ᴬ"), Map.entry('B', "ᴮ"), Map.entry('C', "ᶜ"), Map.entry('D', "ᴰ"),
            Map.entry('E', "ᴱ"), Map.entry('F', "ᶠ"), Map.entry('G', "ᴳ"), Map.entry('H', "ᴴ"),
            Map.entry('I', "ᴵ"), Map.entry('J', "ᴶ"), Map.entry('K', "ᴷ"), Map.entry('L', "ᴸ"),
            Map.entry('M', "ᴹ"), Map.entry('N', "ᴺ"), Map.entry('O', "ᴼ"), Map.entry('P', "ᴾ"),
            Map.entry('R', "ᴿ"), Map.entry('S', "ˢ"), Map.entry('T', "ᵀ"), Map.entry('U', "ᵁ"),
            Map.entry('V', "ⱽ"), Map.entry('W', "ᵂ"), Map.entry('X', "ˣ"), Map.entry('Y', "ʸ"),
            Map.entry('Z', "ᶻ"),
            Map.entry('0', "⁰"), Map.entry('1', "¹"), Map.entry('2', "²"), Map.entry('3', "³"),
            Map.entry('4', "⁴"), Map.entry('5', "⁵"), Map.entry('6', "⁶"), Map.entry('7', "⁷"),
            Map.entry('8', "⁸"), Map.entry('9', "⁹"),
            Map.entry('(', "⁽"), Map.entry(')', "⁾"), Map.entry('+', "⁺"), Map.entry('-', "⁻"),
            Map.entry('=', "⁼")
        );
    }

    // ===== SPECIAL CONVERTERS =====

    private String applyDiacritic(String text, char diacritic) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            result.append(c).append(diacritic);
        }
        return result.toString();
    }

    private String toUpsideDown(String text) {
        Map<Character, Character> map = Map.ofEntries(
            Map.entry('a', 'ɐ'), Map.entry('b', 'q'), Map.entry('c', 'ɔ'), Map.entry('d', 'p'),
            Map.entry('e', 'ǝ'), Map.entry('f', 'ɟ'), Map.entry('g', 'ƃ'), Map.entry('h', 'ɥ'),
            Map.entry('i', 'ᴉ'), Map.entry('j', 'ɾ'), Map.entry('k', 'ʞ'), Map.entry('l', 'l'),
            Map.entry('m', 'ɯ'), Map.entry('n', 'u'), Map.entry('o', 'o'), Map.entry('p', 'd'),
            Map.entry('q', 'b'), Map.entry('r', 'ɹ'), Map.entry('s', 's'), Map.entry('t', 'ʇ'),
            Map.entry('u', 'n'), Map.entry('v', 'ʌ'), Map.entry('w', 'ʍ'), Map.entry('x', 'x'),
            Map.entry('y', 'ʎ'), Map.entry('z', 'z'),
            Map.entry('A', '∀'), Map.entry('B', 'q'), Map.entry('C', 'Ɔ'), Map.entry('D', 'p'),
            Map.entry('E', 'Ǝ'), Map.entry('F', 'Ⅎ'), Map.entry('G', 'פ'), Map.entry('H', 'H'),
            Map.entry('I', 'I'), Map.entry('J', 'ſ'), Map.entry('K', 'ʞ'), Map.entry('L', '˥'),
            Map.entry('M', 'W'), Map.entry('N', 'N'), Map.entry('O', 'O'), Map.entry('P', 'Ԁ'),
            Map.entry('Q', 'Ò'), Map.entry('R', 'ɹ'), Map.entry('S', 'S'), Map.entry('T', '┴'),
            Map.entry('U', '∩'), Map.entry('V', 'Λ'), Map.entry('W', 'M'), Map.entry('X', 'X'),
            Map.entry('Y', '⅄'), Map.entry('Z', 'Z'),
            Map.entry('0', '0'), Map.entry('1', 'Ɩ'), Map.entry('2', 'ᄅ'), Map.entry('3', 'Ɛ'),
            Map.entry('4', 'ㄣ'), Map.entry('5', 'ϛ'), Map.entry('6', '9'), Map.entry('7', 'ㄥ'),
            Map.entry('8', '8'), Map.entry('9', '6'),
            Map.entry('.', '˙'), Map.entry(',', '\''), Map.entry('!', '¡'), Map.entry('?', '¿'),
            Map.entry('\'', ','), Map.entry('"', '„'), Map.entry(';', '؛'),
            Map.entry('(', ')'), Map.entry(')', '(')
        );
        
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            result.append(map.getOrDefault(c, c));
        }
        return result.reverse().toString();
    }

    private String toZalgo(String text, boolean heavy) {
        String[] zalgoUp = {
            "\u030d", "\u030e", "\u0304", "\u0305", "\u033f", "\u0311", "\u0306",
            "\u0310", "\u0352", "\u0357", "\u0351", "\u0307", "\u0308", "\u030a",
            "\u0342", "\u0343", "\u0344", "\u034a", "\u034b", "\u034c", "\u0303",
            "\u0302", "\u030c", "\u0350", "\u0300", "\u0301", "\u030b", "\u030f",
            "\u0312"
        };
        
        String[] zalgoDown = {
            "\u0316", "\u0317", "\u0318", "\u0319", "\u031c", "\u031d", "\u031e",
            "\u031f", "\u0320", "\u0324", "\u0325", "\u0326", "\u0329", "\u032a",
            "\u032b", "\u032c", "\u032d", "\u032e", "\u032f", "\u0330", "\u0331",
            "\u0332", "\u0333", "\u0339", "\u033a", "\u033b", "\u033c", "\u0345",
            "\u0347", "\u0348", "\u0349", "\u034d", "\u034e", "\u0353", "\u0354",
            "\u0355", "\u0356", "\u0359", "\u035a", "\u0323"
        };
        
        String[] zalgoMid = {
            "\u0315", "\u031b", "\u0340", "\u0341", "\u0358", "\u0321", "\u0322",
            "\u0327", "\u0328", "\u0334", "\u0335", "\u0336", "\u034f", "\u035c",
            "\u035d", "\u035e", "\u035f", "\u0360", "\u0362", "\u0338", "\u0337",
            "\u0361", "\u0489"
        };
        
        int maxMarks = heavy ? 5 : 2;
        Random random = new Random();
        StringBuilder result = new StringBuilder();
        
        for (char c : text.toCharArray()) {
            result.append(c);
            
            int upCount = random.nextInt(maxMarks + 1);
            for (int i = 0; i < upCount; i++) {
                result.append(zalgoUp[random.nextInt(zalgoUp.length)]);
            }
            
            int midCount = random.nextInt(maxMarks + 1);
            for (int i = 0; i < midCount; i++) {
                result.append(zalgoMid[random.nextInt(zalgoMid.length)]);
            }
            
            int downCount = random.nextInt(maxMarks + 1);
            for (int i = 0; i < downCount; i++) {
                result.append(zalgoDown[random.nextInt(zalgoDown.length)]);
            }
        }
        
        return result.toString();
    }

    // ===== PUBLIC API =====

    public Map<String, TextStyle> getAllStyles() {
        return new HashMap<>(styles);
    }

    public TextStyle getStyle(String id) {
        return styles.get(id);
    }

    public List<String> getCategories() {
        return new ArrayList<>(categories.keySet());
    }

    public List<TextStyle> getStylesByCategory(String category) {
        List<String> styleIds = categories.get(category);
        if (styleIds == null) return new ArrayList<>();
        
        List<TextStyle> result = new ArrayList<>();
        for (String id : styleIds) {
            result.add(styles.get(id));
        }
        return result;
    }

    public int getStyleCount() {
        return styles.size();
    }
}
