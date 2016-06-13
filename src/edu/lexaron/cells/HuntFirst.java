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
public class HuntFirst extends Cell {

    public HuntFirst(String ID, int x, int y, double energy, int vision, int movement, double efficiency, String color) {
        super(ID, x, y, energy, vision, movement, efficiency, color);
    }

    @Override
    public int[] lookForFood(World w) {
        // Cell type FIRST is only interested in the FIRST sugar tile it finds.
//        System.out.println("Cell " + getGeneCode() + " looking for food from " + getX() + "," + getY() + "...");
        int[] foodLocation = new int[2];
        boolean found = false;
        outerloop:
        for (int i = (getY() - getVision()); i <= (getY() + getVision()); i++) {
            for (int j = (getX() - getVision()); j <= (getX() + getVision()); j++) {
                try {
//                    System.out.print("(" + j + "," + i + ")");
                    if (w.getTheWorld()[i][j].getCell() != this) {
                        if (w.getTheWorld()[i][j].getSugar().getAmount() > 0) {
                            foodLocation[0] = i; // Y
                            foodLocation[1] = j; // X
                            found = true;
                            break outerloop;
                        }
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

    @Override
    public void mutate(World w) {
        if (new Random().nextInt(2) == 0) {
            System.out.println(getGeneCode() + " MITOSIS!");
            int[] childLocation = findFreeTile(w);
            HuntFirst child = new HuntFirst(String.valueOf(getGeneCode() + "." + getOffspring()), childLocation[1], childLocation[0], (getEnergy() / 3), getVision(), getMovementCost(), getEfficiency(), getColor());

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
                    System.out.println(getGeneCode() + " MUTATION! +0.05 efficiency");
                    setEfficiency(Math.round((getEfficiency() * 0.95) * 1000d) / 1000d);
                    break;
                default:
                    System.out.println(getGeneCode() + " Unknown mutation roll!");
                    break;
            }
        }
    }
}
