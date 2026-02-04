package com.textstyle.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single entry in the text conversion history.
 */
public class HistoryEntry {
    private final long id;
    private final String text;
    private final LocalDateTime timestamp;
    
    private static final DateTimeFormatter FORMATTER = 
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public HistoryEntry(long id, String text, LocalDateTime timestamp) {
        this.id = id;
        this.text = text;
        this.timestamp = timestamp;
    }

    public HistoryEntry(String text) {
        this(System.currentTimeMillis(), text, LocalDateTime.now());
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedDate() {
        return timestamp.format(FORMATTER);
    }

    public String getPreviewText(int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "...";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof HistoryEntry)) return false;
        HistoryEntry other = (HistoryEntry) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", getFormattedDate(), getPreviewText(50));
    }
}
