/*
 *  Project name: CellSIM/Engine.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Feb 6, 2016, 3:41:07 AM
 */
package edu.lexaron.simulation;

import edu.lexaron.cells.*;
import edu.lexaron.world.World;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.security.SecureRandom;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Engine {


  // height, width
  private static final int HEIGHT = 200;
  private static final int WIDTH = 330;
  private static final double GLOBAL_SCALE = 5.0;
  private static final Random RANDOM = new SecureRandom();
  
  private final World world = new World(WIDTH, HEIGHT);
  private final Life life = new Life(world);
  private final VBox infoPanel = new VBox();
  private final double sugarFactor = RANDOM.nextInt(100);
  private Canvas canvas;
  private GraphicsContext gc;
  private Timer timer;
  private Label gens_L;
  private Label liveCells_L;
  private Label deadCells_L;
  private Label cells_L;
  private Label sugar_L;
  private int generations = 0;
  private int totalSugar = 0;
  private Image vulture, predator, tree, huntClosest, huntLargest, huntFirst, leech;

  /**
   * @param root
   */
  public void startThread(BorderPane root) {
    root.setRight(infoPanel);
    timer = new Timer();
    canvas.setCache(true);
    canvas.setCacheHint(CacheHint.SPEED);

    // ID, x, y, energy, vision, speed, efficiency, color, bitesize
    Vulture     v = new Vulture     ("V", RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT));
    HuntFirst   f = new HuntFirst   ("F", RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT));
    HuntLargest l = new HuntLargest ("L", RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT));
    HuntClosest c = new HuntClosest ("C", RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT));
    Predator    p = new Predator    ("P", RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT));
    Tree        t = new Tree        ("T", RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT));
    Leech       e = new Leech       ("L", RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT));
    world.getNewBornCells().add(v);
    world.getNewBornCells().add(f);
    world.getNewBornCells().add(l);
    world.getNewBornCells().add(c);
    world.getNewBornCells().add(p);
    world.getNewBornCells().add(t);
    world.getNewBornCells().add(e);

    infoPanel.setPadding(new Insets(10));
    infoPanel.setMinWidth(200);
    infoPanel.setMaxWidth(250);
    infoPanel.setMaxHeight(1000);
    infoPanel.getStyleClass().add("accentText");
    infoPanel.setAlignment(Pos.TOP_CENTER);
    infoPanel.setCache(true);
    infoPanel.setCacheHint(CacheHint.SPEED);

    Runnable lifeThread = new Thread(life);

    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        Platform.runLater(() -> {
          // CELL ACTIVITY //
          synchronized (world) {
            lifeThread.run();
          }
          // CELL ACTIVITY END //
          // UI UPDATE //
          gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
//                    paintGrid();
          paintWorld();
          world.getAllCells().forEach(cell -> paintCells(cell));
          synchronized (world) {
            world.notifyAll();
          }
          if (world.getAllCells().stream().anyMatch(Cell::isAlive)) {
            infoPanel.getChildren().clear();
            infoPanel.getChildren().add(Monitor.refreshCellInformation(world));
          }
          else {
            infoPanel.getChildren().clear();
            Label done = new Label("No live cells!");
            done.getStyleClass().addAll("accentText", "biggerText");
            infoPanel.getChildren().add(done);
            timer.cancel();
            timer.purge();
            System.out.println("Simulation stopped: No more live cells");
          }
          generations++;
          gens_L.setText(generations + " generations");
          cells_L    .setText("Total: " + world.getAllCells().size());
          liveCells_L.setText("Alive: " + world.getAllCells().stream().filter(Cell::isAlive).count());
          deadCells_L.setText("Dead: "  + world.getAllCells().stream().filter(cell -> !cell.isAlive()).count());

          sugar_L.setText("Sugar in world: " + totalSugar);
          totalSugar = 0;
          // END OF UI UPDATE //
//                    System.out.println("\t" + generations);

        });
      }

      // Ovdje nekako provjeri da li je EDT živ i nastavi task ako jeste.
    };
    this.timer.schedule(timerTask, 200, 75);

