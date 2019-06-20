package edu.lexaron.simulation.cells;

/**
 * Author: Mirza <mirza.suljic.ba@gmail.com>
 * Date: 22.4.2018.
 */
@SuppressWarnings ("JavaDoc")
public enum Direction {
  LEFT(-1, 0),
  UP(0, -1),
  RIGHT(1, 0),
  DOWN(0, 1),

  UP_LEFT(-1, -1),
  UP_RIGHT(1, -1),
  DOWN_RIGHT(1, 1),
  DOWN_LEFT(-1, 1);

  int x, y;

  Direction(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /**
   * @return the difference between a {@link Cell}'s horizontal coordinate and it's destination
   */
  public int getDeltaX() {
    return x;
  }

  /**
   * @return the difference between a {@link Cell}'s vertical coordinate and it's destination
   */
  public int getDeltaY() {
    return y;
  }
}
