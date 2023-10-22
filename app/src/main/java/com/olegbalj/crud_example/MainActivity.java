package com.olegbalj.crud_example;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final String dbName = "tasks.db";


    SQLiteDatabase tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tasks = openOrCreateDatabase(dbName, MODE_PRIVATE, null);
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        LinearLayout tasksLayout = findViewById(R.id.linearLayout);

        tasks.execSQL("CREATE TABLE IF NOT EXISTS tasks(id INTEGER PRIMARY KEY AUTOINCREMENT,task TEXT)");

        createCheckboxes(tasksLayout);

        ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        createCheckboxes(tasksLayout);
                    }
                });

        fab.setOnClickListener(listener -> {
            Intent i = new Intent(getApplicationContext(), Add.class);
            someActivityResultLauncher.launch(i);
        });
    }

    void createCheckboxes(LinearLayout linearLayout) {
        List<List<Object>> checkBoxes = new ArrayList<>();

        Cursor g = tasks.rawQuery("SELECT * FROM tasks", null);

        for (int i = 0; i < g.getCount(); i++) {
            g.moveToNext();
            checkBoxes.add(Arrays.asList(g.getInt(0), new CheckBox(MainActivity.this)));
            ((CheckBox) checkBoxes.get(i).get(1)).setText(g.getString(1));
        }

        checkBoxes.forEach(it -> ((CheckBox) it.get(1)).setOnClickListener((listener) -> {
            tasks.execSQL("DELETE FROM tasks WHERE id = " + it.get(0) + "");
            linearLayout.removeView((CheckBox)it.get(1));
        }));
        checkBoxes.forEach(it -> ((CheckBox) it.get(1)).setOnLongClickListener(listener -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            EditText editText = new EditText(this);
            alert.setTitle("Edit task");
            alert.setView(editText);
            alert.setPositiveButton("UPDATE", (dialog, whichButton) -> {
                tasks.execSQL("UPDATE tasks SET task = $arg1 WHERE id = $arg2", new String[]{editText.getText().toString(), String.valueOf(it.get(0))});
                        createCheckboxes(linearLayout);
            });

            alert.setNegativeButton("No Option", (dialog, whichButton) -> dialog.dismiss());

            alert.show();
            return false;
        }));
        checkBoxes.forEach(it -> ((CheckBox) it.get(1)).setPadding(10, 10, 10, 10));
        assert linearLayout != null : "tasks Layout is null!!!";
        linearLayout.removeAllViews();
        checkBoxes.forEach(it -> linearLayout.addView((CheckBox) it.get(1)));
        g.close();
    }
}