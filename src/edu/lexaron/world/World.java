/*
 *  Project name: CellSIM/Wrold.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Feb 5, 2016, 8:54:35 PM
 */
package edu.lexaron.world;

import edu.lexaron.cells.Cell;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class World {

  private final int height;
  private final int width;
  private final Random r = new Random();
  private Tile[][] world;
  private volatile Set<Cell> allCells = new HashSet<>();
  private Set<Cell> newBornCells = new HashSet<>();
  private Set<Cell> eatenCorpses = new HashSet<>();

  /**
   * @param width
   * @param height
   */
  public World(int width, int height) {
    this.height = height;
    this.width = width;
  }

  /**
   * @param sugarFactor
   * @return
   */
  public Tile[][] generateWorld(double sugarFactor) {
    // sf, 0 to 100 in %

    System.out.println("Generating world...");
    world = new Tile[height][width];

    int sugarTiles = (int) (((width * height)) * (sugarFactor / 100));
    System.out.println(""
        + "Setup: "
        + width + "x" + height + ", "
        + "SF=" + sugarFactor + ", "
        + "ST=" + sugarTiles);
    int tileID = 1;
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        world[j][i] = new Tile(tileID, null, new Sugar(j, i, 0), new Trail(0, null));
        tileID++;
      }
    }
    int x = r.nextInt(width);
    int y = r.nextInt(height);
    for (int i = 0; i < sugarTiles; i++) {
      do {
        x = r.nextInt(width);
        y = r.nextInt(height);
      }
      while (hasSugar(y, x));
      world[y][x].setSugar(new Sugar(x, y, r.nextInt(21)));
    }
    System.out.println("Done generating world!");

    return world;
  }

  /**
   * @param x
   * @param y
   * @return
   */
  public boolean hasSugar(int x, int y) {
    return world[x][y].getSugar().getAmount() != 0;

  }

  /**
   *
   */
  public void newFood() {
    int x, y;
//        x = r.nextInt(((width / 4) * 3) - (width / 4)) + (width / 4);
//        y = r.nextInt(((height / 4) * 3) - (height / 4)) + (height / 4);
    x = r.nextInt(width - 2) + 1;
    y = r.nextInt(height - 2) + 1;
    if (world[y][x].getSugar().getAmount() <= 0) {
      world[y][x].getSugar().setAmount(r.nextInt(9) + 1);
    }
    else {
      //if (world[y][x].getSugar().getAmount() <= 18)
      world[y][x].getSugar().setAmount(world[y][x].getSugar().getAmount() + 2);
    }

  }

  /**
   * @return
   */
  public Tile[][] getWorld() {
    return world;
  }

  /**
   * @return
   */
  public int getHeight() {
    return height;
  }

  /**
   * @return
   */
  public int getWidth() {
    return width;
  }

  /**
   * @return
   */
  public Set getAllCells() {
    return allCells;
  }

  /**
   * @return
   */
  public Set<Cell> getNewBornCells() {
    return newBornCells;
  }

  public Set<Cell> getEatenCorpses() {
    return eatenCorpses;
  }

}
