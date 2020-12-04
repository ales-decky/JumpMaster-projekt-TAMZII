package com.example.jumpmaster;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.StrictMath.sin;

public class Player {
    public float x;
    public float y;
    public Direction direction;
    public PlayerState playerState;
    private int anim;

    //dimension
    public float width;
    public float height;
    public float leftX;
    public float rightX;
    public float topY;
    public float bottomY;

    //draw dimension
    private float drawWidth;
    private float drawHeight;

    //speeds
    private float moveSpeed = (float) 20;
    private float maxSpeed = (float) 100;
    private float stopSpeed = (float) 20;
    private float fallSpeed = (float) 7.5;
    private float maxFallSpeed = (float) 200;

    private int maxX;
    private int maxY;
    private float stepX;
    private float stepY;

    private boolean isLaying = false;

    public boolean isFalling = false;

    public boolean isJumping = false;
    private float jumpStartPosY;
    private float jumpTopY;
    private float jumpGravity = (float)9.8;
    private float jumpVelocityX;
    private float jumpVelocityY;

    private long startTimeJump;
    private long endTimeJump;

    private float jumpPower;
    public boolean isReadyToJump = false;

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



        stepX = 0;
        stepY = 0;

        drawWidth = screenX/9;
        drawHeight = screenY/16;

        x = screenX/2;
        y = screenY-2*drawHeight;

        maxX = screenX;
        maxY = screenY;

        leftX = drawWidth * 0.15625f;
        rightX = drawWidth * 0.46875f;
        topY = drawHeight * 0.0625f;
        bottomY = drawHeight * 0.0f;

        width = drawWidth;
        height = drawHeight;
    }

    public void redraw(Canvas canvas){
        x += stepX;
        if(getRightBoundary() > maxX){
            if(direction == Direction.RIGHT)
                x = maxX - (width-rightX);
            else
                x = maxX - (width-leftX);
        } /*else if(getLeftBoundary() < 0){
            if(direction == Direction.RIGHT)
                x = 0 + leftX;
            else
                x = 0 + rightX;
        }*/
        y += stepY;
        /*if(y < maxY){
            //neco
        }*/

        int timer = 0;

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
                    //prvni dve animace vyskoku
                    if(jumpTopY >= y) {
                        if (anim == 2)
                            anim = 1;
                    }
                    else {
                        if (anim == jumping.length)
                            anim = jumping.length-1;
                    }
                    //vyresit cas
                    //isJumping = false;
                    jumping((float)0.48);
                }
                else{
                    bInput = idle[0];
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
                new RectF(x, y, x + drawWidth, y + drawHeight), null);

    }

    public void move(Direction dir, float ratio){
        if(!isJumping) {

            if(direction != dir){
                if(dir == Direction.RIGHT){
                    x = x + rightX - leftX;
                }
                else {
                    x = x + leftX - rightX;
                }
            }

            direction = dir;
            if (direction == Direction.RIGHT) {

                stepX += moveSpeed * ratio;
                if (stepX > maxSpeed * ratio) {
                    stepX = maxSpeed * ratio;
                }
            } else {



                stepX -= moveSpeed * ratio;
                if (stepX < -maxSpeed * ratio) {
                    stepX = -maxSpeed * ratio;
                }
            }
            switchState(PlayerState.MOVING);
        }
    }

    public void stopMove(){
        stepX = 0;
        switchState(PlayerState.IDLE);
    }

    public void stopJumping(){
        if(isJumping){
            isJumping = false;
        }
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

    public void jump(float ratio) {

        if(!isJumping && !isFalling) {

            jumpTopY = y;

            jumpStartPosY = y;

            endTimeJump = System.currentTimeMillis();

            double time = (endTimeJump - startTimeJump);
            double maxSeconds = 0.8*1000;

            jumpPower = (time > maxSeconds)?100f:(float)((time*100f)/maxSeconds);

            jumpGravity = (float) 9.8 * ratio;

            double angle = 70 * 3.141459 / 180;

            if (direction == Direction.RIGHT)
                jumpVelocityX = (jumpPower * (float) cos(angle)) * ratio;
            else
                jumpVelocityX = -(jumpPower * (float) cos(angle)) * ratio;

            jumpVelocityY = (jumpPower * (float) -sin(angle)) * ratio;

            isJumping = true;
            isReadyToJump = false;
            switchState(PlayerState.JUMPING);
        }
    }

    public void changeJumpDirection(){
        jumpVelocityX *= -1;
        direction = (direction == Direction.RIGHT)?Direction.LEFT:Direction.RIGHT;
    }

    private void switchState(PlayerState state){
        if(state != PlayerState.JUMPING && isJumping){
            return;
        }
        if(playerState != state){
            anim = 0;
            playerState = state;
        }
    }

    private void jumping(float dT){
        float mass = (float)1.0;

        if(y > jumpStartPosY){
            y = jumpStartPosY;
            isJumping = false;
            switchState(PlayerState.IDLE);
            return;
        }

        float gravityForce = mass*jumpGravity;

        jumpVelocityX += 0/mass * dT;
        jumpVelocityY += gravityForce/mass * dT;

        x += jumpVelocityX * dT;
        y += jumpVelocityY * dT;

        if(y < jumpTopY) {
            jumpTopY = y;
        }
    }

    public void prepareToJump(){
        if(!isJumping && !isFalling) {
            if (!isReadyToJump)
                startTimeJump = System.currentTimeMillis();
            isReadyToJump = true;
        }
    }

    public float getLeftBoundary(){
        if(direction == Direction.RIGHT)
            return x+rightX;
        else
            return x+leftX;
    }
    public float getRightBoundary(){
        if(direction == Direction.RIGHT)
            return x+width-rightX;
        else
            return x+width-leftX;//-(rightX+leftX);
    }
    public float getBottomBoundary(){
        return y+height;
    }
    public float getTopBoundary(){
        return y - topY;
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