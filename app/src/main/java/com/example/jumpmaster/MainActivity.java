package com.example.jumpmaster;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    private DBHelper mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mydb = new DBHelper(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        findViewById(R.id.play).setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, InsertNameActivity.class),10);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == 10){
            String gameName = data.getStringExtra("gameName");
            mydb.createGame(gameName,0,0,0,0,0,0,0);
            Intent newGame = new Intent(MainActivity.this, GameActivity.class);
            newGame.putExtra("playerX",0);
            newGame.putExtra("playerY",0);
            newGame.putExtra("numOfJumps",0);
            newGame.putExtra("numOfFalls",0);
            newGame.putExtra("selectedLevel",0);
            newGame.putExtra("elapsedTimeSec",0);

            startActivity(newGame);
        }
    }
}