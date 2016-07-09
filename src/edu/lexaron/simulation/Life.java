/*
 *  Project name: CellSIM/Life.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 3, 2016, 1:25:04 AM
 */
package edu.lexaron.simulation;

import edu.lexaron.world.Cell;
import edu.lexaron.world.World;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Life implements Runnable {

    private final World w;

    public Life(World w) {
        this.w = w;
    }

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
        w.getAllCells().stream().map((java.lang.Object c1) -> (Cell) c1).forEach((java.lang.Object c2) -> {
            Cell temp = (Cell) c2;
            if (w.getWorld()[temp.getY()][temp.getX()].getCell() != null && !temp.isAlive()) {
                w.getWorld()[temp.getY()][temp.getX()].setCell(null);
                w.getWorld()[temp.getY()][temp.getX()].setDeadCell(temp);
            }
//                        paintCells((Cell) temp);
        });

    }

    @Override
    public void run() {
        synchronized (w) {
            allLiveCellsHunt(w);
        }
//        while (true) {
//            synchronized (w) {
//                allLiveCellsHunt(w);
//            }
//            try {
//                sleep(50);
//            } catch (InterruptedException ex) {
//                Logger.getLogger(Life.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    }

}
