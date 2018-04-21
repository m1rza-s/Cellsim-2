/*
 *  Project name: CellSIM/Predator.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 13, 2016, 5:53:57 PM
 */
package edu.lexaron.cells;

import edu.lexaron.world.World;
import javafx.scene.image.Image;

/**
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Predator extends Cell {
  private static final Image GFX = new Image("edu/lexaron/gfx/predator.png");

  public Predator(String ID, int x, int y) {
    super(ID, x, y, 50, 5, 1, 0.33, 1);
  }

  public Predator(World world) {
    this("P", getRandom().nextInt(world.getWidth()), getRandom().nextInt(world.getHeight()));
  }

  @Override
  public Image getImage() {
    return GFX;
  }

  @Override
  public Breed getBreed() {
    return Breed.PREDATOR;
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
          usePath(w);
        }
        else {
          for (int i = 1; i <= (int) getSpeed(); i++) {
            moveUp(w);
            randomStep(w);
          }
        }
      }
      if (!getPath().isEmpty()
          && (w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getCell() != null
          || w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getTrail().getAmount() > 0)) {
        usePath(w);
        eat(w);
      }
    }
  }

  /**
   * @param w
   */
  @Override
  public void eat(World w) {
        outterloop:
    for (int i = (getY() - 1); i <= (getY() + 1); i++) {
      for (int j = (getX() - 1); j <= (getX() + 1); j++) {
        try {
          if (w.getWorld()[i][j].getCell() != null
              && !w.getWorld()[i][j].getCell().equals(this)
              && w.getWorld()[i][j].getCell().getBreed() != getBreed()
//              && w.getWorld()[i][j].getCell().getBreed() != Breed.VULTURE
//              && w.getWorld()[i][j].getCell().getBreed() != Breed.TREE
              ) {
            setEnergy(getEnergy() + (w.getWorld()[i][j].getCell().getEnergy() / 2));
            w.getWorld()[i][j].getCell().setAlive(false);
            w.getWorld()[i][j].setDeadCell(w.getWorld()[i][j].getCell());
            w.getWorld()[i][j].setCell(null);
            w.getWorld()[i][j].getSugar().setAmount(10);
//                        System.out.println("KILLED " + w.getWorld()[i][j].getCell().getGeneCode());
            setTargetFood(null);
            break outterloop;
          }
        }
        catch (ArrayIndexOutOfBoundsException ex) {

        }
      }
    }
    setTargetFood(null);
    getPath().clear();
  }

  /**
   * @param w
   * @return
   */
  @Override
  public int[] lookForFood(World w) {
    // Cell type PREDATOR is only interested in hunting other cell types.
//        System.out.println("Cell " + getGeneCode() + " looking for food from " + getX() + "," + getY() + "...");
    int[] foodLocation = new int[2];
    boolean found = false;
    int foundSmell = 0;
        outterloop:
    for (int v = 1; v <= getVision(); v++) {
      for (int i = (getY() - v); i <= (getY() + v); i++) {
        for (int j = (getX() - v); j <= (getX() + v); j++) {
          try {
//                    System.out.print("(" + j + "," + i + ")");
            if (w.getWorld()[i][j].getCell() != null && w.getWorld()[i][j].getCell().isAlive()) {
              if (w.getWorld()[i][j].getCell().getBreed() != getBreed()
//              && w.getWorld()[i][j].getCell().getBreed() != Breed.VULTURE
//              && w.getWorld()[i][j].getCell().getBreed() != Breed.TREE
                  ) {
                foodLocation[0] = i; // Y
                foodLocation[1] = j; // X
                found = true;
                break outterloop;
              }
            }
            else if (w.getWorld()[i][j].getTrail().getSource() != null
                && w.getWorld()[i][j].getTrail().getSource().getBreed() != getBreed()
//              && w.getWorld()[i][j].getCell().getBreed() != Breed.VULTURE
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

  /**
   * @param w
   */
  @Override
  public void mutate(World w) {
    int[] childLocation = findFreeTile(w);
    Predator child = new Predator(String.valueOf(getGeneCode() + "." + getOffspring()), childLocation[1], childLocation[0]);
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
