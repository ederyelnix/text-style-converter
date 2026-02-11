package com.textstyle.android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.textstyle.android.R;
import com.textstyle.android.model.StyleResult;
import com.textstyle.android.util.FontManager;
import java.util.ArrayList;
import java.util.List;

public class StylesAdapter extends RecyclerView.Adapter<StylesAdapter.ViewHolder> {
    private List<StyleResult> results = new ArrayList<>();
    private final OnStyleClickListener listener;

    public interface OnStyleClickListener {
        void onCopyClick(StyleResult result);
        void onViewClick(StyleResult result);
    }

    public StylesAdapter(OnStyleClickListener listener) {
        this.listener = listener;
    }

    public void setResults(List<StyleResult> results) {
        this.results = results;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_style_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StyleResult result = results.get(position);
        
        holder.textViewName.setText(result.getStyle().getName());
        holder.textViewDescription.setText(result.getStyle().getDescription());
        holder.textViewCategory.setText(result.getStyle().getCategory());
        
        String preview = result.getConvertedText();
        if (preview.length() > 50) {
            preview = preview.substring(0, 50) + "...";
        }
        holder.textViewPreview.setText(preview);
        FontManager.applyUnicodeFont(holder.textViewPreview);
        
        holder.buttonCopy.setOnClickListener(v -> listener.onCopyClick(result));
        holder.buttonView.setOnClickListener(v -> listener.onViewClick(result));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        TextView textViewDescription;
        TextView textViewPreview;
        TextView textViewCategory;
        Button buttonCopy;
        Button buttonView;

        ViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewStyleName);
            textViewDescription = itemView.findViewById(R.id.textViewStyleDescription);
            textViewPreview = itemView.findViewById(R.id.textViewStylePreview);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            buttonCopy = itemView.findViewById(R.id.buttonCopy);
            buttonView = itemView.findViewById(R.id.buttonView);
        }
    }
}
