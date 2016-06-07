/*
 *  Project name: CellSIM/cell_max.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 1, 2016, 1:51:43 AM
 */
package edu.lexaron.cells;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import edu.lexaron.world.World;
import java.util.Random;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Cell_first extends Cell {

    int offspring = 1;

    public Cell_first(int ID, int x, int y, double energy, int vision, int movement, double efficiency) {
        super(ID, x, y, energy, vision, movement, efficiency);
    }

    @Override
    public void mutate(World w) {
        if (new Random().nextInt(3) > 0) {
            System.out.println("    MITOSIS!");
            Cell_first child = new Cell_first(getID() + offspring, getX() + 1, getY() + 1, (getEnergy() / 2), getVision(), getMovementCost(), getEfficiency());
            if ((getX() + 1) > w.getWidth() || (getY() + 1) > w.getHeight()) {
                w.getTheWorld()[getX() - 1][getY() - 1].setCell(child);
                w.getMonitor().getAllCells().add(child);
                offspring++;
            } else {
                w.getTheWorld()[getX() + 1][getY() + 1].setCell(child);
                w.getMonitor().getAllCells().add(child);
                offspring++;
            }

        } else {
            switch (new Random().nextInt(2)) {
                case 0:
                    System.out.println("    MUTATION! +1 vision");
                    setVision(getVision() + 1);
                    break;
                case 1:
                    System.out.println("    MUTATION! +0.05 efficiency");
                    setEfficiency(getEfficiency() - 0.05);
                    break;
                default:
                    System.out.println("    Unknown mutation roll!");
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
        for (int i = (getX() - getVision()); i <= (getX() + getVision()); i++) {
            for (int j = (getY() - getVision()); j <= (getY() + getVision()); j++) {
                try {
//                    System.out.print("(" + i + "," + j + ")");
                    if (w.getTheWorld()[i][j].getSugar() > 0) {
                        sugarLocation[0] = i;
                        sugarLocation[1] = j;
                        found = true;
                        break outerloop;
                    }
                } catch (ArrayIndexOutOfBoundsException ex) {

                }
            }
//            System.out.print("\n");
        }
        if (found) {
            System.out.println(getID() + " found food on " + sugarLocation[0] + "," + sugarLocation[1]);
            return sugarLocation;
        } else {
            return null;
        }

    }

    @Override
    public Circle drawCell() {
        Circle cell = new Circle();
        if (getEnergy() > 50) {
            cell.setRadius(5);
        } else if (getEnergy() > 40) {
            cell.setRadius(4);
        } else {
            cell.setRadius(3);
        }
        if (isAlive()) {
            cell.setFill(Color.web("#66ff33"));
        } else {
            cell.setRadius(5);
            cell.setFill(Color.web("#134d00"));
        }
        return cell;
    }
}
