package com.example.cse110_lab5;

import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.cse110_lab5.database.ZooData;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TestGraph {
    @Test
    public void testGraph() {
        Graph<String, ZooData.IdentifiedEdge> g = new DefaultUndirectedWeightedGraph<>(ZooData.IdentifiedEdge.class);

        g.addVertex("entrance_exit_gate");
        g.addVertex("entrance_plaza");
        g.addVertex("gorillas");
        g.addVertex("lions");
        g.addVertex("gators");
        g.addVertex("elephant_odyssey");
        g.addVertex("arctic_foxes");

        g.setEdgeWeight(g.addEdge("entrance_exit_gate", "entrance_plaza"), 10);
        g.setEdgeWeight(g.addEdge("entrance_plaza", "gorillas"), 200);
        g.setEdgeWeight(g.addEdge("gorillas", "lions"), 200);
        g.setEdgeWeight(g.addEdge("lions", "elephant_odyssey"), 200);
        g.setEdgeWeight(g.addEdge("entrance_plaza", "arctic_foxes"), 300);
        g.setEdgeWeight(g.addEdge("entrance_plaza", "gators"), 100);
        g.setEdgeWeight(g.addEdge("gators", "lions"), 200);

        String start1 = "entrance_exit_gate";
        String[] toVisit1 = {"lions", "elephant_odyssey"};

        List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> test1 = GraphActivity.tsp(g, start1, toVisit1);
        // Verify that this goes through 4 total exhibits (beginning, lions, elephants, exit) when 2 are provided
        assertEquals(4, test1.size());

        String start2 = "entrance_exit_gate";
        String[] toVisit2 = {};

        // Verify that this goes through 2 total exhibits (beginning and exit) when 0 are provided
        List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> test2 = GraphActivity.tsp(g, start2, toVisit2);
        assertEquals(2, test2.size());

        ArrayList<String> totalPath = new ArrayList<>();
        for (Pair<String, GraphPath<String, ZooData.IdentifiedEdge>> pairPath : test1) {
            String exhibit = pairPath.getFirst();

            GraphPath<String, ZooData.IdentifiedEdge> path = pairPath.getSecond();
            if (path != null) {
                totalPath.addAll(path.getVertexList().subList(1, path.getVertexList().size()));
            }
        }

        // Verify that the overall path created from start to finish was the correct length for the non-zero case
        assertEquals(8, totalPath.size());

    }

}
