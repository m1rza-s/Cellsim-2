/*
 *  Project name: CellSIM/Cell.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Feb 5, 2016, 8:55:28 PM
 */
package edu.lexaron.cells;

import edu.lexaron.world.Trail;
import edu.lexaron.world.World;

import java.security.SecureRandom;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Random;

/**
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public abstract class Cell {

  private final static Random RANDOM = new SecureRandom();
  private final String color;
  private final int movement;
  private final Queue<Integer> path = new ArrayDeque<>();
  private boolean alive;
  private double energy; // if > 0, alive
  private double speed;
  private double efficiency;
  private double biteSize;
  private int[] targetFood = null;
  private int x;
  private int y; // position
  private int vision;
  private int trailSize;
  private int offspring = 0;
  private int oppositeRandomStep;
  private int lastRandomStep;
  private String geneCode;

  protected Cell(String ID, int x, int y, double energy, int vision, double speed, double efficiency, String color, double biteSize) {
    this.geneCode = ID;
    this.x = x;
    this.y = y;
    this.energy = energy;
    this.vision = vision;
    this.movement = 1;
    this.speed = speed;
    this.efficiency = efficiency;
    if (energy > 0) {
      this.alive = true;
    }
    this.color = color;
    this.trailSize = 50;
    this.targetFood = null;
    this.biteSize = biteSize;
  }

  public abstract int[] lookForFood(World w);
  public abstract void mutate(World w);
  public abstract Breed getBreed();
  public abstract void doHunt(World w);

  static Random getRandom() {
    return RANDOM;
  }

  public void hunt(World w) {
    // CELL TYPE DEPENDANT
    upkeep(w);
    doHunt(w);

  }

  public void eat(World w) {
    //((targetFood[0] >= y-vision && targetFood[0] <= y+vision) && (targetFood[1] >= x-vision && targetFood[1] <= x+vision)) &&
    if (w.getWorld()[y][x].getSugar().getAmount() > 0) {
      w.getWorld()[y][x].getSugar().setAmount(w.getWorld()[y][x].getSugar().getAmount() - biteSize);
      energy += biteSize;
      //            System.out.println(geneCode + "   ate on " + x + "," + y + ": energy +" + w.getWorld()[y][x].getSugar().getAmount());
    }
    else {
      targetFood = null;
    }
  }

  public int[] findFreeTile(World w) {
    int loc[] = new int[2];
    int rx, ry;
    boolean found = false;
    while (!found) {
      rx = getRandom().nextInt(((x + vision) - (x - vision)) + 1) + (x - vision);
      ry = getRandom().nextInt(((y + vision) - (y - vision)) + 1) + (y - vision);
      if (!(ry < 0 || rx < 0 || ry >= w.getHeight() || rx >= w.getWidth())) {
        if (w.getWorld()[ry][rx].getCell() == null && w.getWorld()[ry][rx].getDeadCell() == null) {
          loc[0] = ry;
          loc[1] = rx;
          found = true;
        }
      }
    }
    return loc;
  }

  public void evolve() {
    switch (getRandom().nextInt(5)) {
      case 0:
//                    System.out.println(getGeneCode() + " MUTATION! +1 vision");
        mutateVision();
        break;
      case 1:
//                    System.out.println(getGeneCode() + " MUTATION! +0.01 efficiency");
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

  public void setGeneCode(String geneCode) {
    this.geneCode = geneCode;
  }

  public double getEnergy() {
    return energy;
  }

  public void setEnergy(double energy) {
    this.energy = energy;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public int getVision() {
    return vision;
  }

  public double getSpeed() {
    return speed;
  }

  public double getEfficiency() {
    return efficiency;
  }

  public boolean isAlive() {
    return alive;
  }

  public void setAlive(boolean alive) {
    this.alive = alive;
  }

  public int[] getTargetFood() {
    return targetFood;
  }

  public String getColor() {
    return color;
  }

  public double getBiteSize() {
    return biteSize;
  }

  public void inheritFrom(Cell parent) {
    setEnergy(parent.getEnergy() / 3);
    setVision(parent.getVision());
    setSpeed(parent.getSpeed());
    setEfficiency(parent.getEfficiency());
    setBiteSize(parent.getBiteSize());
  }

  void findPathTo(int[] targetLocation) {
    if (path.isEmpty()) {
      int difY = targetLocation[0] - y;
      int difX = targetLocation[1] - x;
//            System.out.println("Cell " + geneCode + " at " + x + "," + y + ", targetFood distance: " + difX + "," + difY);
      if (difX > 0) {
        for (int i = 0; i < Math.abs(difX); i++) {
          path.offer(3);
        }
      }
      if (difX < 0) {
        for (int i = 0; i < Math.abs(difX); i++) {
          path.offer(9);
        }
      }
      if (difY > 0) {
        for (int i = 0; i < Math.abs(difY); i++) {
          path.offer(6);
        }
      }
      if (difY < 0) {
        for (int i = 0; i < Math.abs(difY); i++) {
          path.offer(12);
        }
      }
    }
  }

  void upkeep(World w) {
    if (energy < 0) {
      alive = false;
      w.getWorld()[y][x].setDeadCell(this);
      w.getWorld()[y][x].setCell(null);
    }
  }

  void usePath(World w) {
    for (int i = 1; i <= getSpeed(); i++) {
      if (!path.isEmpty()) {
        int move = path.poll();
        switch (move) {
          case 3:
            moveRight(w);
            break;
          case 9:
            moveLeft(w);
            break;
          case 6:
            moveDown(w);
            break;
          case 12:
            moveUp(w);
            break;
          default:
            System.out.println("    Unknown move direction in MOVETOFOOD!");
            break;
        }
      }
    }
  }

  void randomStep(World w) {
    int roll = getRandom().nextInt(5);
    while (roll == oppositeRandomStep && roll == lastRandomStep) {
      roll = getRandom().nextInt(5);
    }
    switch (roll) {
      case 0:
        oppositeRandomStep = 2;
        lastRandomStep = 0;
        moveUp(w);
        break;
      case 1:
        oppositeRandomStep = 3;
        lastRandomStep = 1;
        moveRight(w);
        break;
      case 2:
        oppositeRandomStep = 0;
        lastRandomStep = 2;
        moveDown(w);
        break;
      case 3:
        oppositeRandomStep = 1;
        lastRandomStep = 3;
        moveLeft(w);
        break;
      case 4:
        break;
      default:
        System.out.println("Unexpected roll in HUNT method.");
        break;
    }
  }

  void moveUp(World w) { // code 12
    try {
      if (w.getWorld()[y - movement][x].getCell() == null) {
        if ((energy - (movement * efficiency)) > 0) {
          w.getWorld()[y][x].setTrail(new Trail(trailSize, this));
          w.getWorld()[y][x].setCell(null);
          setY(y - movement);
          w.getWorld()[y][x].setCell(this);
          setEnergy(energy - (movement * efficiency));
//                System.out.println(geneCode + " moved up...");
//                System.out.println("Cell " + geneCode + " moved to " + x + "," + y + ", energy: " + energy);
        }
        else {
          alive = false;
          w.getWorld()[y][x].setDeadCell(this);
          w.getWorld()[y][x].setCell(null);
//                    System.out.println(geneCode + "  died on " + x + "," + y + ", energy: " + energy);
        }
      }
      else {
        path.clear();
        randomStep(w);
      }
    }
    catch (ArrayIndexOutOfBoundsException ex) {
      w.getWorld()[0][x].setCell(null);
      setY(w.getHeight() - 1);
      w.getWorld()[y][x].setCell(this);
      setEnergy(energy - (movement * efficiency));
      eat(w);
    }
  }

  void moveRight(World w) { // code 3
    try {
      //(x + movement) < w.getWidth() &&
      if (w.getWorld()[y][x + movement].getCell() == null) {
        if ((energy - (movement * efficiency)) > 0) {
          w.getWorld()[y][x].setTrail(new Trail(trailSize, this));
          w.getWorld()[y][x].setCell(null);
          setX(x + movement);
          w.getWorld()[y][x].setCell(this);
          setEnergy(energy - (movement * efficiency));
//                System.out.println(geneCode + " moved right...");
//                System.out.println("Cell " + geneCode + " moved to " + x + "," + y + ", energy: " + energy);
        }
        else {
          alive = false;
          w.getWorld()[y][x].setDeadCell(this);
          w.getWorld()[y][x].setCell(null);
//                    System.out.println(geneCode + " died on " + x + "," + y + ", energy: " + energy);
        }
      }
      else {
        path.clear();
        randomStep(w);
      }
    }
    catch (ArrayIndexOutOfBoundsException ex) {
      w.getWorld()[y][w.getWidth() - 1].setCell(null);
      setX(0);
      w.getWorld()[y][x].setCell(this);
      setEnergy(energy - (movement * efficiency));
      eat(w);
    }

  }

  void moveDown(World w) { // code 6
    try {
      //(y + movement) < w.getHeight() &&
      if (w.getWorld()[y + movement][x].getCell() == null) {
        if ((energy - (movement * efficiency)) > 0) {
          w.getWorld()[y][x].setTrail(new Trail(trailSize, this));
          w.getWorld()[y][x].setCell(null);
          setY(y + movement);
          w.getWorld()[y][x].setCell(this);
          setEnergy(energy - (movement * efficiency));
//                System.out.println(geneCode + " moved down...");
//                System.out.println("Cell " + geneCode + " moved to " + x + "," + y + ", energy: " + energy);
        }
        else {
          alive = false;
          w.getWorld()[y][x].setDeadCell(this);
          w.getWorld()[y][x].setCell(null);
//                    System.out.println(geneCode + " died on " + x + "," + y + ", energy: " + energy);
        }
      }
      else {
        path.clear();
        randomStep(w);
      }
    }
    catch (ArrayIndexOutOfBoundsException ex) {
      w.getWorld()[w.getHeight() - 1][x].setCell(null);
      setY(0);
      w.getWorld()[y][x].setCell(this);
      setEnergy(energy - (movement * efficiency));
      eat(w);
    }

  }

  void setTargetFood(int[] targetFood) {
    this.targetFood = targetFood;
  }

  void setOffspring(int offspring) {
    this.offspring = offspring;
  }

  Queue<Integer> getPath() {
    return path;
  }

  String getGeneCode() {
    return geneCode;
  }

  int getOffspring() {
    return offspring;
  }

  private void mutateVision() {
    if (vision < 10) {
      vision++;
    }
    else {
      mutateEfficiency();
    }

  }

  private void mutateEfficiency() {
    efficiency *= 0.95;
  }

  private void mutateSpeed() {
    speed += 0.25;
  }

  private void mutateTrailSize() {
    if ((trailSize - 2) > 2) {
      trailSize = trailSize - 2;
    }
  }

  private void mutateBiteSize() {
    biteSize += 0.5;
  }

  private void setEfficiency(double efficiency) {
    this.efficiency = efficiency;
  }

  private void setVision(int vision) {
    this.vision = vision;
  }

  private void setSpeed(double speed) {
    this.speed = speed;
  }

  private void setBiteSize(double biteSize) {
    this.biteSize = biteSize;
  }

  protected void moveLeft(World w) { // code 9
    try {
      //(x - movement) >= 0 &&
      if (w.getWorld()[y][x - movement].getCell() == null) {
        if ((energy - (movement * efficiency)) > 0) {
          w.getWorld()[y][x].setTrail(new Trail(trailSize, this));
          w.getWorld()[y][x].setCell(null);
          setX(x - movement);
          w.getWorld()[y][x].setCell(this);
          setEnergy(energy - (movement * efficiency));
//                System.out.println(geneCode + " moved left...");
//                System.out.println("Cell " + geneCode + " moved to " + x + "," + y + ", energy: " + energy);
        }
        else {
          alive = false;
          w.getWorld()[y][x].setDeadCell(this);
          w.getWorld()[y][x].setCell(null);
//                    System.out.println(geneCode + " died on " + x + "," + y + ", energy: " + energy);
        }
      }
      else {
        path.clear();
        randomStep(w);
      }
    }
    catch (ArrayIndexOutOfBoundsException ex) {
      w.getWorld()[y][0].setCell(null);
      setX(w.getWidth() - 1);
      w.getWorld()[y][x].setCell(this);
      setEnergy(energy - (movement * efficiency));
      eat(w);
    }
  }
}
