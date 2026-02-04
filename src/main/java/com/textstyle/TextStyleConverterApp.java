package com.textstyle;

import com.textstyle.util.I18N;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

import java.io.IOException;
import java.io.InputStream;

/**
 * Main application class for Text Style Converter.
 * Initializes the JavaFX application and loads the UI.
 */
public class TextStyleConverterApp extends Application {

    private static final double SCREEN_PERCENTAGE = 0.75; // 75% of screen
    private static final int MIN_WIDTH = 900;
    private static final int MIN_HEIGHT = 650;

    @Override
    public void start(Stage primaryStage) throws IOException {
        loadFonts();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
        Parent root = loader.load();

        // Get screen dimensions
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        double width = Math.max(MIN_WIDTH, screenBounds.getWidth() * SCREEN_PERCENTAGE);
        double height = Math.max(MIN_HEIGHT, screenBounds.getHeight() * SCREEN_PERCENTAGE);

        Scene scene = new Scene(root, width, height);

        scene.getStylesheets().add(
            getClass().getResource("/css/styles.css").toExternalForm()
        );

        primaryStage.setTitle(I18N.appTitle());
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);

        // Center the window on screen
        primaryStage.setX((screenBounds.getWidth() - width) / 2);
        primaryStage.setY((screenBounds.getHeight() - height) / 2);

        setApplicationIcon(primaryStage);

        // Update title when language changes
        I18N.addListener(locale -> primaryStage.setTitle(I18N.appTitle()));

        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Application closed properly");
        });

        primaryStage.show();

        System.out.println("=== Text Style Converter Started ===");
        System.out.println("Window: " + (int) width + "x" + (int) height);
        System.out.println("Language: " + I18N.getCurrentLocale().getDisplayLanguage());
        System.out.println("Styles loaded: 43+");
    }

    /**
     * Loads Unicode fonts for proper character display.
     */
    private void loadFonts() {
        String[] fontPaths = {
            "/polices/NotoSans-Regular.ttf",
            "/polices/NotoSansMath-Regular.ttf",
            "/polices/NotoSansSymbols-Regular.ttf",
            "/polices/NotoSansSymbols2-Regular.ttf",
            "/polices/STIXTwoMath-Regular.ttf"
        };

        for (String fontPath : fontPaths) {
            try (InputStream fontStream = getClass().getResourceAsStream(fontPath)) {
                if (fontStream != null) {
                    javafx.scene.text.Font.loadFont(fontStream, 14);
                    System.out.println("Font loaded: " + fontPath);
                } else {
                    System.out.println("Font not found: " + fontPath +
                                     " (using system fonts)");
                }
            } catch (IOException e) {
                System.err.println("Font loading error: " + fontPath);
            }
        }
    }

    /**
     * Sets the application icon.
     */
    private void setApplicationIcon(Stage stage) {
        try {
            InputStream iconStream = getClass().getResourceAsStream("/images/icon.png");
            if (iconStream != null) {
                Image icon = new Image(iconStream);
                stage.getIcons().add(icon);
            }
        } catch (Exception e) {
            System.out.println("Icon not found, using default icon");
        }
    }

    /**
     * Application entry point.
     */
    public static void main(String[] args) {
        System.out.println("Starting Text Style Converter...");
        System.out.println("Java Version: " + System.getProperty("java.version"));
        System.out.println("JavaFX Version: " + System.getProperty("javafx.version"));

        launch(args);
    }
}
