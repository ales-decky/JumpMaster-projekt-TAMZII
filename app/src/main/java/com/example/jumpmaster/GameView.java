package com.example.jumpmaster;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
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
    private int screenX, screenY;
    private float screenRatioX, screenRatioY;
    private Paint paint;
    private TileMap levels;
    private int selectedLevel;

    private Player player;

    private MediaPlayer mediaPlayer;

    private Background background1, background2;

    public GameView(GameActivity activity, int screenX, int screenY) {
        super(activity);


        mediaPlayer = MediaPlayer.create(activity.getApplicationContext(), R.raw.theme);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        this.screenX = screenX;
        this.screenY = screenY;

        screenRatioX = 1080f / screenX;
        screenRatioY = 2340f / screenY;


        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());

        background2.x = screenX;

        //levels = new TileMap[1];
        levels = new TileMap(getResources(), screenX, screenY);

        player = new Player(screenY,screenX,getResources());

        selectedLevel = 0;

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
        background1.x -= 3*screenRatioX;
        background2.x -= 3*screenRatioX;

        if (background1.x + background1.background.getWidth() < 0) {
            background1.x = screenX;
        }

        if (background2.x + background2.background.getWidth() < 0) {
            background2.x = screenX;
        }

        if((player.x + player.width > screenX) || (player.x < 0)){
            player.stopMove();
        }

        if(!levels.isGround(player.x,player.y+player.height) && !player.isJumping){
            player.fall(screenRatioY);
        }
        else {
            if(player.isFalling)
                player.svartaJump();
        }

    }

    private void draw() {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(background1.background,background1.x,background1.y, paint);
            canvas.drawBitmap(background2.background,background2.x,background2.y, paint);

            levels.redraw(canvas);

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
        thread.start();
    }

    public void pause () {
        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN: {
                if(event.getX() < screenX * (1/3.0)) {
                    if(player.x >= 0)
                        player.move(Direction.LEFT, screenRatioX);
                    else {
                        player.x = 0;
                    }
                }
                else if(event.getX() < screenX * (2/3.0)) {
                    //jump
                    player.jump(screenRatioX,0);
                }
                else {
                    if(player.x + player.width <= screenX)
                        player.move(Direction.RIGHT,screenRatioX);
                    else{
                        //?????????????
                        player.x = screenX - player.width;
                    }
                }
                return true;
            }
            case MotionEvent.ACTION_UP: {
                player.stopMove();
                break;
            }
        }

        return super.onTouchEvent(event);
    }

    public void checkColision(){
        //down direction
        //if(player.y + player.height)
    }
}
