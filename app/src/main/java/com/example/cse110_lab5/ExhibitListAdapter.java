package com.example.cse110_lab5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse110_lab5.template.ExhibitItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ExhibitListAdapter extends RecyclerView.Adapter<ExhibitListAdapter.ViewHolder> {
    private List<ExhibitItem> allExhibits = Collections.emptyList();
    private Consumer<ExhibitItem> onCheckBoxClicked;
    private Context context;
    private ArrayList<String> selectedExhibits = new ArrayList<>();

    /*public ExhibitListAdapter(List<ExhibitItem> selectedExhibits, Context context){
        this.selectedExhibits = selectedExhibits;
        this.context = context;
    }*/
    public void setExhibitItems(List<ExhibitItem> newExhibitItems) {
        this.allExhibits.clear();
        this.allExhibits = newExhibitItems;
        notifyDataSetChanged();
    }

    public void setOnCheckBoxClickedHandler(Consumer<ExhibitItem> onCheckBoxClicked) {
        this.onCheckBoxClicked = onCheckBoxClicked;
    }

    public void setHashStableIds(boolean b) {
    }

    public static void toggleCompleted(ExhibitItem exhibitItem){
        exhibitItem.selected = !exhibitItem.selected;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.exhibit_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setExhibitItem(allExhibits.get(position));
    }

    @Override
    public int getItemCount() {
        return allExhibits.size();
    }

    public ArrayList<String> getSelectedExhibits() {
        return selectedExhibits;
    }

    @Override
    public long getItemId(int position) {
        return allExhibits.get(position).id;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private ExhibitItem exhibitItem;
        private final CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.exhibit_name);
            this.checkBox = itemView.findViewById(R.id.selected);

            this.checkBox.setOnClickListener(view -> {
                if(selectedExhibits.contains(this.exhibitItem.text)){
                    selectedExhibits.remove(this.exhibitItem.text);
                } else {
                    selectedExhibits.add(this.exhibitItem.text);
                }
                System.out.println("checkbox clicked");
                if(onCheckBoxClicked == null) return;
                onCheckBoxClicked.accept(exhibitItem);
            });
        }

        public ExhibitItem getExhibitItem() {return exhibitItem;}

        public void setExhibitItem(ExhibitItem exhibitItem) {
            this.textView.setText(exhibitItem.text);
            this.checkBox.setChecked(exhibitItem.selected);
            this.exhibitItem = exhibitItem;
        }
    }
}

