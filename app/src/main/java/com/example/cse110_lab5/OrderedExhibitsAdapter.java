package com.example.cse110_lab5;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse110_lab5.database.EdgeDao;
import com.example.cse110_lab5.database.GraphDatabase;
import com.example.cse110_lab5.database.ZooData;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.util.Pair;

import java.util.Collections;
import java.util.List;

public class OrderedExhibitsAdapter extends RecyclerView.Adapter<OrderedExhibitsAdapter.ViewHolder>{
    private List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> plannedExs = Collections.emptyList();
    private Context context;

    public OrderedExhibitsAdapter(Context context, List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> plannedExs) {
        this.plannedExs = plannedExs;
        this.context = context;
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
        Pair<String, GraphPath<String, ZooData.IdentifiedEdge>> object = plannedExs.get(position);
        ZooData.Node node = GraphDatabase.getSingleton(context).nodeDao().get(object.getFirst());

        if(node == null) {
            holder.exhibitName.setText(object.getFirst());
            Log.d("Nodes", "node " + object.getFirst() + " did not exist in database");
        } else {
            holder.exhibitName.setText(node.name);
        }
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
