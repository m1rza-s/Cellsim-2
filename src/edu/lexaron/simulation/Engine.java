/*
 *  Project name: CellSIM/Engine.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Feb 6, 2016, 3:41:07 AM
 */
package edu.lexaron.simulation;

import edu.lexaron.world.Cell;
import edu.lexaron.cells.*;
import edu.lexaron.world.World;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.CacheHint;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Engine {

    final Monitor monitor = new Monitor();
    private final Life life = new Life();

    // height, width
    private final int height = 150;
    private final int width = 300;
    private volatile World world = new World(width, height);
    private Canvas canvas;
    private GraphicsContext gc;
    private final VBox infoPanel = new VBox();

    private Timer timer;
    private Label gens;
    private Label alive;
    private Label dead;
    private Label total;
    private int generations = 0;
    private final double sugarFactor = 100;

    /**
     *
     * @param root
     */
    public void startThread(BorderPane root) {
//        ScrollPane scroll = new ScrollPane(infoPanel);
//        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//        scroll.setMinWidth(250);
//        scroll.setMaxWidth(250);
//        scroll.getStyleClass().add("backgroundColorAccent");
//        scroll.setCache(true);
//        scroll.setCacheHint(CacheHint.SPEED);

        root.setRight(infoPanel);
//        StackPane stack = new StackPane(monitor.getChart());
//        monitor.getChart().setScaleY(0.3);
//        monitor.getChart().setAnimated(true);
//        monitor.getChart().setLegendVisible(true);
//        monitor.getChart().setLegendSide(Side.RIGHT);
//        monitor.getChart().setCache(true);
//        monitor.getChart().setMinHeight(150);
//        monitor.getChart().setMaxHeight(150);
//        monitor.getChart().getData().add(monitor.getliveSeries());
//        monitor.getliveSeries().setName("Live Cells");
//        monitor.getChart().getData().add(monitor.getdeadSeries());
//        monitor.getdeadSeries().setName("Sugar");
//        stack.setAlignment(Pos.TOP_LEFT);
//        root.setBottom(stack);

        this.timer = new Timer();

        
        gc.setStroke(Color.YELLOW);

        canvas.setCache(true);
        canvas.setCacheHint(CacheHint.SPEED);

        // ID, x, y, energy, vision, speed, efficiency
        HuntFirst f = new HuntFirst("F", new Random().nextInt(width), new Random().nextInt(height), 95, 2, 2, 1, "#66ff33");
        HuntLargest l = new HuntLargest("L", new Random().nextInt(width), new Random().nextInt(height), 95, 2, 1, 1, "#ffff33");
        HuntClosest c = new HuntClosest("C", new Random().nextInt(width), new Random().nextInt(height), 95, 2, 1, 1, "#ff33ff");
        Predator p = new Predator("P", new Random().nextInt(width), new Random().nextInt(height), 95, 5, 2, 0.2, "#ff0000");
        Vulture v = new Vulture("V", new Random().nextInt(width), new Random().nextInt(height), 95, 10, 1, 0.2, "#33ffff");
        world.getNewBornCells().add(f);
        world.getNewBornCells().add(l);
        world.getNewBornCells().add(c);
        world.getNewBornCells().add(p);
        world.getNewBornCells().add(v);

        infoPanel.setPadding(new Insets(10));
        infoPanel.setMinWidth(200);
        infoPanel.setMaxWidth(250);
        infoPanel.setMaxHeight(1000);
        infoPanel.getStyleClass().add("accentText");
        infoPanel.setAlignment(Pos.TOP_CENTER);
        infoPanel.setCache(true);
        infoPanel.setCacheHint(CacheHint.SPEED);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    // CELL ACTIVITY //
                    life.allLiveCellsHunt(world);
                    // CELL ACTIVITY END //
                    // UI UPDATE //
                    gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
//                    paintGrid();
                    paintWorld();
                    world.getAllCells().stream().map((c) -> (Cell) c).forEach((c) -> {
                        Cell temp = (Cell) c;
                        if (world.getWorld()[temp.getY()][temp.getX()].getCell() != null && !temp.isAlive()) {                            
                            world.getWorld()[temp.getY()][temp.getX()].setCell(null);
                            world.getWorld()[temp.getY()][temp.getX()].setDeadCell(temp);
                        }
                        paintCells((Cell) temp);
                    });

                    infoPanel.getChildren().clear();
                    if (monitor.worldHasLiveCells(world)) {
                        infoPanel.getChildren().add(monitor.refreshLiveCellInfo(world));
                    } else {
                        Label done = new Label("No live cells!");
                        done.getStyleClass().addAll("accentText", "biggerText");
                        infoPanel.getChildren().add(done);
                        timer.cancel();
                        timer.purge();
                        System.out.println("Simulation stopped: No more live cells");
                    }
                    generations++;
                    gens.setText(generations + " generations");
                    alive.setText("Alive: " + monitor.countLiveCells(world));
                    dead.setText("Dead: " + monitor.countDeadCells(world));
                    total.setText("Total: " + monitor.countAllCells(world));
                    if (generations % 100 == 0) {
//                        monitor.drawChart(world, generations);
                        for (int i = 0; i < (new Random().nextInt(1000) + 1000); i++) {
                            world.newFood();
                        }
                    }

                    // END OF UI UPDATE // 
//                    System.out.println("\t" + generations);
                });
            }
        };
        this.timer.schedule(timerTask, 100, 50);
    }

    public void paintWorld() {
        for (int i = 0; i < world.getHeight(); i++) {
            for (int j = 0; j < world.getWidth(); j++) {
                world.getWorld()[i][j].getTrail().setAmount(world.getWorld()[i][j].getTrail().getAmount() - 1);
                gc.setGlobalAlpha(0.2);
                if (world.getWorld()[i][j].getSugar().getAmount() > 0) {
                    int a = world.getWorld()[i][j].getSugar().getAmount();
                    if (a == 10) {
                        gc.setFill(Color.web("#f2f2f2"));
                    } else if (a < 10 && a >= 8) {
                        gc.setFill(Color.web("#cccccc"));
                    } else if (a < 8 && a >= 6) {
                        gc.setFill(Color.web("#999999"));
                    } else if (a < 6 && a >= 4) {
                        gc.setFill(Color.web("#666666"));
                    } else if (a < 4 && a > 0) {
                        gc.setFill(Color.web("#333333"));
                    } else {
                        gc.setFill(Color.web("#333333"));
                    }

                    gc.fillRect((j - 0.25) * 5, (i - 0.25) * 5, 5, 5);
                }
                gc.setGlobalAlpha(0.5);
                if (world.getWorld()[i][j].getTrail().getAmount() > 0) {

                    int a = world.getWorld()[i][j].getTrail().getAmount();
                    gc.setFill(Color.web(world.getWorld()[i][j].getTrail().getSource().getColor()));
                    if (a == 50) {
                        gc.setGlobalAlpha(0.5);
                    } else if (a < 50 && a >= 40) {
                        gc.setGlobalAlpha(0.4);
                    } else if (a < 40 && a >= 30) {
                        gc.setGlobalAlpha(0.3);
                    } else if (a < 30 && a >= 20) {
                        gc.setGlobalAlpha(0.2);
                    } else if (a < 20 && a > 0) {
                        gc.setGlobalAlpha(0.1);
                    }
                    gc.fillRect((j - 0.25) * 5, (i - 0.25) * 5, 5, 5);

                }
            }
        }
        gc.restore();
    }

    private void paintCells(Cell c) {
        if (!c.isAlive()) {            
            gc.setGlobalAlpha(0.2);
            gc.setFill(Color.web(c.getColor()));
            gc.fillOval((c.getX() - 0.25) * 5, (c.getY() - 0.25) * 5, 6, 6);
//                        gc.fillText(c.getGeneCode(), (c.getX() + 0.25) * 5, (c.getY() + 0.5) * 5);
        } else if (c.isAlive()) {
            gc.setStroke(Color.web(c.getColor()));
            gc.setGlobalAlpha(0.3);
            gc.strokeRect(
                    (c.getX() - c.getVision() - 0.25) * 5,
                    (c.getY() - c.getVision() - 0.25) * 5,
                    ((c.getVision() * 2) + 1) * 5,
                    ((c.getVision() * 2) + 1) * 5);
            gc.setGlobalAlpha(1);
            gc.setFill(Color.web(c.getColor()));
            gc.fillOval((c.getX() - 0.25) * 5, (c.getY() - 0.25) * 5, 6, 6);
            gc.setFill(Color.YELLOW);
            gc.fillText((int) c.getEnergy()+"", (c.getX() + 1) * 5, (c.getY() + 2) * 5);
            paintTargetLine(c);
        }
        gc.restore();
    }

    private void paintGrid() {
        gc.setStroke(Color.web("#1a1a1a"));
        for (int i = 5; i < width * 5; i += 5) {
            gc.strokeLine(i, 0, i, height * 5);
        }
        for (int i = 5; i < height * 5; i += 5) {
            gc.strokeLine(0, i, width * 5, i);
        }
        gc.restore();
    }

    /**
     *
     * @param c
     */
    public void paintTargetLine(Cell c) {
        if (c.getTargetFood() != null) {
            gc.setGlobalAlpha(2);
            gc.setStroke(Color.web(c.getColor()));
            gc.strokeLine(
                    (c.getX() + 0.25) * 5, (c.getY() + 0.25) * 5,
                    (c.getTargetFood()[1] + 0.25) * 5, (c.getTargetFood()[0] + 0.25) * 5
            );
        }
        gc.setGlobalAlpha(1);
    }

    /**
     *
     */
    public void setup() {
        world.generateWorld(sugarFactor);        
        gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     *
     * @param canvas
     */
    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        System.out.println("Canvas size: " + this.canvas.getWidth() + "x" + this.canvas.getHeight());
    }

    /**
     *
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     *
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     *
     * @param l
     */
    public void setGens(Label l) {
        this.gens = l;
    }

    /**
     *
     * @param alive
     */
    public void setAlive(Label alive) {
        this.alive = alive;
    }

    /**
     *
     * @param dead
     */
    public void setDead(Label dead) {
        this.dead = dead;
    }

    /**
     *
     * @param total
     */
    public void setTotal(Label total) {
        this.total = total;
    }

    /**
     *
     * @param world
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     *
     * @return
     */
    public World getWorld() {
        return world;
    }

    /**
     *
     * @return
     */
    public double getSugarFactor() {
        return sugarFactor;
    }
}
