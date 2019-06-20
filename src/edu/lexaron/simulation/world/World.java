package edu.lexaron.simulation.world;

import edu.lexaron.simulation.Engine;
import edu.lexaron.simulation.cells.Cell;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: Mirza Suljić <mirza.suljic.ba@gmail.com>
 * Date & time: Feb 5, 2016, 8:54:35 PM
 */
public class World {

  private final int height, width;
//  private final Tile[][] world; // todo Mirza S. : replace this with the newWorld map
  private final Map<Location, Tile> newWorld = new ConcurrentHashMap<>();
  private final Set<Cell> allCells     = new HashSet<>();
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
//    this.world = new Tile[height][width];
  }

  /**
   * This method generates {@link Sugar} into this {@link World}.
   *
   */
  @SuppressWarnings ({"MagicNumber"})
  public void generateWorld() {

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        Location xy = new Location(i, j);
        Tile tile = new Tile(xy, new Sugar(j, i, (double) Engine.getRandom().nextInt(21)));
        newWorld.put(xy, tile);
      }
    }
  }

  public boolean isValidLocation(Location location) {
    return newWorld.containsKey(location);
  }

  private boolean hasSugar(Location xy) {
//    return newWorld.get(Location.of(x, y)).getSugar().getAmount() != 0.0;
    return newWorld.get(xy).hasSugar();

  }

  public void newFood() {
    int x, y;
    x = Engine.getRandom().nextInt(width - 2) + 1;
    y = Engine.getRandom().nextInt(height - 2) + 1;
    if (newWorld.get(Location.of(x, y)).getSugar().getAmount() <= 0) {
      newWorld.get(Location.of(x, y)).getSugar().setAmount((double) (Engine.getRandom().nextInt(9) + 1));
    }
    else {
      newWorld.get(Location.of(x, y)).getSugar().setAmount(newWorld.get(Location.of(x, y)).getSugar().getAmount() + 2);
    }

  }

  public Tile findTile(Location xy) {
    return newWorld.get(xy);
  }

  public Map<Location, Tile> getNewWorld() {
    return newWorld;
  }

  @Deprecated
  public Tile[][] getWorld() {
//    return world;
    return null;
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

  /**
   * @return total amount of sugar in {@link World}
   */
  public double getTotalSugar() {
//    return 0; // todo Mirza S. : hasSugar throws NPE?
    return newWorld.entrySet()
        .stream()
        .filter(entry -> entry.getValue().hasSugar())
        .reduce(
            0.0,
            (aDouble, entry) -> aDouble + entry.getValue().getSugar().getAmount(),
            (aDouble, aDouble2) -> aDouble + aDouble2
        );
  }
}
