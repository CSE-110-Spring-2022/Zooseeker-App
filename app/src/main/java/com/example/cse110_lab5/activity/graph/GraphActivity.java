package com.example.cse110_lab5.activity.graph;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse110_lab5.R;
import com.example.cse110_lab5.activity.navigation.NavigationActivity;
import com.example.cse110_lab5.database.GraphDatabase;
import com.example.cse110_lab5.database.NodeDao;
import com.example.cse110_lab5.database.ZooData;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ManyToManyShortestPathsAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraManyToManyShortestPaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphActivity extends AppCompatActivity {

    public RecyclerView recyclerView;

    // This creates an ActivityResult launcher that corresponds to navigation
    // It ensures that once navigation finishes and returns to GraphActivity (ie on clear), GraphActivity
    // also just finishes
    ActivityResultLauncher<Intent> navLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                // Usually here we would use the result status code, but for our purposes, we just always go back to
                // MainActivity
                @Override
                public void onActivityResult(ActivityResult result) {
                    finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ensure the graph is properly loaded
        ZooData.loadZooGraphJSON(this, "sample_zoo_graph.json");

        // initialize empty lists for to visit
        String start = "";
        String[] toVisit = {};

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        // if there were extras passed, read them in
        if (bundle != null) {
            toVisit = bundle.getStringArray("toVisit");
            start = bundle.getString("start");
        }

        // Case where the user never selected any exhibits - just show a screen that says that no
        // exhibits were selected and leave it there
        if (toVisit.length == 0) {
            setContentView(R.layout.no_plan);
            Log.d("Graph", "No exhibits were selected for planning");
        }
        else {
            Log.d("Graph", toVisit.length + " exhibits were selected for planning");
            setContentView(R.layout.planned_exhibits);

            // Generate the recyclerView to show the ordered list of exhibits in the plan
            recyclerView = (RecyclerView) findViewById(R.id.planned_exhibits);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            // Generate the plan for how to visit the exhibits using TSP
            List<Pair<String,GraphPath<String, ZooData.IdentifiedEdge>>> plan = tsp(ZooData.graph, start, toVisit);

            // This creates a graph adapter that loads in the plan and the cumulative distances for distance hints for
            // each exhibit
            GraphAdapter oEadapter = new GraphAdapter(this, plan, generateCumulativeDistances(plan));
            recyclerView.setAdapter(oEadapter);

            // update the header to include the total count of exhibits planned
            TextView total = findViewById(R.id.total);
            total.setText("Total: " + oEadapter.getItemCount());

            // Get ready to go to navigation activity
            Intent nav = new Intent(this, NavigationActivity.class);
            ArrayList<String> orderedExhibits = getExhibitNames(plan);
            orderedExhibits.add(start);
            // Setup the plan and the current exhibit for navigation to use
            nav.putExtra("curr_exhibit", 0);
            nav.putExtra("plan", orderedExhibits.toArray(new String[orderedExhibits.size()]));

            // When navigate is pressed, launch the navigation activity using navLauncher - this ensures
            // that there's a callback on navigation finishing
            final Button button = findViewById(R.id.nav_bttn);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    navLauncher.launch(nav);
                }
            });
        }
    }

    /**
     * Approximation of TSP that finds the approximate shortest path starting from start and hitting
     * each node in visit before ending at end.
     *
     * @param g             The graph to search through
     * @param start         The id of the starting node
     * @param visit         The ids of each of the nodes to visit, excluding the start
     * @param end           The id of the ending node
     *
     * @return              The path represented as a list of pairs of nodes and the paths to reach
     *                      that node from the previous node. Returns null if a given node to visit
     *                      is unreachable.
     */
    public static List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> tsp(Graph<String, ZooData.IdentifiedEdge> g, String start, String[] visit, String end) {
        // Call tsp to the get the optimal way between all the nodes to visit
        List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> path = tsp(g, start, visit);

        // Add on the path to the last exhibit to the end using djikstra
        if(path.size() != 0) {
            String prev = path.get(path.size() - 1).getFirst();
            path.add(new Pair<>(end, new DijkstraShortestPath<>(g).getPath(prev, end)));
        } else {
            // case when tsp returns an empty list - just get the distance from the start to end
            path.add(new Pair<>(end, new DijkstraShortestPath<>(g).getPath(start, end)));
        }
        return path;
    }

    /**
     * Approximation of TSP that finds the approximate shortest path starting from start and hitting
     * each node in visit.
     *
     * @param g             The graph to search through
     * @param start         The id of the starting node
     * @param visit         The ids of each of the nodes to visit, excluding the start
     *
     * @return              The path represented as a list of pairs of nodes and the paths to reach
     *                      that node from the previous node. Returns null if a given node to visit
     *                      is unreachable.
     */
    public static List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> tsp(Graph<String, ZooData.IdentifiedEdge> g, String start, String[] visit) {
        List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> finalPath = new ArrayList<>();

        // setup all the exhibits to visit in a set
        Set<String> remaining = new HashSet<>(Arrays.asList(visit));
        String prev = start;

        // Go through all nodes in the set and find the most optimal path for them
        while (!remaining.isEmpty()) {
            // get all possible paths using Djikstra's from the current node to the remaining ones
            ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<String, ZooData.IdentifiedEdge> paths =
                    new DijkstraManyToManyShortestPaths<>(g)
                            .getManyToManyPaths(
                                    new HashSet<>(Arrays.asList(prev)), remaining);

            // Find the shortest path from the ones to the remaining nodes
            GraphPath<String, ZooData.IdentifiedEdge> shortestPath = findShortestPath(paths);
            if (shortestPath == null) {
                // Edge case for no path
                Log.d("TSP Paths", "Unreachable destination given");
                return null;
            }

            // Add the shortest path for this vertex
            finalPath.add(new Pair<>(shortestPath.getEndVertex(), shortestPath));
            // update the prev pointer to the next vertex and remove it from the remaining vertices
            prev = shortestPath.getEndVertex();
            remaining.remove(prev);
        }

        // output the final ordering which should be the optimal way to visit all the vertices
        return finalPath;
    }


    /**
     * Helper function to find the shortest path in a ManyToManyShortestPaths object
     *
     * @param paths     the ManyToManyShortestPaths object
     * @return          the shortest path as a GraphPath
     */
    public static GraphPath<String, ZooData.IdentifiedEdge> findShortestPath(ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<String, ZooData.IdentifiedEdge> paths) {
        String source = paths.getSources().iterator().next();

        GraphPath<String, ZooData.IdentifiedEdge> currShortest = null;
        // Default min value to start with
        double shortestDistance = Double.MAX_VALUE;

        // Go through all the targets for a path and find the shortest weight path with a simple minimum
        // checking if statement
        for (String node : paths.getTargets()) {
            GraphPath<String, ZooData.IdentifiedEdge> path = paths.getPath(source, node);
            if (path != null && path.getWeight() < shortestDistance) {
                currShortest = path;
                shortestDistance = path.getWeight();
            }
        }

        return currShortest;
    }

    /**
     * Helper function to get the list of exhibits names we plan to visit, in the order we plan to visit
     * them in.
     *
     * @param plan      a List of pairs of (destination, path_to_destination) in the order we plan
     *                      to visit them
     * @return          the list of exhibits in order in String[] format
     */
    public static ArrayList<String> getExhibitNames(List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> plan) {
        ArrayList<String> exhibitNames = new ArrayList<>();

        // Input is the order we plan to visit the exhibits, so we just return each index's name
        for(int i = 0; i < plan.size(); i++) {
            exhibitNames.add(plan.get(i).getFirst());
        }

        return exhibitNames;
    }

    /**
     * Helper function for iterating over the plan generated by TSP to find the cumulative distance
     * to each node in the exhibitPlan
     *
     * @param plan      a List of Pair<String, GraphPath>, the target id and the
     *                  GraphPath<String, ZooData.IdentifiedEdge> to from the previous exhibit to
     *                  the target
     * @return          A List of the minimum total distance required in the plan to each
     *                  respective target node
     */
    public List<Double> generateCumulativeDistances
            (List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> plan) {
        ArrayList<Double> cumulativeDistances = new ArrayList<>();
        Double totalDistance = 0.0;

        // Go through all the pairs in the path to get the total to each vertex through all the intermediary edges
        for (Pair<String, GraphPath<String, ZooData.IdentifiedEdge>> path : plan) {
            if (path.getSecond() != null) {
                totalDistance += path.getSecond().getWeight();
            }
            cumulativeDistances.add(totalDistance);
        }
        return cumulativeDistances;
    }

    /**
     * Sets all exhibit items unselected in database
     * Closes the corresponding NavigationActivity which will subsequently return
     * to exhibit selection
     *
     * @param view the corresponding display View for the NavigationActivity
     */
    public void onClearPlanPressed(View view){
        NodeDao nodeDao = GraphDatabase.getSingleton(this).nodeDao();
        nodeDao.clearAll();
        finish();
    }
}


