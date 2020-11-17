package com.example.jumpmaster;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

public class Player {
    public int x;
    public int y;
    public Direction direction;
    public PlayerState playerState;
    private int anim;

    public int width;
    public int height;

    private float stepX;
    private float stepY;

    private boolean isLaying = false;

    public boolean isFalling = false;

    public boolean isJumping = false;

    private Bitmap[] idle;
    private Bitmap[] moving;
    private Bitmap[] jumping;
    private Bitmap[] fall;

    public Player(int screenY, int screenX, Resources res){
        idle = new Bitmap[]{
                BitmapFactory.decodeResource(res, R.drawable.idle_01),
                BitmapFactory.decodeResource(res, R.drawable.idle_02),
                BitmapFactory.decodeResource(res, R.drawable.idle_03),
                BitmapFactory.decodeResource(res, R.drawable.idle_04),
                BitmapFactory.decodeResource(res, R.drawable.idle_05),
                BitmapFactory.decodeResource(res, R.drawable.idle_06)
                };

        moving = new Bitmap[]{
                BitmapFactory.decodeResource(res, R.drawable.run_01),
                BitmapFactory.decodeResource(res, R.drawable.run_02),
                BitmapFactory.decodeResource(res, R.drawable.run_03),
                BitmapFactory.decodeResource(res, R.drawable.run_04),
                BitmapFactory.decodeResource(res, R.drawable.run_05),
                BitmapFactory.decodeResource(res, R.drawable.run_06),
                BitmapFactory.decodeResource(res, R.drawable.run_07)
        };

        jumping = new Bitmap[]{
                BitmapFactory.decodeResource(res, R.drawable.jump_01),
                BitmapFactory.decodeResource(res, R.drawable.jump_02),
                BitmapFactory.decodeResource(res, R.drawable.jump_landing_01),
                BitmapFactory.decodeResource(res, R.drawable.jump_landing_02),
                BitmapFactory.decodeResource(res, R.drawable.jump_landing_03)
        };

        fall = new Bitmap[]{
                BitmapFactory.decodeResource(res, R.drawable.dead_02),
                BitmapFactory.decodeResource(res, R.drawable.dead_03),
                BitmapFactory.decodeResource(res, R.drawable.dead_04)
        };

        direction = Direction.RIGHT;
        playerState = PlayerState.IDLE;

        anim = 0;

        x = 32;
        y = 50;

        stepX = 0;
        stepY = 0;

        width = screenX/9;
        height = screenY/16;
    }

    public void redraw(Canvas canvas){
        x += stepX;
        y += stepY;

        Matrix m = new Matrix();

        if(direction == Direction.RIGHT) {
            m.setScale(1,1);
        }
        else {
            m.setScale(-1,1);
        }

        m.postTranslate(canvas.getWidth(),0);

        Bitmap bInput = null;

        switch (playerState){
            case FALL : {
                bInput = fall[0];
                break;
            }
            case MOVING: {
                if(isLaying)
                    isLaying = false;
                if(anim == moving.length)
                    anim = 0;
                bInput = moving[anim++];
                break;
            }
            case JUMPING: {
                if(isJumping){
                    bInput = jumping[anim++];
                    //vyresit cas
                    isJumping = false;
                }
                break;
            }
            case IDLE: {
                if (isLaying) {
                    if (anim == fall.length)
                        anim = fall.length - 1;
                    bInput = fall[anim++];
                } else {
                    if (anim == idle.length)
                        anim = 0;
                    bInput = idle[anim++];

                }
                break;
            }
            default: break;
        }

        canvas.drawBitmap(Bitmap.createBitmap(bInput,0,0, bInput.getWidth(), bInput.getHeight(), m, true ), null,
                new Rect(x, y, x + width, y + height), null);

    }

    public void move(Direction dir, float ratio){
        direction = dir;
        if(direction == Direction.RIGHT){
            stepX = 10*ratio;
        }
        else{
            stepX = -10*ratio;
        }
        switchState(PlayerState.MOVING);
    }

    public void stopMove(){
        stepX = 0;
        switchState(PlayerState.IDLE);
    }

    public void fall(float ratio){
        isFalling = true;
        stepY = 30 * ratio;
        stepX = 0;
        switchState(PlayerState.FALL);
    }

    public void svartaJump(){
        isFalling = false;
        isLaying = true;
        switchState(PlayerState.IDLE);
        stepY = 0;
    }

    public void jump(float ratio, float power) {
        stepY -= 60 * ratio;
        if(direction == Direction.RIGHT){
            stepX += 5 * ratio;
        }
        else{
            stepX -=5 * ratio;
        }
        isJumping = true;
        switchState(PlayerState.JUMPING);
    }

    private void switchState(PlayerState state){
        if(playerState != state){
            anim = 0;
            playerState = state;
        }
    }
}

enum Direction {
    LEFT,
    RIGHT
}

enum PlayerState {
    IDLE,
    MOVING,
    JUMPING,
    FALL
}