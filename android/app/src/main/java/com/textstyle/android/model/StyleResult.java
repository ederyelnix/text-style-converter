package com.textstyle.android.model;

public class StyleResult {
    private final TextStyle style;
    private final String convertedText;

    public StyleResult(TextStyle style, String convertedText) {
        this.style = style;
        this.convertedText = convertedText;
    }

    public TextStyle getStyle() {
        return style;
    }

    public String getConvertedText() {
        return convertedText;
    }
}
