/*
 *  Project name: CellSIM/Tile.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Feb 5, 2016, 8:55:11 PM
 */
package edu.lexaron.world;

import edu.lexaron.cells.Cell;

/**
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Tile {
  private final int x, y;
  private Cell cell;
  private Cell deadCell;
  private Sugar sugar;
  private Trail trail;

  Tile(int x, int y, Sugar sugar) {
    this.sugar = sugar;
    this.x = x;
    this.y = y;
    this.trail = new Trail(0, null);
  }

  public Sugar getSugar() {
    return sugar;
  }

  public void setSugar(Sugar sugar) {
    this.sugar = sugar;
  }

  public Cell getCell() {
    return cell;
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
    this.trail = trail;
  }
}
