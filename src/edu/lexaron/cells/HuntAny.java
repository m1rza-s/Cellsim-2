/*
 *
 *
 *
 */
package edu.lexaron.cells;

import edu.lexaron.world.Location;
import edu.lexaron.world.Sugar;
import edu.lexaron.world.World;
import javafx.scene.image.Image;

/**
 * A {@link Herbivorous} {@link Cell}, feeds on the first {@link Sugar} it sees.
 *
 * Project name: CellSIM/cell_max.java
 * Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 * Date & time: Jun 1, 2016, 1:51:43 AM
 * Refactored: 24.04.2018
 */
public class HuntAny extends Herbivorous {
  private static final Image GFX = new Image("edu/lexaron/gfx/huntFirst.png"); // todo Mirza S. : move gfx to breed enum

  private HuntAny(String id, int x, int y) {
    super(id, x, y);
  }

  /**
   * Creates a new default {@link HuntAny} at a random location in the provided {@link World}.
   *
   * @param world where the {@link HuntAny} is to be created
   */
  public HuntAny(World world) {
    this("F", getRandom().nextInt(world.getWidth()), getRandom().nextInt(world.getHeight()));
  }

  @Override
  public Image getImage() {
    return GFX;
  }

  @Override
  public Breed getBreed() {
    return Breed.HUNT_ANY;
  }

  @Override
  Cell doGiveBirth(int x, int y) {
    return new HuntAny(getGeneCode() + getOffspring(), x, y);
  }

  @Override
  public void lookForFood(World world) {
    setFood(world.getNewWorld().keySet().stream()
        .unordered()
        .parallel()
        .filter(location -> inVision(world, location) && world.findTile(location).hasSugar())
        .findAny()
        .orElse(Location.NIL));
    findPathTo(getFood());
  }
  }

