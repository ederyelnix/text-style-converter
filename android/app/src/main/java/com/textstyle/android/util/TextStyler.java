package com.textstyle.android.util;

import com.textstyle.android.model.TextStyle;
import java.util.*;

public class TextStyler {
    private final Map<String, TextStyle> styles;

    public TextStyler() {
        this.styles = new LinkedHashMap<>();
        initializeAllStyles();
    }

    private void initializeAllStyles() {
        // SERIF STYLES (4 variants)
        addStyle("serifNormal", "Serif Normal", "Text with serifs", "Serif", createSerifNormalMap());
        addStyle("serifBold", "Serif Bold", "Bold serif style", "Serif", createBoldSerifMap());
        addStyle("serifItalic", "Serif Italic", "Italic serif", "Serif", createItalicMap());
        addStyle("serifBoldItalic", "Serif Bold Italic", "Bold and italic", "Serif", createBoldItalicMap());

        // SANS SERIF STYLES (4 variants)
        addStyle("sansSerifNormal", "Sans Serif Normal", "Without serifs", "Sans-Serif", createSansSerifMap());
        addStyle("sansSerifBold", "Sans Serif Bold", "Bold sans serif", "Sans-Serif", createSansSerifBoldMap());
        addStyle("sansSerifItalic", "Sans Serif Italic", "Italic sans serif", "Sans-Serif", createSansSerifItalicMap());
        addStyle("sansSerifBoldItalic", "Sans Serif Bold Italic", "Bold italic sans", "Sans-Serif", createSansSerifBoldItalicMap());

        // SCRIPT STYLES (2 variants)
        addStyle("scriptNormal", "Script", "Cursive writing style", "Script", createScriptMap());
        addStyle("scriptBold", "Script Bold", "Bold script", "Script", createBoldScriptMap());

        // FRAKTUR STYLES (2 variants)
        addStyle("frakturNormal", "Fraktur", "Gothic German style", "Fraktur", createFrakturMap());
        addStyle("frakturBold", "Fraktur Bold", "Bold gothic", "Fraktur", createBoldFrakturMap());

        // MONOSPACE
        addStyle("monospace", "Monospace", "Fixed-width font", "Monospace", createMonospaceMap());

        // DOUBLE STRUCK
        addStyle("doubleStruck", "Double Struck", "Mathematical style", "Mathematical", createDoubleStruckMap());

        // CIRCLED STYLES (2 variants)
        addStyle("circled", "Circled", "Letters in circles", "Circled", createCircledMap());
        addStyle("circledNegative", "Circled Negative", "White on black circles", "Circled", createCircledNegativeMap());

        // SQUARED STYLES (2 variants)
        addStyle("squared", "Squared", "Letters in squares", "Squared", createSquaredMap());
        addStyle("squaredNegative", "Squared Negative", "White on black squares", "Squared", createSquaredNegativeMap());

        // PARENTHESIZED
        addStyle("parenthesized", "Parenthesized", "Letters in parentheses", "Decorative", createParenthesizedMap());

        // FULLWIDTH
        addStyle("fullwidth", "Fullwidth", "Asian-style width", "Fullwidth", createFullwidthMap());

        // SMALL CAPS
        addStyle("smallCaps", "Small Caps", "Small capital letters", "Caps", createSmallCapsMap());

        // SUPERSCRIPT & SUBSCRIPT
        addStyle("superscript", "Superscript", "Superscript text", "Mathematical", createSuperscriptMap());
        addStyle("subscript", "Subscript", "Subscript text", "Mathematical", createSubscriptMap());

        // SPECIAL STYLES
        addStyle("currency", "Currency", "Currency symbols", "Special", createCurrencyMap());
        addStyle("medieval", "Medieval", "Ancient style", "Decorative", createMedievalMap());
        addStyle("asianStyle", "Asian Style", "CJK-style characters", "Fullwidth", createAsianStyleMap());

        // BUBBLE STYLES (2 variants)
        addStyle("bubble", "Bubble", "Bubble text", "Circled", createBubbleMap());
        addStyle("bubbleNegative", "Bubble Negative", "Black bubbles", "Circled", createBubbleNegativeMap());

        // REGIONAL FLAGS
        addStyle("regionalFlags", "Regional Flags", "Letters as flag emoji", "Special", createRegionalFlagsMap());

        // MATH STYLES (2 variants)
        addStyle("mathBold", "Math Bold", "Bold mathematical", "Mathematical", createMathBoldMap());
        addStyle("mathBoldItalic", "Math Bold Italic", "Bold italic math", "Mathematical", createMathBoldItalicMap());

        // CURLY
        addStyle("curly", "Curly", "Curly cursive style", "Script", createCurlyMap());

        // TINY
        addStyle("tiny", "Tiny", "ᵗⁱⁿʸ ᵗᵉˣᵗ", "Mathematical", createTinyMap());

        // DECORATIONS - using custom converters
        addStyleWithConverter("strikethrough", "Strikethrough", "Text with line through", "Decoration", text -> applyDiacritic(text, '\u0336'));
        addStyleWithConverter("underline", "Underline", "Underlined text", "Decoration", text -> applyDiacritic(text, '\u0332'));
        addStyleWithConverter("overline", "Overline", "Text with line above", "Decoration", text -> applyDiacritic(text, '\u0305'));
        addStyleWithConverter("doubleUnderline", "Double Underline", "Double underlined", "Decoration", text -> applyDiacritic(text, '\u0333'));
        addStyleWithConverter("slashed", "Slashed", "Diagonal slash", "Decoration", text -> applyDiacritic(text, '\u0338'));

        // UPSIDE DOWN
        addStyleWithConverter("upsideDown", "Upside Down", "Vertically flipped", "Transform", this::toUpsideDown);

        // REVERSED
        addStyleWithConverter("reversed", "Reversed", "Mirrored text", "Transform", text -> new StringBuilder(text).reverse().toString());

        // WIDE
        addStyleWithConverter("wide", "Wide", "W i d e   t e x t", "Transform", text -> String.join(" ", text.split("")));

        // CUTE/KAWAII
        addStyleWithConverter("cute", "Cute/Kawaii", "Cute with decorations", "Decorative", text -> {
            String[] decorations = {"✧", "♡", "✿", "❀", "⊹", "˚", "✩", "★", "☆"};
            Random rand = new Random();
            String deco1 = decorations[rand.nextInt(decorations.length)];
            String deco2 = decorations[rand.nextInt(decorations.length)];
            return deco1 + " " + text + " " + deco2;
        });

        // ZALGO (2 variants)
        addStyleWithConverter("zalgoLight", "Zalgo Light", "Light glitch effect", "Glitch", text -> toZalgo(text, false));
        addStyleWithConverter("zalgoHeavy", "Zalgo Heavy", "Heavy glitch effect", "Glitch", text -> toZalgo(text, true));
    }

