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

@Entity(tableName = "edges")
public class Edge {
    @PrimaryKey @NonNull
    public String id;

    @NonNull
    public String street;

    @Override
    public String toString() {
        return "TodoListItem{" +
                "id=" + id +
                ", street=" + street +
                '}';
    }

    public Edge(String id, String street){
        this.id = id;
        this.street = street;
    }

    // 3. Factory method for loading our JSON.
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
