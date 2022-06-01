package com.example.cse110_lab5.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.List;
import java.util.concurrent.Executors;

@Database(entities = {ZooData.Node.class, ZooData.Edge.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class GraphDatabase extends RoomDatabase {
    private static GraphDatabase singleton = null;

    public abstract NodeDao nodeDao();
    public abstract EdgeDao edgeDao();

    /**
     * Get the GraphDatabase singleton
     * @param context       the context to construct the GraphDatabase in, if it doesn't already
     *                      exist
     * @return              the singular GraphDatabase object
     */
    public synchronized static GraphDatabase getSingleton(Context context) {
        if(singleton == null) {
            singleton = GraphDatabase.makeDatabase(context);
        }
        return singleton;
    }

    /**
     * Construct a GraphDatabase
     * @param context       the context to construct the GraphDatabase in
     * @return              the constructed GraphDatabase object
     */
    private static GraphDatabase makeDatabase(Context context) {
        return Room.databaseBuilder(context, GraphDatabase.class, "graph.db")
                .allowMainThreadQueries()
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        // Use a separate thread to populate the Edge and Node databases
                        Executors.newSingleThreadScheduledExecutor().execute(() -> {
                            // Load the Edges into the database from the JSON asset
                            List<ZooData.Edge> edges = ZooData.Edge
                                    .loadJSON(context, "sample_edge_info.json");
                            getSingleton(context).edgeDao().insertAll(edges);

                            // Load the Nodes into the database from the JSON asset
                            List<ZooData.Node> nodes = ZooData.Node
                                    .loadJSON(context, "sample_node_info.json");
                            getSingleton(context).nodeDao().insertAll(nodes);

                        });
                    }
                })
                .build();
    }

    /**
     * Allow for injecting a GraphDatabase into the application for the purposes of testing
     * @param testDatabase      the GraphDatabase to inject
     */
    @VisibleForTesting
    public static void injectTestDatabase(GraphDatabase testDatabase) {
        if(singleton != null ) {
            singleton.close();
        }

        singleton = testDatabase;
    }
}
