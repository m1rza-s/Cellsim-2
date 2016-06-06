/*
 *  Project name: CellSIM/cell_max.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 1, 2016, 1:51:43 AM
 */
package edu.lexaron.cells;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import edu.lexaron.world.World;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Cell_first extends Cell {

    public Cell_first(int ID, int x, int y, int energy, int vision, int movement, int efficiency) {
        super(ID, x, y, energy, vision, movement, efficiency);
    }
// Teach cell to lock onto found food's coords, only go for closer!

    @Override
    public int[] lookForFood(World w) {
        // Cell type FIRST is only interested in the FIRST sugar tile it finds.
        System.out.println("Cell " + getID() + " looking for food from " + getX() + "," + getY() + "...");
        int[] sugarLocation = new int[2];
        boolean found = false;
        outerloop:
        for (int i = (this.getX() - this.getVision()); i <= (this.getX() + this.getVision()); i++) {
            for (int j = (this.getY() - this.getVision()); j <= (this.getY() + this.getVision()); j++) {
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
            return sugarLocation;
        } else {
            return null;
        }

    }

    @Override
    public Circle drawCell() {
        Circle cell = new Circle();
        if (this.getEnergy() > 50) {
            cell.setRadius(5);
        } else if (this.getEnergy() > 40) {
            cell.setRadius(4);
        } else {
            cell.setRadius(3);
        }
        if (this.isAlive()) {
            cell.setFill(Color.web("#66ff33"));
        } else {
            cell.setRadius(5);
            cell.setFill(Color.web("#134d00"));
        }
        return cell;
    }
}
