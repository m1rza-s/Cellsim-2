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

    public void allLiveCellsHunt(World w) {
        for (Object o : w.getAllCells()) {
            Cell c = (Cell) o;
            c.hunt(w);
        }
        w.getAllCells().addAll(w.getNewBornCells());
        w.getNewBornCells().clear();

    }

}
