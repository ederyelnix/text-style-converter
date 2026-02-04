package com.textstyle.controller;

import com.textstyle.model.TextStyle;
import com.textstyle.model.HistoryEntry;
import com.textstyle.util.TextStyler;
import com.textstyle.util.HistoryManager;
import com.textstyle.util.I18N;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Main controller for the Text Style Converter application.
 * Fully internationalized with support for French, English, Spanish, and Portuguese.
 */
public class TextStyleConverterController implements Initializable {
    
    // Existing FXML fields
    @FXML private TextArea textInput;
    @FXML private Button convertBtn;
    @FXML private Button clearBtn;
    @FXML private TextField searchFilter;
    @FXML private ComboBox<Integer> resultsPerPageCombo;
    @FXML private Label charCountLabel;
    @FXML private Label paginationInfoLabel;
    @FXML private FlowPane resultsContainer;
    @FXML private HBox paginationBox;
    @FXML private VBox historyPane;
    @FXML private VBox historyList;
    @FXML private Button clearHistoryBtn;
    @FXML private Button exportHistoryBtn;
    @FXML private Button toggleHistoryBtn;
    
    // I18N FXML fields
    @FXML private Text titleText;
    @FXML private Text subtitleText;
    @FXML private Text footerTitleText;
    @FXML private Text footerDisclaimerText;
    @FXML private Text footerHintText;
    @FXML private Label historyTitleLabel;
    @FXML private Label inputSectionLabel;
    @FXML private Text inputExampleText;
    @FXML private Label stylesSectionLabel;
    @FXML private Label perPageLabel;
    @FXML private ComboBox<Locale> languageCombo;
    
    private TextStyler styler;
    private HistoryManager historyManager;
    private Map<String, TextStyle> currentResults;
    private List<Map.Entry<String, TextStyle>> filteredResults;
    private int currentPage = 1;
    private int resultsPerPage = 12;
    private boolean hasGeneratedResults = false;
    
    private static final String UNICODE_FONT_FAMILY = "Noto Sans, Noto Sans Math, " +
                                                     "Noto Sans Symbols, Noto Sans Symbols 2, " +
                                                     "Noto Color Emoji, STIX Two Math";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        styler = new TextStyler();
        historyManager = new HistoryManager();
        currentResults = new HashMap<>();
        filteredResults = new ArrayList<>();
        
        setupControls();
        setupLanguageSelector();
        setupListeners();
        loadHistory();
        updateAllTexts(I18N.getCurrentLocale());
        
