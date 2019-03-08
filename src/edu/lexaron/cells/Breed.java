package edu.lexaron.cells;

/**
 * Author: Mirza <mirza.suljic.ba@gmail.com>
 * Date: 19.4.2018. @ 00:49
 */
public enum Breed {
  HUNT_CLOSEST("#ff33ff"),
  HUNT_ANY("#66ff33"),
  HUNT_MAX("#ffff33"),
  LEECH("#0000ff"),
  SPIDER("#ff0000"),
  TREE("#ffffff"),
  VULTURE("#33ffff");

  private final String colorCode;

  Breed(String colorCode) {
    this.colorCode = colorCode;
  }

  /**
   * @return a HEX value of this breeds color
   */
  public String getColorCode() {
    return colorCode;
  }
}
