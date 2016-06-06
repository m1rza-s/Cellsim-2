/*
 *  Project name: CellSIM/Wrold.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Feb 5, 2016, 8:54:35 PM
 */
package edu.lexaron.world;

import java.util.Random;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class World {

    private Tile[][] theWorld;
    private final int height;
    private final int width;

    public World(int height, int width) {
        this.height = height;
        this.width = width;
    }
    
    public Tile[][] generateWorld(double sugarFactor) {
        // sf, 0 to 100 in %
        Random r = new Random();

        System.out.println("Generating world...");
        theWorld = new Tile[height][width];

        int sugarTiles = (int) (((width * height)) * (sugarFactor / 100));
        System.out.println(""
                + "Setup: "
                + height + "x" + width + ", "
                + "SF=" + sugarFactor + ", "
                + "ST=" + sugarTiles);
        int tileID = 1;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {                
                theWorld[i][j] = new Tile(tileID, null, 0);                
                tileID++;                
            }
        }
        int x = r.nextInt(width);
        int y = r.nextInt(height);
        for (int i = 0; i < sugarTiles; i++) {
            do {
                x = r.nextInt(width);
                y = r.nextInt(height);
            } while (hasSugar(x, y));
            theWorld[x][y].setSugar(r.nextInt(9) + 1);
        }
        System.out.println("Done generating world!");
        
        return theWorld;
    }

    public boolean hasSugar(int x, int y) {
        if (theWorld[x][y].getSugar() != 0) {
            return true;
        } else {
            return false;
        }

    }
    
    // ONLY AFTER ALL DONE!
    public Tile[][] getTheWorld() {
        return theWorld;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

}
