package com.example.jumpmaster;

import android.graphics.Bitmap;

public class Tile {
    public TileType tileType;
    public char tileChar;
    public Bitmap bitmap;

    public Tile (char tileChar, TileType tileType, Bitmap bitmap) {
        this.tileType = tileType;
        this.tileChar = tileChar;
        this.bitmap = bitmap;
    }
}

enum TileType {
    AIR,
    GROUND
}