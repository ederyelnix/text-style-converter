package com.textstyle.util;

import com.textstyle.model.HistoryEntry;
import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Manages the history of text conversions.
 * Handles persistence, retrieval, and management of history entries.
 */
public class HistoryManager {
    private static final int MAX_HISTORY_SIZE = 50;
    private static final String HISTORY_FILE = System.getProperty("user.home") + 
                                               "/.textstyle_history.txt";
    
    private final List<HistoryEntry> history;
    private final List<HistoryChangeListener> listeners;

    public HistoryManager() {
        this.history = new ArrayList<>();
        this.listeners = new ArrayList<>();
        loadHistory();
    }

    /**
     * Adds a new entry to the history.
     * Avoids duplicate consecutive entries.
     */
    public void addEntry(String text) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }
        
        text = text.trim();
        
        if (!history.isEmpty() && history.get(0).getText().equals(text)) {
            return;
        }
        
        HistoryEntry entry = new HistoryEntry(text);
        history.add(0, entry);
        
        if (history.size() > MAX_HISTORY_SIZE) {
            history.remove(history.size() - 1);
        }
        
        saveHistory();
        notifyListeners();
    }

    /**
     * Returns a copy of all history entries.
     */
    public List<HistoryEntry> getAllEntries() {
        return new ArrayList<>(history);
    }

    /**
     * Clears all history entries.
     */
    public void clearHistory() {
        history.clear();
        saveHistory();
        notifyListeners();
    }

    /**
     * Returns the number of entries in history.
     */
    public int size() {
        return history.size();
    }

    /**
     * Exports history to a formatted string.
     */
    public String exportHistory() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== HISTORIQUE TEXT STYLE CONVERTER ===\n");
        sb.append("Exporté le : ").append(LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n");
        sb.append("Total : ").append(history.size()).append(" entrée");
        if (history.size() > 1) sb.append("s");
        sb.append("\n\n");
        sb.append("=".repeat(50)).append("\n\n");
        
        for (int i = 0; i < history.size(); i++) {
            HistoryEntry entry = history.get(i);
            sb.append(i + 1).append(". [").append(entry.getFormattedDate()).append("]\n");
            sb.append("Texte: ").append(entry.getText()).append("\n");
            sb.append("-".repeat(50)).append("\n\n");
        }
        
        return sb.toString();
    }

    /**
     * Saves history to file.
     */
    private void saveHistory() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE))) {
            for (HistoryEntry entry : history) {
                writer.write(entry.getId() + "|" + 
                           entry.getTimestamp().toString() + "|" + 
                           entry.getText().replace("\n", "\\n"));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Failed to save history: " + e.getMessage());
        }
    }

    /**
     * Loads history from file.
     */
    private void loadHistory() {
        File file = new File(HISTORY_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|", 3);
                if (parts.length == 3) {
                    long id = Long.parseLong(parts[0]);
                    LocalDateTime timestamp = LocalDateTime.parse(parts[1]);
                    String text = parts[2].replace("\\n", "\n");
                    history.add(new HistoryEntry(id, text, timestamp));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Failed to load history: " + e.getMessage());
        }
    }

    /**
     * Adds a listener for history changes.
     */
    public void addListener(HistoryChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener.
     */
    public void removeListener(HistoryChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all listeners of history changes.
     */
    private void notifyListeners() {
        for (HistoryChangeListener listener : listeners) {
            listener.onHistoryChanged();
        }
    }

    /**
     * Interface for listening to history changes.
     */
    @FunctionalInterface
    public interface HistoryChangeListener {
        void onHistoryChanged();
    }
}
