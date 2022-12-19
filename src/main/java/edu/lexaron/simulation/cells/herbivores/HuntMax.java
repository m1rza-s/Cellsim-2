package edu.lexaron.simulation.cells.herbivores;

import edu.lexaron.simulation.cells.Breed;
import edu.lexaron.simulation.cells.Cell;
import edu.lexaron.simulation.world.Location;
import edu.lexaron.simulation.world.Sugar;
import edu.lexaron.simulation.world.World;
import javafx.scene.image.Image;

import java.util.Comparator;

/**
 * A {@link Herbivorous} {@link Cell}, feeds on the largest {@link Sugar} it can find.
 *
 * Project name: CellSIM/HuntMax.java
 * Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 * Date & time: Jun 12, 2016, 7:37:15 PM
 * Refactored: 24.04.2018
 */
public class HuntMax extends Herbivorous {

  private static final Image GFX = new Image("gfx/huntMax.png");

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
  protected Cell doGiveBirth(int x, int y) {
    return new HuntMax(getGeneCode() + getOffspring(), x, y);
  }

  @Override
  public void lookForFood(World world) {
    setFood(world.getNewWorld().keySet().stream()
        .unordered()
        .parallel()
        .filter(location -> inVision(world, location) && world.findTile(location).hasSugar())
        .max(Comparator.comparingDouble(o -> world.findTile(o).getSugar().getAmount()))
        .orElse(Location.NIL));

    findPathTo(getFood());
  }
}
