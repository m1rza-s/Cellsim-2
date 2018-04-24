package edu.lexaron.world;

/**
 * A point of interest.
 *
 * Author: Mirza <mirza.suljic.ba@gmail.com>
 * Date: 22.4.2018.
 */
public class Location {
  private final int x, y;

  public Location(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }


}