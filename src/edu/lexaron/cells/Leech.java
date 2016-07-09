/*
 *  Project name: CellSIM/Leech.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 19, 2016, 8:00:46 PM
 */
package edu.lexaron.cells;

import edu.lexaron.world.Cell;
import edu.lexaron.world.World;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Leech extends Cell {

    public Leech(String ID, int x, int y, double energy, int vision, double speed, double efficiency, String color, double biteSize) {
        super(ID, x, y, energy, vision, speed, efficiency, color, biteSize);
    }

    @Override
    public void hunt(World w) {
        // CELL TYPE DEPENDANT
        if (this.isAlive()) {
            upkeep(w);
            if (getPath().isEmpty()) {
                setTargetFood(lookForFood(w));
                if (getTargetFood() != null) {
                    findPathTo(getTargetFood());
                    usePath(w);
                } else {
                    moveLeft(w);
                    randomStep(w);
                }
            }
            if (!getPath().isEmpty() && getTargetFood() != null
                    && (w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getCell() != null
                    || w.getWorld()[getTargetFood()[0]][getTargetFood()[1]].getTrail().getAmount() >= 11)) {
                if (getTargetFood() != null && (getTargetFood()[0] >= (getY() - getVision())) && (getTargetFood()[0] <= (getY() + getVision()))
                        && (getTargetFood()[1] >= (getX() - getVision())) && (getTargetFood()[1] <= (getX() + getVision()))) {
                    eat(w);
                }
            } else {
                getPath().clear();
            }

            if (getEnergy() >= 100) {
                mutate(w);
                setEnergy(getEnergy() / 3);
            }
            if (getOffspring() >= 5) {
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
        } else {
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
                        if (w.getWorld()[i][j].getCell() != null && w.getWorld()[i][j].getCell() != this && !w.getWorld()[i][j].getCell().getClass().getSimpleName().equals(this.getClass().getSimpleName())) {                        
                                foodLocation[0] = i; // Y
                                foodLocation[1] = j; // X
                                found = true;
                                break outterloop;                        
//                        } else if (getTargetFood() == null && w.getWorld()[i][j].getCell() != null && w.getWorld()[i][j].getCell().isAlive() && w.getWorld()[i][j].getCell().getClass().equals(this.getClass()) && w.getWorld()[i][j].getCell().getTargetFood() != null                                ) {
//                            setTargetFood(w.getWorld()[i][j].getCell().getTargetFood());
////                            System.out.println("GOT COORDS: " + getTargetFood()[1] + "," + getTargetFood()[0]);
//                            found = true;
//                            break outterloop;
                        } else if (w.getWorld()[i][j].getTrail().getSource() != null && !w.getWorld()[i][j].getTrail().getSource().getClass().getSimpleName().equalsIgnoreCase(this.getClass().getSimpleName())
                                //                                && !w.getWorld()[i][j].getTrail().getSource().equalsIgnoreCase("vulture")                                
                                && w.getWorld()[i][j].getTrail().getAmount() > foundSmell) {
                            foundSmell = w.getWorld()[i][j].getTrail().getAmount();
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
        int[] childLocation = findFreeTile(w);
        Leech child = new Leech(String.valueOf(getGeneCode() + "." + getOffspring()), childLocation[1], childLocation[0], (getEnergy() / 3), getVision(), getSpeed(), getEfficiency(), getColor(), getBiteSize());
        try {
            child.evolve();
            w.getNewBornCells().add(child);
            setOffspring(getOffspring() + 1);
            setEnergy(getEnergy() / 3);
        } catch (Exception ex) {
            System.out.println(getGeneCode() + " failed to divide:\n" + ex);
        }
    }

}
