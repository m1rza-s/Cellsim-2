/*
 *  Project name: CellSIM/Wrold.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Feb 5, 2016, 8:54:35 PM
 */
package classes;

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
        theWorld = new Tile[width][height];

        int sugarTiles = (int) (((width * height)) * (sugarFactor / 100));
        System.out.println(""
                + "Setup: "
                + "H=" + height + ", "
                + "W=" + width + ", "
                + "SF=" + sugarFactor + ", "
                + "ST=" + sugarTiles);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                theWorld[i][j] = new Tile(null, 0);
            }
        }
        int x = r.nextInt(width);
        int y = r.nextInt(height);
        for (int i = 0; i < sugarTiles; i++) {
            do {
                x = r.nextInt(width);
                y = r.nextInt(height);
            } while (hasSugar(x, y));
            this.theWorld[x][y].setSugar(r.nextInt(5) + 1);
        }
        System.out.println("Done generating world!");
        
        return theWorld;
    }

    public boolean hasSugar(int x, int y) {
        if (this.theWorld[x][y].getSugar() != 0) {
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
