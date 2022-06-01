package com.example.cse110_lab5.activity.exhibitlist;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.cse110_lab5.database.GraphDatabase;
import com.example.cse110_lab5.database.NodeDao;
import com.example.cse110_lab5.database.ZooData;

import java.util.List;

public class ExhibitListViewModel extends AndroidViewModel {
    private LiveData<List<ZooData.Node>> todoListItems;
    private final NodeDao nodeDao;

    public ExhibitListViewModel(@NonNull Application application) {
        super(application);
        Context context = getApplication().getApplicationContext();
        GraphDatabase db = GraphDatabase.getSingleton(context);
        nodeDao = db.nodeDao();
    }

    public LiveData<List<ZooData.Node>> getExhibitItems() {
        if (todoListItems == null) {
            loadExhibits();
        }

        return todoListItems;
    }

    private void loadExhibits() {
        todoListItems = nodeDao.getAllExhibitLive();
    }

    public LiveData<List<String>> getSelectedItems() {
        return nodeDao.getSelectedLive();
    }

    public void toggleSelected(ZooData.Node zooDataNode) {
        zooDataNode.selected = !zooDataNode.selected;
        nodeDao.update(zooDataNode);
    }

}
