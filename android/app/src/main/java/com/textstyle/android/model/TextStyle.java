package com.textstyle.android.model;

public class TextStyle {
    private final String id;
    private final String name;
    private final String description;
    private final String category;
    private final StyleConverter converter;

    public TextStyle(String id, String name, String description, String category, StyleConverter converter) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.converter = converter;
    }

    public String convert(String text) {
        return converter.convert(text);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public interface StyleConverter {
        String convert(String text);
    }
}
