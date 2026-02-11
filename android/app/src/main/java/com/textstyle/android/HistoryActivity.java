package com.textstyle.android;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.textstyle.android.adapter.HistoryAdapter;
import com.textstyle.android.data.AppDatabase;
import com.textstyle.android.data.HistoryEntity;
import com.textstyle.android.databinding.ActivityHistoryBinding;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private ActivityHistoryBinding binding;
    private HistoryAdapter adapter;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Charger la langue sauvegardée
        loadSavedLanguage();
        
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        
        database = AppDatabase.getDatabase(this);
        
        setupRecyclerView();
        loadHistory();
        
        binding.buttonClearHistory.setOnClickListener(v -> confirmClearHistory());
    }
    
    private void loadSavedLanguage() {
        String languageCode = getSharedPreferences("Settings", MODE_PRIVATE)
            .getString("language", "en");
        
        java.util.Locale locale = new java.util.Locale(languageCode);
        java.util.Locale.setDefault(locale);
        
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);
        
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    private void setupRecyclerView() {
        adapter = new HistoryAdapter(new HistoryAdapter.OnHistoryClickListener() {
            @Override
            public void onEditClick(HistoryEntity entry) {
                finish();
            }

            @Override
            public void onCopyClick(HistoryEntity entry) {
                copyToClipboard(entry.getText());
            }
        });
        
        binding.recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewHistory.setAdapter(adapter);
    }

    private void loadHistory() {
        List<HistoryEntity> history = database.historyDao().getAllHistory();
        adapter.setHistory(history);
        
        if (history.isEmpty()) {
            binding.recyclerViewHistory.setVisibility(View.GONE);
            binding.textViewEmpty.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerViewHistory.setVisibility(View.VISIBLE);
            binding.textViewEmpty.setVisibility(View.GONE);
        }
    }

    private void confirmClearHistory() {
        new AlertDialog.Builder(this)
            .setTitle(R.string.dialog_confirmation)
            .setMessage(R.string.history_confirm_clear)
            .setPositiveButton(R.string.btn_ok, (dialog, which) -> clearHistory())
            .setNegativeButton(R.string.btn_cancel, null)
            .show();
    }

    private void clearHistory() {
        database.historyDao().clearAll();
        loadHistory();
        Toast.makeText(this, R.string.notif_history_cleared, Toast.LENGTH_SHORT).show();
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("History", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, R.string.notif_copied_simple, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
