package com.example.jumpmaster;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean isPlaying;
    private float screenX, screenY;
    private float screenRatioX, screenRatioY;
    private Paint paint;
    private TileMap[] levels;
    private int selectedLevel;
    private int topLevel;
    private int gameId;
    private int elapsedTimeSec;

    private long startGameTime;

    private Player player;

    private MediaPlayer mediaPlayer;
    private SharedPreferences prefs;

    private Background background1, background2, backgroundStill;

    public GameView(GameActivity activity, int screenX, int screenY, float playerX, float playerY, int numOfJumps, int numOfFalls, int selectedLevel, int topLevel, int elapsedTimeSec, int gameId) {
        super(activity);

        mediaPlayer = MediaPlayer.create(activity.getApplicationContext(), R.raw.theme);
        mediaPlayer.setLooping(true);

        prefs = activity.getSharedPreferences("game",Context.MODE_PRIVATE);

        if(!prefs.getBoolean("isMute",false))
            mediaPlayer.start();

        this.screenX = screenX;
        this.screenY = screenY;

        screenRatioX = 1080f / screenX;
        screenRatioY = 2340f / screenY;


        background1 = new Background(screenX, screenY, getResources(),R.drawable.background_01);
        background2 = new Background(screenX, screenY, getResources(),R.drawable.background_01);

        background2.x = screenX;

        backgroundStill = new Background(screenX,screenY,getResources(),R.drawable.no_sky_background);

        levels = new TileMap[2];
        levels[0] = new TileMap(getResources(), screenX, screenY, 0);
        levels[1] = new TileMap(getResources(), screenX, screenY, 1);


        player = new Player(screenY,screenX,getResources(),playerX,playerY, numOfJumps, numOfFalls);


        this.selectedLevel = selectedLevel;
        this.topLevel = topLevel;
        this.gameId = gameId;
        this.elapsedTimeSec = elapsedTimeSec;

        startGameTime = System.currentTimeMillis();

        paint = new Paint();
    }

    @Override
    public void run() {
        while (isPlaying){
            update();
            draw();
            sleep();
        }
    }

    private void update() {
        background1.x -= 1.5*screenRatioX;
        background2.x -= 1.5*screenRatioX;

        if (background1.x + background1.background.getWidth() < 0) {
            background1.x = screenX;
        }

        if (background2.x + background2.background.getWidth() < 0) {
            background2.x = screenX;
        }
        try {
            //kolize
            RectF playerRect = player.getCollisionShape();

            //prepinani levelu
            if (playerRect.bottom < 0) {
                selectedLevel++;
                if (topLevel < selectedLevel)
                    topLevel = selectedLevel;
                //vyhjál
                if(selectedLevel > levels.length-1){
                    pause();
                    //dodelat you won efect
                }
                player.y += screenY;
                playerRect = player.getCollisionShape();
            } else if (playerRect.bottom > screenY) {
                selectedLevel--;
                player.y -= screenY;
                playerRect = player.getCollisionShape();
            }

            //hranice steny
            if (playerRect.left < 0) {
                player.setX(0);
                if (player.isJumping && player.direction == Direction.LEFT) {
                    player.changeJumpDirection();
                } else
                    player.stopMove();

            }

            if ((playerRect.right > screenX)) {
                player.setX(screenX - (playerRect.right - playerRect.left));
                if (player.isJumping && player.direction == Direction.RIGHT) {
                    player.changeJumpDirection();
                } else
                    player.stopMove();

            }

            if (!levels[selectedLevel].isGround(playerRect.left, playerRect.right, playerRect.bottom) && !player.isJumping) {
                player.fall(screenRatioY);
            }


            RectF levelRect = levels[selectedLevel].isIntersectWithGround(playerRect);


            if (levelRect != null) {
                //nahore

                if (((((playerRect.right - levelRect.left) < (levelRect.bottom - playerRect.top)) || ((levelRect.right - playerRect.left) < (levelRect.bottom - playerRect.top)))) && player.isJumping) {

                    if (player.isJumping && player.isJumpingDown && ((levelRect.right - playerRect.left > playerRect.bottom - levelRect.top && player.direction == Direction.LEFT) || ((playerRect.right - levelRect.left > playerRect.bottom - levelRect.top && player.direction == Direction.RIGHT)))) {
                        player.stopJumping(levelRect.top, levelRect.bottom - levelRect.top);
                        if (player.doSvarta)
                            player.svartaJump(levelRect.top, levelRect.bottom - levelRect.top);
                    } else if (player.isFalling) {
                        player.svartaJump(levelRect.top, levelRect.bottom - levelRect.top);
                    }
                    //strana
                    //kontrola sousednich bloku
                    else {
                        player.changeJumpDirection();

                    }
                } else if (((playerRect.right - levelRect.left) > (levelRect.bottom - playerRect.top)) || ((levelRect.right - playerRect.left) > (levelRect.bottom - playerRect.top))/* playerRect.top < levelRect.bottom*/ && player.isJumping) {

                    player.changeJumpTopDirection();
                } else if (playerRect.bottom > levelRect.top && player.isFalling) {

                    player.svartaJump(levelRect.top, levelRect.bottom - levelRect.top);

                }
            }
        }
        catch (Exception e){

        }
    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(background1.background,background1.x,background1.y, paint);
            canvas.drawBitmap(background2.background,background2.x,background2.y, paint);

            if(selectedLevel == 0)
                canvas.drawBitmap(backgroundStill.background,backgroundStill.x,backgroundStill.y, paint);

            levels[selectedLevel].redraw(canvas);

            player.redraw(canvas);

            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        startGameTime = System.currentTimeMillis();
        thread.start();
    }

    public void pause () {
        try {
            isPlaying = false;
            elapsedTimeSec += (int)((System.currentTimeMillis() - startGameTime)/2000);
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onStop(){
        DBHelper mydb = new DBHelper(getContext());
        elapsedTimeSec += (int)((System.currentTimeMillis() - startGameTime)/2000);
        mydb.updateGame(player.x,player.y,selectedLevel,player.numOfJumps,player.numOfFalls,elapsedTimeSec,gameId,topLevel);
        if(!prefs.getBoolean("isMute",false))
            mediaPlayer.stop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN: {
                if(event.getX() < screenX * (1/3.0)) {
                    if(player.getLeftBoundary() >= 0)
                        player.move(Direction.LEFT, screenRatioX);
                }
                else if(event.getX() < screenX * (2/3.0)) {
                    //jump
                    player.prepareToJump();
                }
                else {
                    if(player.getRightBoundary() < screenX)
                        player.move(Direction.RIGHT,screenRatioX);
                }
                return true;
            }
            case MotionEvent.ACTION_UP: {
                player.stopMove();
                if(player.isReadyToJump) {
                    player.jump(screenRatioY);
                    safeBeforeJump();
                }
                break;
            }
        }

        return super.onTouchEvent(event);
    }

    private void safeBeforeJump(){
        DBHelper mydb = new DBHelper(getContext());
        elapsedTimeSec += (int)((System.currentTimeMillis() - startGameTime)/2000);
        mydb.updateGame(player.x,player.y,selectedLevel,player.numOfJumps,player.numOfFalls,elapsedTimeSec,gameId,topLevel);
        startGameTime = System.currentTimeMillis();
    }

}
