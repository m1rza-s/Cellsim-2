package edu.lexaron.cells;

import edu.lexaron.world.Location;
import edu.lexaron.world.Tile;
import edu.lexaron.world.Trail;
import edu.lexaron.world.World;
import javafx.scene.image.Image;

import java.security.SecureRandom;
import java.util.ArrayDeque;
import java.util.Queue;
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
public abstract class Cell {

  private static final Random RANDOM    = new SecureRandom();
  private static final double BIRTH_REQ = 100.0;
  private static final int    OFFSPRING_LIMIT = 3;
  private final int               movement;
  private final String            geneCode;
  private final Queue<Direction>  path;

  private boolean alive;
  private int    x, y, vision, trailSize, offspring, oppositeRandomStep, lastRandomStep;
  private double energy, speed, efficiency, biteSize;
  private Location food = null;

  /**
   * Creates a new {@link Cell} based on the provided parameters.
   *
   * @param id          unique {@link Breed} ID
   * @param x           horizontal coordinate of birth location
   * @param y           vertical coordinate of birth location
   * @param energy      initial energy level, usually 50
   * @param vision      initial vision range, determines the FoV
   * @param speed       initial speed, determines how fast the {@link Cell} uses it's {@link Cell#path}
   * @param efficiency  initial efficiency, determines how much energy a {@link Cell} expends for each action it takes
   * @param biteSize    initial size of bite, determines how fast the {@link Cell} consumes it's food source
   */
  @SuppressWarnings ({"UnnecessaryThis"})
  protected Cell(String id, int x, int y, double energy, int vision, double speed, double efficiency, double biteSize) {
    this.path = new ArrayDeque<>();
    this.geneCode = id;
    this.x = x;
    this.y = y;
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
   * @param w the {@link World} that contains the food
   */
  public abstract void lookForFood(World w);

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

  abstract void eat(World w);

  abstract Cell doGiveBirth(int x, int y);

  @SuppressWarnings ({"MagicCharacter"})
  private void tryBirth(World world) {
    if (energy >= BIRTH_REQ) {
      Location birthPlace = findBirthplace(world);
      Cell child = doGiveBirth(birthPlace.getX(), birthPlace.getY());
      child.inheritFrom(this);
      child.evolve();
      world.getNewBornCells().add(child);
      offspring += 1;
      energy /= 3.0;
    }
  }

  /**
   * Handles upkeep, includes the {@link Cell#doHunt(World)} method, tries to produce offspring and handles death.
   *
   * @param world where it all takes place
   */
  @SuppressWarnings ("MagicNumber")
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
    return x;
  }

  /**
   * @return the vertical coordinate of this {@link Cell}
   */
  public final int getY() {
    return y;
  }

  /**
   * @param x new horizontal coordinate of this {@link Cell}
   */
  public final void setX(int x) {
    this.x = x;
  }

