/*
 *  Project name: CellSIM/Life.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 3, 2016, 1:25:04 AM
 */
package edu.lexaron.simulation;

import edu.lexaron.world.Cell;
import edu.lexaron.world.World;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Life {

    /**
     *
     * @param w
     */
    public void allLiveCellsHunt(World w) {
        for (Object o : w.getNewBornCells()) {
            Cell c = (Cell) o;
//            System.out.println("NEW CELL: " + c.getGeneCode() + " @ " + c.getX() + "," + c.getY());
            w.getWorld()[c.getY()][c.getX()].setCell(c);
        }
        w.getAllCells().addAll(w.getNewBornCells());
        w.getNewBornCells().clear();

        for (Object o : w.getEatenCorpses()) {
            Cell c = (Cell) o;
            w.getWorld()[c.getY()][c.getX()].setDeadCell(null);
        }
        w.getAllCells().removeAll(w.getEatenCorpses());
        w.getEatenCorpses().clear();

        for (Object o : w.getAllCells()) {
            Cell c = (Cell) o;
            c.hunt(w);
        }
    }

}
