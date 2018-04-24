package edu.lexaron.cells;

import edu.lexaron.world.Sugar;
import edu.lexaron.world.World;

/**
 * Author: Mirza <mirza.suljic.ba@gmail.com>
 * Date: 23.4.2018.
 */
abstract class Herbivorous extends Cell {

  /**
   * Creates a new {@link Cell} based on the provided parameters.
   *
   * @param id         unique {@link Breed} ID
   * @param x          horizontal coordinate of birth location
   * @param y          vertical coordinate of birth location
   */
  @SuppressWarnings ("MagicNumber")
  Herbivorous(String id, int x, int y) {
    super(id, x, y, 50.0, 3, 1.0, 1.0, 1.0);
  }

  @Override
  public void doHunt(World world) {
    if (getPath().isEmpty()) {
      if (getFood() == null) {
        lookForFood(world);
      }
      else {
        eat(world);
      }
    }
    else {
      useWholePath(world);
    }
    if (getFood() == null) randomStep(world);
  }

  @Override
  public void eat(World w) {
    Sugar prey = w.getWorld()[getY()][getX()].getSugar();
    if (prey.getAmount() > 0.0) {
      prey.setAmount(prey.getAmount() - getBiteSize());
      setEnergy(getEnergy() + getBiteSize());
    }
    else {
      resetFoodAndPath();
    }
  }
}