  /**
   * @param y new vertical coordinate of this {@link Cell}
   */
  public final void setY(int y) {
    this.y = y;
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

  static Random getRandom() {
    return RANDOM;
  }

  void findPathTo(Location target) {
    if (target != null) {
      int difY = target.getY() - y;
      int difX = target.getX() - x;
//      System.out.println("Cell: " + x + "," + y);
//      System.out.println("Food: " + target.getX() + "," + target.getY());
//      System.out.println("dist: " + difX + "," + difY);
      if (difX > 0) {
        for (int i = 0; i < Math.abs(difX); i++) {
          path.offer(Direction.RIGHT);
        }
      }
      if (difX < 0) {
        for (int i = 0; i < Math.abs(difX); i++) {
          path.offer(Direction.LEFT);
        }
      }
      if (difY > 0) {
        for (int i = 0; i < Math.abs(difY); i++) {
          path.offer(Direction.DOWN);
        }
      }
      if (difY < 0) {
        for (int i = 0; i < Math.abs(difY); i++) {
          path.offer(Direction.UP);
        }
      }
    }
    else {
      resetFoodAndPath();
    }
  }

  void setFood(int x, int y) {
    food = new Location(x, y);
  }

  void resetFoodAndPath() {
    path.clear();
    food = null;
  }

  void useWholePath(World w) {
    for (int i = 0; i < speed; i++) {
      if (!path.isEmpty()) {
        move(w, path.poll());
      }
    }
  }

  void randomStep(World w) {
    int roll = RANDOM.nextInt(5);
    while (roll == oppositeRandomStep && roll == lastRandomStep) {
      roll = RANDOM.nextInt(5);
    }
    switch (roll) {
      case 0:
        oppositeRandomStep = 2;
        lastRandomStep = 0;
        move(w, Direction.UP);
        break;
      case 1:
        oppositeRandomStep = 3;
        lastRandomStep = 1;
//        moveRight(w);
        move(w, Direction.RIGHT);
        break;
      case 2:
        oppositeRandomStep = 0;
        lastRandomStep = 2;
//        moveDown(w);
        move(w, Direction.DOWN);
        break;
      case 3:
        oppositeRandomStep = 1;
        lastRandomStep = 3;
//        moveLeft(w);
        move(w, Direction.LEFT);
        break;
      case 4:
        break;
    }
  }

  @SuppressWarnings ({"ImplicitNumericConversion", "ProhibitedExceptionCaught"})
  void move(World world, Direction dir) {
      if (isValidLocation(world, x + dir.getDeltaX(), y + dir.getDeltaY())) {
        Tile targetLocation = world.getWorld()[y + dir.getDeltaY()][x + dir.getDeltaX()];
        if (targetLocation.getCell() == null) {
          if ((energy - (movement * efficiency)) > 0) {
            energy -= (movement * efficiency);
            world.getWorld()[y][x].setCell(null);
            y += dir.getDeltaY();
            x += dir.getDeltaX();
            world.getWorld()[y][x].setTrail(new Trail(trailSize, this));
            world.getWorld()[y][x].setCell(this);
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
        circumnavigate(world, x + dir.getDeltaX(), y + dir.getDeltaY());
      }
  }

  private void circumnavigate(World world, int x, int y) {
    x = x >= world.getWidth()  ? 0 : x < 0 ? world.getWidth() -1 : x;
    y = y >= world.getHeight() ? 0 : y < 0 ? world.getHeight() -1 : y;
    world.getWorld()[this.y][this.x].setCell(null);
    world.getWorld()[y][x].setTrail(new Trail(trailSize, this));
    world.getWorld()[y][x].setCell(this);
    this.x = x;
    this.y = y;
    resetFoodAndPath();
  }

  void setEnergy(double energy) {
    this.energy = energy;
  }

  boolean isValidLocation(World world, int x, int y) {
    return x >= 0 && x < world.getWidth()
        && y >= 0 && y < world.getHeight();
  }

  @SuppressWarnings ("AssignmentOrReturnOfFieldWithMutableType")
  Queue<Direction> getPath() {
    return path;
  }

  Location findBirthplace(World w) { // todo Mirza : can cause app to hang if no suitable place is available
    Location birthplace = null;
    boolean found = false;
    while (!found) {
      int rx = RANDOM.nextInt(((x + vision) - (x - vision)) + 1) + (x - vision);
      int ry = RANDOM.nextInt(((y + vision) - (y - vision)) + 1) + (y - vision);
      if (!(ry < 0 || rx < 0 || ry >= w.getHeight() || rx >= w.getWidth())) {
        if (w.getWorld()[ry][rx].getCell() == null && w.getWorld()[ry][rx].getDeadCell() == null) {
          birthplace = new Location(rx, ry);
          found = true;
        }
      }
    }
    return birthplace;
  }

  String getGeneCode() {
    return geneCode;
  }

  int getOffspring() {
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

  private void upkeep(World w) {
//    energy -= biteSize * efficiency / 10.0; // todo Mirza : think of a tax
    if (!alive || energy <= 0.0 || offspring >= OFFSPRING_LIMIT) {
      die(w);
    }
  }

  void die(World world) {
    alive = false;
    world.getWorld()[y][x].setDeadCell(this);
    world.getWorld()[y][x].setCell(null);
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
