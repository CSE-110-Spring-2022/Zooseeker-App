package com.example.cse110_lab5;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse110_lab5.database.GraphDatabase;
import com.example.cse110_lab5.database.ZooData;
import com.example.cse110_lab5.template.ExhibitItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ExhibitListAdapter extends RecyclerView.Adapter<ExhibitListAdapter.ViewHolder> {
    private List<ZooData.Node> allExhibits = Collections.emptyList();
    private Consumer<ZooData.Node> onCheckBoxClicked;
    private Context context;
    private ArrayList<String> selectedExhibits = new ArrayList<>();

    /*public ExhibitListAdapter(List<ExhibitItem> selectedExhibits, Context context){
        this.selectedExhibits = selectedExhibits;
        this.context = context;
    }*/
    public void setExhibitItems(List<ZooData.Node> newExhibitItems) {
        this.allExhibits.clear();
        this.allExhibits = newExhibitItems;
        notifyDataSetChanged();
    }

    public void setOnCheckBoxClickedHandler(Consumer<ZooData.Node> onCheckBoxClicked) {
        this.onCheckBoxClicked = onCheckBoxClicked;
    }

    public void setHashStableIds(boolean b) {
    }

    public static void toggleCompleted(ZooData.Node exhibitItem){
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
        //ZooData.Node node = GraphDatabase.getSingleton(context).nodeDao().get(object.getFirst());

    }

    @Override
    public int getItemCount() {
        return allExhibits.size();
    }

    public ArrayList<String> getSelectedExhibits() {
        return selectedExhibits;
    }

    //@Override
    //public long getItemId(int position) {return allExhibits.get(position).id;}

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private ZooData.Node exhibitItem;
        private final CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textView = itemView.findViewById(R.id.exhibit_name);
            this.checkBox = itemView.findViewById(R.id.selected);

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

