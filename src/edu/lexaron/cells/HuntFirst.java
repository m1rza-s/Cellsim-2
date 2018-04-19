/*
 *  Project name: CellSIM/cell_max.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 1, 2016, 1:51:43 AM
 */
package edu.lexaron.cells;

import edu.lexaron.world.World;

/**
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class HuntFirst extends Cell {
  public HuntFirst(String ID, int x, int y) {
    super(ID, x, y, 50, 3, 3, 1, "#66ff33", 1);
  }

  @Override
  public Breed getBreed() {
    return Breed.HUNT_FIRST;
  }

  @Override
  public void doHunt(World w) {
    if (isAlive()) {
      if (getPath().isEmpty()) {
        if (w.getWorld()[getY()][getX()].getSugar().getAmount() <= 0) {
          setTargetFood(lookForFood(w));
          if (getTargetFood() != null) {
            findPathTo(getTargetFood());
            usePath(w);
          }
          else {
            for (int i = 1; i <= getSpeed(); i++) {
              moveLeft(w);
              randomStep(w);
            }
          }
        }
        else {
          eat(w);
        }
      }
      else {
        usePath(w);
      }

      if (getEnergy() >= 100) {
        mutate(w);
      }
      if (getOffspring() >= 3) {
        setAlive(false);
        w.getWorld()[getY()][getX()].setDeadCell(this);
        w.getWorld()[getY()][getX()].setCell(null);
      }
    }
  }

  /**
   * @param w
   * @return
   */
  @Override
  public int[] lookForFood(World w) {
    // Cell type FIRST is only interested in the FIRST sugar tile it finds.
//        System.out.println("Cell " + getGeneCode() + " looking for food from " + getX() + "," + getY() + "...");
    int[] foodLocation = new int[2];
    boolean found = false;
        outerloop:
    for (int i = (getY() + getVision()); i >= (getY() - getVision()); i--) {
      for (int j = (getX() - getVision()); j <= (getX() + getVision()); j++) {
        try {
//                    System.out.print("(" + j + "," + i + ")");
          if (w.getWorld()[i][j].getSugar().getAmount() > 0) {
            foodLocation[0] = i; // Y
            foodLocation[1] = j; // X
            found = true;
            break outerloop;
          }

        }
        catch (ArrayIndexOutOfBoundsException ex) {

        }
      }
//            System.out.print("\n");
    }
    if (found) {
//            System.out.println(getGeneCode() + " found food on " + sugarLocation[0] + "," + sugarLocation[1]);
      return foodLocation;
    }
    else {
//            System.out.println(getGeneCode() + " found no food.");
      return null;
    }

  }

  /**
   * @param w
   */
  @Override
  public void mutate(World w) {
    int[] childLocation = findFreeTile(w);
    HuntFirst child = new HuntFirst(String.valueOf(getGeneCode() + "." + getOffspring()), childLocation[1], childLocation[0]);
    child.inheritFrom(this);
    try {
      child.eat(w);
      child.evolve();
      w.getNewBornCells().add(child);
      setOffspring(getOffspring() + 1);
      setEnergy(getEnergy() / 3);
    }
    catch (Exception ex) {
      System.out.println(getGeneCode() + " failed to divide:\n" + ex);
    }
  }
}
