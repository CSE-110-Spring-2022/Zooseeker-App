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

    public synchronized static GraphDatabase getSingleton(Context context) {
        if(singleton == null) {
            singleton = GraphDatabase.makeDatabase(context);
        }
        return singleton;
    }

    private static GraphDatabase makeDatabase(Context context) {
        return Room.databaseBuilder(context, GraphDatabase.class, "graph.db")
                .allowMainThreadQueries()
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Executors.newSingleThreadScheduledExecutor().execute(() -> {
                            List<ZooData.Edge> edges = ZooData.Edge
                                    .loadJSON(context, "sample_edge_info.json");
                            getSingleton(context).edgeDao().insertAll(edges);

                            //TEST
                            List<ZooData.Node> nodes = ZooData.Node
                                    .loadJSON(context, "sample_node_info.json");
                            getSingleton(context).nodeDao().insertAll(nodes);

                        });
                    }
                })
                .build();
    }

    @VisibleForTesting
    public static void injectTestDatabase(GraphDatabase testDatabase) {
        if(singleton != null ) {
            singleton.close();
        }

        singleton = testDatabase;
    }
}
