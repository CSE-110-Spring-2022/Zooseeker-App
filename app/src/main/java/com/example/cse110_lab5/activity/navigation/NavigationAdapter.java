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
import com.example.cse110_lab5.database.EdgeDao;
import com.example.cse110_lab5.database.GraphDatabase;
import com.example.cse110_lab5.database.NodeDao;
import com.example.cse110_lab5.database.ZooData;

import org.jgrapht.GraphPath;

import java.util.ArrayList;
import java.util.Collections;

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.ViewHolder>{
    private Context context;

    private GraphPath<String, ZooData.IdentifiedEdge> path;

    private EdgeDao edgeDao;
    private NodeDao nodeDao;

    private ArrayList<String> detailedPath = new ArrayList<>();

    public NavigationAdapter(Context context, GraphPath<String, ZooData.IdentifiedEdge> path) {
        this.context = context;
        this.path = path;

        GraphDatabase db = GraphDatabase.getSingleton(context);
        this.edgeDao = db.edgeDao();
        this.nodeDao = db.nodeDao();

        boolean useDetailedPath = false;
        detailedPath = generatePathStrings(path, useDetailedPath);

        Log.d("detailed path", detailedPath.toString());
    }

    private ArrayList<String> generatePathStrings(GraphPath<String, ZooData.IdentifiedEdge> path, boolean useDetailedPath) {
        ArrayList<String> pathStrings = new ArrayList<>();
        String lastStreetName = "";
        if(useDetailedPath) {
            for(int i = 0; i < path.getEdgeList().size(); i++) {
                ZooData.IdentifiedEdge edge = path.getEdgeList().get(i);
                ZooData.Node targetNode = nodeDao.get(edge.getTargetId());
                ZooData.Edge dbEdge = edgeDao.get(edge.getId());

                String streetName = dbEdge.street;
                String proceedOrContinue = lastStreetName.equals(streetName) ? "Continue" : "Proceed";
                String weight = String.valueOf(path.getGraph().getEdgeWeight(edge));
                String destination = targetNode.kind.equals("intersection") ?
                        targetNode.name.replace("/", "and") :
                        targetNode.name;

                pathStrings.add(String.format(
                        i != path.getEdgeList().size() - 1 ? "%s on %s %s ft towards the corner of %s" : "%s on %s %s ft to the %s Exhibit",
                        proceedOrContinue,
                        streetName,
                        weight,
                        destination
                ));

                lastStreetName = streetName;
            }
        } else {

            double totalDist = 0;
            for(int i = 0; i < path.getEdgeList().size(); i++) {
                ZooData.IdentifiedEdge edge = path.getEdgeList().get(i);
                ZooData.Node targetNode = nodeDao.get(edge.getTargetId());
                ZooData.Edge dbEdge = edgeDao.get(edge.getId());

                String destination = targetNode.kind.equals("intersection") ?
                        "the corner of " + targetNode.name.replace("/", "and") :
                        "the " + targetNode.name + " Exhibit";

                String streetName = dbEdge.street;
                if(i != path.getEdgeList().size() - 1) {
                    ZooData.IdentifiedEdge nextEdge = path.getEdgeList().get(i+1);
                    String nextStreetName = edgeDao.get(nextEdge.getId()).street;
                    totalDist += path.getGraph().getEdgeWeight(edge);

                    if(!streetName.equals(nextStreetName)) {
                        pathStrings.add(String.format("Proceed on %s %s ft towards %s",
                                streetName,
                                totalDist,
                                destination
                        ));
                        totalDist = 0;
                    }
                } else {
                    pathStrings.add(String.format("Proceed on %s %s ft to the %s Exhibit",
                            streetName,
                            totalDist + path.getGraph().getEdgeWeight(edge),
                            targetNode.name
                    ));
                }
            }
        }
        return pathStrings;
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
        holder.exhibitName.setText(detailedPath.get(position));
        /*
        ZooData.IdentifiedEdge vertex = path.getEdgeList().get(position);
        ZooData.Edge edge = GraphDatabase.getSingleton(context).edgeDao().get(vertex.getId());

        if(edge == null) {
            holder.exhibitName.setText(vertex.getId());
            Log.d("Nodes", "node " + vertex + " did not exist in database");
        } else {
            holder.exhibitName.setText(edge.street);
            double weight = path.getGraph().getEdgeWeight(vertex);
            holder.distance.setText(String.format("%s ft", String.valueOf(weight)));
        }*/
    }

    @Override
    public int getItemCount() {
        /*return path.getEdgeList().size();*/
        return detailedPath.size();
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
