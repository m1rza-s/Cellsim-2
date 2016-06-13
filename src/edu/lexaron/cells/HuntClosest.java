/*
 *  Project name: CellSIM/HuntClosest.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 13, 2016, 4:05:01 PM
 */
package edu.lexaron.cells;

import edu.lexaron.world.Cell;
import edu.lexaron.world.World;
import java.util.Random;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class HuntClosest extends Cell {

    public HuntClosest(String ID, int x, int y, double energy, int vision, int movementCost, double efficiency, String color) {
        super(ID, x, y, energy, vision, movementCost, efficiency, color);
    }

    @Override
    public void mutate(World w) {
        if (new Random().nextInt(2) == 0) {
            System.out.println(getGeneCode() + " MITOSIS!");
            int[] childLocation = findFreeTile(w);
            HuntClosest child = new HuntClosest(String.valueOf(getGeneCode() + "." + getOffspring()), childLocation[1], childLocation[0], (getEnergy() / 3), getVision(), getMovementCost(), getEfficiency(), getColor());
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

    @Override
    public int[] lookForFood(World w) {
        // Cell type CLOSEST is only interested in the CLOSEST sugar tile it finds.
//        System.out.println("Cell " + getGeneCode() + " looking for food from " + getX() + "," + getY() + "...");
        int[] foodLocation = new int[2];
        boolean found = false;

        outterloop:
        for (int v = 1; v <= getVision(); v++) {
            for (int i = (getY() - v); i <= (getY() + v); i++) {
                for (int j = (getX() - v); j <= (getX() + v); j++) {
                    try {
//                    System.out.print("(" + j + "," + i + ")");        
                        if (w.getTheWorld()[i][j].getCell() != this) {
                            if (w.getTheWorld()[i][j].getSugar().getAmount() > 0) {
                                foodLocation[0] = i; // Y
                                foodLocation[1] = j; // X
                                found = true;
                                break outterloop;
                            }
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

}
