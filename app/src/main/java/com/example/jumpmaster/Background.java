package com.example.jumpmaster;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Background {
    public float x = 0, y = 0;
    Bitmap background;

    Background (int screenX, int screenY, Resources res, int resId){
        background = BitmapFactory.decodeResource(res, resId);
        background = Bitmap.createScaledBitmap(background, screenX, screenY, false);
    }
}
