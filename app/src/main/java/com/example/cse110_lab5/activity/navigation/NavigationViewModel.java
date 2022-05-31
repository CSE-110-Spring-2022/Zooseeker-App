package com.example.cse110_lab5.activity.navigation;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import static com.example.cse110_lab5.activity.graph.GraphActivity.tsp;
import static com.example.cse110_lab5.activity.graph.GraphActivity.getOrderedExhibits;
import com.example.cse110_lab5.activity.location.Coord;
import com.example.cse110_lab5.database.EdgeDao;
import com.example.cse110_lab5.database.GraphDatabase;
import com.example.cse110_lab5.database.NodeDao;
import com.example.cse110_lab5.database.ZooData;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.lifecycle.MutableLiveData;

public class NavigationViewModel extends AndroidViewModel {
    private final EdgeDao edgeDao;
    private final NodeDao nodeDao;

    GraphPath<String, ZooData.IdentifiedEdge> curr_path;

    // TODO: make these SharedPreferences
    String[] plan;
    int currExhibit = 0;
    boolean useDetailedPath = false;

    Coord lastKnownCoord;
    MutableLiveData<List<String>> displayStrings;

    public NavigationViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        GraphDatabase db = GraphDatabase.getSingleton(context);
        this.edgeDao = db.edgeDao();
        this.nodeDao = db.nodeDao();

        ZooData.Node start = nodeDao.get("entrance_exit_gate");
        this.lastKnownCoord = new Coord(start.lat, start.lng);
    }

    public void setPlan(String[] plan) {
        this.plan = plan;
    }

    public void updateFromLocation() {
        updateFromLocation(lastKnownCoord);
    }

    public MutableLiveData<List<String>> getDisplayStrings() {
        if (displayStrings == null) {
            displayStrings = new MutableLiveData<>();
        }
        return displayStrings;
    }

    public String getCurrExhibitName() {
        return nodeDao.get(plan[currExhibit]).name;
    }

    public void toggleDetailedDirections() {
        this.useDetailedPath = !this.useDetailedPath;
        updateFromLocation();
    }

    public void toNextExhibit() {
        if(currExhibit < plan.length - 1) {
            currExhibit++;
            updateFromLocation();
        }
    }

    public void toPrevExhibit() {
        if(currExhibit > 0) {
            currExhibit--;
            updateFromLocation();
        }
    }

    public void skipExhibit() {
        if(this.plan.length > 1 && currExhibit != plan.length - 1) {
            ArrayList<String> newPlan = new ArrayList<>(Arrays.asList(this.plan));
            newPlan.remove(this.currExhibit);
            this.plan = newPlan.toArray(new String[0]);
            updateFromLocation();
        }
    }

    public void updateFromLocation(Coord coord) {
        this.lastKnownCoord = coord;
        String target = nodeDao.get(plan[currExhibit]).id;
        String closestExhibit = findClosestExhibit(coord, nodeDao.getExhibitsWithLocations());

        ArrayList<String> visitedExhibits = new ArrayList<>(List.of(this.plan)
                .subList(0, currExhibit));
        ArrayList<String> remainingExhibits = new ArrayList<>(List.of(this.plan)
                .subList(currExhibit, this.plan.length));
        ArrayList<String> tspPath = getOrderedExhibits(tsp(
                ZooData.graph,
                closestExhibit,
                remainingExhibits
                        .subList(0, remainingExhibits.size() - 1) // Exclude returning to the start
                        .toArray(new String[remainingExhibits.size() - 1]),
                "entrance_exit_gate"));

        Log.d("Navigation/Closest Exhibit", closestExhibit);
        Log.d("Navigation/Remaining Exhibits", remainingExhibits.toString());
        Log.d("Navigation/Optimal Remaining Path", tspPath.toString());

        if(!remainingExhibits.equals(tspPath)) { // If the optimal path is not the current path
            //TODO: Prompt replan

            // Construct the new plan and update the field
            ArrayList<String> newPlan = new ArrayList<>();
            newPlan.addAll(visitedExhibits);
            newPlan.addAll(tspPath);
            this.plan = newPlan.toArray(new String[0]);

            Log.d("Navigation/Replanned Route", newPlan.toString());

            // Change the target to the optimal next exhibit
            target = nodeDao.get(plan[currExhibit]).id;
        }
        this.curr_path = new DijkstraShortestPath<>(ZooData.graph).getPath(closestExhibit, target);
        displayStrings.setValue(generatePathStrings(this.curr_path, this.useDetailedPath));
    }

    public String findClosestExhibit(Coord coord, List<ZooData.Node> exhibits) {
        ZooData.Node first = exhibits.get(0);
        String closestExhibit = first.id;
        double min_distance = coord.distanceTo(new Coord(first.lat, first.lng));
        for(ZooData.Node exhibit: exhibits){
            Coord current_exhibit_location = new Coord(exhibit.lat, exhibit.lng);
            double distance = coord.distanceTo(current_exhibit_location);
            if(distance < min_distance) {
                min_distance = distance;
                closestExhibit = exhibit.id;
            }
        }
        return closestExhibit;
    }

    private ArrayList<String> generatePathStrings(GraphPath<String, ZooData.IdentifiedEdge> path, boolean useDetailedPath) {

        ArrayList<String> pathStrings2 = new ArrayList<>();
        for(int i = 0; i < path.getEdgeList().size(); i++) {
            pathStrings2.add(path.getEdgeList().get(i).toString());
        }
        return pathStrings2;
        /*
        ArrayList<String> pathStrings = new ArrayList<>();
        String lastStreetName = "";
        if(useDetailedPath) {
            Log.d("new path", "using detailed");
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
            Log.d("new path", "using basic");
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
        return pathStrings;*/
    }

    private ArrayList<ArrayList<ZooData.Edge>> generateCommonEdgeConnections(GraphPath<String, ZooData.IdentifiedEdge> exhibitPath){
        ArrayList<ArrayList<ZooData.Edge>> edgeConnections = new ArrayList<>();
        List<ZooData.IdentifiedEdge> edgeList = exhibitPath.getEdgeList();
        ArrayList<ZooData.Edge> currentRoad = new ArrayList<>();
        String lastStreetName = edgeDao.get(edgeList.get(0).getId()).street;
        for(int i = 0; i < exhibitPath.getEdgeList().size(); i++) {
            ZooData.IdentifiedEdge edge = edgeList.get(i);
            ZooData.Edge dbEdge = edgeDao.get(edge.getId());
            String currentStreetName = dbEdge.street;
            if(!currentStreetName.equals(lastStreetName)) {
                edgeConnections.add(currentRoad);
                currentRoad = new ArrayList<>();
            }
            currentRoad.add(dbEdge);
            lastStreetName = currentStreetName;
        }
        edgeConnections.add(currentRoad);
        return edgeConnections;
    }
}
