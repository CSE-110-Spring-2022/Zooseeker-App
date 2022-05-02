package com.example.cse110_lab5;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ManyToManyShortestPathsAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraManyToManyShortestPaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.nio.json.JSONImporter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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

        List<Node> nodes = Node.loadJSON(this, "sample_node_info.json");
        List<Edge> edges = Edge.loadJSON(this, "sample_edge_info.json");

        Graph<String, IdentifiedWeightedEdge> g = ZooData.loadZooGraphJSON(this, "sample_zoo_graph.json");
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




        /**
        viewModel = new ViewModelProvider(this)
                .get(GraphViewModel.class);


        TodoListAdapter adapter = new TodoListAdapter();
        adapter.setHasStableIds(true);
        adapter.setOnCheckBoxClickedHandler(viewModel::toggleCompleted);
        adapter.setOnDeleteClickedHandler(viewModel::deleteTodo);
        adapter.setOnTextEditedHandler(viewModel::updateText);
        viewModel.getTodoListItems().observe(this, adapter::setTodoListItems);

        recyclerView = findViewById(R.id.todo_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        this.newTodoText = this.findViewById(R.id.new_todo_text);
        this.addTodoButton = this.findViewById(R.id.add_todo_btn);

        addTodoButton.setOnClickListener(this::onAddTodoClicked);

        //adapter.setTodoListItems(TodoListItem.loadJSON(this, "demo_todos.json"));
        */
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
    public static List<Pair<String, GraphPath<String, IdentifiedWeightedEdge>>> tsp(Graph<String, IdentifiedWeightedEdge> g, String start, String[] visit) {
        List<Pair<String, GraphPath<String, IdentifiedWeightedEdge>>> finalPath = new ArrayList<>();
        finalPath.add(new Pair<>(start, null));

        Set<String> remaining = new HashSet<>(Arrays.asList(visit));
        String prev = start;

        while (!remaining.isEmpty()) {
            ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<String, IdentifiedWeightedEdge> paths =
                    new DijkstraManyToManyShortestPaths<>(g)
                            .getManyToManyPaths(
                                    new HashSet<>(Arrays.asList(prev)), remaining);

            GraphPath<String, IdentifiedWeightedEdge> shortestPath = findShortestPath(paths);
            finalPath.add(new Pair<>(shortestPath.getEndVertex(), shortestPath));
            ;
            prev = shortestPath.getEndVertex();
            remaining.remove(prev);
        }

        finalPath.add(new Pair<>(start, new DijkstraShortestPath<>(g).getPath(prev, start)));

        return finalPath;
    }

    public static GraphPath<String, IdentifiedWeightedEdge> findShortestPath(ManyToManyShortestPathsAlgorithm.ManyToManyShortestPaths<String, IdentifiedWeightedEdge> paths) {
        String source = paths.getSources().iterator().next();

        GraphPath<String, IdentifiedWeightedEdge> currShortest = null;
        double shortestDistance = Double.MAX_VALUE;

        for (String node : paths.getTargets()) {
            GraphPath<String, IdentifiedWeightedEdge> path = paths.getPath(source, node);
            if (path.getWeight() < shortestDistance) {
                currShortest = path;
                shortestDistance = path.getWeight();
            }
        }

        return currShortest;
    }



    /**
    void onAddTodoClicked(View view) {
        String text = newTodoText.getText().toString();
        newTodoText.setText("");
        viewModel.createTodo(text);
    }
     */
}