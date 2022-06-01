package com.example.cse110_lab5.activity.graph;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.cse110_lab5.database.EdgeDao;
import com.example.cse110_lab5.database.GraphDatabase;
import com.example.cse110_lab5.database.NodeDao;

import java.util.List;

public class GraphViewModel extends AndroidViewModel {
    private final EdgeDao edgeDao;
    private final NodeDao nodeDao;

    public GraphViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        GraphDatabase db = GraphDatabase.getSingleton(context);
        edgeDao = db.edgeDao();
        nodeDao = db.nodeDao();
    }
}
