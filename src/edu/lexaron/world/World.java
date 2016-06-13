/*
 *  Project name: CellSIM/Wrold.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Feb 5, 2016, 8:54:35 PM
 */
package edu.lexaron.world;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class World {

    private final int height;
    private final int width;
    private Tile[][] theWorld;
    private final Random r = new Random();

    private volatile List<Cell> allCells = new LinkedList<>();
    private List<Cell> newBornCells = new LinkedList();

    public World(int width, int height) {
        this.height = height;
        this.width = width;
    }

    public Tile[][] generateWorld(double sugarFactor) {
        // sf, 0 to 100 in %

        System.out.println("Generating world...");
        theWorld = new Tile[height][width];

        int sugarTiles = (int) (((width * height)) * (sugarFactor / 100));
        System.out.println(""
                + "Setup: "
                + width + "x" + height + ", "
                + "SF=" + sugarFactor + ", "
                + "ST=" + sugarTiles);
        int tileID = 1;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                theWorld[j][i] = new Tile(tileID, null, new Sugar(j, i, 0));
                tileID++;
            }
        }
        int x = r.nextInt(width);
        int y = r.nextInt(height);
        for (int i = 0; i < sugarTiles; i++) {
            do {
                x = r.nextInt(width);
                y = r.nextInt(height);
            } while (hasSugar(y, x));
            theWorld[y][x].setSugar(new Sugar(x, y, r.nextInt(9) + 1));
        }
        System.out.println("Done generating world!");

        return theWorld;
    }

    public boolean hasSugar(int x, int y) {
        return theWorld[x][y].getSugar().getAmount() != 0;

    }

    public void newFood() {
        int x, y;
//        x = r.nextInt(((width / 4) * 3) - (width / 4)) + (width / 4);
//        y = r.nextInt(((height / 4) * 3) - (height / 4)) + (height / 4);
        x = r.nextInt(width);
        y = r.nextInt(height);
        if (theWorld[y][x].getSugar().getAmount() == 0) {
            theWorld[y][x].getSugar().setAmount(r.nextInt(8) + 1);
        } else if (theWorld[y][x].getSugar().getAmount() < 9) {
            theWorld[y][x].getSugar().setAmount(theWorld[y][x].getSugar().getAmount() + 1);
        }

    }

    public Tile[][] getTheWorld() {
        return theWorld;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public List getAllCells() {
        return allCells;
    }

    public List<Cell> getNewBornCells() {
        return newBornCells;
    }

}
