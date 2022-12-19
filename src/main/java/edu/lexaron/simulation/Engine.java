package edu.lexaron.simulation;

import edu.lexaron.simulation.cells.Cell;
import edu.lexaron.simulation.cells.carnivours.Leech;
import edu.lexaron.simulation.cells.carnivours.Spider;
import edu.lexaron.simulation.cells.carnivours.Vulture;
import edu.lexaron.simulation.cells.herbivores.HuntAny;
import edu.lexaron.simulation.cells.herbivores.HuntClosest;
import edu.lexaron.simulation.cells.herbivores.HuntMax;
import edu.lexaron.simulation.cells.herbivores.Tree;
import edu.lexaron.simulation.world.World;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.security.SecureRandom;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static edu.lexaron.simulation.WorldPainter.paintCell;
import static edu.lexaron.simulation.WorldPainter.paintCellVision;

/**
 * This class analyzes the {@link World} and it's {@link Cell}s. Apart from that, it starts a thread which is
 * the backbone of this entire project.
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 * Date & time: Feb 6, 2016, 3:41:07 AM
 *
 * Refactored: 22.04.2018
 */
@SuppressWarnings ("unused")
public class Engine {
  private static final int    HEIGHT = 400;
  private static final int    WIDTH  = 600;
  private static final Random RANDOM = new SecureRandom();

  private final double  sugarFactor = Math.random();
  static final World WORLD = new World(WIDTH, HEIGHT);
  private final Life    life        = new Life(WORLD);
  private final VBox    infoPanel;
  private final Label   gens_L, liveCells_L, deadCells_L, cells_L, sugar_L;

  private int generations = 0;
  private int totalSugar  = 0;

  Engine(VBox infoPanel, Label totalGenerations, Label liveCells, Label deadCells, Label totalCells, Label totalSugar) {
    this.infoPanel = infoPanel;
    gens_L = totalGenerations;
    liveCells_L = liveCells;
    deadCells_L = deadCells;
    cells_L = totalCells;
    sugar_L = totalSugar;
  }

  /**
   * @return the {@link SecureRandom} for number generation.
   */
  public static Random getRandom() {
    return RANDOM;
  }

  /**
   * @return the width of the cellular {@link World}
   */
  public int getWidth() {
    return WIDTH;
  }

  /**
   * @return the height of the cellular {@link World}
   */
  public int getHeight() {
    return HEIGHT;
  }

  @SuppressWarnings ("MagicNumber")
  void startThread(Canvas canvas, Timer timer) {
    canvas.setCache(true);
    canvas.setCacheHint(CacheHint.SPEED);

    infoPanel.setPadding(new Insets(10.0));
    infoPanel.setSpacing(5.0);
    infoPanel.setMinWidth(200.0);
    infoPanel.setMaxWidth(250.0);
    infoPanel.setMaxHeight(1000.0);
    infoPanel.getStyleClass().add("accentText");
    infoPanel.setAlignment(Pos.TOP_CENTER);
    infoPanel.setCache(true);
    infoPanel.setCacheHint(CacheHint.SPEED);

    reseedCells(WORLD);
    Runnable lifeThread = new Thread(life);
//    paintWholeWorld(world, canvas); // todo Mirza S. : somehow refresh the canvas without deleting everything, if possible
    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        Platform.runLater(() -> {
          synchronized (WORLD) {
            lifeThread.run();
          }
          canvas.getGraphicsContext2D().clearRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
          paintCellVision(WORLD, canvas);
          WORLD.getAllCells().forEach(cell -> paintCell(cell, canvas));
          if (WORLD.getAllCells().parallelStream().unordered().anyMatch(Cell::isAlive)) {
            Monitor.refreshCellInformation(infoPanel);
          }
          else {
            infoPanel.getChildren().clear();
            Label done = new Label("No live cells!");
            done.getStyleClass().addAll("accentText", "biggerText");
            infoPanel.getChildren().add(done);
            timer.cancel();
            timer.purge();
          }
          generations++;
          gens_L     .setText(generations + " generations");
          cells_L    .setText("Total: " + WORLD.getAllCells().size());
          liveCells_L.setText("Alive: " + WORLD.getAllCells().parallelStream().unordered().filter(Cell::isAlive).count());
          deadCells_L.setText("Dead: "  + WORLD.getAllCells().parallelStream().unordered().filter(cell -> !cell.isAlive()).count());
          sugar_L    .setText("Sugar: " + WORLD.getTotalSugar());
          totalSugar = 0;
        });
      }
    };
    timer.schedule(timerTask, 200L, 200L);
  }

  double getSugarFactor() {
    return sugarFactor;
  }

  void generateWorld(boolean cellsToo, Canvas canvas) {
    WORLD.generateWorld();
    canvas.getGraphicsContext2D().clearRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
    if (cellsToo) {
      reseedCells(WORLD);
    }
  }

  private static void reseedCells(World world) {
    world.getNewBornCells().add(new HuntClosest(world));
    world.getNewBornCells().add(new HuntAny(world));
    world.getNewBornCells().add(new HuntMax(world));
    world.getNewBornCells().add(new Leech(world));
    world.getNewBornCells().add(new Spider(world));
    world.getNewBornCells().add(new Tree(world));
    world.getNewBornCells().add(new Vulture(world));
  }

}
