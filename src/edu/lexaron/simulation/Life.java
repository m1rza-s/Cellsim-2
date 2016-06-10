/*
 *  Project name: CellSIM/Life.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 3, 2016, 1:25:04 AM
 */
package edu.lexaron.simulation;

import edu.lexaron.world.Cell;
import edu.lexaron.world.World;
import java.util.List;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Life {    
    
    public void allLiveCellsHunt(World w, List<Cell> allCells) {
//        for (Cell c : allCells) {
//            if (c.isAlive()) {
//                c.hunt(w);
//            }
//        }
        for (int i = 0; i < w.getWidth(); i++) {
            for (int j = 0; j < w.getHeight(); j++) {
                if (w.getTheWorld()[j][i].getCell() != null && w.getTheWorld()[j][i].getCell().isAlive()) {
                    w.getTheWorld()[j][i].getCell().hunt(w);
                }
            }
        }
            
    }
    
}
