package edu.lexaron.world;

import edu.lexaron.cells.Cell;
import edu.lexaron.simulation.Engine;

import java.util.HashSet;
import java.util.Set;

/**
 * Author: Mirza Suljić <mirza.suljic.ba@gmail.com>
 * Date & time: Feb 5, 2016, 8:54:35 PM
 */
public class World {

  private final int height, width;
  private final Tile[][] world;
  private final Set<Cell> allCells = new HashSet<>();
  private final Set<Cell> newBornCells = new HashSet<>();
  private final Set<Cell> eatenCorpses = new HashSet<>();

  /**
   * Creates a new {@link World} with the provided dimensions.
   *
   * @param width   the width of the {@link World}
   * @param height  the height of the {@link World}
   */
  public World(int width, int height) {
    this.height = height;
    this.width = width;
    world = new Tile[height][width];
  }

  /**
   * This method generates {@link Sugar} into this {@link World}.
   *
   * @param sugarFactor a factor of how much {@link Sugar} should be
   */
  @SuppressWarnings ({"MagicNumber", "UnusedAssignment"})
  public void generateWorld(double sugarFactor) {
    assert sugarFactor > 0.0 && sugarFactor <= 1.0 : "SugarFactor must be greater than 0 and less than or equal to 1.";

    int sugarTiles = (int) ((double) (width * height) * sugarFactor);

    int tileID = 1;
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        world[j][i] = new Tile(tileID, null, new Sugar(j, i, 0), new Trail(0, null));
        tileID++;
      }
    }
    int x = -1;
    int y = -1;
    for (int i = 0; i < sugarTiles; i++) {
      do {
        x = Engine.getRandom().nextInt(width);
        y = Engine.getRandom().nextInt(height);
      }
      while (hasSugar(x, y));
      world[y][x].setSugar(new Sugar(x, y, Engine.getRandom().nextInt(21)));
    }
  }

  private boolean hasSugar(int x, int y) {
    return world[y][x].getSugar().getAmount() != 0.0;

  }

  public void newFood() {
    int x, y;
    x = Engine.getRandom().nextInt(width - 2) + 1;
    y = Engine.getRandom().nextInt(height - 2) + 1;
    if (world[y][x].getSugar().getAmount() <= 0) {
      world[y][x].getSugar().setAmount((double) (Engine.getRandom().nextInt(9) + 1));
    }
    else {
      world[y][x].getSugar().setAmount(world[y][x].getSugar().getAmount() + 2);
    }

  }

  public Tile[][] getWorld() {
    return world;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

  public Set<Cell> getAllCells() {
    return allCells;
  }

  public Set<Cell> getNewBornCells() {
    return newBornCells;
  }

  public Set<Cell> getEatenCorpses() {
    return eatenCorpses;
  }

  @SuppressWarnings ("ImplicitNumericConversion")
  public int getTotalSugar() {
    int result = 0;
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        result += world[j][i].getSugar().getAmount();
      }
    }
    return result;
  }
}
