package com.example.jumpmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Dictionary;

public class GameContinue extends AppCompatActivity {

    DBHelper mydb;
    ListView listView;
    ArrayAdapter<Pair<Integer, String>> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_continue);

        mydb = new DBHelper(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ArrayList<Pair<Integer, String>> gameList = mydb.getGameList();
        arrayAdapter = new ArrayAdapter<Pair<Integer, String>>(this,android.R.layout.simple_list_item_1, gameList);

        listView = findViewById(R.id.listView);

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Pair<Integer, String> myItem = (Pair<Integer, String>)(listView.getItemAtPosition(position));

                Intent intent = new Intent(getApplicationContext(),GameActivity.class);

                Cursor res = mydb.loadGame(myItem.first);

                res.moveToFirst();

                intent.putExtra("playerX", res.getFloat(res.getColumnIndex("playerX")));
                intent.putExtra("playerY", res.getFloat(res.getColumnIndex("playerY")));
                intent.putExtra("numOfJumps", res.getInt(res.getColumnIndex("numOfJumps")));
                intent.putExtra("numOfFalls", res.getInt(res.getColumnIndex("numOfFalls")));
                intent.putExtra("selectedLevel", res.getInt(res.getColumnIndex("selectedLevel")));
                intent.putExtra("elapsedTimeSec", res.getInt(res.getColumnIndex("elapsedTimeSec")));
                intent.putExtra("topLevel", res.getInt(res.getColumnIndex("topLevel")));
                intent.putExtra("gameId",myItem.first);

                startActivity(intent);
                finish();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){
                Pair<Integer, String> myItem = (Pair<Integer, String>)(listView.getItemAtPosition(position));
                arrayAdapter.remove(myItem);
                return mydb.deleteGame(myItem.first);
            }
        });

    }
}