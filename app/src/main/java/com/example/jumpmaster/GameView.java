package com.example.jumpmaster;

import android.content.Context;
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

        //hranice steny
        if((player.getRightBoundary() > screenX) || (player.getLeftBoundary() < 0)){
            if(player.isJumping){
                player.changeJumpDirection();
            }
            player.stopMove();
        }

        /*if(!levels.isGround(player.getLeftBoundary(),player.getRightBoundary(),player.getBottomBoundary()) && !player.isJumping){
            player.fall(screenRatioY);
        }
        else {
            if(player.isFalling) {
                player.svartaJump();
            }
        }*/

        //kontrola skoku
        /*if(player.isJumping && levels.isGround(player.getLeftBoundary(),player.getRightBoundary(),player.getBottomBoundary())){
            player.stopJumping();
        }*/

        //kolize
        RectF playerRect = player.getCollisionShape();
        RectF levelRect = levels.isIntersectWithGround(playerRect);
        if(levelRect != null){
            //nahore
            if(playerRect.bottom < levelRect.bottom){
                if(player.isJumping){
                    player.stopJumping(levelRect.top);
                }
                else if(player.isFalling) {
                    player.svartaJump();
                }
            }
            //leva strana

            if(playerRect.left > levelRect.left && player.isJumping)
                 player.changeJumpDirection();
            //prava strana
            if(playerRect.right < levelRect.right && player.isJumping)
                player.changeJumpDirection();

            else{
                player.fall(screenRatioY);
            }
            if(playerRect.top > levelRect.top && player.isJumping){
                player.changeJumpTopDirection();
            }
        }
        else if(!player.isJumping) {
            player.fall(screenRatioY);
        }

        /*if(player.isJumping && levels.isBouncable(player.getLeftBoundary(),player.getRightBoundary(),player.getBottomBoundary())){
            player.changeJumpDirection();
        }*/

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
                }
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
