package edu.lexaron.cells;

import edu.lexaron.world.Trail;
import edu.lexaron.world.World;
import javafx.scene.image.Image;

/**
 * {@link Spider} are {@link Carnivorous} {@link Cell}s hunt other non-{@link Spider} cells. They can also utilize {@link Trail}s
 *  left by other cells to track them down.
 *
 * Project name: CellSIM/Spider.java
 * Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 * Date & time: Jun 13, 2016, 5:53:57 PM
 * Refactored: 24.04.2018
 */
@SuppressWarnings ("MagicNumber")
public class Spider extends Carnivorous {
  private static final Image GFX = new Image("edu/lexaron/gfx/predator.png");

  private Spider(String id, int x, int y) {
    super(id, x, y, 50.0, 5, 1, 0.33, 1);
  }

  /**
   * Creates a new default {@link Spider} at a random location in the provided {@link World}.
   *
   * @param world where the {@link Spider} is to be created
   */
  public Spider(World world) {
    this("S", getRandom().nextInt(world.getWidth()), getRandom().nextInt(world.getHeight()));
  }

  @Override
  public Image getImage() {
    return GFX;
  }

  @Override
  public Breed getBreed() {
    return Breed.SPIDER;
  }

  @Override
  Cell doGiveBirth(int x, int y) {
    return new Spider(getGeneCode() + getOffspring(), x, y);
  }

  @Override
  public void eat(World world) {
        loop:
    for (int y = getY() - 1; y <= (getY() + 1); y++) {
      for (int x = getX() - 1; x <= (getX() + 1); x++) {
        if (isValidLocation(world, x, y)) {
          Cell prey = world.getWorld()[y][x].getCell();
          if (prey != null && !prey.equals(this) && prey.getBreed() != getBreed()) {
            prey.setEnergy(prey.getEnergy() / 2.0);
            setEnergy(getEnergy() + (prey.getEnergy() / 2.0));
            prey.die(world);
            break loop;
          }
        }
      }
    }
  }

}
