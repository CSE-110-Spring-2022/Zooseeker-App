package com.example.cse110_lab5.activity.graph;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse110_lab5.R;
import com.example.cse110_lab5.database.GraphDatabase;
import com.example.cse110_lab5.database.ZooData;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.util.Pair;

import java.util.Collections;
import java.util.List;

public class GraphAdapter extends RecyclerView.Adapter<GraphAdapter.ViewHolder>{
    private List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> plannedExs = Collections.emptyList();
    private Context context;
    private List<Double> cumulativeDistances = Collections.emptyList();

    public GraphAdapter(Context context, List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> plannedExs, List<Double> cumulativeDistances) {
        this.plannedExs = plannedExs;
        this.context = context;
        this.cumulativeDistances = cumulativeDistances;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.exhibit_planned_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pair<String, GraphPath<String, ZooData.IdentifiedEdge>> object = plannedExs.get(position);
        ZooData.Node node = GraphDatabase.getSingleton(context).nodeDao().get(object.getFirst());
        Double distance = this.cumulativeDistances.get(position);
        if(node == null) {
            holder.exhibitName.setText(object.getFirst());
            Log.d("Nodes", "node " + object.getFirst() + " did not exist in database");
        } else {
            holder.exhibitName.setText(node.name);
            holder.cumulativeDistance.setText(distance.toString() + "ft");
        }
    }

    @Override
    public int getItemCount() {
        return plannedExs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView exhibitName;
        private final TextView cumulativeDistance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            exhibitName = (TextView) itemView.findViewById(R.id.exhibit_planned);
            cumulativeDistance = (TextView) itemView.findViewById(R.id.cumulative_distance);
        }

    }
}
