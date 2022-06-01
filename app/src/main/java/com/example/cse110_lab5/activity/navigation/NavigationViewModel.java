package com.example.cse110_lab5.activity.navigation;

import static com.example.cse110_lab5.activity.graph.GraphActivity.getExhibitNames;
import static com.example.cse110_lab5.activity.graph.GraphActivity.tsp;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

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

public class NavigationViewModel extends AndroidViewModel {
    private final EdgeDao edgeDao;
    private final NodeDao nodeDao;

    GraphPath<String, ZooData.IdentifiedEdge> curr_path;

    String[] plan;
    int currExhibit = 0;
    boolean useDetailedPath = false;
    boolean replanRoute = false;

    Coord lastKnownCoord;
    MutableLiveData<List<String>> displayStrings;
    MutableLiveData<Boolean> offTrack = new MutableLiveData<>();

    public NavigationViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        GraphDatabase db = GraphDatabase.getSingleton(context);
        this.edgeDao = db.edgeDao();
        this.nodeDao = db.nodeDao();

        // Set the default location to the Zoo's gate
        ZooData.Node start = nodeDao.getGate();
        this.lastKnownCoord = new Coord(start.lat, start.lng);
    }

    /**
     * Set the list of exhibits to visit
     * @param plan      the list of exhibits, in the order we will visit them
     */
    public void setPlan(String[] plan) {
        this.plan = plan;
    }

    /**
     * Get the list of exhibits to visit
     * @return          the list of exhibits, in the order we will visit them
     */
    public String[] getPlan(){
        return this.plan;
    }

    /**
     * Set the current destination
     * @param curr      an integer representing the destination as an index of the plan
     */
    public void setCurrExhibit(int curr) {
        this.currExhibit= curr;
    }

    /**
     * Get the current destination
     * @return          an integer representing the destination as an index of the plan
     */
    public int getCurrExhibit() {
        return currExhibit;
    }

    /**
     * Get the name of the exhibit corresponding to the current exhibit
     * @return          the human-readable name of the current exhibit
     */
    public String getCurrExhibitName() {
        return nodeDao.get(plan[currExhibit]).name;
    }

    /**
     * Update the instructions based on the user's last known location
     */
    public void updateFromLocation() {
        updateFromLocation(lastKnownCoord);
    }

    /**
     * Get the list of instructions as a LiveData that can be observed
     * @return          the list of instructions as a LiveData that can be observed
     */
    public MutableLiveData<List<String>> getDisplayStrings() {
        if (displayStrings == null) {
            displayStrings = new MutableLiveData<>();
        }
        return displayStrings;
    }

    /**
     * Toggles between basic and detailed instructions
     */
    public void toggleDetailedDirections() {
        this.useDetailedPath = !this.useDetailedPath;
        updateFromLocation();
    }

    /**
     * Moves the target to the next exhibit in the plan, if it is valid, and update the
     * instructions.
     */
    public void toNextExhibit() {
        if(currExhibit < plan.length - 1) {
            currExhibit++;
            updateFromLocation();
        }
    }

    /**
     * Moves the target to the previous exhibit in the plan, if it is valid, and update the
     * instructions.
     */
    public void toPrevExhibit() {
        if(currExhibit > 0) {
            currExhibit--;
            updateFromLocation();
        }
    }

    /**
     * Removes the current destination from the plan and replans the route to all exhibits that
     * come after the current one.
     */
    public void skipExhibit() {
        if(this.plan.length > 1 && currExhibit != plan.length - 1) {
            ArrayList<String> newPlan = new ArrayList<>(Arrays.asList(this.plan));
            newPlan.remove(this.currExhibit);
            this.plan = newPlan.toArray(new String[0]);
            updateFromLocation();
        }
    }

    /**
     * Get the field determining if there is a more optimal route through the exhibits
     * than the current route.
     *
     * @return      if there is a more optimal route through the exhibits than the current route,
     *              as a LiveData that can be observed.
     */
    public MutableLiveData<Boolean> getOffTrack() {
        return offTrack;
    }

    /**
     * Indicate that the user desires to replan the exhibits to visit
     */
    public void replan() {
        offTrack.setValue(false);
        replanRoute = true;
        updateFromLocation();
    }

    /**
     * Update the route to the current destination based on the user's location
     *
     * @param coord         the user's location
     */
    public void updateFromLocation(Coord coord) {
        this.lastKnownCoord = coord;

        // Determine the closest exhibit to our location to start routing from
        String target = nodeDao.get(plan[currExhibit]).id;
        String closestExhibit = findClosestExhibit(coord, nodeDao.getExhibitsWithLocations());

        // Split the plan into visited and remaining exhibits
        ArrayList<String> visitedExhibits = new ArrayList<>(List.of(this.plan)
                .subList(0, currExhibit));
        ArrayList<String> remainingExhibits = new ArrayList<>(List.of(this.plan)
                .subList(currExhibit, this.plan.length));

        // Calculate the optimal path through the remaining exhibits
        ArrayList<String> tspPath = getExhibitNames(tsp(
                ZooData.graph,
                closestExhibit,
                remainingExhibits
                        .subList(0, remainingExhibits.size() - 1) // Exclude returning to the start
                        .toArray(new String[remainingExhibits.size() - 1]),
                nodeDao.getGate().id));

        Log.d("Navigation/Closest Exhibit", closestExhibit);
        Log.d("Navigation/Remaining Exhibits", remainingExhibits.toString());
        Log.d("Navigation/Optimal Remaining Path", tspPath.toString());

        if(!remainingExhibits.equals(tspPath)) { // If the optimal path is not the current path
            offTrack.setValue(true);
            if (replanRoute) { // If the user indicates they want to replan the route
                ArrayList<String> newPlan = new ArrayList<>();
                newPlan.addAll(visitedExhibits);
                newPlan.addAll(tspPath);
                this.plan = newPlan.toArray(new String[0]);

                Log.d("Navigation/Replanned Route", newPlan.toString());

                // Change the target to the optimal next exhibit
                target = nodeDao.get(plan[currExhibit]).id;
                replanRoute = false;
            }
            // Construct the new plan and update the field
        }
        // Generate the route to the target based on the user's current location
        this.curr_path = new DijkstraShortestPath<>(ZooData.graph).getPath(closestExhibit, target);
        displayStrings.setValue(generatePathStrings(this.curr_path, this.useDetailedPath));
    }

    /**
     * Helper function to find the closest exhibit to a user's current location
     *
     * @param coord         the user's location in coordinate format
     * @param exhibits      the list of all exhibits
     * @return              the closest exhibit in the list to the user
     */
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

    /**
     * Helper function to generate the human-readable instructions from the current location
     * to the destination exhibit.
     *
     * @param path                      the GraphPath representation of the optimal path
     * @param useDetailedPath           whether to generate basic or detailed paths
     * @return
     */
    private ArrayList<String> generatePathStrings(GraphPath<String, ZooData.IdentifiedEdge> path, boolean useDetailedPath) {
        ArrayList<String> pathStrings = new ArrayList<>();
        String lastStreetName = "";

        if(useDetailedPath) {
            Log.d("new path", "using detailed");
            for(int i = 0; i < path.getEdgeList().size(); i++) {
                ZooData.IdentifiedEdge edge = path.getEdgeList().get(i);
                ZooData.Node targetNode = nodeDao.get(path.getVertexList().get(i+1));
                ZooData.Edge dbEdge = edgeDao.get(edge.getId());

                // The edge's human-readable name
                String streetName = dbEdge.street;
                // We use "Continue" if we're still on the same street, and "Proceed" otherwise
                String proceedOrContinue = lastStreetName.equals(streetName) ? "Continue" : "Proceed";
                // The length of this street
                String weight = String.valueOf(path.getGraph().getEdgeWeight(edge));

                // Determine how to indicate the destination based on the type of the next Node
                String destination;
                switch(targetNode.kind) {
                    case "exhibit":
                        destination = String.format("the %s Exhibit", targetNode.name);
                        break;
                    case "exhibit_group":
                        destination = String.format("%s", targetNode.name);
                        break;
                    default:
                    case "intersection":
                        if(targetNode.name.split("/").length > 1) {
                            destination = String.format("the corner of %s", targetNode.name.replace("/", "and"));
                        } else {
                            destination = String.format("the %s intersection", targetNode.name);
                        }
                        break;
                }

                // Handle the edge case where the exhibit has a parent group
                if(targetNode.parent_id != null) {
                    pathStrings.add(String.format("Find the %s Exhibit", targetNode.name));
                } else {
                    pathStrings.add(String.format(
                            i != path.getEdgeList().size() - 1 ? "%s on %s %s ft towards %s" : "%s on %s %s ft to %s",
                            proceedOrContinue,
                            streetName,
                            weight,
                            destination
                    ));
                }

                lastStreetName = streetName;
            }

        } else {
            Log.d("new path", "using basic");
            double totalDist = 0;
            for(int i = 0; i < path.getEdgeList().size(); i++) {
                ZooData.IdentifiedEdge edge = path.getEdgeList().get(i);
                ZooData.Node targetNode = nodeDao.get(path.getVertexList().get(i+1));
                ZooData.Edge dbEdge = edgeDao.get(edge.getId());

                // Determine how to indicate the destination based on the type of the next Node
                String destination;
                switch(targetNode.kind) {
                    case "exhibit":
                        destination = String.format("the %s Exhibit", targetNode.name);
                        break;
                    case "exhibit_group":
                        destination = String.format("%s", targetNode.name);
                        break;
                    default:
                    case "intersection":
                        if(targetNode.name.split("/").length > 1) {
                            destination = String.format("the corner of %s", targetNode.name.replace("/", "and"));
                        } else {
                            destination = String.format("the %s intersection", targetNode.name);
                        }
                        break;
                }

                String streetName = dbEdge.street;
                if(i != path.getEdgeList().size() - 1) { // If this is the last edge in the instruction
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
                    // Handle the edge case where the exhibit has a parent group
                    if (targetNode.parent_id != null) {
                        pathStrings.add(String.format("Find the %s Exhibit", targetNode.name));
                    } else {
                        pathStrings.add(String.format("Proceed on %s %s ft towards %s",
                                streetName,
                                totalDist + path.getGraph().getEdgeWeight(edge),
                                destination
                        ));
                    }
                }
            }
        }
        return pathStrings;
    }
}
