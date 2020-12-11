package com.example.jumpmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class TotalStatsActivity extends AppCompatActivity {

    DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_total_stats);

        TextView jumps = findViewById(R.id.jumps);
        TextView falls = findViewById(R.id.falls);
        TextView elapsedTime = findViewById(R.id.elapsedTime);
        TextView topLevel = findViewById(R.id.topLevel);

        mydb = new DBHelper(this);


        Cursor res = mydb.getTotalStats();

        res.moveToFirst();

        jumps.setText("Number of jumps: " + res.getInt(0));
        falls.setText("Number of falls: " + res.getInt(1));
        elapsedTime.setText("Elapsed time: " + res.getInt(2) + " sec");
        topLevel.setText("Top achieved level: " + (res.getInt(3)+1));

    }
}