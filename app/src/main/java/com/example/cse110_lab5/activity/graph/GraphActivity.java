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
            g = loadZooGraphJSON(this, bundle.getString("path"));
            start = bundle.getString("start");
            toVisit = bundle.getStringArray("toVisit");
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

            int amtExs = oEadapter.getItemCount() -2;
            TextView total = findViewById(R.id.total);
            total.setText("Total: " + amtExs);
            recyclerView.setAdapter(oEadapter);

            Intent nav = new Intent(this, NavigationActivity.class);

            for (int i = 0;  i< plan.size(); i++) {
                nav.putExtra(String.valueOf(i), plan.get(i));
            }

	    nav.putExtra("toVisit", toVisit);

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
     * each node in visit before ending at start.
     *
     * @param g             The graph to search through
     * @param start         The id of the starting node
     * @param visit         The ids of each of the nodes to visit, excluding the start
     *
     * @return              The path represented as a list of pairs of nodes and the paths to reach
     *                      that node from the previous node. Includes the start node itself (with a
     *                      null path). Returns null if a given node to visit is unreachable.
     */
    public static List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> tsp(Graph<String, ZooData.IdentifiedEdge> g, String start, String[] visit) {
        List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> finalPath = new ArrayList<>();
        finalPath.add(new Pair<>(start, null));

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

        finalPath.add(new Pair<>(start, new DijkstraShortestPath<>(g).getPath(prev, start)));

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
}
