/*
 *  Project name: CellSIM/cell_max.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 1, 2016, 1:51:43 AM
 */
package edu.lexaron.cells;

import edu.lexaron.world.Cell;
import edu.lexaron.world.World;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class HuntFirst extends Cell {

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
    public HuntFirst(String ID, int x, int y, double energy, int vision, double speed, double efficiency, String color, double biteSize) {
        super(ID, x, y, energy, vision, speed, efficiency, color, biteSize);
    }

    /**
     *
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

                } catch (ArrayIndexOutOfBoundsException ex) {

                }
            }
//            System.out.print("\n");
        }
        if (found) {
//            System.out.println(getGeneCode() + " found food on " + sugarLocation[0] + "," + sugarLocation[1]);
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
        HuntFirst child = new HuntFirst(String.valueOf(getGeneCode() + "." + getOffspring()), childLocation[1], childLocation[0], (getEnergy() / 3), getVision(), getSpeed(), getEfficiency(), getColor(), getBiteSize());
        try {
            child.eat(w);
            child.evolve();
            w.getNewBornCells().add(child);
            setOffspring(getOffspring() + 1);
            setEnergy(getEnergy() / 3);
        } catch (Exception ex) {
            System.out.println(getGeneCode() + " failed to divide:\n" + ex);
        }
    }
}
