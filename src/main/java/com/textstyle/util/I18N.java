package com.textstyle.util;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

/**
 * Internationalization manager for the application.
 * Handles loading and accessing translated messages with locale persistence.
 * Version 2.0.1 - Added locale preference saving
 */
public class I18N {
    private static final String BUNDLE_NAME = "i18n.messages";
    private static ResourceBundle bundle;
    private static Locale currentLocale;
    private static final List<LocaleChangeListener> listeners = new ArrayList<>();
    
    // Locale preference file
    private static final String PREFERENCES_FILE = System.getProperty("user.home") + 
                                                   "/.textstyle_preferences.txt";
    
    // Supported locales
    public static final Locale FRENCH = new Locale("fr");
    public static final Locale ENGLISH = new Locale("en");
    public static final Locale SPANISH = new Locale("es");
    public static final Locale PORTUGUESE = new Locale("pt");
    
    private static final List<Locale> SUPPORTED_LOCALES = Arrays.asList(
        FRENCH, ENGLISH, SPANISH, PORTUGUESE
    );
    
    static {
        // Initialize with saved locale or system locale or French as default
        Locale initialLocale = loadSavedLocale();
        
        if (initialLocale == null) {
            Locale systemLocale = Locale.getDefault();
            initialLocale = FRENCH; // Default
            
            for (Locale supported : SUPPORTED_LOCALES) {
                if (supported.getLanguage().equals(systemLocale.getLanguage())) {
                    initialLocale = supported;
                    break;
                }
            }
        }
        
        setLocale(initialLocale);
    }
    
    /**
     * Loads the saved locale from preferences file.
     */
    private static Locale loadSavedLocale() {
        File file = new File(PREFERENCES_FILE);
        if (!file.exists()) {
            return null;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String languageCode = reader.readLine();
            if (languageCode != null && !languageCode.trim().isEmpty()) {
                for (Locale locale : SUPPORTED_LOCALES) {
                    if (locale.getLanguage().equals(languageCode.trim())) {
                        return locale;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load locale preference: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Saves the current locale to preferences file.
     */
    private static void saveLocale(Locale locale) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PREFERENCES_FILE))) {
            writer.write(locale.getLanguage());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to save locale preference: " + e.getMessage());
        }
    }
    
    /**
     * Gets a translated message by key.
     */
    public static String get(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            System.err.println("Missing translation key: " + key);
            return "!" + key + "!";
        }
    }
    
    /**
     * Gets a translated message with parameters.
     * Uses MessageFormat for placeholders {0}, {1}, etc.
     */
    public static String get(String key, Object... params) {
        String pattern = get(key);
        try {
            return MessageFormat.format(pattern, params);
        } catch (IllegalArgumentException e) {
            System.err.println("Error formatting message: " + key);
            return pattern;
        }
    }
    
    /**
     * Gets a translated message with proper plural handling.
     */
    public static String getPlural(String singularKey, String pluralKey, int count) {
        if (count <= 1) {
            return get(singularKey, count);
        } else {
            return get(pluralKey, count);
        }
    }
    
    /**
     * Changes the current locale and saves it to preferences.
     */
    public static void setLocale(Locale locale) {
        if (!SUPPORTED_LOCALES.contains(locale)) {
            System.err.println("Unsupported locale: " + locale + ", using French");
            locale = FRENCH;
        }
        
        currentLocale = locale;
        bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
        Locale.setDefault(locale);
        
        // Save locale preference
        saveLocale(locale);
        
        notifyListeners();
    }
    
    /**
     * Gets the current locale.
     */
    public static Locale getCurrentLocale() {
        return currentLocale;
    }
    
    /**
     * Gets all supported locales.
     */
    public static List<Locale> getSupportedLocales() {
        return new ArrayList<>(SUPPORTED_LOCALES);
    }
    
    /**
     * Gets a display name for a locale.
     */
    public static String getLocaleDisplayName(Locale locale) {
        return locale.getDisplayLanguage(locale);
    }
    
    /**
     * Adds a listener for locale changes.
     */
    public static void addListener(LocaleChangeListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Removes a listener.
     */
    public static void removeListener(LocaleChangeListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notifies all listeners of locale change.
     */
    private static void notifyListeners() {
        for (LocaleChangeListener listener : listeners) {
            listener.onLocaleChanged(currentLocale);
        }
    }
    
    /**
     * Interface for listening to locale changes.
     */
    @FunctionalInterface
    public interface LocaleChangeListener {
        void onLocaleChanged(Locale newLocale);
    }
    
    // Convenience methods for common UI strings
    
    public static String appTitle() {
        return get("app.title");
    }
    
    public static String appSubtitle() {
        return get("app.subtitle");
    }
    
    public static String btnGenerate() {
        return get("btn.generate");
    }
    
    public static String btnCopy() {
        return get("btn.copy");
    }
    
    public static String btnView() {
        return get("btn.view");
    }
    
    public static String charCount(int count) {
        return getPlural("input.charCount", "input.charCountPlural", count);
    }
    
    public static String paginationInfo(int page, int total, int results) {
        String key = (results <= 1) ? "pagination.info" : "pagination.infoPlural";
        return get(key, page, total, results);
    }
    
    public static String stylesGenerated(int count) {
        return getPlural("notif.stylesGenerated", "notif.stylesGeneratedPlural", count);
    }
    
    public static String category(String categoryKey) {
        return get("category." + categoryKey);
    }
    
    public static String styleName(String styleId) {
        return get("style." + styleId);
    }
    
    public static String styleDescription(String styleId) {
        return get("style.desc." + styleId);
    }
}
