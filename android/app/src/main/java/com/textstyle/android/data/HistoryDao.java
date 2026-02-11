package com.textstyle.android.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY timestamp DESC LIMIT 50")
    List<HistoryEntity> getAllHistory();

    @Insert
    void insert(HistoryEntity entry);

    @Query("DELETE FROM history")
    void clearAll();

    @Query("DELETE FROM history WHERE id NOT IN (SELECT id FROM history ORDER BY timestamp DESC LIMIT 50)")
    void trimToLimit();
}