//        lifeThread.start();
  }

  public void paintWorld() {
    for (int i = 0; i < world.getHeight(); i++) {
      for (int j = 0; j < world.getWidth(); j++) {
        world.getWorld()[i][j].getTrail().setAmount(world.getWorld()[i][j].getTrail().getAmount() - 1);
        gc.setGlobalAlpha(0.25);
        totalSugar += world.getWorld()[i][j].getSugar().getAmount();
        if (world.getWorld()[i][j].getSugar().getAmount() < 0) {
          world.getWorld()[i][j].getSugar().setAmount(0);
        }
        double sugarTemp = world.getWorld()[i][j].getSugar().getAmount();
        if (sugarTemp >= 10) {
          gc.setFill(Color.web("#ffff00"));
        }
        else if (sugarTemp < 10 && sugarTemp >= 8) {
          gc.setFill(Color.web("#66cc00"));
        }
        else if (sugarTemp < 8 && sugarTemp >= 6) {
          gc.setFill(Color.web("#4d9900"));
        }
        else if (sugarTemp < 6 && sugarTemp >= 4) {
          gc.setFill(Color.web("#336600"));
        }
        else if (sugarTemp < 4 && sugarTemp >= 1) {
          gc.setFill(Color.web("#264d00"));
        }
        else {
          gc.setFill(Color.web("#331a00"));
        }
        gc.setGlobalAlpha(0.1);
        gc.fillRect((j - 0.5) * GLOBAL_SCALE, (i - 0.5) * GLOBAL_SCALE, 10, 10);
        gc.setGlobalAlpha(0.5);
        if (world.getWorld()[i][j].getTrail().getAmount() > 0) {

          int trailTemp = world.getWorld()[i][j].getTrail().getAmount();
          gc.setFill(Color.web(world.getWorld()[i][j].getTrail().getSource().getBreed().getColorCode()));
          if (trailTemp == 50) {
            gc.setGlobalAlpha(0.5);
          }
          else if (trailTemp < 50 && trailTemp >= 40) {
            gc.setGlobalAlpha(0.4);
          }
          else if (trailTemp < 40 && trailTemp >= 30) {
            gc.setGlobalAlpha(0.3);
          }
          else if (trailTemp < 30 && trailTemp >= 20) {
            gc.setGlobalAlpha(0.2);
          }
          else if (trailTemp < 20 && trailTemp > 0) {
            gc.setGlobalAlpha(0.1);
          }
          gc.fillRect((j - 0.5) * GLOBAL_SCALE, (i - 0.5) * GLOBAL_SCALE, 5, 5);

        }
      }
    }
    gc.restore();
  }

  private static void reseedCells(World world) {
    world.getNewBornCells().add(new Vulture(world));
    world.getNewBornCells().add(new Predator(world));
    world.getNewBornCells().add(new HuntFirst(world));
    world.getNewBornCells().add(new HuntLargest(world));
    world.getNewBornCells().add(new HuntClosest(world));
    world.getNewBornCells().add(new Tree(world));
    world.getNewBornCells().add(new Leech(world));
  }

  private void paintCells(Cell c) {
    if (!c.isAlive()) {
      gc.setGlobalAlpha(0.2);
      paintCellGFX(c);
    }
    else if (c.isAlive()) {
      gc.setFill(Color.web(c.getBreed().getColorCode()));
      gc.setGlobalAlpha(0.1);
//      gc.fillRect(
//          (c.getX() - c.getVision() - 0.25) * globalScale,
//          (c.getY() - c.getVision() - 0.25) * globalScale,
//          ((c.getVision() * 2) + 1) * globalScale,
//          ((c.getVision() * 2) + 1) * globalScale);
//            gc.setFill(Color.web(c.getColor()));
      double a = c.getEnergy();
      gc.setGlobalAlpha(1);
      if (a < 50 && a >= 40) {
        gc.setGlobalAlpha(0.9);
      }
      else if (a < 40 && a >= 30) {
        gc.setGlobalAlpha(0.8);
      }
      else if (a < 30 && a >= 20) {
        gc.setGlobalAlpha(0.7);
      }
      else if (a < 20 && a > 0) {
        gc.setGlobalAlpha(0.6);
      }
      paintCellGFX(c);
      gc.setStroke(Color.web(c.getBreed().getColorCode()));
      gc.fillText((int) c.getEnergy() + "", (c.getX() - 3) * GLOBAL_SCALE, (c.getY() - 1.5) * GLOBAL_SCALE);
      paintTargetLine(c);
    }
    gc.restore();
  }

  private void paintCellGFX(Cell cell) {
    switch (cell.getBreed()) {
      case TREE:
        gc.drawImage(tree, (cell.getX() - 1.5) * GLOBAL_SCALE, (cell.getY() - 1.5) * GLOBAL_SCALE);
        break;
      case VULTURE:
        gc.drawImage(vulture, (cell.getX() - 3) * GLOBAL_SCALE, (cell.getY() - 1.5) * GLOBAL_SCALE);
        break;
      case PREDATOR:
        gc.drawImage(predator, (cell.getX() - 1.5) * GLOBAL_SCALE, (cell.getY() - 1.5) * GLOBAL_SCALE);
        break;
      case HUNT_CLOSEST:
        gc.drawImage(huntClosest, (cell.getX() - 1.5) * GLOBAL_SCALE, (cell.getY() - 1.5) * GLOBAL_SCALE);
        break;
      case HUNT_MAX:
        gc.drawImage(huntLargest, (cell.getX() - 1.5) * GLOBAL_SCALE, (cell.getY() - 1.5) * GLOBAL_SCALE);
        break;
      case HUNT_FIRST:
        gc.drawImage(huntFirst, (cell.getX() - 1.5) * GLOBAL_SCALE, (cell.getY() - 1.5) * GLOBAL_SCALE);
        break;
      case LEECH:
        gc.drawImage(leech, (cell.getX() - 1.5) * GLOBAL_SCALE, (cell.getY() - 1.5) * GLOBAL_SCALE);
        break;
    }
    gc.restore();
  }

  private void paintGrid() {
    gc.setStroke(Color.web("#1a1a1a"));
    for (int i = 5; i < WIDTH * GLOBAL_SCALE; i += 5) {
      gc.strokeLine(i, 0, i, HEIGHT * GLOBAL_SCALE);
    }
    for (int i = 5; i < HEIGHT * GLOBAL_SCALE; i += 5) {
      gc.strokeLine(0, i, WIDTH * GLOBAL_SCALE, i);
    }
    gc.restore();
  }

  /**
   * @param c
   */
  private void paintTargetLine(Cell c) {
    if (c.getTargetFood() != null) {
      gc.setGlobalAlpha(2);
      gc.setStroke(Color.web(c.getBreed().getColorCode()));
      gc.strokeLine(
          (c.getX() + 0.25) * GLOBAL_SCALE, (c.getY() + 0.25) * GLOBAL_SCALE,
          (c.getTargetFood()[1] + 0.25) * GLOBAL_SCALE, (c.getTargetFood()[0] + 0.25) * GLOBAL_SCALE
      );
    }
    gc.restore();
  }

  /**
   * @param cellsToo
   */
  public void setup(boolean cellsToo) {
    world.generateWorld(sugarFactor);
    gc = canvas.getGraphicsContext2D();
    gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    huntClosest = new Image("edu/lexaron/gfx/huntClosest.png");
    huntFirst   = new Image("edu/lexaron/gfx/huntFirst.png");
    huntLargest = new Image("edu/lexaron/gfx/huntLargest.png");
    leech       = new Image("edu/lexaron/gfx/leech.png");
    predator    = new Image("edu/lexaron/gfx/predator.png");
    vulture     = new Image("edu/lexaron/gfx/vulture.png");
    tree        = new Image("edu/lexaron/gfx/tree.png");
    if (cellsToo) {
      reseedCells(world);
    }
  }

  /**
   * @param canvas
   */
  public void setCanvas(Canvas canvas) {
    this.canvas = canvas;
    System.out.println("Canvas size: " + this.canvas.getWidth() + "x" + this.canvas.getHeight());
  }

  /**
   * @return
   */
  public int getWidth() {
    return WIDTH;
  }

  /**
   * @return
   */
  public int getHeight() {
    return HEIGHT;
  }

  /**
   * @param l
   */
  public void setGens_L(Label l) {
    this.gens_L = l;
  }

  /**
   * @param alive
   */
  public void setAlive(Label alive) {
    this.liveCells_L = alive;
  }

  /**
   * @param dead
   */
  public void setDead(Label dead) {
    this.deadCells_L = dead;
  }

  /**
   * @param total
   */
  public void setTotal(Label total) {
    this.cells_L = total;
  }

  /**
   * @return
   */
  public World getWorld() {
    return world;
  }

  /**
   * @return
   */
  public double getSugarFactor() {
    return sugarFactor;
  }

  public void setSugar_L(Label sugar_L) {
    this.sugar_L = sugar_L;
  }

}
