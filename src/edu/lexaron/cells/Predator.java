/*
 *  Project name: CellSIM/Predator.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 13, 2016, 5:53:57 PM
 */
package edu.lexaron.cells;

import edu.lexaron.world.Cell;
import edu.lexaron.world.World;
import java.util.Random;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Predator extends Cell {

    public Predator(String ID, int x, int y, double energy, int vision, int movementCost, double efficiency, String color) {
        super(ID, x, y, energy, vision, movementCost, efficiency, color);
    }

    @Override
    public void hunt(World w) {
        // CELL TYPE DEPENDANT
        if (this.isAlive()) {
            if (getPath().isEmpty()) {
                setTargetFood(lookForFood(w));
                if (getTargetFood() != null) {
                    findPathTo(getTargetFood());
                    usePath(w);
                } else {
                    this.setEnergy(getEnergy() - 0.02);
                }
            }
            if (!getPath().isEmpty() && w.getTheWorld()[getTargetFood()[0]][getTargetFood()[1]].getCell() != null) {
                usePath(w);
                if (getPath().isEmpty()) {
                    eat(w);
                    setTargetFood(null);
                }
            } else {
                getPath().clear();
            }

            if (getEnergy() >= 100) {
                mutate(w);
                setEnergy(getEnergy() / 3);
            }
        }
    }

    @Override
    public void eat(World w) {
        outterloop:
        for (int i = (getY() - 1); i <= (getY() + 1); i++) {
            for (int j = (getX() - 1); j <= (getX() + 1); j++) {
                try {
                    if (w.getTheWorld()[i][j].getCell() != null
                            && w.getTheWorld()[i][j].getCell() != this
                            && !w.getTheWorld()[i][j].getCell().getClass().getSimpleName().equalsIgnoreCase(this.getClass().getSimpleName())) {
                        setEnergy(getEnergy() + (w.getTheWorld()[i][j].getCell().getEnergy()/2));
                        w.getTheWorld()[i][j].getCell().setEnergy(0);
                        w.getTheWorld()[i][j].getCell().setAlive(false);
                        w.getTheWorld()[i][j].setDeadCell(w.getTheWorld()[i][j].getCell());
                        w.getTheWorld()[i][j].setCell(null);
                        break outterloop;
//                        if (w.getTheWorld()[i][j].getCell() != this) {
//                            
//                        }
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {

                }
            }
        }

    }

    @Override
    public int[] lookForFood(World w) {
        // Cell type CLOSEST is only interested in the CLOSEST sugar tile it finds.
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
                        if (w.getTheWorld()[i][j].getCell() != null && w.getTheWorld()[i][j].getCell().isAlive()) {
                            if (!w.getTheWorld()[i][j].getCell().getClass().getSimpleName().equalsIgnoreCase(this.getClass().getSimpleName())) {
                                foodLocation[0] = i; // Y
                                foodLocation[1] = j; // X
                                found = true;
                                break outterloop;
                            }
                        } else if (w.getTheWorld()[i][j].getSmell() > 0 && w.getTheWorld()[i][j].getSmell() > foundSmell) {
                            foundSmell = w.getTheWorld()[i][j].getSmell();
                            foodLocation[0] = i; // Y
                            foodLocation[1] = j; // X
                            found = true;
                        }

                    } catch (ArrayIndexOutOfBoundsException ex) {

                    }
                }
//            System.out.print("\n");
            }
        }
        if (found) {
//            System.out.println(getGeneCode() + " found food on " + foodLocation[0] + "," + foodLocation[1]);
            return foodLocation;
        } else {
//            System.out.println(getGeneCode() + " found no food.");
            return null;
        }

    }

    @Override
    public void mutate(World w) {
        if (new Random().nextInt(2) == 0) {
            System.out.println(getGeneCode() + " MITOSIS!");
            int[] childLocation = findFreeTile(w);
            Predator child = new Predator(String.valueOf(getGeneCode() + "." + getOffspring()), childLocation[1], childLocation[0], (getEnergy() / 3), getVision(), getMovementCost(), getEfficiency(), getColor());
            try {
                w.getTheWorld()[childLocation[0]][childLocation[1]].setCell(child);
                child.eat(w);
                w.getNewBornCells().add(child);
                setOffspring(getOffspring() + 1);
            } catch (Exception ex) {
                System.out.println(getGeneCode() + " failed to divide.");
            }

        } else {
            switch (new Random().nextInt(2)) {
                case 0:
                    System.out.println(getGeneCode() + " MUTATION! +1 vision");
                    setVision(getVision() + 1);
                    break;
                case 1:
                    System.out.println(getGeneCode() + " MUTATION! +0.01 efficiency");
                    setEfficiency(Math.round((getEfficiency() * 0.91) * 1000d) / 1000d);
                    break;
                default:
                    System.out.println(getGeneCode() + " Unknown mutation roll!");
                    break;
            }
        }
    }

}
