/*
 *  Project name: CellSIM/Tree.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 19, 2016, 12:57:57 AM
 */
package edu.lexaron.cells;

import edu.lexaron.world.World;

import java.util.Random;

/**
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Tree extends Cell {

  public Tree(String ID, int x, int y) {
    super(ID, x, y, 50, 10, 1, 0.5, "#ffffff", 0.5);
  }

  @Override
  public void hunt(World w) {
    if (isAlive()) {
      upkeep(w);
      if (getTargetFood() == null) {
        setTargetFood(lookForFood(w));
      }
      else {
        for (int i = 0; i < (int) getSpeed(); i++) {
          eat(w);
        }
      }
      if (getEnergy() < 0) {
        setAlive(false);
        w.getWorld()[getY()][getX()].setDeadCell(this);
        w.getWorld()[getY()][getX()].setCell(null);
      }
      if (getEnergy() >= 100) {
        mutate(w);
      }
      if (getOffspring() >= 2) {
        setAlive(false);
        w.getWorld()[getY()][getX()].setDeadCell(this);
        w.getWorld()[getY()][getX()].setCell(null);
      }
    }

  }

  @Override
  public void eat(World w) {
    if (getTargetFood() != null && w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getSugar().getAmount() > 0) {
      setEnergy(getEnergy() + getBiteSize());
      w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getSugar().setAmount(w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getSugar().getAmount() - getBiteSize());
      if (w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getSugar().getAmount() < 1) {
        setTargetFood(null);
      }
      //            System.out.println(geneCode + "   ate on " + x + "," + y + ": energy +" + w.getWorld()[y][x].getSugar().getAmount());
    }
    else if (getTargetFood() != null && w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getDeadCell() != null && w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getDeadCell().getEnergy() > 0) {
      w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getDeadCell().setEnergy(w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getDeadCell().getEnergy() - getBiteSize());
      setEnergy(getEnergy() + getBiteSize());
      if (w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getDeadCell().getEnergy() < 0) {
        setTargetFood(null);
      }
    }
    else if (getTargetFood() != null && w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getCell() != null && w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getCell().getClass().equals(this.getClass())) {
      setEnergy(getEnergy() + getBiteSize());
      w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getCell().setEnergy(w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getCell().getEnergy() - getBiteSize());
    }
    else {
      setTargetFood(null);
    }
  }

  @Override
  public int[] lookForFood(World w) {
    // Cell type TREE decrements sugar in it's surrounding.
//        System.out.println("Cell " + getGeneCode() + " looking for food from " + getX() + "," + getY() + "...");
    int[] foodLocation = new int[2];
    boolean found = false;
    int rx, ry;

    rx = new Random().nextInt(((getX() + getVision()) - (getX() - getVision())) + 1) + (getX() - getVision());
    ry = new Random().nextInt(((getY() + getVision()) - (getY() - getVision())) + 1) + (getY() - getVision());
    try {
      if ((rx < w.getWidth() && ry < w.getHeight() && rx >= 0 && ry >= 0)
          && (w.getWorld()[ry][rx].getSugar().getAmount() > 0)) {
        foodLocation[0] = ry;
        foodLocation[1] = rx;
        found = true;
      }
      else if ((rx < w.getWidth() && ry < w.getHeight() && rx >= 0 && ry >= 0) && !found && new Random().nextInt(3) == 2) {
        w.getWorld()[ry][rx].getSugar().setAmount(3);
      }
    }
    catch (ArrayIndexOutOfBoundsException ex) {
      System.out.println("Tree: " + ex + "rx: " + rx + ", ry: " + ry);
    }
    if (found) {
//            System.out.println(getGeneCode() + " found food on " + foodLocation[0] + "," + foodLocation[1]);
      return foodLocation;
    }
    else {
//            System.out.println(getGeneCode() + " found no food.");
      return null;
    }
  }

  @Override
  public void mutate(World w) {
    int[] childLocation = findFreeTile(w);
    Tree child = new Tree(String.valueOf(getGeneCode() + "." + getOffspring()), childLocation[1], childLocation[0]);
    child.inheritFrom(this);
    try {
      child.evolve();
      w.getNewBornCells().add(child);
      setOffspring(getOffspring() + 1);
      setEnergy(getEnergy() / 4);
    }
    catch (Exception ex) {
      System.out.println(getGeneCode() + " failed to divide:\n" + ex);
    }
  }

  @Override
  public int[] findFreeTile(World w) {
    int loc[] = new int[2];
    int rx, ry;
    boolean found = false;
    while (!found) {
      rx = new Random().nextInt(((getX() + (getVision() * 2)) - (getX() - (getVision() * 2))) + 1) + (getX() - (getVision() * 2));
      ry = new Random().nextInt(((getY() + (getVision() * 2)) - (getY() - (getVision() * 2))) + 1) + (getY() - (getVision() * 2));
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
}
