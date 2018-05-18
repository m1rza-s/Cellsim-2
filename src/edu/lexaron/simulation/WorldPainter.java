package edu.lexaron.simulation;

import edu.lexaron.cells.Cell;
import edu.lexaron.world.World;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

/**
 * This method handles the {@link Canvas} by displaying the current state of the {@link World} and it's {@link Cell}s.
 *
 * Author: Mirza Suljić <mirza.suljic.ba@gmail.com>
 * Date: 22.4.2018.
 */
class WorldPainter {
  private WorldPainter() {
  }

  private static final double GLOBAL_SCALE  = 2.5;

  @SuppressWarnings ({"ImplicitNumericConversion", "MagicNumber"})
  static void paintWorld(World world, Canvas canvas) {
    for (int i = 0; i < world.getHeight(); i++) {
      for (int j = 0; j < world.getWidth(); j++) {
        world.getWorld()[i][j].getTrail().setAmount(world.getWorld()[i][j].getTrail().getAmount() - 1);
        if (world.getWorld()[i][j].getSugar().getAmount() < 0) {
          world.getWorld()[i][j].getSugar().setAmount(0);
        }
        canvas.getGraphicsContext2D().setGlobalAlpha(world.getWorld()[i][j].getSugar().getAmount() / 100);
        canvas.getGraphicsContext2D().setFill(Color.web("#4d9900")); // todo Mirza : consider a GFX for sugar
        canvas.getGraphicsContext2D().fillRect((j - 0.5) * GLOBAL_SCALE, (i - 0.5) * GLOBAL_SCALE, 5, 5);
        if (world.getWorld()[i][j].getTrail().getAmount() > 0) {
          canvas.getGraphicsContext2D().setFill(Color.web(world.getWorld()[i][j].getTrail().getSource().getBreed().getColorCode()));
          canvas.getGraphicsContext2D().setGlobalAlpha(world.getWorld()[i][j].getTrail().getAmount() / 100.0);
          canvas.getGraphicsContext2D().fillRect((j - 0.5) * GLOBAL_SCALE, (i - 0.5) * GLOBAL_SCALE, 5, 5);
        }
      }
    }
    canvas.getGraphicsContext2D().restore();
  }

  @SuppressWarnings ({"MagicNumber", "ImplicitNumericConversion", "NumericCastThatLosesPrecision"})
  static void paintCell(Cell cell, Canvas canvas) {
    if (!cell.isAlive()) {
      canvas.getGraphicsContext2D().setGlobalAlpha(0.2);
      canvas.getGraphicsContext2D().drawImage(cell.getImage(), (cell.getX() - 1.5) * GLOBAL_SCALE, (cell.getY() - 1.5) * GLOBAL_SCALE);
    }
    else if (cell.isAlive()) {
      canvas.getGraphicsContext2D().setGlobalAlpha(cell.getEnergy() / 100.0 + 0.5);
      canvas.getGraphicsContext2D().setFill(Color.web(cell.getBreed().getColorCode()));
      canvas.getGraphicsContext2D().setStroke(Color.web(cell.getBreed().getColorCode()));
//      paintCellFoV(cell, canvas);
      paintTargetLine(cell, canvas);
      canvas.getGraphicsContext2D().drawImage(cell.getImage(), (cell.getX() - 1.5) * GLOBAL_SCALE, (cell.getY() - 1.5) * GLOBAL_SCALE);
      canvas.getGraphicsContext2D().fillText(String.valueOf((int) cell.getEnergy()), (cell.getX() - 3) * GLOBAL_SCALE, (cell.getY() - 1.5) * GLOBAL_SCALE);
    }
    canvas.getGraphicsContext2D().restore();
  }

  @SuppressWarnings ({"ImplicitNumericConversion", "MagicNumber", "unused"})
  static void paintCellFoV(Cell cell, Canvas canvas) {
    canvas.getGraphicsContext2D().fillRect(
        (cell.getX() - cell.getVision() - 0.25) * GLOBAL_SCALE,
        (cell.getY() - cell.getVision() - 0.25) * GLOBAL_SCALE,
        ((cell.getVision() * 2) + 1) * GLOBAL_SCALE,
        ((cell.getVision() * 2) + 1) * GLOBAL_SCALE);
    canvas.getGraphicsContext2D().setFill(Color.web(cell.getBreed().getColorCode()));
    canvas.getGraphicsContext2D().restore();
  }

  @SuppressWarnings ({"ImplicitNumericConversion", "unused"})
  static void paintGrid(World world, Canvas canvas) {
    canvas.getGraphicsContext2D().setStroke(Color.web("#1a1a1a"));
    for (int i = 5; i < world.getWidth() * GLOBAL_SCALE; i += 5) {
      canvas.getGraphicsContext2D().strokeLine(i, 0, i, world.getHeight() * GLOBAL_SCALE);
    }
    for (int i = 5; i < world.getHeight() * GLOBAL_SCALE; i += 5) {
      canvas.getGraphicsContext2D().strokeLine(0, i, world.getWidth() * GLOBAL_SCALE, i);
    }
    canvas.getGraphicsContext2D().restore();
  }

  @SuppressWarnings ({"MagicNumber", "ImplicitNumericConversion"})
  private static void paintTargetLine(Cell cell, Canvas canvas) {
    if (cell.getFood() != null) {
      canvas.getGraphicsContext2D().setStroke(Color.web(cell.getBreed().getColorCode()));
      canvas.getGraphicsContext2D().strokeLine(
          (cell.getX() + 0.25) * GLOBAL_SCALE, (cell.getY() + 0.25) * GLOBAL_SCALE,
          (cell.getFood().getX() + 0.25) * GLOBAL_SCALE, (cell.getFood().getY() + 0.25) * GLOBAL_SCALE
      );
    }
    canvas.getGraphicsContext2D().restore();
  }
}

