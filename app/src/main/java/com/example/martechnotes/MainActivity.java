package com.example.martechnotes;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    public Spinner spinner;
    private EditText edTopic, edContent;
    private FileManager flManager = new FileManager();
    private String editingName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edTopic = (EditText) findViewById(R.id.edTopic);
        edContent = (EditText) findViewById(R.id.edContent);

        listView = findViewById(R.id.lvList);
        spinner = findViewById(R.id.spNotes);

        Button buttonSave = (Button) findViewById(R.id.btnSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = edTopic.getText().toString();
                String content = edContent.getText().toString();

                if (editingName == null) {
                    String noteLine = name + "@" + content + "\n";
                    flManager.writeToFile(noteLine, getApplicationContext());
                    Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_LONG).show();
                } else {
                    flManager.updateNote(getApplicationContext(), editingName, name, content);
                    Toast.makeText(getApplicationContext(), "Updated!", Toast.LENGTH_LONG).show();
                    editingName = null;
                }

                refreshAll();
                edTopic.setText("");
                edContent.setText("");
            }
        });

        Button buttonRefresh = (Button) findViewById(R.id.btnRefresh);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                refreshAll();
                Toast.makeText(getApplicationContext(), "Refreshed!", Toast.LENGTH_SHORT).show();
            }
        });

        Button buttonEdit = findViewById(R.id.btnEdit);
        buttonEdit.setOnClickListener(v -> {
            String selectedName = (String) spinner.getSelectedItem();
            if (selectedName == null) {
                Toast.makeText(getApplicationContext(), "Nothing to edit", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<String> notes = flManager.readFromFile(getApplicationContext());
            for (String note : notes) {
                String[] parts = note.split("@", 2);
                if (parts[0].equals(selectedName)) {
                    edTopic.setText(parts[0]);
                    if (parts.length > 1) {
                        edContent.setText(parts[1]);
                    } else {
                        edContent.setText("");
                    }
                    editingName = selectedName;
                    break;
                }
            }
        });

        Button buttonDelete = findViewById(R.id.btnDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String selectedName = (String) spinner.getSelectedItem();

                if (selectedName == null) {
                    Toast.makeText(getApplicationContext(), "Nothing to delete", Toast.LENGTH_SHORT).show();
                    return;
                }

                flManager.deleteFromFile(getApplicationContext(), selectedName);
                Toast.makeText(getApplicationContext(), "Deleted!", Toast.LENGTH_SHORT).show();

                refreshAll();
            }
        });

    }

    private void refreshAll() {
        ArrayList<String> notes = flManager.readFromFile(getApplicationContext());
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_list_item_1,
                notes
        );
        listView.setAdapter(listAdapter);

        ArrayList<String> names = flManager.getNoteNames(getApplicationContext());
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                getApplicationContext(),
                android.R.layout.simple_spinner_item,
                names
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
    }
}