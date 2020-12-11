package com.example.jumpmaster;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private DBHelper mydb;
    private boolean isMute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mydb = new DBHelper(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final SharedPreferences prefs = getSharedPreferences("game", MODE_PRIVATE);

        isMute = prefs.getBoolean("isMute",false);

        final ImageView volumeControl = findViewById(R.id.volumeCTRL);

        if(isMute){
            volumeControl.setImageResource(R.drawable.ic_baseline_volume_off_24);
        }
        else{
            volumeControl.setImageResource(R.drawable.ic_baseline_volume_up_24);
        }

        volumeControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMute = !isMute;
                if(isMute){
                    volumeControl.setImageResource(R.drawable.ic_baseline_volume_off_24);
                }
                else{
                    volumeControl.setImageResource(R.drawable.ic_baseline_volume_up_24);
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isMute", isMute);
                editor.apply();
            }
        });

        findViewById(R.id.play).setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, InsertNameActivity.class),10);
            }
        });

        findViewById(R.id.textView2).setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, GameContinue.class));
            }
        });

        findViewById(R.id.total_stats_btn).setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TotalStatsActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode == 10 && requestCode == 10){
            String gameName;
            Bundle extra = data.getExtras();

            gameName = extra.getString("result");

            int id = mydb.createGame(gameName,0,0,0,0,0,0,0);
            Intent newGame = new Intent(MainActivity.this, GameActivity.class);
            newGame.putExtra("playerX",0);
            newGame.putExtra("playerY",0);
            newGame.putExtra("numOfJumps",0);
            newGame.putExtra("numOfFalls",0);
            newGame.putExtra("selectedLevel",0);
            newGame.putExtra("topLevel",0);
            newGame.putExtra("elapsedTimeSec",0);
            newGame.putExtra("gameId",id);

            startActivityForResult(newGame, 20);
        }
        if(resultCode == 10 && requestCode == 20) {

        }
    }
}