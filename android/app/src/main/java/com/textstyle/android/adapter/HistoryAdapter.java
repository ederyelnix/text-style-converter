package com.textstyle.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.textstyle.android.R;
import com.textstyle.android.data.HistoryEntity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<HistoryEntity> history = new ArrayList<>();
    private final OnHistoryClickListener listener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public interface OnHistoryClickListener {
        void onEditClick(HistoryEntity entry);
        void onCopyClick(HistoryEntity entry);
    }

    public HistoryAdapter(OnHistoryClickListener listener) {
        this.listener = listener;
    }

    public void setHistory(List<HistoryEntity> history) {
        this.history = history;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_history_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryEntity entry = history.get(position);
        
        String preview = entry.getText();
        if (preview.length() > 100) {
            preview = preview.substring(0, 100) + "...";
        }
        holder.textViewText.setText(preview);
        
        String date = dateFormat.format(new Date(entry.getTimestamp()));
        holder.textViewDate.setText("🕒 " + date);
        
        holder.buttonEdit.setOnClickListener(v -> listener.onEditClick(entry));
        holder.buttonCopy.setOnClickListener(v -> listener.onCopyClick(entry));
    }

    @Override
    public int getItemCount() {
        return history.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewText;
        TextView textViewDate;
        Button buttonEdit;
        Button buttonCopy;

        ViewHolder(View itemView) {
            super(itemView);
            textViewText = itemView.findViewById(R.id.textViewHistoryText);
            textViewDate = itemView.findViewById(R.id.textViewHistoryDate);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonCopy = itemView.findViewById(R.id.buttonCopy);
        }
    }
}
