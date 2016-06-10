/*
 *  Project name: CellSIM/cell_max.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 1, 2016, 1:51:43 AM
 */
package edu.lexaron.cells;

import edu.lexaron.world.Cell;
import edu.lexaron.world.World;
import java.util.Random;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Cell_first extends Cell {

    private int offspring = 1;

    public Cell_first(int ID, int x, int y, double energy, int vision, int movement, double efficiency) {
        super(ID, x, y, energy, vision, movement, efficiency);
    }

    @Override
    public void mutate(World w) {
        if (new Random().nextInt(3) > 1) {
            System.out.println(getID() + " MITOSIS!");
            Cell_first child = new Cell_first(getID() + offspring, getX() + 1, getY() + 1, (getEnergy() / 2), getVision(), getMovementCost(), getEfficiency());
            
            try {
                w.getTheWorld()[getY() - 1][getX() - 1].setCell(child);
                w.getAllCells().add(child);
                offspring++;
            } catch (Exception ex) {
                System.out.println(getID() + " failed to divide.");
            }

        } else {
            switch (new Random().nextInt(2)) {
                case 0:
                    System.out.println(getID() + " MUTATION! +1 vision");
                    setVision(getVision() + 1);
                    break;
                case 1:
                    System.out.println(getID() + " MUTATION! +0.05 efficiency");
                    setEfficiency(Math.round((getEfficiency() * 0.95) * 1000d) / 1000d);
                    break;
                default:
                    System.out.println(getID() + " Unknown mutation roll!");
                    break;
            }
        }
    }

    @Override
    public int[] lookForFood(World w) {
        // Cell type FIRST is only interested in the FIRST sugar tile it finds.
//        System.out.println("Cell " + getID() + " looking for food from " + getX() + "," + getY() + "...");
        int[] sugarLocation = new int[2];
        boolean found = false;
        outerloop:
        for (int i = (getY() - getVision()); i <= (getY() + getVision()); i++) {
            for (int j = (getX() - getVision()); j <= (getX() + getVision()); j++) {
                try {
//                    System.out.print("(" + j + "," + i + ")");
                    if (w.getTheWorld()[i][j].getSugar().getAmount() > 0) {
                        sugarLocation[0] = i; // Y
                        sugarLocation[1] = j; // X
                        found = true;
                        break outerloop;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {
                    
                }
            }
//            System.out.print("\n");
        }
        if (found) {
//            System.out.println(getID() + " found food on " + sugarLocation[0] + "," + sugarLocation[1]);
            return sugarLocation;
        } else {
//            System.out.println(getID() + " found no food.");
            return null;
        }

    }
}
