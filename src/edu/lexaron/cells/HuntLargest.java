/*
 *  Project name: CellSIM/HuntLargest.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 12, 2016, 7:37:15 PM
 */
package edu.lexaron.cells;

import edu.lexaron.world.World;

/**
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class HuntLargest extends Cell {
  public HuntLargest(String ID, int x, int y) {
    super(ID, x, y, 50, 3, 2, 1, "#ffff33", 1);
  }

  @Override
  public Breed getBreed() {
    return Breed.HUNT_LARGEST;
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
    // Cell type LARGEST is only interested in the LARGEST sugar tile it finds.
//        System.out.println("Cell " + getGeneCode() + " looking for food from " + getX() + "," + getY() + "...");
    int[] foodLocation = new int[2];
    boolean found = false;
    int foundSugar = 0;

        outterloop:
    for (int v = getVision(); v > 0; v--) {
      for (int i = (getY() - v); i <= (getY() + v); i++) {
        for (int j = (getX() - v); j <= (getX() + v); j++) {
          try {
//                    System.out.print("(" + j + "," + i + ")");   
            if (w.getWorld()[i][j].getSugar().getAmount() > foundSugar) {
              foundSugar = (int) w.getWorld()[i][j].getSugar().getAmount();
              foodLocation[0] = i; // Y
              foodLocation[1] = j; // X
              found = true;
            }
          }
          catch (ArrayIndexOutOfBoundsException ex) {

          }
        }
      }
//            System.out.print("\n");
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

  /**
   * @param w
   */
  @Override
  public void mutate(World w) {
    int[] childLocation = findFreeTile(w);
    HuntLargest child = new HuntLargest(String.valueOf(getGeneCode() + "." + getOffspring()), childLocation[1], childLocation[0]);
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
