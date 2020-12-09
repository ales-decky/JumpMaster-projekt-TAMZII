package com.example.jumpmaster;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import java.io.InputStream;
import java.util.List;

import java.io.IOException;

public class TileMap {
    public Tile[] mapSet;// = new char[9*16];
    public List<Tile> tileList;

    private Resources resource;
    private int LevelNumber;

    private int width;
    private int height;

    private int lx = 9;
    private int ly = 16;

    TileMap(Resources res, int screenX, int screenY, int levelNumber){
        //tileList.add(new Tile(BitmapFactory.decodeResource(getResources(), R.drawable.left_corner)));

        width = screenX;
        height = screenY;

        resource = res;

        LevelNumber = levelNumber;

        AssetManager assetManager = resource.getAssets();
        InputStream input;
        try {
            input = assetManager.open("levels.txt");

            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            String text = new String(buffer);

            String[] splittedText =  text.split("\r\n\r\n");

            String levelek = splittedText[LevelNumber];
            //for (String levelek : ) {
                //Log.d("splittedText ", levelek);
                //levelek = levelek.split("'\r\n")[1];
                levelek = levelek.replace("\r","");
                String[] rows = levelek.split("\n");
                int colsCount = 0;
                for(int i = 0; i < rows.length; i++){
                    if(rows[i].length() > colsCount)
                        colsCount = rows[i].length();
                }
                int rowsCount = rows.length;
                //String testText = "";
                char[] level = new char[colsCount*rowsCount];
                int k = 0;
                for(int i = 0; i < rows.length; i++){
                    for(int j = 0; j < rows[i].length(); j++){
                        level[k++] = rows[i].charAt(j);
                        //testText += rows[i].charAt(j);
                    }
                    while (colsCount > (k-i*colsCount)){
                        level[k++] = 0;
                        //testText += 0;
                    }
                    //testText += "\n";
                }
                //levels.add(new Level(level,rowsCount,colsCount));
                textToTiles(level);
                //mapSet = level;
                //Log.d("testText ", testText);
            //}

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void textToTiles(char[] level){
        mapSet = new Tile[level.length];

        for(int i = 0; i < level.length; i++){
            switch (level[i]){
                case '0' : {
                    mapSet[i] = new Tile('0', TileType.AIR, BitmapFactory.decodeResource(resource, R.drawable.empty));
                    break;
                }
                case 'f' : {
                    mapSet[i] =  new Tile('f', TileType.GROUND, BitmapFactory.decodeResource(resource, R.drawable.left_corner));
                    break;
                }
                default: {
                    break;
                }
            }
        }

    }

    public void redraw(Canvas canvas){

        /*canvas.drawBitmap(mapSet[135].bitmap, null,
                new Rect(0, 0, width/lx, height/ly), null);*/
        for(int i = 0; i < ly; i++){
            for( int j = 0; j < lx; j++) {
                canvas.drawBitmap(mapSet[i*lx + j].bitmap, null,
                        new RectF(j * (width/lx), i * (height/ly), (j + 1) * (width/lx), (i + 1) * (height/ly)), null);
            }
        }
    }

    public boolean isGround(float leftP, float rightP, float y) {
        return getTile(leftP, y).tileType == TileType.GROUND || getTile(rightP, y).tileType == TileType.GROUND;
    }

    /*public boolean isBouncable(float leftP, float rightP,float y){

        //leva strana panacka
        if(getTile(leftP,y).tileType == TileType.GROUND)

        return (isGround(leftP-width/lx,rightP-width/lx,y) && getTileRight(leftP-width/lx) > leftP) || (getTileLeft(rightP+width/lx) < rightP && isGround(leftP+width/lx,rightP+width/lx,y));
    }*/

    public RectF isIntersectWithGround(RectF player){
        //left top
        RectF tileRect = getGroundTileRect(player.left,player.top);
        //right top
        if(tileRect == null)
            tileRect = getGroundTileRect(player.right,player.top);
        //left bot
        if(tileRect == null)
            tileRect = getGroundTileRect(player.left,player.bottom);
        //right bot
        if(tileRect == null)
            tileRect = getGroundTileRect(player.right,player.bottom);

        if(tileRect != null){
            if(RectF.intersects(player,tileRect))
                return tileRect;
            else
                return null;
        }
        return null;
    }

    private Tile getTile(float x, float y) {
        int i = (int)x/(width/lx);
        int j = (int)y/(height/ly);
        return mapSet[j*lx + i];
    }

    private RectF getGroundTileRect(float x, float y){
        int i = (int)x/(width/lx);
        int j = (int)y/(height/ly);
        if(mapSet[j*lx + i].tileType == TileType.GROUND){
            return new RectF(i * (width/lx), j * (height/ly), (i + 1) * (width/lx), (j + 1) * (height/ly));
        }
        return null;
    }

    /*private float getTileRight(float x){
        return x/(width/lx)+(width/lx);
    }

    private float getTileLeft(float x){
        return x/(width/lx);
    }

    private float getTileTop(float y){
        return y/(height/ly);
    }

    private float getTileBottom(float y){
        return y/(height/ly)+(height/ly);
    }*/

}
