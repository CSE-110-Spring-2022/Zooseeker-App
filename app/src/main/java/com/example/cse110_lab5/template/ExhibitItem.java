package com.example.cse110_lab5.template;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

//EDIT THIS TO MATCH ZOOSEEKER
@Entity(tableName = "todo_list_items")
public class ExhibitItem {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String text;
    public boolean selected;

    @Override
    public String toString() {
        return "TodoListItem{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", completed=" + selected +
                '}';
    }

    public ExhibitItem(String text){
        this.text = text;
        this.selected = false;
    }
}
