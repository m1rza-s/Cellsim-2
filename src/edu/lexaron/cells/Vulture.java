/*
 *  Project name: CellSIM/Vulture.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 14, 2016, 1:21:12 AM
 */
package edu.lexaron.cells;

import edu.lexaron.world.Cell;
import edu.lexaron.world.World;
import java.util.Random;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Vulture extends Cell {

    public Vulture(String ID, int x, int y, double energy, int vision, double speed, double efficiency, String color) {
        super(ID, x, y, energy, vision, speed, efficiency, color);
    }

    /**
     *
     * @param w
     */
    @Override
    public void hunt(World w) {
        // CELL TYPE DEPENDANT
        if (this.isAlive()) {
            if (getPath().isEmpty()) {
                setTargetFood(lookForFood(w));
                if (getTargetFood() != null) {
                    findPathTo(getTargetFood());
                } else {
//                    this.setEnergy(getEnergy() - 0.10);
randomStep(w);
                    moveRight(w);
                }
            }
            if (getTargetFood() != null && w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getDeadCell() != null) {
                usePath(w);
                if (getPath().isEmpty()) {
                    eat(w);
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

    /**
     *
     * @param w
     */
    @Override
    public void eat(World w) {
        if (w.getWorld()[getY()][getX()].getDeadCell() != null) {
            setEnergy(getEnergy() + (new Random().nextInt(11) + 10) + w.getWorld()[getY()][getX()].getDeadCell().getEnergy());
            if (w.getWorld()[getY()][getX()].getDeadCell() != null) {
                w.getEatenCorpses().add(w.getWorld()[getY()][getX()].getDeadCell());
                w.getWorld()[getY()][getX()].setDeadCell(null);
            }
//            else if (w.getWorld()[getY()][getX()].getCell() != null) {
//                w.getEatenCorpses().add(w.getWorld()[getY()][getX()].getCell());
////                w.getWorld()[getY()][getX()].setCell(null);
//            }

            setTargetFood(null);
            getPath().clear();
//            System.out.println(geneCode + "   ate on " + x + "," + y + ": energy +" + w.getWorld()[y][x].getSugar().getAmount());
        }

//        outterloop:
//        for (int i = (getY() - 1); i <= (getY() + 1); i++) {
//            for (int j = (getX() - 1); j <= (getX() + 1); j++) {
//                try {
//                    if (w.getWorld()[i][j].getDeadCell() != null) {
//                        setEnergy(getEnergy() + 15);
//                        w.getEatenCorpses().add(w.getWorld()[i][j].getDeadCell());
////                        w.getWorld()[i][j].setDeadCell(null);
////                        w.getWorld()[i][j].setCell(null);
//
//                        break outterloop;
//                    }
//                } catch (ArrayIndexOutOfBoundsException ex) {
//
//                }
//            }
//        }
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
//                        else if (w.getWorld()[i][j].getCell() != null
//                                && w.getWorld()[i][j].getCell().getClass().getSimpleName().equalsIgnoreCase("predator")) {
//                            
//                            foodLocation[0] = i; // Y
//                            foodLocation[1] = j; // X
//                            found = true;
//                            break outterloop;
//                        }

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
//            System.out.println(getGeneCode() + " MITOSIS!");
            int[] childLocation = findFreeTile(w);
            Vulture child = new Vulture(String.valueOf(getGeneCode() + "." + getOffspring()), childLocation[1], childLocation[0], (getEnergy() / 3), getVision(), getSpeed(), getEfficiency(), getColor());
            try {
                w.getNewBornCells().add(child);
                setOffspring(getOffspring() + 1);
            } catch (Exception ex) {
                System.out.println(getGeneCode() + " failed to divide.");
            }

        } else {
            evolve();
        }
    }

}
