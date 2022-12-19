package edu.lexaron.simulation.cells.herbivores;

import edu.lexaron.simulation.cells.Breed;
import edu.lexaron.simulation.cells.Cell;
import edu.lexaron.simulation.cells.Direction;
import edu.lexaron.simulation.world.Location;
import edu.lexaron.simulation.world.Sugar;
import edu.lexaron.simulation.world.World;

/**
 * {@link Plant}s are {@link Cell} that feed on {@link Sugar}.
 * They cannot use the {@link Cell#move(World, Direction)} method, but they can generate {@link Sugar} in their FoV.
 *
 * Author: Mirza <mirza.suljic.ba@gmail.com>
 * Date: 24.4.2018.
 */
@SuppressWarnings ("MagicNumber")
abstract class Plant extends Cell {
  private static final int VISION_MODIFIER = 5;

  /**
   * Creates a new {@link Cell} based on the provided parameters.
   *
   * @param id         unique {@link Breed} ID
   * @param x          horizontal coordinate of birth location
   * @param y          vertical coordinate of birth location
   * @param energy     initial energy level, usually 50
   * @param vision     initial vision range, determines the FoV
   * @param speed      initial speed, determines how fast the {@link Cell} uses it's {@link Cell#path}
   * @param efficiency initial efficiency, determines how much energy a {@link Cell} expends for each action it takes
   * @param biteSize   initial size of bite, determines how fast the {@link Cell} consumes it's food source
   */
  Plant(String id, int x, int y, double energy, int vision, double speed, double efficiency, double biteSize) {
    super(id, Location.of(x, y), energy, vision, speed, efficiency, biteSize);
  }

  @SuppressWarnings ("MethodDoesntCallSuperMethod")
  @Override
  protected void move(World world, Direction dir) {
    resetFoodAndPath();
  }

  @Override
  public void lookForFood(World world) {
    resetFoodAndPath();
    int rx = getRandom().nextInt(((getX() + getVision()) - (getX() - getVision())) + 1) + (getX() - getVision());
    int ry = getRandom().nextInt(((getY() + getVision()) - (getY() - getVision())) + 1) + (getY() - getVision());
    Location temp = Location.of(rx, ry);
    if (world.isValidLocation(temp)) {
      if (world.getNewWorld().get(temp).hasSugar()) {
        setFood(temp);
      }
      else if (world.getNewWorld().get(temp).hasLiveCell() && world.getNewWorld().get(temp).getCell().getBreed() == getBreed()) {
        setFood(temp);
      }
      else if (getRandom().nextInt(3) == 2) {
        world.getNewWorld().get(temp).getSugar().setAmount(3.0);
      }
    }
  }
}

