package edu.lexaron.cells;

import edu.lexaron.simulation.cells.Cell;
import edu.lexaron.simulation.world.Location;
import edu.lexaron.simulation.world.Sugar;
import edu.lexaron.simulation.world.World;
import edu.lexaron.simulation.cells.Breed;

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
    super(id, Location.of(x, y), 50.0, 10, 1.0, 1.0, 0.5);
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
  public void eat(World world) {
    Sugar prey = world.getNewWorld().get(this.getLocation()).getSugar();
    if (prey.getAmount() > 0.0) {
      prey.setAmount(prey.getAmount() - getBiteSize());
      setEnergy(getEnergy() + getBiteSize());
    }
    else {
      resetFoodAndPath();
    }
  }
}

