package com.example.martechnotes;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class FileManager {
    public void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(Constants.Notes_File_Name, Context.MODE_APPEND));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
    public ArrayList <String> readFromFile(Context context) {
        ArrayList <String> notes = new ArrayList<>();
        try {
            FileInputStream fis = context.openFileInput(Constants.Notes_File_Name);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                notes.add(line);
            }
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
        return notes;
    }
    public ArrayList<String> getNoteNames(Context context) {
        ArrayList<String> names = new ArrayList<>();
        for (String note : readFromFile(context)) {
            String[] parts = note.split("@", 2);
            names.add(parts[0]);
        }
        return names;
    }
    public void deleteFromFile(Context context, String nameToDelete) {
        ArrayList<String> notes = readFromFile(context);
        ArrayList<String> remaining = new ArrayList<>();

        for (String note : notes) {
            String[] parts = note.split("@", 2);
            String name = parts[0];
            if (!name.equals(nameToDelete)) {
                remaining.add(note);
            }
        }

        try {
            OutputStreamWriter writer = new OutputStreamWriter(
                    context.openFileOutput(Constants.Notes_File_Name, Context.MODE_PRIVATE));
            for (String note : remaining) {
                writer.write(note + "\n");
            }
            writer.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

    }
    public ArrayList<String> updateNote(Context context, String newName, String oldName, String updatedContent) {
        ArrayList<String> notes = readFromFile(context);
        ArrayList<String> updatedNotes = new ArrayList<>();
        for (String note: notes){
            String[] parts = note.split("@", 2);
            String name = parts[0];
            if (name.equals(oldName)){
                updatedNotes.add(newName + "@" + updatedContent);
        }
            else {
                updatedNotes.add(note);
            }
        }
        try{
            OutputStreamWriter writer = new OutputStreamWriter(
            context.openFileOutput(Constants.Notes_File_Name, Context.MODE_PRIVATE));
            for (String note : updatedNotes){
                writer.write(note + "\n");
            }
            writer.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
        return updatedNotes;
    }
}
