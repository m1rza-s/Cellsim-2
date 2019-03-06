package edu.lexaron.cells;

import edu.lexaron.world.Location;
import edu.lexaron.world.Sugar;
import edu.lexaron.world.World;
import javafx.scene.image.Image;

/**
 * A {@link Herbivorous} {@link Cell}, feeds on the largest {@link Sugar} it can find.
 *
 * Project name: CellSIM/HuntMax.java
 * Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 * Date & time: Jun 12, 2016, 7:37:15 PM
 * Refactored: 24.04.2018
 */
public class HuntMax extends Herbivorous {

  private static final Image GFX = new Image("edu/lexaron/gfx/huntMax.png");

  private HuntMax(String ID, int x, int y) {
    super(ID, x, y);
  }

  /**
   * Creates a new default {@link HuntMax} at a random location in the provided {@link World}.
   *
   * @param world where the {@link HuntMax} is to be created
   */
  public HuntMax(World world) {
    this("L", getRandom().nextInt(world.getWidth()), getRandom().nextInt(world.getHeight()));
  }

  @Override
  public Image getImage() {
    return GFX;
  }

  @Override
  public Breed getBreed() {
    return Breed.HUNT_MAX;
  }

  @Override
  Cell doGiveBirth(int x, int y) {
    return new HuntMax(getGeneCode() + getOffspring(), x, y);
  }

  @Override
  public void lookForFood(World world) {
    double foundSugar = 0.0;
    for (int v = getVision(); v > 0; v--) {
      for (int i = getY() - v; i <= (getY() + v); i++) {
        for (int j = getX() - v; j <= (getX() + v); j++) {
          Location temp = Location.of(j,i);
          if (world.isValidLocation(temp) && world.getNewWorld().get(temp).getSugar().getAmount() > foundSugar) {
            foundSugar = world.getNewWorld().get(temp).getSugar().getAmount();
            setFood(temp);
          }
        }
      }
    }
    findPathTo(getFood());
  }
}
