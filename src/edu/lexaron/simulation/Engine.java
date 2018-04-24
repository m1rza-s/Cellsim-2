package edu.lexaron.simulation;

import edu.lexaron.cells.*;
import edu.lexaron.world.World;
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
import static edu.lexaron.simulation.WorldPainter.paintWorld;

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
  private static final int    HEIGHT = 200;
  private static final int    WIDTH  = 330;
  private static final Random RANDOM = new SecureRandom();

  private final double  sugarFactor = (double) RANDOM.nextInt(100);
  private final World   world       = new World(WIDTH, HEIGHT);
  private final Life    life        = new Life(world);
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

    reseedCells(world);
    Runnable lifeThread = new Thread(life);

    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        Platform.runLater(() -> {
          synchronized (world) {
            lifeThread.run();
          }
          canvas.getGraphicsContext2D().clearRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
//          paintGrid(world, canvas);
          paintWorld(world, canvas);
          world.getAllCells().forEach(cell -> paintCell(cell, canvas));
          if (world.getAllCells().stream().anyMatch(Cell::isAlive)) {
            Monitor.refreshCellInformation(world, infoPanel);
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
          cells_L    .setText("Total: " + world.getAllCells().size());
          liveCells_L.setText("Alive: " + world.getAllCells().stream().filter(Cell::isAlive).count());
          deadCells_L.setText("Dead: "  + world.getAllCells().stream().filter(cell -> !cell.isAlive()).count());
          sugar_L    .setText("Sugar: " + world.getTotalSugar());
          totalSugar = 0;
        });
      }
    };
    timer.schedule(timerTask, 200L, 75L);
  }

  /**
   * @return the cellular {@link World} where all cells and food sources exist
   */
  public World getWorld() {
    return world;
  }

  double getSugarFactor() {
    return sugarFactor;
  }

  void generateWorld(boolean cellsToo, Canvas canvas) {
    world.generateWorld(sugarFactor);
    canvas.getGraphicsContext2D().clearRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());
    if (cellsToo) {
      reseedCells(world);
    }
  }

  private static void reseedCells(World world) {
    world.getNewBornCells().add(new HuntClosest(world));
    world.getNewBornCells().add(new HuntFirst(world));
    world.getNewBornCells().add(new HuntMax(world));
    world.getNewBornCells().add(new Leech(world));
    world.getNewBornCells().add(new Spider(world));
    world.getNewBornCells().add(new Tree(world));
    world.getNewBornCells().add(new Vulture(world));
  }

}
