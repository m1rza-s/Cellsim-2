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
import javafx.scene.control.ScrollPane;
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

    // ID, x, y, energy, vision, movement, efficiency
    HuntFirst a = new HuntFirst("A", new Random().nextInt(width), new Random().nextInt(height), 95, 4, 1, 0.7, "#66ff33");
    HuntLargest b = new HuntLargest("B", new Random().nextInt(width), new Random().nextInt(height), 95, 2, 1, 1.2, "#3333ff");
    HuntClosest c = new HuntClosest("C", new Random().nextInt(width), new Random().nextInt(height), 95, 2, 1, 2, "#33ffff");
    Predator d = new Predator("D", new Random().nextInt(width), new Random().nextInt(height), 95, 5, 1, 0.5, "#ff00ff");

    public void startThread(BorderPane root) {
//        ScrollPane scroll = new ScrollPane(infoPanel);
//        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
//        scroll.setMinWidth(250);
//        scroll.setMaxWidth(250);
//        scroll.getStyleClass().add("backgroundColorAccent");
//        scroll.setCache(true);
//        scroll.setCacheHint(CacheHint.SPEED);

        root.setRight(infoPanel);
        StackPane stack = new StackPane(monitor.getChart());
//        monitor.getChart().setScaleY(0.3);
        monitor.getChart().setAnimated(true);
        monitor.getChart().setLegendVisible(true);
        monitor.getChart().setLegendSide(Side.RIGHT);
        monitor.getChart().setCache(true);
        monitor.getChart().setMinHeight(150);
        monitor.getChart().setMaxHeight(150);
        monitor.getChart().getData().add(monitor.getliveSeries());
        monitor.getliveSeries().setName("Alive");
        monitor.getChart().getData().add(monitor.getdeadSeries());
        monitor.getdeadSeries().setName("Dead");
        stack.setAlignment(Pos.TOP_LEFT);
        root.setBottom(stack);

        this.timer = new Timer();

        gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.YELLOW);

        canvas.setCacheHint(CacheHint.SPEED);
        canvas.setCache(true);

        world.getAllCells().add(a);
        world.getAllCells().add(b);
        world.getAllCells().add(c);
        world.getAllCells().add(d);
        seedCells();

        infoPanel.setPadding(new Insets(10));
        infoPanel.setMinWidth(200);
        infoPanel.setMaxWidth(200);
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
                    paintGrid();
                    paintWorld();
                    world.getAllCells().stream().map((c) -> (Cell) c).forEach((c) -> {
                        paintCells((Cell) c);
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
                    if (generations % 25 == 0) {
                        monitor.drawChart(world, generations);
                        for (int i = 0; i < 250; i++) {
                            world.newFood();
                        }
                    }

                    // END OF UI UPDATE // 
                    System.out.println("\t" + generations);
                });
            }
        };
        this.timer.scheduleAtFixedRate(timerTask, 0, 100);
    }

    private void paintWorld() {

        for (int i = 0; i < world.getHeight(); i++) {
            for (int j = 0; j < world.getWidth(); j++) {
                world.getTheWorld()[i][j].setSmell(world.getTheWorld()[i][j].getSmell() - 1);
                if (world.getTheWorld()[i][j].getCell() != null && world.getTheWorld()[i][j].getCell().getEnergy() < 0.1) {
                    world.getTheWorld()[i][j].getCell().setAlive(false);
                    world.getTheWorld()[i][j].setDeadCell(world.getTheWorld()[i][j].getCell());
                    world.getTheWorld()[i][j].setCell(null);
                }
                gc.setGlobalAlpha(0.2);
                if (world.getTheWorld()[i][j].getSugar().getAmount() > 0) {
                    int a = world.getTheWorld()[i][j].getSugar().getAmount();
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
                if (world.getTheWorld()[i][j].getSmell() > 0) {
                    int a = world.getTheWorld()[i][j].getSmell();
                    if (a == 10) {
                        gc.setFill(Color.web("#ffff1a"));
                    } else if (a < 10 && a >= 8) {
                        gc.setFill(Color.web("#ffff4d"));
                    } else if (a < 8 && a >= 6) {
                        gc.setFill(Color.web("#ffff80"));
                    } else if (a < 6 && a >= 4) {
                        gc.setFill(Color.web("#ffffb3"));
                    } else if (a < 4 && a > 0) {
                        gc.setFill(Color.web("#ffffe6"));
                    }
                    gc.fillRect((j - 0.25) * 5, (i - 0.25) * 5, 5, 5);

                }
            }
        }
        gc.setGlobalAlpha(1);
    }

    private void paintCells(Cell c) {
        if (c.isAlive()) {

            gc.setStroke(Color.web(c.getColor()));
            gc.setGlobalAlpha(0.3);
            gc.strokeRect(
                    (c.getX() - c.getVision() - 0.25) * 5,
                    (c.getY() - c.getVision() - 0.25) * 5,
                    ((c.getVision() * 2) + 1) * 5,
                    ((c.getVision() * 2) + 1) * 5);
            gc.setGlobalAlpha(1);
            gc.setFill(Color.web(c.getColor()));
            gc.fillRect((c.getX() - 0.25) * 5, (c.getY() - 0.25) * 5, 5, 5);
            gc.setFill(Color.YELLOW);
//            gc.fillText(c.getGeneCode(), (c.getX() + 0.25) * 5, (c.getY() + 0.5) * 5);
            paintTargetFoodLine(c);
        }
        if (!c.isAlive()) {
            gc.setFill(Color.web("#330000"));
            gc.fillRect((c.getX() - 0.25) * 5, (c.getY() - 0.25) * 5, 5, 5);
        } 
        
        gc.restore();
    }

    private void paintGrid() {
        gc.setStroke(Color.web("#1a1a1a"));
        for (int i = 10; i < width * 5; i += 5) {
            gc.strokeLine(i, 0, i, height * 5);
        }
        for (int i = 10; i < height * 5; i += 5) {
            gc.strokeLine(0, i, width * 5, i);
        }
        gc.restore();
    }

    public void paintTargetFoodLine(Cell c) {
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

    public void setup() {
        world.generateWorld(sugarFactor);
    }

    public void seedCells() {
        for (Object temp : world.getAllCells()) {
            Cell c = (Cell) temp;
            world.getTheWorld()[c.getY()][c.getX()].setCell((Cell) c);
        }
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        System.out.println("Canvas size: " + this.canvas.getWidth() + "x" + this.canvas.getHeight());
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setGens(Label l) {
        this.gens = l;
    }

    public void setAlive(Label alive) {
        this.alive = alive;
    }

    public void setDead(Label dead) {
        this.dead = dead;
    }

    public void setTotal(Label total) {
        this.total = total;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public World getWorld() {
        return world;
    }

    public double getSugarFactor() {
        return sugarFactor;
    }
}
