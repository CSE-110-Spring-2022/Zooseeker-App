package com.example.cse110_lab5.activity.exhibitlist;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse110_lab5.R;
import com.example.cse110_lab5.database.ZooData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ExhibitListAdapter extends RecyclerView.Adapter<ExhibitListAdapter.ViewHolder> {
    private List<ZooData.Node> allExhibits = Collections.emptyList();
    private Consumer<ZooData.Node> onCheckBoxClicked;
    private ArrayList<String> selectedExhibits = new ArrayList<>();

    public void setExhibitItems(List<ZooData.Node> newExhibitItems) {
        // Clear current exhibit list and update it to the new one passed in
        this.allExhibits.clear();
        this.allExhibits = newExhibitItems;
        notifyDataSetChanged();
    }

    public void setOnCheckBoxClickedHandler(Consumer<ZooData.Node> onCheckBoxClicked) {
        this.onCheckBoxClicked = onCheckBoxClicked;
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
        holder.setExhibitItem(allExhibits.get(position));
    }

    @Override
    public int getItemCount() {
        return allExhibits.size();
    }

    public ArrayList<String> getSelectedExhibits() {
        return selectedExhibits;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private ZooData.Node exhibitItem;
        private final CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.exhibit_name);
            this.checkBox = itemView.findViewById(R.id.selected);

            // when an exhibit's check box is selected, check if it's already in the selectedExhibits list
            // and remove or add it accordingly
            this.checkBox.setOnClickListener(view -> {
                if(selectedExhibits.contains(this.exhibitItem.id)){
                    selectedExhibits.remove(this.exhibitItem.id);
                    Log.d("Exhibits Menu", this.exhibitItem.name + " was unchecked");
                } else {
                    selectedExhibits.add(this.exhibitItem.id);
                    Log.d("Exhibits Menu", this.exhibitItem.name + " was checked");
                }
                if(onCheckBoxClicked == null) return;
                onCheckBoxClicked.accept(exhibitItem);
            });
        }

        public ZooData.Node getExhibitItem() {return exhibitItem;}

        public void setExhibitItem(ZooData.Node exhibitItem) {
            this.textView.setText(exhibitItem.name);
            this.checkBox.setChecked(exhibitItem.selected);
            this.exhibitItem = exhibitItem;
        }


    }
}

