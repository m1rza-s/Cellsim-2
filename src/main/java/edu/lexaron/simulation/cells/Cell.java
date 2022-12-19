package edu.lexaron.simulation.cells;

import edu.lexaron.simulation.world.Location;
import edu.lexaron.simulation.world.Tile;
import edu.lexaron.simulation.world.Trail;
import edu.lexaron.simulation.world.World;
import javafx.scene.image.Image;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * This class contains the basic functionalities of a living organism in this simulation.
 * It provides a number of abstract methods that must be implemented in order to create a new {@link Breed}.
 * Other more general activities, such as finding a unoccupied {@link Location} are share by all {@link Breed}s.
 *
 * Project name: CellSIM/Cell.java
 * Author: Mirza Suljić <mirza.suljic.ba@gmail.com>
 * Date & time: Feb 5, 2016, 8:55:28 PM
 * Refactored: 24.04.2018
 */
@SuppressWarnings ("MagicNumber")
public abstract class Cell {

  private static final Random RANDOM    = new SecureRandom();
  private static final double BIRTH_REQ = 100.0;
  private static final int    OFFSPRING_LIMIT = 3;
  private final int               movement;
  private final String            geneCode;
  protected final List<Direction> path;

  private boolean alive;



  private Location location;
  private int    vision, trailSize, offspring, oppositeRandomStep;
  private double energy, speed, efficiency, biteSize;
  private Location food = null;

  /**
   * Creates a new {@link Cell} based on the provided parameters.
   * @param id          unique {@link Breed} ID
   * @param location    cell's position
   * @param energy      initial energy level, usually 50
   * @param vision      initial vision range, determines the FoV
   * @param speed       initial speed, determines how fast the {@link Cell} uses it's {@link Cell#path}
   * @param efficiency  initial efficiency, determines how much energy a {@link Cell} expends for each action it takes
   * @param biteSize    initial size of bite, determines how fast the {@link Cell} consumes it's food source
   */
  @SuppressWarnings ({"UnnecessaryThis"})
  protected Cell(String id, Location location, double energy, int vision, double speed, double efficiency, double biteSize) {
    this.path = new ArrayList<>();
    this.geneCode = id;
    this.location = location;
    this.energy     = energy;
    this.vision     = vision;
    this.movement   = 1;
    this.speed      = speed;
    this.efficiency = efficiency;
    if (energy > 0.0) {
      this.alive = true;
    }
    this.trailSize = 50;
    this.biteSize = biteSize;
  }

  /**
   * @return the {@link Image} that represents this {@link Cell} subclass
   */
  public abstract Image getImage();

  /**
   * Handle how this {@link Cell} subclass looks for food.
   *
   * @param world the {@link World} that contains the food
   */
  public abstract void lookForFood(World world);

  /**
   * @return the {@link Breed} this {@link Cell} belongs to
   */
  public abstract Breed getBreed();

  /**
   * Each {@link Breed} might hunt differently. Use this method to determine how your {@link Cell} hunts in the provided
   * {@link World}.
   *
   * @param world where the {@link Cell} hunts
   */
  public abstract void doHunt(World world);

  protected abstract void eat(World world);

  protected abstract Cell doGiveBirth(int x, int y);

  private void tryBirth(World world) {
    if (energy >= BIRTH_REQ) {
      Location birthPlace = findBirthplace(world);
      if (birthPlace != null) {
        Cell child = doGiveBirth(birthPlace.getX(), birthPlace.getY());
        child.inheritFrom(this);
        child.evolve();
        world.getNewBornCells().add(child);
        offspring += 1;
      }
      energy /= 3.0;
    }
  }

  /**
   * Handles upkeep, includes the {@link Cell#doHunt(World)} method, tries to produce offspring and handles death.
   *
   * @param world where it all takes place
   */
  public final void live(World world) {
    upkeep(world);
    if (alive) {
      doHunt(world);
      if (path.isEmpty() && food == null) { // todo Mirza :transform into organic wandering
        move(world, Direction.DOWN_LEFT);
        randomStep(world);
      }
    }
    tryBirth(world);
  }

