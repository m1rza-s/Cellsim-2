package edu.lexaron.simulation.cells.carnivours;

import edu.lexaron.simulation.cells.Breed;
import edu.lexaron.simulation.cells.Cell;
import edu.lexaron.simulation.world.Location;
import edu.lexaron.simulation.world.Trail;
import edu.lexaron.simulation.world.World;

/**
 * {@link Carnivorous} {@link Cell}s feed on other live {@link Cell}s by either looking for the {@link Cell}s themselves
 * or their {@link Trail}. They do not hunt members of their own {@link Breed}.
 *
 * Author: Mirza <mirza.suljic.ba@gmail.com>
 * Date: 24.4.2018.
 */
public abstract class Carnivorous extends Cell {

  /**
   * Creates a new {@link Cell} based on the provided parameters.
   *
   * @param id         unique {@link Breed} ID
   * @param x          horizontal coordinate of birth location
   * @param y          vertical coordinate of birth location
   * @param energy     initial energy level, usually 50
   * @param vision     initial vision range, determines the FoV
   * @param speed      initial speed, determines how fast the {@link Cell} travels it's path
   * @param efficiency initial efficiency, determines how much energy a {@link Cell} expends for each action it takes
   * @param biteSize   initial size of bite, determines how fast the {@link Cell} consumes it's food source
   */
  Carnivorous(String id, int x, int y, double energy, int vision, double speed, double efficiency, double biteSize) {
    super(id, Location.of(x, y), energy, vision, speed, efficiency, biteSize);
  }

  @Override
  public void doHunt(World world) {
    if (getPath().isEmpty() || getFood() == null) {
      lookForFood(world);
    }
    else if (getFood() != null) {
      useWholePath(world);
      eat(world);
    }
    else {
      useWholePath(world);
    }
  }

  @Override
  public void lookForFood(World world) {
    resetFoodAndPath();
//    int foundSmell = 0;
//        loop:
//    for (int v = 1; v <= getVision(); v++) { // todo Mirza S. : stream in vision in AbstractCell?
//      for (int y = getY() - v; y <= (getY() + v); y++) {
//        for (int x = getX() - v; x <= (getX() + v); x++) {
//          if (isValidLocation(world, x , y)) {
//            Cell  prey  = world.getWorld()[y][x].getCell();
//            Trail trail = world.getWorld()[y][x].getTrail();
//            if (prey != null && prey.isAlive() && prey.getBreed() != getBreed()) {
//              setFood(Location.of(prey.getX(), prey.getY()));
//              break loop;
//            }
//            else {
//              if (trail.getSource() != null && trail.getSource().isAlive() && trail.getSource().getBreed() != getBreed() && trail.getAmount() > foundSmell) {
//                foundSmell = trail.getAmount();
//                setFood(Location.of(x, y));
//              }
//            }
//          }
//        }
//      }
//    }
    world.getNewWorld().keySet().stream()
        .filter(location -> inVision(world, location) && world.findTile(location).hasLiveCell())
        .findFirst()
        .ifPresentOrElse(this::setFood, this::resetFoodAndPath); // todo Mirza S. : make it look for trails too
    findPathTo(getFood());
  }
}

