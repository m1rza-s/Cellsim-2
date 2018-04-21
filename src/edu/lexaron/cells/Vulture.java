/*
 *  Project name: CellSIM/Vulture.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 14, 2016, 1:21:12 AM
 */
package edu.lexaron.cells;

import edu.lexaron.world.World;

/**
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Vulture extends Cell {

  public Vulture(String ID, int x, int y) {
    super(ID, x, y, 50, 3, 1, 0.5,  1);
  }

  public Vulture(World world) {
    this("V", getRandom().nextInt(world.getWidth()), getRandom().nextInt(world.getHeight()));
  }

  @Override
  public Breed getBreed() {
    return Breed.VULTURE;
  }

  /**
   * @param w
   */
  @Override
  public void doHunt(World w) {
    if (isAlive()) {
      if (getPath().isEmpty()) {
        setTargetFood(lookForFood(w));
        if (getTargetFood() != null) {
          findPathTo(getTargetFood());
        }
        else {
          randomStep(w);
          moveRight(w);
        }
      }
      if (getTargetFood() != null && w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getDeadCell() != null) {
        usePath(w);
        if (getTargetFood()[0] == getY() && getTargetFood()[1] == getX()) {
          eat(w);
        }
      }
      else {
        getPath().clear();
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
   */
  @Override
  public void eat(World w) {
    if (w.getWorld()[getY()][getX()].getDeadCell() != null) {
      setEnergy(getEnergy() +  w.getWorld()[getY()][getX()].getDeadCell().getEnergy());
//            if (w.getWorld()[getY()][getX()].getDeadCell() != null) {
      w.getEatenCorpses().add(w.getWorld()[getY()][getX()].getDeadCell());
      w.getWorld()[getY()][getX()].getSugar().setAmount(10);
//            }
      setTargetFood(null);
      getPath().clear();
//            System.out.println(geneCode + "   ate on " + x + "," + y + ": energy +" + w.getWorld()[y][x].getSugar().getAmount());
    }
  }

  @Override
  public int[] lookForFood(World w) {
    // Cell type VULTURE is only interested in dead cells.
//        System.out.println("Cell " + getGeneCode() + " looking for food from " + getX() + "," + getY() + "...");
    int[] foodLocation = new int[2];
    boolean found = false;
        outterloop:
    for (int v = 1; v <= getVision(); v++) {
      for (int i = (getY() - v); i <= (getY() + v); i++) {
        for (int j = (getX() - v); j <= (getX() + v); j++) {
          try {
//                    System.out.print("(" + j + "," + i + ")");
            if (w.getWorld()[i][j].getDeadCell() != null) {
              foodLocation[0] = i; // Y
              foodLocation[1] = j; // X
              found = true;
              break outterloop;
            }
//           else if (w.getWorld()[i][j].getCell() != null
//              && w.getWorld()[i][j].getCell().getBreed() != Breed.PREDATOR) {
//
//                            foodLocation[0] = i; // Y
//                            foodLocation[1] = j; // X
//                            found = true;
//                            break outterloop;
//                        }

          }
          catch (ArrayIndexOutOfBoundsException ex) {

          }
        }
//            System.out.print("\n");
      }
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
    Vulture child = new Vulture(String.valueOf(getGeneCode() + "." + getOffspring()), childLocation[1], childLocation[0]);
    child.inheritFrom(this);
    try {
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
