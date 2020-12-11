package com.example.jumpmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.WindowManager;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);

        float playerX = getIntent().getFloatExtra("playerX",0);
        float playerY = getIntent().getFloatExtra("playerY",0);
        int numOfJumps = getIntent().getIntExtra("numOfJumps",0);
        int numOfFalls = getIntent().getIntExtra("numOfFalls",0);
        int selectedLevel = getIntent().getIntExtra("selectedLevel",0);
        int topLevel = getIntent().getIntExtra("topLevel",0);
        int elapsedTimeSec = getIntent().getIntExtra("elapsedTimeSec",0);
        int gameId = getIntent().getIntExtra("gameId",0);

        gameView = new GameView(this, point.x, point.y, playerX,playerY,numOfJumps,numOfFalls,selectedLevel,topLevel,elapsedTimeSec,gameId);

        setContentView(gameView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    protected void onStop(){
        super.onStop();
        gameView.onStop();
    }
}