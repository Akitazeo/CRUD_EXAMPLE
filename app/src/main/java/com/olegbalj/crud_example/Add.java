package com.olegbalj.crud_example;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Add extends AppCompatActivity {
    final String dbName = "tasks.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        SQLiteDatabase tasks = openOrCreateDatabase(dbName, MODE_PRIVATE, null);
        Button addButton = findViewById(R.id.Add);
        Button cancelButton = findViewById(R.id.Cancel);
        EditText taskDescription = findViewById(R.id.editTextText);

        addButton.setOnClickListener(it -> {
            if (taskDescription != null)
                if (!taskDescription.getText().toString().equals("")) {
                    tasks.execSQL("INSERT OR IGNORE INTO tasks(task) VALUES ('" + taskDescription.getText().toString() + "')");
                    setResult(Activity.RESULT_OK);
                }
            cancelButton.callOnClick();
        });

        cancelButton.setOnClickListener(it -> this.finish());
    }
}