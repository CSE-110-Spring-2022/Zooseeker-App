package com.example.cse110_lab5.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Entity;
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

public class ZooData {

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

    @Entity(tableName = "nodes")
    public static class Node {
        @PrimaryKey @NonNull
        public String id;

        @NonNull
        public String kind;
        public String name;
        public List<String> tags;
        public boolean selected;

        @Override
        public String toString() {
            return "Node{" +
                    "id=" + id +
                    ", kind='" + kind + '\'' +
                    ", name=" + name +
                    ", tags=" + tags +
                    '}';
        }

        public Node(String id, String kind, String name, List<String> tags){
            this.id = id;
            this.kind = kind;
            this.name = name;
            this.tags = tags;
            this.selected = false;
        }

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

    public static class IdentifiedEdge extends DefaultWeightedEdge {
        private String id = null;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        @Override
        public String toString() {
            return "(" + getSource() + " :" + id + ": " + getTarget() + ")";
        }

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
        Graph<String, IdentifiedEdge> g = new DefaultUndirectedWeightedGraph<>(IdentifiedEdge.class);

        JSONImporter<String, IdentifiedEdge> importer = new JSONImporter<>();
        importer.setVertexFactory(v -> v);
        importer.addEdgeAttributeConsumer(IdentifiedEdge::attributeConsumer);

        try {
            InputStream inputStream = context.getAssets().open(path);
            Reader reader = new InputStreamReader(inputStream);

            importer.importGraph(g, reader);
            return g;
        } catch (IOException e) {
            Log.e("IO", "Could not load zoo graph info, " + e);
            return null;
        }
    }


}
