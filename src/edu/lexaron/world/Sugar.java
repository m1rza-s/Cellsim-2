/*
 *  Project name: CellSIM/Sugar.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 10, 2016, 3:15:02 PM
 */
package edu.lexaron.world;

/**
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Sugar {

  private final int x;
  private final int y;
  private double amount;

  /**
   * @param x
   * @param y
   * @param amount
   */
  public Sugar(int x, int y, int amount) {
    this.amount = amount;
    this.x = x;
    this.y = y;
  }

  /**
   * @return
   */
  public double getAmount() {
    return amount;
  }

  /**
   * @param amount
   */
  public void setAmount(double amount) {
    this.amount = amount;
  }

  /**
   * @return
   */
  public int getX() {
    return x;
  }

  /**
   * @return
   */
  public int getY() {
    return y;
  }

}
