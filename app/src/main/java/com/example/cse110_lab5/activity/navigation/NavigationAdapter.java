package com.example.cse110_lab5.activity.navigation;
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

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.ViewHolder>{
    private Context context;

    private GraphPath<String, ZooData.IdentifiedEdge> path;

    public NavigationAdapter(Context context, GraphPath<String, ZooData.IdentifiedEdge> path) {
        this.context = context;
        this.path = path;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.direction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ZooData.IdentifiedEdge vertex = path.getEdgeList().get(position);
        ZooData.Edge edge = GraphDatabase.getSingleton(context).edgeDao().get(vertex.getId());

        if(edge == null) {
            holder.exhibitName.setText(vertex.getId());
            Log.d("Nodes", "node " + vertex + " did not exist in database");
        } else {
            holder.exhibitName.setText(edge.street);
            double weight = path.getGraph().getEdgeWeight(vertex);
            holder.distance.setText(String.format("%s ft", String.valueOf(weight)));
        }
    }

    @Override
    public int getItemCount() {
        return path.getEdgeList().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView exhibitName;
        private final TextView distance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            exhibitName = (TextView) itemView.findViewById(R.id.direction);
            distance = (TextView) itemView.findViewById(R.id.distance);
        }

    }
}
