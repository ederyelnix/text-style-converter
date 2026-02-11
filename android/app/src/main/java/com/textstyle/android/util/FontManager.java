package com.textstyle.android.util;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

public class FontManager {
    private static Typeface unicodeTypeface;

    public static void loadFonts(Context context) {
        try {
            unicodeTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/NotoSans-Regular.ttf");
        } catch (Exception e) {
            unicodeTypeface = Typeface.DEFAULT;
        }
    }

    public static void applyUnicodeFont(TextView textView) {
        if (unicodeTypeface != null) {
            textView.setTypeface(unicodeTypeface);
        }
    }

    public static Typeface getUnicodeTypeface() {
        return unicodeTypeface != null ? unicodeTypeface : Typeface.DEFAULT;
    }
}
