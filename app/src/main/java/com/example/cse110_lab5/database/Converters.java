package com.example.cse110_lab5.database;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * This class has basic Converters that can go from a list of strings to a delimited string value
 * and from a delimited string to a list of strings. This comes in handy when serializing our list
 * exhibits, and for reading in tags from the json file
 */
public class Converters {
    @TypeConverter
    public static List<String> fromString(String value) {
        // Use GSON parsers to create a list of strings from a single string input
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(List<String> list) {
        // Use GSON parsers to convert a list of strings to a json serialized string
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}