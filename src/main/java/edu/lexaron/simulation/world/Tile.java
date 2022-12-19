/*
 *  Project name: CellSIM/Tile.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Feb 5, 2016, 8:55:11 PM
 */
package edu.lexaron.simulation.world;

import edu.lexaron.simulation.cells.Cell;

/**
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Tile {
  private final Location location;
  private Cell cell;
  private Cell deadCell;
  private Sugar sugar;
  private Trail trail;

  Tile(Location location, Sugar sugar) {
    this.sugar = sugar;
    this.location = location;
    this.trail = new Trail(0, null);
  }

  public Location getLocation() {
    return location;
  }

  public Sugar getSugar() {
    return sugar;
  }

  public boolean hasSugar() {
    return sugar != null && sugar.getAmount() > 0.0;
  }

  public void setSugar(Sugar sugar) {
    this.sugar = sugar;
  }

  public Cell getCell() {
    return cell;
  }

  public boolean hasLiveCell() {
    return cell != null && cell.isAlive();
  }

  public void setCell(Cell cell) {
    this.cell = cell;
  }

  public Cell getDeadCell() {
    return deadCell;
  }

  public void setDeadCell(Cell deadCell) {
    this.deadCell = deadCell;
  }

  public Trail getTrail() {
    return trail;
  }

  public void setTrail(Trail trail) {
//    this.trail = trail;
  }
}
