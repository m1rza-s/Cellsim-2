/*
 *  Project name: CellSIM/HuntLargest.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 12, 2016, 7:37:15 PM
 */
package edu.lexaron.cells;

import edu.lexaron.world.Cell;
import edu.lexaron.world.World;
import java.util.Random;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class HuntLargest extends Cell {

    /**
     *
     * @param ID
     * @param x
     * @param y
     * @param energy
     * @param vision
     * @param speed
     * @param efficiency
     * @param color
     */
    public HuntLargest(String ID, int x, int y, double energy, int vision, double speed, double efficiency, String color) {
        super(ID, x, y, energy, vision, speed, efficiency, color);
    }

    /**
     *
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

        for (int v = getVision(); v > 0; v--) {
            for (int i = (getY() - v); i <= (getY() + v); i++) {
                for (int j = (getX() - v); j <= (getX() + v); j++) {
                    try {
//                    System.out.print("(" + j + "," + i + ")");   
                        if (w.getWorld()[i][j].getCell() != this) {
                            if (w.getWorld()[i][j].getSugar().getAmount() > foundSugar) {
                                foundSugar = w.getWorld()[i][j].getSugar().getAmount();
                                foodLocation[0] = i; // Y
                                foodLocation[1] = j; // X
                                found = true;
                            }
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {

                    }
                }
            }
//            System.out.print("\n");
        }
        if (found) {
//            System.out.println(getGeneCode() + " found food on " + foodLocation[0] + "," + foodLocation[1]);
            return foodLocation;
        } else {
//            System.out.println(getGeneCode() + " found no food.");
            return null;
        }

    }

    /**
     *
     * @param w
     */
    @Override
    public void mutate(World w) {
        int[] childLocation = findFreeTile(w);
        HuntLargest child = new HuntLargest(String.valueOf(getGeneCode() + "." + getOffspring()), childLocation[1], childLocation[0], (getEnergy() / 3), getVision(), getSpeed(), getEfficiency(), getColor());
        try {
            child.eat(w);
            child.evolve();
            w.getNewBornCells().add(child);
            setOffspring(getOffspring() + 1);
            setEnergy(getEnergy() / 3);
        } catch (Exception ex) {
            System.out.println(getGeneCode() + " failed to divide.");
        }
    }
}
