package com.example.cse110_lab5;

import static com.example.cse110_lab5.database.ZooData.loadZooGraphJSON;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse110_lab5.GraphViewModel;
import com.example.cse110_lab5.OrderedExhibitsAdapter;
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
    private GraphViewModel viewModel;
    private EditText newTodoText;
    private Button addTodoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.planned_exhibits);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher_round);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(" ZooSeeker");

        Graph<String, ZooData.IdentifiedEdge> g = loadZooGraphJSON(this, "sample_zoo_graph.json");
        String start = "entrance_exit_gate";
        String[] toVisit = {};

        recyclerView = (RecyclerView) findViewById(R.id.planned_exhibits);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        OrderedExhibitsAdapter oEadapter = new OrderedExhibitsAdapter(tsp(g, start, toVisit));
        if (oEadapter.getItemCount() == 2) {
            setContentView(R.layout.no_plan);
        }
        else{
            recyclerView.setAdapter(oEadapter);
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
     *                      null path).
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
            finalPath.add(new Pair<>(shortestPath.getEndVertex(), shortestPath));
            ;
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
            if (path.getWeight() < shortestDistance) {
                currShortest = path;
                shortestDistance = path.getWeight();
            }
        }

        return currShortest;
    }
}