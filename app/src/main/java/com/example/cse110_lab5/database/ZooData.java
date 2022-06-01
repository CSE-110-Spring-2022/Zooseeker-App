package com.example.cse110_lab5.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jgrapht.Graph;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultUndirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.json.JSONImporter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * Utility class containing the definitions of the Edge, Node, and IdentifiedEdge classes
 * for use in graphing and determining optimal paths. Also contains helper functions
 * contributing to those tasks.
 */
public class ZooData {

    // The graph representation of the Zoo
    public static Graph<String, IdentifiedEdge> graph = new DefaultUndirectedWeightedGraph<>(IdentifiedEdge.class);

    /**
     * Class defining an Edge object for use in the Edge database
     */
    @Entity(tableName = "edges")
    public static class Edge {
        @PrimaryKey
        @NonNull
        public String id;

        @NonNull
        public String street;

        @Override
        public String toString() {
            return "Edge{" +
                    "id=" + id +
                    ", street=" + street +
                    '}';
        }

        public Edge(String id, String street){
            this.id = id;
            this.street = street;
        }

        /**
         * Helper function to parse a JSON file into a list of Edges
         * @param context       the context to open the JSON reader stream
         * @param path          the path to the JSON file
         * @return              the list of Edges in the JSON
         */
        public static List<Edge> loadJSON(Context context, String path) {
            try {
                InputStream input = context.getAssets().open(path);
                Reader reader = new InputStreamReader(input);
                Gson gson = new Gson();
                Type type = new TypeToken<List<Edge>>(){}.getType();
                return gson.fromJson(reader, type);
            } catch (IOException e) {
                e.printStackTrace();
                return Collections. emptyList();
            }
        }

    }

    /**
     * Class defining a Node object for use in the Node database
     */
    @Entity(tableName = "nodes")
    public static class Node {
        @PrimaryKey @NonNull
        public String id;

        @NonNull
        public String kind;
        public String name;
        public List<String> tags;
        public boolean selected;
        public Double lat;
        public Double lng;

        public String parent_id;

        @Override
        public String toString() {
            return "Node{" +
                    "id=" + id +
                    ", kind='" + kind + '\'' +
                    ", name=" + name +
                    ", tags=" + tags +
                    ", lat=" + lat +
                    ", lng=" + lng +
                    '}';
        }

        /**
         * Constructor for normal exhibits (those that don't belong to exhibit_groups)
         */
        public Node(String id, String kind, String name, List<String> tags, Double lat, Double lng){
            this.id = id;
            this.kind = kind;
            this.name = name;
            this.tags = tags;
            this.selected = false;
            this.lat = lat;
            this.lng = lng;
        }

        /**
         * Constructor for exhibits that have parents (are part of exhibit_groups)
         */
        @Ignore //know idea what this does but it fixes the errors for multiple constructors
        public Node(String id, String kind, String name, List<String> tags, String parent_id){
            this.id = id;
            this.kind = kind;
            this.name = name;
            this.tags = tags;
            this.selected = false;
            this.parent_id = parent_id;
        }

        /**
         * Helper function to parse a JSON file into a list of Nodes
         * @param context       the context to open the JSON reader stream
         * @param path          the path to the JSON file
         * @return              the list of Nodes in the JSON
         */
        public static List<Node> loadJSON(Context context, String path) {
            try {
                InputStream input = context.getAssets().open(path);
                Reader reader = new InputStreamReader(input);
                Gson gson = new Gson();
                Type type = new TypeToken<List<Node>>(){}.getType();
                return gson.fromJson(reader, type);
            } catch (IOException e) {
                e.printStackTrace();
                return Collections. emptyList();
            }
        }
    }

    /**
     * Class representing an Edge for use in graphing
     */
    public static class IdentifiedEdge extends DefaultWeightedEdge {
        private String id = null;

        /**
         * Getter for the IdentifiedEdge's id
         * @return      the id of the IdentifiedEdge
         */
        public String getId() { return id; }

        /**
         * Setter for the IdentifiedEdge's id
         * @param id    the id to set
         */
        public void setId(String id) { this.id = id; }

        @Override
        public String toString() {
            return "(" + getSource() + " :" + id + ": " + getTarget() + ")";
        }

        /**
         * Get the ID of the target node in the graph
         * @return      the id of the target node
         */
        public String getTargetId() {
            return getTarget().toString();
        }

        /**
         * Get the id of the source node in the graph
         * @return      the id of the source node
         */
        public String getSourceId() {
            return getSource().toString();
        }

        /**
         * Function necessary for parsing IdentifiedEdges from JSONs
         */
        public static void attributeConsumer(Pair<IdentifiedEdge, String> pair, Attribute attr) {
            IdentifiedEdge edge = pair.getFirst();
            String attrName = pair.getSecond();
            String attrValue = attr.getValue();

            if (attrName.equals("id")) {
                edge.setId(attrValue);
            }
        }
    }

    /**
     * Helper function to load a Graph from JSON data
     *
     * @param context       the Application Context
     * @param path          the path to the JSON file
     * @return              a Graph object containing the graph represented by the JSON file
     */
    public static Graph<String, IdentifiedEdge> loadZooGraphJSON(Context context, String path) {
        graph = new DefaultUndirectedWeightedGraph<>(IdentifiedEdge.class);

        // Configure the JSONImporter
        JSONImporter<String, IdentifiedEdge> importer = new JSONImporter<>();
        importer.setVertexFactory(v -> v);
        importer.addEdgeAttributeConsumer(IdentifiedEdge::attributeConsumer);

        try {
            // Open an input stream to read from the JSON file
            InputStream inputStream = context.getAssets().open(path);
            Reader reader = new InputStreamReader(inputStream);

            importer.importGraph(graph, reader);

            GraphDatabase db = GraphDatabase.getSingleton(context);
            NodeDao nodeDao = db.nodeDao();
            EdgeDao edgeDao = db.edgeDao();

            // Load the exhibits that belong to groups into the Graph with 0 weight edges
            List<ZooData.Node> nodes = nodeDao.getExhibits();
            for (ZooData.Node node : nodes){
                if(node.parent_id != null){
                    graph.addVertex(node.id);
                    IdentifiedEdge edge = graph.addEdge(node.id, node.parent_id);
                    edge.setId(node.id + "_to_" + node.parent_id);
                    graph.setEdgeWeight(edge, 0);

                    // Load these new edges into the Edge database
                    edgeDao.insert(new Edge(edge.id, edge.id));
                }
            }
            return graph;
        } catch (IOException e) { // If the JSON file could not be opened for parsing
            Log.e("IO", "Could not load zoo graph info, " + e);
            return null;
        }
    }


}
