package edu.lexaron.cells;

import edu.lexaron.world.Tile;
import edu.lexaron.world.World;
import javafx.scene.image.Image;

/**
 * {@link Vulture}s are {@link Carnivorous} {@link Cell}s that feed on corpses.
 *
 * Project name: CellSIM/Vulture.java
 * Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 * Date & time: Jun 14, 2016, 1:21:12 AM
 * Refactored: 24.04.2018
 */
@SuppressWarnings ("MagicNumber")
public class Vulture extends Carnivorous {
  private static final Image GFX = new Image("edu/lexaron/gfx/vulture.png");

  private Vulture(String id, int x, int y) {
    super(id, x, y, 50.0, 10, 1.0, 0.5,  1.0);
  }

  /**
   * Creates a new default {@link Vulture} at a random location in the provided {@link World}.
   *
   * @param world where the {@link Vulture} is to be created
   */
  public Vulture(World world) {
    this("V", getRandom().nextInt(world.getWidth()), getRandom().nextInt(world.getHeight()));
  }

  @Override
  public Image getImage() {
    return GFX;
  }

  @Override
  public Breed getBreed() {
    return Breed.VULTURE;
  }

  @Override
  Cell doGiveBirth(int x, int y) {
    return new Vulture(getGeneCode() + getOffspring(), x, y);
  }

  @SuppressWarnings ("MethodDoesntCallSuperMethod")
  @Override
  public void lookForFood(World w) {
    resetFoodAndPath();
        loop:
    for (int v = 1; v <= getVision(); v++) {
      for (int i = getY() - v; i <= (getY() + v); i++) {
        for (int j = getX() - v; j <= (getX() + v); j++) {
          if (isValidLocation(w, j, i)) {
            Cell prey = w.getWorld()[i][j].getDeadCell();
            if (prey != null) {
              setFood(prey.getX(), prey.getY());
              break loop;
            }
          }
        }
      }
    }
    findPathTo(getFood());
  }

  @SuppressWarnings ("MethodDoesntCallSuperMethod")
  @Override
  public void eat(World world) {
        loop:
    for (int y = getY() - 1; y <= (getY() + 1); y++) {
      for (int x = getX() - 1; x <= (getX() + 1); x++) {
        if (isValidLocation(world, x, y)) {
          Tile preyLocation = world.getWorld()[y][x];
          Cell prey         = preyLocation.getDeadCell();
          if (prey != null) {
            setEnergy(getEnergy() + prey.getEnergy() + 10.0);
            preyLocation.getSugar().setAmount(10.0); // todo Mirza : randomize?
            preyLocation.setDeadCell(null);
            world.getEatenCorpses().add(prey);
            break loop;
          }
        }
      }
    }
  }
}
