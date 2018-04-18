/*
 *  Project name: CellSIM/Trail.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 13, 2016, 11:22:12 PM
 */
package edu.lexaron.world;

import edu.lexaron.cells.Cell;

/**
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Trail {

  private int amount;
  private Cell source;

  public Trail(int amount, Cell source) {
    this.amount = amount;
    this.source = source;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public Cell getSource() {
    return source;
  }

  public void setSource(Cell source) {
    this.source = source;
  }

}
