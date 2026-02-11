package com.textstyle.android;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import com.textstyle.android.adapter.StylesAdapter;
import com.textstyle.android.data.AppDatabase;
import com.textstyle.android.data.HistoryEntity;
import com.textstyle.android.databinding.ActivityMainBinding;
import com.textstyle.android.model.StyleResult;
import com.textstyle.android.model.TextStyle;
import com.textstyle.android.util.FontManager;
import com.textstyle.android.util.TextStyler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private TextStyler styler;
    private StylesAdapter adapter;
    private List<StyleResult> allResults = new ArrayList<>();
    private List<StyleResult> filteredResults = new ArrayList<>();
    private int currentPage = 1;
    private int resultsPerPage = 12;
    private AppDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Charger la langue sauvegardée
        loadSavedLanguage();
        
        FontManager.loadFonts(this);
        
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        setSupportActionBar(binding.toolbar);
        
        styler = new TextStyler();
        database = AppDatabase.getDatabase(this);
        
        setupUI();
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

    private void setupUI() {
        setupRecyclerView();
        setupInputListeners();
        setupPagination();
        setupSearchFilter();
        updateCharCount();
        updateEmptyState(true);
        
        // Masquer les boutons de pagination au démarrage
        binding.buttonPrevPage.setVisibility(View.GONE);
        binding.buttonNextPage.setVisibility(View.GONE);
    }

    private void setupRecyclerView() {
        adapter = new StylesAdapter(new StylesAdapter.OnStyleClickListener() {
            @Override
            public void onCopyClick(StyleResult result) {
                copyToClipboard(result.getConvertedText(), result.getStyle().getName());
            }

            @Override
            public void onViewClick(StyleResult result) {
                showFullTextDialog(result);
            }
        });
        
        binding.recyclerViewStyles.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerViewStyles.setAdapter(adapter);
    }

    private void setupInputListeners() {
        binding.editTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateCharCount();
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.buttonGenerate.setOnClickListener(v -> generateStyles());
        binding.buttonClear.setOnClickListener(v -> clearInput());
    }

    private void setupPagination() {
        Integer[] items = {6, 12, 24, 48};
        ArrayAdapter<Integer> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPerPage.setAdapter(spinnerAdapter);
        binding.spinnerPerPage.setSelection(1);
        
        binding.spinnerPerPage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                resultsPerPage = items[position];
                currentPage = 1;
                updateDisplay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        binding.buttonPrevPage.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                updateDisplay();
            }
        });

        binding.buttonNextPage.setOnClickListener(v -> {
            int totalPages = (int) Math.ceil((double) filteredResults.size() / resultsPerPage);
            if (currentPage < totalPages) {
                currentPage++;
                updateDisplay();
            }
        });
    }

    private void setupSearchFilter() {
        binding.editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStyles(s.toString());
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void generateStyles() {
        String text = binding.editTextInput.getText().toString().trim();
        
        if (text.isEmpty()) {
            Toast.makeText(this, R.string.notif_enter_text, Toast.LENGTH_SHORT).show();
            return;
        }
        
        allResults.clear();
        Map<String, TextStyle> styles = styler.getAllStyles();
        
        for (Map.Entry<String, TextStyle> entry : styles.entrySet()) {
            String convertedText = entry.getValue().convert(text);
            allResults.add(new StyleResult(entry.getValue(), convertedText));
        }
        
        filteredResults = new ArrayList<>(allResults);
        currentPage = 1;
        
        database.historyDao().insert(new HistoryEntity(text, System.currentTimeMillis()));
        
        updateDisplay();
        Toast.makeText(this, getString(R.string.notif_styles_generated, allResults.size()), Toast.LENGTH_SHORT).show();
    }

    private void filterStyles(String query) {
        if (allResults.isEmpty()) return;
        
        if (query.isEmpty()) {
            filteredResults = new ArrayList<>(allResults);
        } else {
            filteredResults.clear();
            String lowerQuery = query.toLowerCase();
            
            for (StyleResult result : allResults) {
                if (result.getStyle().getName().toLowerCase().contains(lowerQuery) ||
                    result.getStyle().getDescription().toLowerCase().contains(lowerQuery) ||
                    result.getStyle().getCategory().toLowerCase().contains(lowerQuery)) {
                    filteredResults.add(result);
                }
            }
        }
        
        currentPage = 1;
        updateDisplay();
    }

    private void updateDisplay() {
        if (filteredResults.isEmpty()) {
            updateEmptyState(true);
            updatePaginationInfo();
            binding.buttonPrevPage.setVisibility(View.GONE);
            binding.buttonNextPage.setVisibility(View.GONE);
            return;
        }
        
        updateEmptyState(false);
        
        int total = filteredResults.size();
        int totalPages = (int) Math.ceil((double) total / resultsPerPage);
        
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }
        
        int startIndex = (currentPage - 1) * resultsPerPage;
        int endIndex = Math.min(startIndex + resultsPerPage, total);
        
        List<StyleResult> pageResults = filteredResults.subList(startIndex, endIndex);
        adapter.setResults(pageResults);
        
        updatePaginationInfo();
        
        // Afficher/masquer les boutons de pagination
        if (totalPages > 1) {
            binding.buttonPrevPage.setVisibility(View.VISIBLE);
            binding.buttonNextPage.setVisibility(View.VISIBLE);
            binding.buttonPrevPage.setEnabled(currentPage > 1);
            binding.buttonNextPage.setEnabled(currentPage < totalPages);
        } else {
            binding.buttonPrevPage.setVisibility(View.GONE);
            binding.buttonNextPage.setVisibility(View.GONE);
        }
    }

    private void updatePaginationInfo() {
        if (filteredResults.isEmpty()) {
            binding.textViewPaginationInfo.setText(R.string.pagination_empty);
        } else {
            int totalPages = (int) Math.ceil((double) filteredResults.size() / resultsPerPage);
            binding.textViewPaginationInfo.setText(
                getString(R.string.pagination_info, currentPage, totalPages, filteredResults.size())
            );
        }
    }

    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            binding.recyclerViewStyles.setVisibility(View.GONE);
            binding.layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerViewStyles.setVisibility(View.VISIBLE);
            binding.layoutEmptyState.setVisibility(View.GONE);
        }
    }

    private void updateCharCount() {
        int count = binding.editTextInput.getText().length();
        binding.textViewCharCount.setText(getString(R.string.input_char_count, count));
    }

    private void clearInput() {
        binding.editTextInput.setText("");
        binding.editTextSearch.setText("");
        allResults.clear();
        filteredResults.clear();
        currentPage = 1;
        updateDisplay();
        Toast.makeText(this, R.string.notif_text_cleared, Toast.LENGTH_SHORT).show();
    }

    private void copyToClipboard(String text, String styleName) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(styleName, text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, getString(R.string.notif_copied, styleName), Toast.LENGTH_SHORT).show();
    }

    private void showFullTextDialog(StyleResult result) {
        new AlertDialog.Builder(this)
            .setTitle(result.getStyle().getName())
            .setMessage(result.getConvertedText())
            .setPositiveButton(R.string.btn_copy, (dialog, which) -> 
                copyToClipboard(result.getConvertedText(), result.getStyle().getName()))
            .setNegativeButton(R.string.btn_close, null)
            .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_history) {
            startActivity(new Intent(this, HistoryActivity.class));
            return true;
        } else if (id == R.id.action_language) {
            showLanguageDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void showLanguageDialog() {
        String[] languages = {"English", "Français", "Español", "Português"};
        String[] codes = {"en", "fr", "es", "pt"};
        
        new AlertDialog.Builder(this)
            .setTitle("Select Language / Langue")
            .setItems(languages, (dialog, which) -> {
                String selectedCode = codes[which];
                changeLanguage(selectedCode);
            })
            .show();
    }
    
    private void changeLanguage(String languageCode) {
        java.util.Locale locale = new java.util.Locale(languageCode);
        java.util.Locale.setDefault(locale);
        
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);
        
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        
        // Sauvegarder la préférence
        getSharedPreferences("Settings", MODE_PRIVATE)
            .edit()
            .putString("language", languageCode)
            .apply();
        
        // Redémarrer l'activité pour appliquer les changements
        recreate();
    }
}