  /**
   * @return the location of this cell
   */
  public Location getLocation() {
    return location;
  }

  /**
   * At 0.0 energy, a {@link Cell} dies. At over 100, a {@link Cell} tries to divide.
   *
   * @return the amount of energy this {@link Cell} has
   */
  public final double getEnergy() {
    return energy;
  }

  /**
   * @return the horizontal coordinate of this {@link Cell}
   */
  public final int getX() {
    return location.getX();
  }

  /**
   * @return the vertical coordinate of this {@link Cell}
   */
  public final int getY() {
    return location.getY();
  }

  /**
   * @param x new horizontal coordinate of this {@link Cell}
   */
  public final void setX(int x) {
    location = Location.of(x, getY());
  }

  /**
   * @param y new vertical coordinate of this {@link Cell}
   */
  public final void setY(int y) {
    location = Location.of(getX(), y);
  }

  /**
   * Determines a {@link Cell}'s field of vision.
   *
   * @return this {@link Cell}'s range of sight
   */
  public final int getVision() {
    return vision;
  }

  /**
   * Determines how fast a {@link Cell} uses it's path.
   *
   * @return this {@link Cell}'s speed
   */
  public final double getSpeed() {
    return speed;
  }

  /**
   * Determines a {@link Cell}'s cost of living.
   *
   * @return a coefficient which influences how much each energy is used in each activity
   */
  public final double getEfficiency() {
    return efficiency;
  }

  /**
   * Determines a {@link Cell}'s rate of consuming a food source.
   *
   * @return the amount of energy a {@link Cell} consumes in one go
   */
  public double getBiteSize() {
    return biteSize;
  }

  /**
   * @return whether or not this {@link Cell} is alive
   */
  public final boolean isAlive() {
    return alive;
  }

  /**
   * @return the {@link Location} where this {@link Cell} detected a food source
   */
  public Location getFood() {
    return food;
  }

  protected static Random getRandom() {
    return RANDOM;
  }

  protected void findPathTo(Location target) {
    if (target != null && !target.equals(Location.NIL)) {
      int difY = target.getY() - location.getY();
      int difX = target.getX() - location.getX();
      if (difX > 0) {
        for (int i = 0; i < Math.abs(difX); i++) {
          path.add(Direction.RIGHT);
        }
      }
      if (difX < 0) {
        for (int i = 0; i < Math.abs(difX); i++) {
          path.add(Direction.LEFT);
        }
      }
      if (difY > 0) {
        for (int i = 0; i < Math.abs(difY); i++) {
          path.add(Direction.DOWN);
        }
      }
      if (difY < 0) {
        for (int i = 0; i < Math.abs(difY); i++) {
          path.add(Direction.UP);
        }
      }
    }
    else {
      resetFoodAndPath();
    }
  }

  protected void setFood(Location food) {
    this.food = food;
  }

  protected void resetFoodAndPath() {
    path.clear();
    food = null;
  }

  protected void useWholePath(World world) {
    for (int i = 0; i < speed; i++) {
      if (!path.isEmpty()) {
        move(world, path.get(0));
        path.remove(0);
      }
    }
//    if (!path.isEmpty()) {
//      path.forEach(direction -> move(world, direction));
//      path.clear();
//    }
  }

  protected void randomStep(World world) {
    int roll = RANDOM.nextInt(5);
    while (roll == oppositeRandomStep) {
      roll = RANDOM.nextInt(5);
    }
    switch (roll) {
      case 0:
        oppositeRandomStep = 2; // todo Mirza S. : replace with enum?
        move(world, Direction.UP);
        break;
      case 1:
        oppositeRandomStep = 3;
        move(world, Direction.RIGHT);
        break;
      case 2:
        oppositeRandomStep = 0;
        move(world, Direction.DOWN);
        break;
      case 3:
        oppositeRandomStep = 1;
        move(world, Direction.LEFT);
        break;
      case 4:
        break;
    }
  }

