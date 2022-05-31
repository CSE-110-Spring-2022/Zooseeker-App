package com.example.cse110_lab5.activity.graph;

import static com.example.cse110_lab5.database.ZooData.loadZooGraphJSON;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse110_lab5.activity.navigation.NavigationActivity;
import com.example.cse110_lab5.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Graph<String, ZooData.IdentifiedEdge> g = null;
        String start = "";
        String[] toVisit = {};

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            toVisit = bundle.getStringArray("toVisit");
            g = loadZooGraphJSON(this, bundle.getString("filepath"));
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

            List<Pair<String,GraphPath<String, ZooData.IdentifiedEdge>>> plan = tsp(g, start, toVisit);
            GraphAdapter oEadapter = new GraphAdapter(this, plan);
            recyclerView.setAdapter(oEadapter);

            TextView total = findViewById(R.id.total);
            total.setText("Total: " + oEadapter.getItemCount());

            Intent nav = new Intent(this, NavigationActivity.class);
            nav.putExtra("plan", getOrderedExhibits(plan));

            final Button button = findViewById(R.id.nav_bttn);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(nav);
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
        String prev = path.get(path.size()-1).getFirst();
        path.add(new Pair<>(end, new DijkstraShortestPath<>(g).getPath(prev, end)));
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
    public static String[] getOrderedExhibits(List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> plan) {
        ArrayList<String> orderedExhibits = new ArrayList<>();

        for(int i = 0; i < plan.size(); i++) {
            orderedExhibits.add(plan.get(i).getFirst());
        }

        return (String[]) orderedExhibits.toArray();
    }
}
