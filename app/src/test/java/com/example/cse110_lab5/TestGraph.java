package com.example.cse110_lab5;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.cse110_lab5.activity.graph.GraphActivity;
import com.example.cse110_lab5.database.ZooData;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class TestGraph {

    private Graph<String, ZooData.IdentifiedEdge> g;

    /**
     * Initialize sample data graph for testing
     */
    @Before
    public void initGraph() {
        g = new DefaultUndirectedWeightedGraph<>(ZooData.IdentifiedEdge.class);

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
    }

    /**
     * Ensure that path-generating algorithm safely returns null when unreachable exhibits are given
     */
    @Test
    public void unreachableDestinationTest() {
        g.addVertex("unreachable_node");

        String start = "entrance_exit_gate";
        String[] toVisit = {"unreachable_node"};

        List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> path = GraphActivity.tsp(g, start, toVisit);

        assertNull(path);
    }

    /**
     * Ensure that path-generating algorithm safely returns null when nonexistent exhibits are given
     */
    @Test
    public void invalidDestinationTest() {
        String start = "entrance_exit_gate";
        String[] toVisit = {"invalid_node"};

        List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> path = GraphActivity.tsp(g, start, toVisit);

        assertNull(path);
    }

    /**
     * Ensure that path-generating algorithm safely returns an "empty" path when there are no exhibits given
     */
    @Test
    public void testNoPath() {
        String start = "entrance_exit_gate";
        String[] toVisit = {};

        List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> path = GraphActivity.tsp(g, start, toVisit);

        // Ensure that the generated path is of expected size (just the entrance and exit)
        assertEquals(0, path.size());
    }

    /**
     * Ensure that path-generating algorithm returns the correct shortest path in the normal case
     */
    @Test
    public void testPath() {
        String start = "entrance_exit_gate";
        String[] toVisit = {"lions", "elephant_odyssey"};

        List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> paths = GraphActivity.tsp(g, start, toVisit);

        // Ensure that the generated path is of expected size (entrance and exit, and two exhibits)
        assertEquals(2, paths.size());

        // Ensure that the paths between each exhibit are the smallest possible size and have correct lengths
        int[] expectedWeights = {200, 510};
        int[] expectedLengths = {1, 4};
        for(int i = 1; i < paths.size(); i++) {
            Pair<String, GraphPath<String, ZooData.IdentifiedEdge>> pair = paths.get(i);
            if (pair.getSecond() != null) {
                assertEquals(expectedWeights[i - 1], pair.getSecond().getWeight(), 0.0001);
                assertEquals(expectedLengths[i - 1], pair.getSecond().getLength());
            }
        }
    }

    /**
     * Ensure that path-generating algorithm that tracks back to the entrance gate following
     * visiting list of exhibits returns the correct shortest path
     */
    @Test
    public void testEndingNodeShortestPath(){
        String start = "entrance_exit_gate";
        String[] toVisit = {"lions", "gorillas", "arctic_foxes"};
        List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> paths = GraphActivity.tsp(g, start, toVisit, start);
        assertEquals(4, paths.size());
        System.out.println(paths.toString());
        int[] expectedWeights = {200, 600, 310, 200};
        int[] expectedLengths = {1, 3, 2, 2};
        for(int i = 1; i < paths.size(); i++) {
            Pair<String, GraphPath<String, ZooData.IdentifiedEdge>> pair = paths.get(i);
            if (pair.getSecond() != null) {
                System.out.println(pair.getSecond().getLength());
                assertEquals(expectedWeights[i - 1], pair.getSecond().getWeight(), 0.0001);
                assertEquals(expectedLengths[i - 1], pair.getSecond().getLength());
            }
        }
    }

    /**
     * Ensure that the iterative algorithm returning cumulative distances returned for the route
     * summary page are correct
     */
    @Test
    public void testGenerateCumulativeDistances(){
        String start = "entrance_exit_gate";
        String[] toVisit = {"lions", "gorillas", "arctic_foxes"};
        List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> paths = GraphActivity.tsp(g, start, toVisit);

        List<Double> cumulativeDistances = GraphActivity.generateCumulativeDistances(paths);
        List<Double> expectedDistances = new ArrayList<>();
        expectedDistances.add(210.0);
        expectedDistances.add(410.0);
        expectedDistances.add(1010.0);
        for (int i = 0; i < cumulativeDistances.size(); i++){
            assertEquals(cumulativeDistances.get(i), expectedDistances.get(i), 0.0001);
        }
    }

    /**
     * Ensure that the algorithm for extracting the order of exhibits returns the list of path
     * exhibits in the correct order
     */
    @Test
    public void testGetExhibitNames(){
        String start = "entrance_exit_gate";
        String[] toVisit = {"lions", "gorillas", "arctic_foxes"};
        List<Pair<String, GraphPath<String, ZooData.IdentifiedEdge>>> paths = GraphActivity.tsp(g, start, toVisit);

        ArrayList<String> exhibitNames = GraphActivity.getExhibitNames(paths);
        ArrayList<String> expectedExhibitNames =
                new ArrayList<>(Arrays.asList("gorillas", "lions", "arctic_foxes"));
        assertEquals(exhibitNames, expectedExhibitNames);
    }
}
