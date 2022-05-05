package com.example.cse110_lab5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse110_lab5.template.ExhibitItem;

import java.util.Collections;
import java.util.List;

public class ExhibitListAdapter extends RecyclerView.Adapter<ExhibitListAdapter.ViewHolder> {
    private List<ExhibitItem> selectedExhibits = Collections.emptyList();
    private Context context;

    /*public ExhibitListAdapter(List<ExhibitItem> selectedExhibits, Context context){
        this.selectedExhibits = selectedExhibits;
        this.context = context;
    }*/

    public void setExhibitItems(List<ExhibitItem> newExhibitItems){
        this.selectedExhibits.clear();
        this.selectedExhibits = newExhibitItems;
        notifyDataSetChanged();
    }

    public void setHashStableIds(boolean b) {

    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.exhibit_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setExhibitItem(selectedExhibits.get(position));
    }

    @Override
    public int getItemCount() {
        return selectedExhibits.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private ExhibitItem exhibitItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.exhibit_name);
        }

        public ExhibitItem getTodoItem() {return exhibitItem;}

        public void setExhibitItem(ExhibitItem exhibitItem) {
            this.textView.setText(exhibitItem.text);
        }
    }
}
