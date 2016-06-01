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

    @Override
    public void hunt(World w) {
        int[] food = lookForFood(w);
        if (food != null) {
            System.out.println("Found sugar! " + food[0] + "," + food[1]);
        }
    }

    private int[] lookForFood(World w) {
        int[] sugarLocation = new int[2];
        for (int i = (this.getX() - this.getVision()); i <= (this.getX() + this.getVision()); i++) {
            for (int j = (this.getY() - this.getVision()); j <= (this.getY() + this.getVision()); j++) {
                if (w.getTheWorld()[i][j].getSugar() > 0) {
                    sugarLocation[0] = i;
                    sugarLocation[1] = j;
                    return sugarLocation;
                }
            }
        }
        
        return null;
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
