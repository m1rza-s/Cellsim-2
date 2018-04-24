package edu.lexaron.cells;

import edu.lexaron.world.Location;
import edu.lexaron.world.Sugar;
import edu.lexaron.world.World;

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
    super(id, x, y, energy, vision, speed, efficiency, biteSize);
  }

  @SuppressWarnings ("MethodDoesntCallSuperMethod")
  @Override
  void move(World world, Direction dir) {
    resetFoodAndPath();
  }

  @Override
  public void lookForFood(World world) {
    resetFoodAndPath();
    int rx = getRandom().nextInt(((getX() + getVision()) - (getX() - getVision())) + 1) + (getX() - getVision());
    int ry = getRandom().nextInt(((getY() + getVision()) - (getY() - getVision())) + 1) + (getY() - getVision());
    if (isValidLocation(world, rx, ry)) {
      if (world.getWorld()[ry][rx].getSugar().getAmount() > 0.0) {
        setFood(rx, ry);
      }
      else if (world.getWorld()[ry][rx].getCell() != null && world.getWorld()[ry][rx].getCell().getBreed() == getBreed()) {
        setFood(rx, ry);
      }
      else if (getRandom().nextInt(3) == 2) {
        world.getWorld()[ry][rx].getSugar().setAmount(3.0);
      }
    }
  }

  @SuppressWarnings ("MethodDoesntCallSuperMethod")
  @Override
  Location findBirthplace(World w) {
    Location birthplace = null;
    boolean found = false;
    int limit = 0;
    while (!found || limit > 9) {
      int rx = getRandom().nextInt(((getX() + (getVision() * VISION_MODIFIER)) - (getX() - getVision())) + 1) + (getX() - (getVision() * VISION_MODIFIER));
      int ry = getRandom().nextInt(((getY() + (getVision() * VISION_MODIFIER)) - (getY() - getVision())) + 1) + (getY() - (getVision() * VISION_MODIFIER));
      if (!(ry < 0 || rx < 0 || ry >= w.getHeight() || rx >= w.getWidth())) {
        if (w.getWorld()[ry][rx].getCell() == null && w.getWorld()[ry][rx].getDeadCell() == null) {
          birthplace = new Location(rx, ry);
          found = true;
        }
        limit++;
      }
    }
    return birthplace;
  }
}