        Platform.runLater(() -> textInput.requestFocus());
    }

    private void setupControls() {
        resultsPerPageCombo.setItems(FXCollections.observableArrayList(6, 12, 24, 48));
        resultsPerPageCombo.setValue(12);
        
        textInput.setWrapText(true);
        searchFilter.setDisable(true);
        resultsPerPageCombo.setDisable(true);
        historyPane.setVisible(false);
        historyPane.setManaged(false);
    }

    private void setupLanguageSelector() {
        languageCombo.setItems(FXCollections.observableArrayList(I18N.getSupportedLocales()));
        languageCombo.setValue(I18N.getCurrentLocale());
        languageCombo.setCellFactory(param -> new LocaleListCell());
        languageCombo.setButtonCell(new LocaleListCell());
        
        languageCombo.setOnAction(e -> I18N.setLocale(languageCombo.getValue()));
        I18N.addListener(this::updateAllTexts);
    }

    private void setupListeners() {
        textInput.textProperty().addListener((obs, oldVal, newVal) -> updateCharCount());
        convertBtn.setOnAction(e -> convertText());
        clearBtn.setOnAction(e -> clearInput());
        
        searchFilter.textProperty().addListener((obs, oldVal, newVal) -> {
            if (hasGeneratedResults) filterAndPaginate();
        });
        
        resultsPerPageCombo.setOnAction(e -> {
            if (hasGeneratedResults) {
                resultsPerPage = resultsPerPageCombo.getValue();
                filterAndPaginate();
            }
        });
        
        toggleHistoryBtn.setOnAction(e -> toggleHistory());
        clearHistoryBtn.setOnAction(e -> clearHistory());
        exportHistoryBtn.setOnAction(e -> exportHistory());
        historyManager.addListener(this::loadHistory);
        
        textInput.setOnKeyPressed(e -> {
            if (e.isControlDown() && e.getCode().toString().equals("ENTER")) {
                convertText();
            }
        });
    }

    private void updateAllTexts(Locale newLocale) {
        titleText.setText(I18N.appTitle().toUpperCase());
        subtitleText.setText(I18N.appSubtitle());
        footerTitleText.setText(I18N.get("footer.title"));
        footerDisclaimerText.setText(I18N.get("footer.disclaimer"));
        footerHintText.setText(I18N.get("footer.hint"));
        
        historyTitleLabel.setText("📜 " + I18N.get("section.history"));
        inputSectionLabel.setText("⌨️ " + I18N.get("section.input") + " :");
        inputExampleText.setText(I18N.get("input.example"));
        stylesSectionLabel.setText("🎨 " + I18N.get("section.styles"));
        perPageLabel.setText(I18N.get("pagination.resultsPerPage"));
        
        textInput.setPromptText(I18N.get("input.placeholder"));
        searchFilter.setPromptText(I18N.get("search.placeholder"));
        
        convertBtn.setText("✨ " + I18N.btnGenerate());
        toggleHistoryBtn.setText("📜 " + I18N.get("section.history"));
        clearHistoryBtn.setText("🗑️ " + I18N.get("btn.clearHistory"));
        exportHistoryBtn.setText("📥 " + I18N.get("btn.export"));
        
        updateCharCount();
        updateUIState();
        
        if (hasGeneratedResults) updateDisplay();
        loadHistory();
    }

    private void updateCharCount() {
        int count = textInput.getText().length();
        charCountLabel.setText(I18N.charCount(count));
    }

    @FXML
    private void convertText() {
        String text = textInput.getText().trim();
        
        if (text.isEmpty()) {
            showNotification(I18N.get("notif.enterText"), "error");
            textInput.requestFocus();
            return;
        }
        
        convertBtn.setDisable(true);
        convertBtn.setText(I18N.get("btn.generating"));
        
        Task<Map<String, TextStyle>> task = new Task<>() {
            @Override
            protected Map<String, TextStyle> call() throws Exception {
                Thread.sleep(100);
                return styler.getAllStyles();
            }
        };
        
        task.setOnSucceeded(e -> {
            currentResults = task.getValue();
            filteredResults = new ArrayList<>(currentResults.entrySet());
            currentPage = 1;
            hasGeneratedResults = true;
            
            historyManager.addEntry(text);
            updateUIState();
            updateDisplay();
            
            int styleCount = filteredResults.size();
            showNotification(I18N.stylesGenerated(styleCount));
            
            convertBtn.setDisable(false);
            convertBtn.setText("✨ " + I18N.btnGenerate());
            resultsContainer.requestFocus();
        });
        
        task.setOnFailed(e -> {
            showNotification(I18N.get("notif.conversionError"), "error");
            convertBtn.setDisable(false);
            convertBtn.setText("✨ " + I18N.btnGenerate());
        });
        
        new Thread(task).start();
    }

    @FXML
    private void clearInput() {
        textInput.clear();
        textInput.requestFocus();
        updateCharCount();
        currentResults.clear();
        filteredResults.clear();
        hasGeneratedResults = false;
        currentPage = 1;
        
        displayEmptyState();
        paginationBox.getChildren().clear();
        paginationInfoLabel.setText(I18N.get("pagination.empty"));
        searchFilter.clear();
        updateUIState();
        showNotification(I18N.get("notif.textCleared"), "info");
    }

    private void updateUIState() {
        boolean hasResults = hasGeneratedResults && !filteredResults.isEmpty();
        searchFilter.setDisable(!hasResults);
        resultsPerPageCombo.setDisable(!hasResults);
        
        if (!hasResults) {
            searchFilter.clear();
            paginationInfoLabel.setText(hasGeneratedResults ? 
                I18N.get("pagination.noResults") : 
                I18N.get("pagination.empty"));
        }
    }

    private void filterAndPaginate() {
        if (!hasGeneratedResults) return;
        
        String searchTerm = searchFilter.getText().toLowerCase().trim();
        
        if (searchTerm.isEmpty()) {
            filteredResults = new ArrayList<>(currentResults.entrySet());
        } else {
            filteredResults = currentResults.entrySet().stream()
                .filter(entry -> {
                    TextStyle style = entry.getValue();
                    return style.getName().toLowerCase().contains(searchTerm) ||
                           style.getDescription().toLowerCase().contains(searchTerm) ||
                           style.getCategory().toLowerCase().contains(searchTerm);
                })
                .toList();
        }
        
        currentPage = 1;
        updateDisplay();
        
        if (filteredResults.isEmpty()) {
            showNotification(I18N.get("notif.noResultsFor", searchTerm), "info");
        }
    }

    private void updateDisplay() {
        displayResults();
        createPagination();
        updateUIState();
    }

    private void displayResults() {
        resultsContainer.getChildren().clear();
        
        if (!hasGeneratedResults || filteredResults.isEmpty()) {
            displayEmptyState();
            return;
        }
        
        int totalResults = filteredResults.size();
        int totalPages = (int) Math.ceil((double) totalResults / resultsPerPage);
        
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }
        
        int startIndex = (currentPage - 1) * resultsPerPage;
        int endIndex = Math.min(startIndex + resultsPerPage, totalResults);
        
        List<Map.Entry<String, TextStyle>> currentPageResults = 
            filteredResults.subList(startIndex, endIndex);
        
        for (Map.Entry<String, TextStyle> entry : currentPageResults) {
            String convertedText = entry.getValue().convert(textInput.getText());
            VBox styleCard = createStyleCard(entry.getKey(), entry.getValue(), convertedText);
            resultsContainer.getChildren().add(styleCard);
        }
    }

    private VBox createStyleCard(String styleId, TextStyle style, String convertedText) {
        VBox card = new VBox(10);
        card.getStyleClass().add("style-card");
        card.setPadding(new Insets(15));
        card.setMaxWidth(380);
        card.setPrefWidth(330);
        
        Label nameLabel = new Label(style.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label descLabel = new Label(style.getDescription());
        descLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 11px;");
        descLabel.setWrapText(true);
        
        String previewText = convertedText;
        final int MAX_PREVIEW_LENGTH = 50;
        if (previewText.length() > MAX_PREVIEW_LENGTH) {
            previewText = previewText.substring(0, MAX_PREVIEW_LENGTH) + "...";
        }
        
        Label previewLabel = new Label(previewText);
        previewLabel.setStyle("-fx-font-family: '" + UNICODE_FONT_FAMILY + "'; " +
                              "-fx-font-size: 16px; " +
                              "-fx-padding: 10px; " +
                              "-fx-background-color: #f8f9fa; " +
                              "-fx-background-radius: 5px; " +
                              "-fx-border-color: #dee2e6; " +
                              "-fx-border-width: 1px; " +
                              "-fx-border-radius: 5px;");
        previewLabel.setWrapText(true);
        previewLabel.setMaxHeight(60);
        previewLabel.setMinHeight(60);
        
        HBox buttonsBox = new HBox(8);
        buttonsBox.setAlignment(Pos.CENTER);
        
        Button copyBtn = new Button("📋 " + I18N.btnCopy());
        copyBtn.getStyleClass().add("copy-button");
        copyBtn.setOnAction(e -> copyToClipboard(convertedText, style.getName()));
        
        Button viewBtn = new Button("👁 " + I18N.btnView());
        viewBtn.getStyleClass().add("view-button");
        viewBtn.setOnAction(e -> showFullTextDialog(style.getName(), convertedText));
        
        buttonsBox.getChildren().addAll(copyBtn, viewBtn);
        
        Label categoryLabel = new Label(I18N.get("common.category") + ": " + style.getCategory());
        categoryLabel.setStyle("-fx-text-fill: #0066cc; -fx-font-size: 10px;");
        
        card.getChildren().addAll(nameLabel, descLabel, previewLabel, buttonsBox, categoryLabel);
        
        return card;
    }

    private void showFullTextDialog(String styleName, String fullText) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle(styleName);
        dialog.setHeaderText(I18N.get("dialog.fullText", styleName));
        
        TextArea textArea = new TextArea(fullText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(15);
        textArea.setPrefColumnCount(50);
        textArea.setStyle("-fx-font-family: '" + UNICODE_FONT_FAMILY + "'; -fx-font-size: 16px;");
        
        VBox content = new VBox(10);
        content.getChildren().add(textArea);
        
        Button copyFullBtn = new Button("📋 " + I18N.get("btn.copyAll"));
        copyFullBtn.setOnAction(e -> {
            copyToClipboard(fullText, styleName);
            dialog.close();
        });
        content.getChildren().add(copyFullBtn);
        
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    private void displayEmptyState() {
        resultsContainer.getChildren().clear();
        
        VBox emptyState = new VBox(15);
        emptyState.setAlignment(Pos.CENTER);
        emptyState.setPadding(new Insets(40));
        
        Label icon = new Label("🔤");
        icon.setStyle("-fx-font-size: 64px;");
        
        Label title = new Label(hasGeneratedResults ? 
            I18N.get("empty.noResults.title") : 
            I18N.get("empty.noText.title"));
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        Label message = new Label(hasGeneratedResults ?
            I18N.get("empty.noResults.message") :
            I18N.get("empty.noText.message"));
        message.setStyle("-fx-text-fill: gray;");
        
        Label hint = new Label(I18N.get("empty.noText.hint"));
        hint.setStyle("-fx-text-fill: #0066cc; -fx-font-style: italic;");
        
        emptyState.getChildren().addAll(icon, title, message, hint);
        
        if (hasGeneratedResults) {
            Button clearSearchBtn = new Button(I18N.get("empty.noResults.btnClear"));
            clearSearchBtn.setOnAction(e -> {
                searchFilter.clear();
                filterAndPaginate();
            });
            emptyState.getChildren().add(clearSearchBtn);
        }
        
        resultsContainer.getChildren().add(emptyState);
    }

    private void createPagination() {
        paginationBox.getChildren().clear();
        
        if (!hasGeneratedResults || filteredResults.isEmpty()) {
            paginationInfoLabel.setText(hasGeneratedResults ? 
                I18N.get("pagination.noResults") :
                I18N.get("pagination.empty"));
            return;
        }
        
        int totalResults = filteredResults.size();
        int totalPages = (int) Math.ceil((double) totalResults / resultsPerPage);
        
        if (totalPages <= 1) {
            paginationInfoLabel.setText(I18N.paginationInfo(1, 1, totalResults));
            return;
        }
        
        paginationInfoLabel.setText(I18N.paginationInfo(currentPage, totalPages, totalResults));
        
        Button prevBtn = new Button("◀");
        prevBtn.setDisable(currentPage == 1);
        prevBtn.setOnAction(e -> {
            if (currentPage > 1) {
                currentPage--;
                updateDisplay();
            }
        });
        paginationBox.getChildren().add(prevBtn);
        
        int maxVisiblePages = 5;
        int startPage = Math.max(1, currentPage - maxVisiblePages / 2);
        int endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);
        
        if (endPage - startPage + 1 < maxVisiblePages) {
            startPage = Math.max(1, endPage - maxVisiblePages + 1);
        }
        
        if (startPage > 1) {
            Button firstBtn = new Button("1");
            firstBtn.setOnAction(e -> {
                currentPage = 1;
                updateDisplay();
            });
            paginationBox.getChildren().add(firstBtn);
            
            if (startPage > 2) {
                Label ellipsis = new Label("...");
                paginationBox.getChildren().add(ellipsis);
            }
        }
        
        for (int i = startPage; i <= endPage; i++) {
            final int page = i;
            Button pageBtn = new Button(String.valueOf(i));
            if (i == currentPage) {
                pageBtn.getStyleClass().add("active");
            }
            pageBtn.setOnAction(e -> {
                currentPage = page;
                updateDisplay();
            });
            paginationBox.getChildren().add(pageBtn);
        }
        
        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                Label ellipsis = new Label("...");
                paginationBox.getChildren().add(ellipsis);
            }
            
            Button lastBtn = new Button(String.valueOf(totalPages));
            lastBtn.setOnAction(e -> {
                currentPage = totalPages;
                updateDisplay();
            });
            paginationBox.getChildren().add(lastBtn);
        }
        
        Button nextBtn = new Button("▶");
        nextBtn.setDisable(currentPage == totalPages);
        nextBtn.setOnAction(e -> {
            if (currentPage < totalPages) {
                currentPage++;
                updateDisplay();
            }
        });
        paginationBox.getChildren().add(nextBtn);
    }

    private void copyToClipboard(String text, String styleName) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
        
        showNotification(I18N.get("notif.copied", styleName));
    }

    private void toggleHistory() {
        boolean isVisible = historyPane.isVisible();
        historyPane.setVisible(!isVisible);
        historyPane.setManaged(!isVisible);
    }

    private void loadHistory() {
        historyList.getChildren().clear();
        
        List<HistoryEntry> entries = historyManager.getAllEntries();
        
        if (entries.isEmpty()) {
            VBox emptyMsg = new VBox(10);
            emptyMsg.setAlignment(Pos.CENTER);
            emptyMsg.setPadding(new Insets(20));
            
            Label icon = new Label("🕒");
            icon.setStyle("-fx-font-size: 32px;");
            Label msg = new Label(I18N.get("history.empty"));
            msg.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");
            Label hint = new Label(I18N.get("history.emptyHint"));
            hint.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");
            
            emptyMsg.getChildren().addAll(icon, msg, hint);
            historyList.getChildren().add(emptyMsg);
            return;
        }
        
        for (HistoryEntry entry : entries) {
            VBox entryBox = createHistoryEntryBox(entry);
            historyList.getChildren().add(entryBox);
        }
    }

    private VBox createHistoryEntryBox(HistoryEntry entry) {
        VBox box = new VBox(8);
        box.getStyleClass().add("history-entry");
        box.setPadding(new Insets(10));
        
        Label textLabel = new Label(entry.getPreviewText(100));
        textLabel.setWrapText(true);
        textLabel.setStyle("-fx-font-family: '" + UNICODE_FONT_FAMILY + "';");
        
        Label dateLabel = new Label(I18N.get("history.date", entry.getFormattedDate()));
        dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");
        
        HBox actions = new HBox(5);
        actions.setAlignment(Pos.CENTER_LEFT);
        
        Button loadBtn = new Button("✏️ " + I18N.get("btn.edit"));
        loadBtn.getStyleClass().add("small-button");
        loadBtn.setOnAction(e -> loadHistoryItem(entry));
        
        Button copyBtn = new Button("📋 " + I18N.get("btn.copy"));
        copyBtn.getStyleClass().add("small-button");
        copyBtn.setOnAction(e -> copyToClipboard(entry.getText(), I18N.get("common.text")));
        
        Button genBtn = new Button("✨ " + I18N.btnGenerate());
        genBtn.getStyleClass().add("small-button");
        genBtn.setOnAction(e -> generateFromHistory(entry));
        
        actions.getChildren().addAll(loadBtn, copyBtn, genBtn);
        box.getChildren().addAll(textLabel, dateLabel, actions);
        
        return box;
    }

    private void loadHistoryItem(HistoryEntry entry) {
        textInput.setText(entry.getText());
        updateCharCount();
        toggleHistory();
        textInput.requestFocus();
        showNotification(I18N.get("notif.textLoaded"), "info");
    }

    private void generateFromHistory(HistoryEntry entry) {
        textInput.setText(entry.getText());
        updateCharCount();
        toggleHistory();
        convertText();
    }

    private void clearHistory() {
        if (historyManager.size() == 0) {
            showNotification(I18N.get("notif.historyEmpty"), "info");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(I18N.get("dialog.confirmation"));
        alert.setHeaderText(I18N.get("dialog.clearHistory"));
        alert.setContentText(I18N.getPlural(
            "history.confirmClear", 
            "history.confirmClearPlural", 
            historyManager.size()
        ));
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                historyManager.clearHistory();
                showNotification(I18N.get("notif.historyCleared"));
            }
        });
    }

    private void exportHistory() {
        if (historyManager.size() == 0) {
            showNotification(I18N.get("notif.historyEmpty"), "error");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(I18N.get("history.exportTitle"));
        fileChooser.setInitialFileName("historique_text_converter_" + 
                                      java.time.LocalDate.now() + ".txt");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text files", "*.txt"));
        
        File file = fileChooser.showSaveDialog(exportHistoryBtn.getScene().getWindow());
        
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(historyManager.exportHistory());
                showNotification(I18N.get("notif.historyExported"));
            } catch (IOException e) {
                showNotification(I18N.get("notif.exportError"), "error");
            }
        }
    }

    private void showNotification(String message) {
        showNotification(message, "success");
    }

    private void showNotification(String message, String type) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notification");
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        if ("error".equals(type)) {
            alert.setAlertType(Alert.AlertType.ERROR);
        } else if ("warning".equals(type)) {
            alert.setAlertType(Alert.AlertType.WARNING);
        }
        
        alert.show();
        
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                Platform.runLater(alert::close);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private static class LocaleListCell extends ListCell<Locale> {
        @Override
        protected void updateItem(Locale locale, boolean empty) {
            super.updateItem(locale, empty);
            if (empty || locale == null) {
                setText(null);
            } else {
                String flag = getFlag(locale);
                String name = locale.getDisplayLanguage(locale);
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                setText(flag + " " + name);
            }
        }
        
        private String getFlag(Locale locale) {
            switch(locale.getLanguage()) {
                case "fr": return "🇫🇷";
                case "en": return "🇬🇧";
                case "es": return "🇪🇸";
                case "pt": return "🇵🇹";
                default: return "🌐";
            }
        }
    }
}
