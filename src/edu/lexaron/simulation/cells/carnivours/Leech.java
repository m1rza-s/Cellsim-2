package edu.lexaron.simulation.cells.carnivours;

import edu.lexaron.simulation.cells.Breed;
import edu.lexaron.simulation.cells.Cell;
import edu.lexaron.simulation.world.World;
import javafx.scene.image.Image;

/**
 * {@link Leech} are a kind of {@link Carnivorous} {@link Cell} that follows it's prey
 * and slowly steals the prey's energy.
 *
 * Project name: CellSIM/Leech.java
 * Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 * Date & time: Jun 19, 2016, 8:00:46 PM
 * Refactored: 24.04.2018
 */
@SuppressWarnings ("MagicNumber")
public class Leech extends Carnivorous {
  private static final Image GFX = new Image("edu/lexaron/gfx/leech.png");

  private Leech(String ID, int x, int y) {
    super(ID, x, y, 50.0, 15, 3, 0.1,  2.5);
  }

  /**
   * Creates a new default {@link Leech} at a random location in the provided {@link World}.
   *
   * @param world where the {@link Leech} is to be created
   */
  public Leech(World world) {
    this("L", getRandom().nextInt(world.getWidth()), getRandom().nextInt(world.getHeight()));
  }

  @Override
  public Image getImage() {
    return GFX;
  }

  @Override
  public Breed getBreed() {
    return Breed.LEECH;
  }

  @Override
  protected Cell doGiveBirth(int x, int y) {
    return new Leech(getGeneCode() + getOffspring(), x, y);
  }

  @Override
  public void eat(World world) {
//    if (getFood() != null && world.getWorld()[getFood().getY()][getFood().getX()].hasLiveCell()) {
//      world.getWorld()[getFood().getY()][getFood().getX()].getCell().setEnergy(world.getWorld()[getFood().getY()][getFood().getX()].getCell().getEnergy() - getBiteSize());
//      setEnergy(getEnergy() + getBiteSize());
//      if (world.getWorld()[getFood().getY()][getFood().getX()].getCell().getEnergy() < 0) {
//        world.getWorld()[getFood().getY()][getFood().getX()].getCell().die(world);
//        world.getWorld()[getFood().getY()][getFood().getX()].setDeadCell(world.getWorld()[getFood().getY()][getFood().getX()].getCell());
//        world.getWorld()[getFood().getY()][getFood().getX()].setCell(null);
//      }
//    }
//    else {
//      getPath().clear();
//      resetFoodAndPath();
//    }

    if (getFood() != null && world.getNewWorld().get(getFood()).hasLiveCell()) {
      world.getNewWorld().get(getFood()).getCell().setEnergy(world.getNewWorld().get(getFood()).getCell().getEnergy() - getBiteSize());
      setEnergy(getEnergy() + getBiteSize());
      if (world.getNewWorld().get(getFood()).getCell().getEnergy() < 0) {
        world.getNewWorld().get(getFood()).getCell().die(world);
        world.getNewWorld().get(getFood()).setDeadCell(world.getNewWorld().get(getFood()).getCell());
        world.getNewWorld().get(getFood()).setCell(null);
      }
    }
    else {
      getPath().clear();
      resetFoodAndPath();
    }

  }
}
