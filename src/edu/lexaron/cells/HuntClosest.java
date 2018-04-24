package edu.lexaron.cells;

import edu.lexaron.world.Sugar;
import edu.lexaron.world.World;
import javafx.scene.image.Image;

/**
 * A {@link Herbivorous} {@link Cell}, feeds on the closest {@link Sugar} it can find.
 *
 * Project name: CellSIM/HuntClosest.java
 * Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 * Date & time: Jun 13, 2016, 4:05:01 PM *
 * Refactored: 24.04.2018
 */
public class HuntClosest extends Herbivorous {
  private static final Image GFX = new Image("edu/lexaron/gfx/huntClosest.png");

  private HuntClosest(String id, int x, int y) {
    super(id, x, y);
  }

  /**
   * Creates a new default {@link HuntClosest} at a random location in the provided {@link World}.
   *
   * @param world where the {@link HuntClosest} is to be created
   */
  public HuntClosest(World world) {
    this("C", getRandom().nextInt(world.getWidth()), getRandom().nextInt(world.getHeight()));
  }

  @Override
  public Image getImage() {
    return GFX;
  }

  @Override
  public Breed getBreed() {
    return Breed.HUNT_CLOSEST;
  }

  @Override
  Cell doGiveBirth(int x, int y) {
    return new HuntClosest(getGeneCode() + getOffspring(), x, y);
  }

  @Override
  public void lookForFood(World w) {
        outterloop:
    for (int v = 0; v <= getVision(); v++) {
      for (int i = (getY() - v); i <= (getY() + v); i++) {
        for (int j = (getX() - v); j <= (getX() + v); j++) {
          if (isValidLocation(w, j, i) && w.getWorld()[i][j].getSugar().getAmount() > 0.0) {
            setFood(j, i);
            findPathTo(getFood());
            break outterloop;
          }
        }
      }
    }
  }
}

