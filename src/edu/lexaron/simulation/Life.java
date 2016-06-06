/*
 *  Project name: CellSIM/Life.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 3, 2016, 1:25:04 AM
 */
package edu.lexaron.simulation;

import edu.lexaron.cells.Cell;
import edu.lexaron.world.World;
import java.util.Set;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Life {    
    
    public void allLiveCellsHunt(World w, Set<Cell> allCells) {
        for (Cell c : allCells) {
            if (c.isAlive()) {
                c.hunt(w);
            }
        }
    }
    
}