    private void addStyle(String id, String name, String description, String category, Map<Character, String> charMap) {
        styles.put(id, new TextStyle(id, name, description, category, text -> convertWithMap(text, charMap)));
    }

    private void addStyleWithConverter(String id, String name, String description, String category, TextStyle.StyleConverter converter) {
        styles.put(id, new TextStyle(id, name, description, category, converter));
    }

    private String convertWithMap(String text, Map<Character, String> map) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            String converted = map.get(c);
            result.append(converted != null ? converted : c);
        }
        return result.toString();
    }

    public Map<String, TextStyle> getAllStyles() {
        return new HashMap<>(styles);
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
        map.put('a', "ᴀ"); map.put('b', "ʙ"); map.put('c', "ᴄ"); map.put('d', "ᴅ");
        map.put('e', "ᴇ"); map.put('f', "ꜰ"); map.put('g', "ɢ"); map.put('h', "ʜ");
        map.put('i', "ɪ"); map.put('j', "ᴊ"); map.put('k', "ᴋ"); map.put('l', "ʟ");
        map.put('m', "ᴍ"); map.put('n', "ɴ"); map.put('o', "ᴏ"); map.put('p', "ᴘ");
        map.put('q', "ǫ"); map.put('r', "ʀ"); map.put('s', "s"); map.put('t', "ᴛ");
        map.put('u', "ᴜ"); map.put('v', "ᴠ"); map.put('w', "ᴡ"); map.put('x', "x");
        map.put('y', "ʏ"); map.put('z', "ᴢ");
        for (int i = 0; i < 26; i++) {
            map.put((char) ('A' + i), String.valueOf((char) ('A' + i)));
        }
        for (int i = 0; i < 10; i++) {
            map.put((char) ('0' + i), String.valueOf((char) ('0' + i)));
        }
        return map;
    }

    private Map<Character, String> createSuperscriptMap() {
        Map<Character, String> map = new HashMap<>();
        map.put('0', "⁰"); map.put('1', "¹"); map.put('2', "²"); map.put('3', "³");
        map.put('4', "⁴"); map.put('5', "⁵"); map.put('6', "⁶"); map.put('7', "⁷");
        map.put('8', "⁸"); map.put('9', "⁹");
        map.put('a', "ᵃ"); map.put('b', "ᵇ"); map.put('c', "ᶜ"); map.put('d', "ᵈ");
        map.put('e', "ᵉ"); map.put('f', "ᶠ"); map.put('g', "ᵍ"); map.put('h', "ʰ");
        map.put('i', "ⁱ"); map.put('j', "ʲ"); map.put('k', "ᵏ"); map.put('l', "ˡ");
        map.put('m', "ᵐ"); map.put('n', "ⁿ"); map.put('o', "ᵒ"); map.put('p', "ᵖ");
        map.put('r', "ʳ"); map.put('s', "ˢ"); map.put('t', "ᵗ"); map.put('u', "ᵘ");
        map.put('v', "ᵛ"); map.put('w', "ʷ"); map.put('x', "ˣ"); map.put('y', "ʸ");
        map.put('z', "ᶻ");
        return map;
    }

    private Map<Character, String> createSubscriptMap() {
        Map<Character, String> map = new HashMap<>();
        map.put('0', "₀"); map.put('1', "₁"); map.put('2', "₂"); map.put('3', "₃");
        map.put('4', "₄"); map.put('5', "₅"); map.put('6', "₆"); map.put('7', "₇");
        map.put('8', "₈"); map.put('9', "₉");
        map.put('a', "ₐ"); map.put('e', "ₑ"); map.put('h', "ₕ"); map.put('i', "ᵢ");
        map.put('j', "ⱼ"); map.put('k', "ₖ"); map.put('l', "ₗ"); map.put('m', "ₘ");
        map.put('n', "ₙ"); map.put('o', "ₒ"); map.put('p', "ₚ"); map.put('r', "ᵣ");
        map.put('s', "ₛ"); map.put('t', "ₜ"); map.put('u', "ᵤ"); map.put('v', "ᵥ");
        map.put('x', "ₓ");
        return map;
    }

    private Map<Character, String> createCurrencyMap() {
        Map<Character, String> map = new HashMap<>();
        map.put('a', "₳"); map.put('b', "฿"); map.put('c', "₵"); map.put('d', "đ");
        map.put('e', "€"); map.put('f', "ƒ"); map.put('l', "£"); map.put('n', "₦");
        map.put('p', "₱"); map.put('r', "₹"); map.put('s', "$"); map.put('t', "₮");
        map.put('w', "₩"); map.put('y', "¥");
        map.put('A', "₳"); map.put('B', "฿"); map.put('C', "₵"); map.put('D', "Đ");
        map.put('E', "€"); map.put('F', "Ƒ"); map.put('L', "£"); map.put('N', "₦");
        map.put('P', "₱"); map.put('R', "₹"); map.put('S', "$"); map.put('T', "₮");
        map.put('W', "₩"); map.put('Y', "¥");
        return map;
    }

    private Map<Character, String> createMedievalMap() {
        Map<Character, String> map = new HashMap<>();
        map.put('a', "α"); map.put('b', "ϐ"); map.put('c', "¢"); map.put('d', "∂");
        map.put('e', "ε"); map.put('f', "ƒ"); map.put('g', "ց"); map.put('h', "հ");
        map.put('i', "ì"); map.put('j', "ʝ"); map.put('k', "ҝ"); map.put('l', "ӏ");
        map.put('m', "ʍ"); map.put('n', "ղ"); map.put('o', "σ"); map.put('p', "ρ");
        map.put('q', "φ"); map.put('r', "ɾ"); map.put('s', "ร"); map.put('t', "τ");
        map.put('u', "մ"); map.put('v', "ѵ"); map.put('w', "ա"); map.put('x', "×");
        map.put('y', "ყ"); map.put('z', "ʐ");
        map.put('A', "Ⱥ"); map.put('B', "Ᏸ"); map.put('C', "Ꮯ"); map.put('D', "Ꭰ");
        map.put('E', "Ɛ"); map.put('F', "Ƒ"); map.put('G', "Ɠ"); map.put('H', "Ƕ");
        map.put('I', "Ꭵ"); map.put('J', "Ʝ"); map.put('K', "Ҡ"); map.put('L', "Ꝉ");
        map.put('M', "Ɱ"); map.put('N', "Ɲ"); map.put('O', "Ơ"); map.put('P', "Ᵽ");
        map.put('Q', "Ҩ"); map.put('R', "Ɍ"); map.put('S', "Ꞩ"); map.put('T', "Ⱦ");
        map.put('U', "Ա"); map.put('V', "Ꮙ"); map.put('W', "Ꮤ"); map.put('X', "Ӿ");
        map.put('Y', "Ƴ"); map.put('Z', "Ȥ");
        return map;
    }

    private Map<Character, String> createAsianStyleMap() {
        Map<Character, String> map = new HashMap<>();
        map.put('a', "ﾑ"); map.put('b', "乃"); map.put('c', "ᄃ"); map.put('d', "り");
        map.put('e', "乇"); map.put('f', "ｷ"); map.put('g', "ム"); map.put('h', "ん");
        map.put('i', "ﾉ"); map.put('j', "ﾌ"); map.put('k', "ズ"); map.put('l', "ﾚ");
        map.put('m', "ﾶ"); map.put('n', "刀"); map.put('o', "の"); map.put('p', "ｱ");
        map.put('q', "ゐ"); map.put('r', "尺"); map.put('s', "丂"); map.put('t', "ｲ");
        map.put('u', "ひ"); map.put('v', "ｳ"); map.put('w', "W"); map.put('x', "ﾒ");
        map.put('y', "ﾘ"); map.put('z', "乙");
        map.put('A', "ﾑ"); map.put('B', "乃"); map.put('C', "ᄃ"); map.put('D', "り");
        map.put('E', "乇"); map.put('F', "ｷ"); map.put('G', "ム"); map.put('H', "ん");
        map.put('I', "ﾉ"); map.put('J', "ﾌ"); map.put('K', "ズ"); map.put('L', "ﾚ");
        map.put('M', "ﾶ"); map.put('N', "刀"); map.put('O', "の"); map.put('P', "ｱ");
        map.put('Q', "ゐ"); map.put('R', "尺"); map.put('S', "丂"); map.put('T', "ｲ");
        map.put('U', "ひ"); map.put('V', "ｳ"); map.put('W', "W"); map.put('X', "ﾒ");
        map.put('Y', "ﾘ"); map.put('Z', "乙");
        return map;
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
        Map<Character, String> map = new HashMap<>();
        map.put('a', "𝒶"); map.put('b', "𝒷"); map.put('c', "𝒸"); map.put('d', "𝒹");
        map.put('e', "𝑒"); map.put('f', "𝒻"); map.put('g', "𝑔"); map.put('h', "𝒽");
        map.put('i', "𝒾"); map.put('j', "𝒿"); map.put('k', "𝓀"); map.put('l', "𝓁");
        map.put('m', "𝓂"); map.put('n', "𝓃"); map.put('o', "𝑜"); map.put('p', "𝓅");
        map.put('q', "𝓆"); map.put('r', "𝓇"); map.put('s', "𝓈"); map.put('t', "𝓉");
        map.put('u', "𝓊"); map.put('v', "𝓋"); map.put('w', "𝓌"); map.put('x', "𝓍");
        map.put('y', "𝓎"); map.put('z', "𝓏");
        map.put('A', "𝒜"); map.put('B', "𝐵"); map.put('C', "𝒞"); map.put('D', "𝒟");
        map.put('E', "𝐸"); map.put('F', "𝐹"); map.put('G', "𝒢"); map.put('H', "𝐻");
        map.put('I', "𝐼"); map.put('J', "𝒥"); map.put('K', "𝒦"); map.put('L', "𝐿");
        map.put('M', "𝑀"); map.put('N', "𝒩"); map.put('O', "𝒪"); map.put('P', "𝒫");
        map.put('Q', "𝒬"); map.put('R', "𝑅"); map.put('S', "𝒮"); map.put('T', "𝒯");
        map.put('U', "𝒰"); map.put('V', "𝒱"); map.put('W', "𝒲"); map.put('X', "𝒳");
        map.put('Y', "𝒴"); map.put('Z', "𝒵");
        return map;
    }

    private Map<Character, String> createTinyMap() {
        Map<Character, String> map = new HashMap<>();
        map.put('a', "ᵃ"); map.put('b', "ᵇ"); map.put('c', "ᶜ"); map.put('d', "ᵈ");
        map.put('e', "ᵉ"); map.put('f', "ᶠ"); map.put('g', "ᵍ"); map.put('h', "ʰ");
        map.put('i', "ⁱ"); map.put('j', "ʲ"); map.put('k', "ᵏ"); map.put('l', "ˡ");
        map.put('m', "ᵐ"); map.put('n', "ⁿ"); map.put('o', "ᵒ"); map.put('p', "ᵖ");
        map.put('r', "ʳ"); map.put('s', "ˢ"); map.put('t', "ᵗ"); map.put('u', "ᵘ");
        map.put('v', "ᵛ"); map.put('w', "ʷ"); map.put('x', "ˣ"); map.put('y', "ʸ");
        map.put('z', "ᶻ");
        map.put('A', "ᴬ"); map.put('B', "ᴮ"); map.put('C', "ᶜ"); map.put('D', "ᴰ");
        map.put('E', "ᴱ"); map.put('F', "ᶠ"); map.put('G', "ᴳ"); map.put('H', "ᴴ");
        map.put('I', "ᴵ"); map.put('J', "ᴶ"); map.put('K', "ᴷ"); map.put('L', "ᴸ");
        map.put('M', "ᴹ"); map.put('N', "ᴺ"); map.put('O', "ᴼ"); map.put('P', "ᴾ");
        map.put('R', "ᴿ"); map.put('S', "ˢ"); map.put('T', "ᵀ"); map.put('U', "ᵁ");
        map.put('V', "ⱽ"); map.put('W', "ᵂ"); map.put('X', "ˣ"); map.put('Y', "ʸ");
        map.put('Z', "ᶻ");
        map.put('0', "⁰"); map.put('1', "¹"); map.put('2', "²"); map.put('3', "³");
        map.put('4', "⁴"); map.put('5', "⁵"); map.put('6', "⁶"); map.put('7', "⁷");
        map.put('8', "⁸"); map.put('9', "⁹");
        map.put('(', "⁽"); map.put(')', "⁾"); map.put('+', "⁺"); map.put('-', "⁻");
        map.put('=', "⁼");
        return map;
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
        Map<Character, Character> map = new HashMap<>();
        map.put('a', 'ɐ'); map.put('b', 'q'); map.put('c', 'ɔ'); map.put('d', 'p');
        map.put('e', 'ǝ'); map.put('f', 'ɟ'); map.put('g', 'ƃ'); map.put('h', 'ɥ');
        map.put('i', 'ᴉ'); map.put('j', 'ɾ'); map.put('k', 'ʞ'); map.put('l', 'l');
        map.put('m', 'ɯ'); map.put('n', 'u'); map.put('o', 'o'); map.put('p', 'd');
        map.put('q', 'b'); map.put('r', 'ɹ'); map.put('s', 's'); map.put('t', 'ʇ');
        map.put('u', 'n'); map.put('v', 'ʌ'); map.put('w', 'ʍ'); map.put('x', 'x');
        map.put('y', 'ʎ'); map.put('z', 'z');
        map.put('A', '∀'); map.put('B', 'q'); map.put('C', 'Ɔ'); map.put('D', 'p');
        map.put('E', 'Ǝ'); map.put('F', 'Ⅎ'); map.put('G', 'פ'); map.put('H', 'H');
        map.put('I', 'I'); map.put('J', 'ſ'); map.put('K', 'ʞ'); map.put('L', '˥');
        map.put('M', 'W'); map.put('N', 'N'); map.put('O', 'O'); map.put('P', 'Ԁ');
        map.put('Q', 'Ò'); map.put('R', 'ɹ'); map.put('S', 'S'); map.put('T', '┴');
        map.put('U', '∩'); map.put('V', 'Λ'); map.put('W', 'M'); map.put('X', 'X');
        map.put('Y', '⅄'); map.put('Z', 'Z');
        map.put('0', '0'); map.put('1', 'Ɩ'); map.put('2', 'ᄅ'); map.put('3', 'Ɛ');
        map.put('4', 'ㄣ'); map.put('5', 'ϛ'); map.put('6', '9'); map.put('7', 'ㄥ');
        map.put('8', '8'); map.put('9', '6');
        map.put('.', '˙'); map.put(',', '\''); map.put('!', '¡'); map.put('?', '¿');
        map.put('\'', ','); map.put('"', '„'); map.put(';', '؛');
        map.put('(', ')'); map.put(')', '(');
        
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
}
