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

    ActivityResultLauncher<Intent> navLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    finish();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ZooData.loadZooGraphJSON(this, "sample_zoo_graph.json");

        String start = "";
        String[] toVisit = {};

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            toVisit = bundle.getStringArray("toVisit");
            start = bundle.getString("start");
        }

        if (toVisit.length == 0) {
            setContentView(R.layout.no_plan);
            Log.d("Graph", "No exhibits were selected for planning");
        }
        else {
            Log.d("Graph", toVisit.length + " exhibits were selected for planning");
            setContentView(R.layout.planned_exhibits);

            recyclerView = (RecyclerView) findViewById(R.id.planned_exhibits);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            List<Pair<String,GraphPath<String, ZooData.IdentifiedEdge>>> plan = tsp(ZooData.graph, start, toVisit);
            GraphAdapter oEadapter = new GraphAdapter(this, plan, generateCumulativeDistances(plan));
            recyclerView.setAdapter(oEadapter);


            TextView total = findViewById(R.id.total);
            total.setText("Total: " + oEadapter.getItemCount());

            Intent nav = new Intent(this, NavigationActivity.class);
            ArrayList<String> orderedExhibits = getOrderedExhibits(plan);
            orderedExhibits.add(start);
            nav.putExtra("curr_exhibit", 0);
            nav.putExtra("plan", orderedExhibits.toArray(new String[orderedExhibits.size()]));

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
        List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> path = tsp(g, start, visit);
        if(path.size() != 0) {
            String prev = path.get(path.size() - 1).getFirst();
            path.add(new Pair<>(end, new DijkstraShortestPath<>(g).getPath(prev, end)));
        } else {
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

        Set<String> remaining = new HashSet<>(Arrays.asList(visit));
        String prev = start;

        while (!remaining.isEmpty()) {
            ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<String, ZooData.IdentifiedEdge> paths =
                    new DijkstraManyToManyShortestPaths<>(g)
                            .getManyToManyPaths(
                                    new HashSet<>(Arrays.asList(prev)), remaining);

            GraphPath<String, ZooData.IdentifiedEdge> shortestPath = findShortestPath(paths);
            if (shortestPath == null) {
                Log.d("TSP Paths", "Unreachable destination given");
                return null;
            }

            finalPath.add(new Pair<>(shortestPath.getEndVertex(), shortestPath));
            prev = shortestPath.getEndVertex();
            remaining.remove(prev);
        }

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
        double shortestDistance = Double.MAX_VALUE;

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
     * Helper function to get the list of exhibits we plan to visit, in the order we plan to visit
     * them in.
     *
     * @param plan      a List of pairs of (destination, path_to_destination)
     * @return          the list of exhibits in order in String[] format
     */
    public static ArrayList<String> getOrderedExhibits(List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> plan) {
        ArrayList<String> orderedExhibits = new ArrayList<>();

        for(int i = 0; i < plan.size(); i++) {
            orderedExhibits.add(plan.get(i).getFirst());
        }

        return orderedExhibits;
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


