package com.example.cse110_lab5;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.util.Pair;

import java.util.Collections;
import java.util.List;

public class OrderedExhibitsAdapter extends RecyclerView.Adapter<OrderedExhibitsAdapter.ViewHolder>{
    private List<Pair<String, GraphPath<String,IdentifiedWeightedEdge>>> plannedExs = Collections.emptyList();

    public OrderedExhibitsAdapter(List<Pair<String, GraphPath<String, IdentifiedWeightedEdge>>> plannedExs) {
        this.plannedExs = plannedExs;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.exhibit_planned, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pair<String, GraphPath<String, IdentifiedWeightedEdge>> object = plannedExs.get(position);
        holder.exhibitName.setText(object.getFirst());
    }

    @Override
    public int getItemCount() {
        return plannedExs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView exhibitName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            exhibitName = (TextView) itemView.findViewById(R.id.exhibit_planned);
        }

    }
}