  protected boolean inVision(World world, Location xy) {
    if (!world.isValidLocation(xy)) {
      return false;
    }
    var goodX = (getX() - vision) <= xy.getX() && xy.getX() <= (getX() + vision);
    var goodY = (getY() - vision) <= xy.getY() && xy.getY() <= (getY() + vision);
    return goodX && goodY;
  }

  @SuppressWarnings ({"ImplicitNumericConversion"})
  protected void move(World world, Direction dir) {
    Location target = Location.of(getX() + dir.getDeltaX(), getY() + dir.getDeltaY());
    if (world.isValidLocation(target)) {
        Tile targetTile = world.getNewWorld().get(target);
        assert targetTile != null : "cannot move to null tile at " + target;
        if (targetTile.getCell() == null) {
          if ((energy - (movement * efficiency)) > 0) {
            energy -= movement * efficiency;
            world.getNewWorld().get(location).setTrail(new Trail(trailSize, this));
            world.getNewWorld().get(location).setCell(this);
            location = target;
          }
          else {
            die(world);
          }

        }
        else {
          randomStep(world);
        }
      }
      else {
        circumnavigate(world, getX() + dir.getDeltaX(), getY() + dir.getDeltaY());
      }
  }

  private void circumnavigate(World world, int x, int y) {
    x = x >= world.getWidth()  ? 0 : x < 0 ? world.getWidth() -1 : x;
    y = y >= world.getHeight() ? 0 : y < 0 ? world.getHeight() -1 : y;
    world.getNewWorld().get(location).setTrail(new Trail(trailSize, this));
    world.getNewWorld().get(location).setCell(this);
    location = Location.of(x, y);
    resetFoodAndPath();
  }

  public void setEnergy(double energy) {
    this.energy = energy;
  }



  @SuppressWarnings ("AssignmentOrReturnOfFieldWithMutableType")
  protected List<Direction> getPath() {
    return path;
  }

  private Location findBirthplace(World world) {
    int rx = RANDOM.nextInt(((getX() + vision) - (getX() - vision)) + 1) + (getX() - vision);
    int ry = RANDOM.nextInt(((getY() + vision) - (getY() - vision)) + 1) + (getY() - vision);
    if (ry < 0 || rx < 0 || ry >= world.getHeight() || rx >= world.getWidth()) {
      return null;
    }
    else {
      if (world.getNewWorld().get(Location.of(rx, ry)).getCell() == null) {
        return new Location(rx, ry);
      }
      else {
        return null;
      }
    }
  }

  protected String getGeneCode() {
    return geneCode;
  }

  protected int getOffspring() {
    return offspring;
  }

  private void evolve() {
    switch (RANDOM.nextInt(5)) {
      case 0:
        mutateVision();
        break;
      case 1:
        mutateEfficiency();
        break;
      case 2:
        mutateSpeed();
        break;
      case 3:
        mutateTrailSize();
        break;
      case 4:
        mutateBiteSize();
        break;

    }
  }

  private void inheritFrom(Cell parent) {
    energy      = parent.getEnergy() / 3.0;
    vision      = parent.getVision();
    speed       = parent.getSpeed();
    efficiency  = parent.getEfficiency();
    biteSize    = parent.getBiteSize();
  }

  private void upkeep(World world) {
    energy -= efficiency * 0.01; // todo Mirza S. : why 1% of efficiency?
    if (!alive || energy <= 0.0 || offspring >= OFFSPRING_LIMIT) {
      die(world);
    }
  }

  public void die(World world) {
    alive = false;
    world.getNewWorld().get(location).setDeadCell(this);
    world.getNewWorld().get(location).setCell(null);
  }

  private void mutateVision() {
    vision++;
  }

  private void mutateEfficiency() {
    efficiency *= 0.95;
  }

  private void mutateSpeed() {
    speed += 0.25;
  }

  private void mutateTrailSize() {
    if ((trailSize - 2) > 2) {
      trailSize -= 2;
    }
  }

  private void mutateBiteSize() {
    biteSize *= 1.05;
  }

}
