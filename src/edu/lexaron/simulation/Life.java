/*
 *  Project name: CellSIM/Life.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 3, 2016, 1:25:04 AM
 */
package edu.lexaron.simulation;

import edu.lexaron.world.World;

/**
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Life implements Runnable {

  private final World world;

  Life(World world) {
    this.world = world;
  }

  /**
   */
  private void allLiveCellsHunt() {
    world.getNewBornCells().forEach(cell -> world.getWorld()[cell.getY()][cell.getX()].setCell(cell));
    world.getAllCells().addAll(world.getNewBornCells());
    world.getNewBornCells().clear();

    world.getEatenCorpses().forEach(cell -> world.getWorld()[cell.getY()][cell.getX()].setDeadCell(null));
    world.getAllCells().removeAll(world.getEatenCorpses());
    world.getEatenCorpses().clear();

    world.getAllCells().forEach(cell -> cell.hunt(world));

//    world.getAllCells().forEach(cell -> {
//      if (world.getWorld()[cell.getY()][cell.getX()].getCell() != null && !cell.isAlive()) {
//        world.getWorld()[cell.getY()][cell.getX()].setCell(null);
//        world.getWorld()[cell.getY()][cell.getX()].setDeadCell(cell);
//      }
//    });

  }

  @Override
  public void run() {
    synchronized (world) {
      allLiveCellsHunt();
    }
  }

}
