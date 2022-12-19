package edu.lexaron.simulation.world;

import java.util.Objects;

/**
 * A point of interest.
 *
 * Author: Mirza <mirza.suljic.ba@gmail.com>
 * Date: 22.4.2018.
 */
public class Location {
  private final int x, y;

  public static Location NIL = of(-1, -1);

  /**
   * @param x horizontal coordinate
   * @param y vertical coordinate
   * @return a new location based on the provided coordinates
   */
  public static Location of (int x, int y) {
    if (x == -1 && y == -1) return  NIL;
    return new Location(x, y);
  }

  /**
   * A location is an exact place on the {@link World}.
   *
   * @param x horizontal coordinate
   * @param y vertical coordinate
   */
  public Location(int x, int y) {
    this.x = x;
    this.y = y;
  }

  /**
   * @return the horizontal coordinate
   */
  public int getX() {
    return x;
  }

  /**
   * @return the vertical coordinate
   */
  public int getY() {
    return y;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof Location) {
      Location other = (Location) obj;
      return x == other.getX() && y == other.getY();
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @Override
  public String toString() {
    return "[" + x + "," + y + "]";
  }
}