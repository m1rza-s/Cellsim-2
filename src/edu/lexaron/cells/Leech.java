/*
 *  Project name: CellSIM/Leech.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 19, 2016, 8:00:46 PM
 */
package edu.lexaron.cells;

import edu.lexaron.world.World;

/**
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Leech extends Cell {

  public Leech(String ID, int x, int y) {
    super(ID, x, y, 50, 5, 3, 0.25,  0.5);
  }

  public Leech(World world) {
    this("L", getRandom().nextInt(world.getWidth()), getRandom().nextInt(world.getHeight()));
  }

  @Override
  public Breed getBreed() {
    return Breed.LEECH;
  }

  @Override
  public void doHunt(World w) {
    if (isAlive()) {
      if (getPath().isEmpty()) {
        setTargetFood(lookForFood(w));
        if (getTargetFood() != null) {
          findPathTo(getTargetFood());
          usePath(w);
        }
        else {
          for (int i = 1; i <= (int) getSpeed(); i++) {
            moveDown(w);
            moveRight(w);
            randomStep(w);
          }
        }
      }
      if (!getPath().isEmpty() && getTargetFood() != null
          && (w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getCell() != null
          || w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getTrail().getAmount() >= 11)) {
        if (getTargetFood() != null && (getTargetFood()[0] >= (getY() - getVision())) && (getTargetFood()[0] <= (getY() + getVision()))
            && (getTargetFood()[1] >= (getX() - getVision())) && (getTargetFood()[1] <= (getX() + getVision()))) {
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

  @Override
  public void eat(World w) {
    if (getTargetFood() != null && w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getCell() != null) {
      w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getCell().setEnergy(w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getCell().getEnergy() - getBiteSize());
      setEnergy(getEnergy() + getBiteSize());
      if (w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getCell().getEnergy() < 0) {
        w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getCell().setAlive(false);
        w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].setDeadCell(w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getCell());
        w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].setCell(null);
      }
    }
    else {
      setTargetFood(null);
    }

  }

  @Override
  public int[] lookForFood(World w) {
    int[] foodLocation = new int[2];
    boolean found = false;
    int foundSmell = 0;
        outterloop:
    for (int v = 1; v <= getVision(); v++) {
      for (int i = (getY() - v); i <= (getY() + v); i++) {
        for (int j = (getX() - v); j <= (getX() + v); j++) {
          try {
//                    System.out.print("(" + j + "," + i + ")");        
            if (w.getWorld()[i][j].getCell() != null && !w.getWorld()[i][j].getCell().equals(this) && w.getWorld()[i][j].getCell().getBreed() != getBreed()) {
              foodLocation[0] = i; // Y
              foodLocation[1] = j; // X
              found = true;
              break outterloop;
//                        } else if (getTargetFood() == null && w.getWorld()[i][j].getCell() != null && w.getWorld()[i][j].getCell().isAlive() && w.getWorld()[i][j].getCell().getClass().equals(this.getClass()) && w.getWorld()[i][j].getCell().getTargetFood() != null                                ) {
//                            setTargetFood(w.getWorld()[i][j].getCell().getTargetFood());
////                            System.out.println("GOT COORDS: " + getTargetFood()[1] + "," + getTargetFood()[0]);
//                            found = true;
//                            break outterloop;
            }
            else if (w.getWorld()[i][j].getTrail().getSource() != null
                && w.getWorld()[i][j].getTrail().getSource().getBreed() != getBreed()
//                && !w.getWorld()[i][j].getTrail().getSource().equalsIgnoreCase("vulture")
                && w.getWorld()[i][j].getTrail().getAmount() > foundSmell) {
              foundSmell = w.getWorld()[i][j].getTrail().getAmount();
              foodLocation[0] = i; // Y
              foodLocation[1] = j; // X
              found = true;
            }
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
    Leech child = new Leech(String.valueOf(getGeneCode() + "." + getOffspring()), childLocation[1], childLocation[0]);
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
