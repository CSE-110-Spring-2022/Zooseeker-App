package com.example.cse110_lab5;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

@Entity(tableName = "nodes")
public class Node {
    @PrimaryKey @NonNull
    public String id;

    @NonNull
    public String kind;
    public String name;
    public List<String> tags;

    @Override
    public String toString() {
        return "TodoListItem{" +
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
    }

    // 3. Factory method for loading our JSON.
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
