package com.textstyle.model;

import com.textstyle.util.I18N;
import java.util.Map;
import java.util.HashMap;

/**
 * Represents a text style with Unicode character mappings.
 * Each style contains conversion logic for transforming standard text into styled Unicode.
 * Names and descriptions are loaded from I18N resource bundles.
 */
public class TextStyle {
    private final String id;
    private final String icon;
    private final String category;
    private final Map<Character, String> charMap;
    private final StyleConverter converter;

    public TextStyle(String id, String icon, String category, Map<Character, String> charMap) {
        this.id = id;
        this.icon = icon;
        this.category = category;
        this.charMap = charMap;
        this.converter = new MapBasedConverter(charMap);
    }

    public TextStyle(String id, String icon, String category, StyleConverter converter) {
        this.id = id;
        this.icon = icon;
        this.category = category;
        this.charMap = new HashMap<>();
        this.converter = converter;
    }

    public String convert(String text) {
        return converter.convert(text);
    }

    // Getters with I18N support
    public String getId() { 
        return id; 
    }
    
    public String getName() { 
        return I18N.styleName(id); 
    }
    
    public String getIcon() { 
        return icon; 
    }
    
    public String getDescription() { 
        return I18N.styleDescription(id); 
    }
    
    public String getCategory() { 
        return I18N.category(category); 
    }
    
    public String getCategoryKey() {
        return category;
    }

    /**
     * Interface for style conversion strategies
     */
    @FunctionalInterface
    public interface StyleConverter {
        String convert(String text);
    }

    /**
     * Default converter using character mapping
     */
    private static class MapBasedConverter implements StyleConverter {
        private final Map<Character, String> map;

        public MapBasedConverter(Map<Character, String> map) {
            this.map = map;
        }

        @Override
        public String convert(String text) {
            StringBuilder result = new StringBuilder();
            for (char c : text.toCharArray()) {
                String converted = map.get(c);
                result.append(converted != null ? converted : c);
            }
            return result.toString();
        }
    }
}
