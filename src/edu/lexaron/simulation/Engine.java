/*
 *  Project name: CellSIM/Engine.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Feb 6, 2016, 3:41:07 AM
 */
package edu.lexaron.simulation;

import edu.lexaron.world.Cell;
import edu.lexaron.cells.Cell_first;
import edu.lexaron.world.World;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.CacheHint;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Engine {

    private final Monitor monitor = new Monitor();
    private final Life life = new Life();
    // ID, x, y, energy, vision, movement, efficiency
    Cell_first a = new Cell_first(1, 50, 50, 95, 3, 1, 1);
    Cell_first b = new Cell_first(1, 51, 50, 95, 3, 1, 1);

    // height, width
    private final int height = 80;
    private final int width = 150;
    private volatile World world = new World(width, height);
    private Canvas canvas;
    private GraphicsContext gc;
    private final VBox infoPanel = new VBox();

    private Timer timer;
    private Label gens;
    private int generations = 0;
    private final double sugarFactor = 10;

    public void startThread(BorderPane root) {
        root.setLeft(infoPanel);
        this.timer = new Timer();

        gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.YELLOW);
        
        
        canvas.setCacheHint(CacheHint.SPEED);
        canvas.setCache(true);
//        canvas.setScaleX(0.5);
//        canvas.setScaleY(0.5);

        world.getAllCells().add(a);
        world.getAllCells().add(b);
        seedCells();

        infoPanel.setPadding(new Insets(10));
        infoPanel.setMinWidth(300);
        infoPanel.setMaxWidth(300);
//        infoPanel.getStyleClass().add("backgroundColorAccent");
        infoPanel.getStyleClass().add("accentText");
        infoPanel.setCache(true);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    // UI UPDATE //
                    
                    gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    paintWorld();
                    paintGrid();
                    world.getAllCells().stream().map((c) -> (Cell) c).forEach((c) -> {
                        paintCells((Cell) c);
                    });
                    infoPanel.getChildren().clear();
                    if (monitor.worldHasLiveCells(world)) {
                        infoPanel.getChildren().add(monitor.refreshLiveCellInfo(world));
                    } else {
                        Label done = new Label("No live cells!");
                        done.getStyleClass().add("accentText");
                        done.getStyleClass().add("biggerText");
                        infoPanel.getChildren().add(done);
                        timer.cancel();
                        timer.purge();

                        System.out.println("Simulation stopped: No more live cells");
                    }
                    // END OF UI UPDATE //                    
                    // CELL ACTIVITY //
                    life.allLiveCellsHunt(world, world.getAllCells());
                    // CELL ACTIVITY END //
                    generations++;
                    gens.setText(generations + " generations");

                });
            }
        };
        this.timer.scheduleAtFixedRate(timerTask, 0, 100);
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

    private void paintWorld() {
        for (int i = 0; i < world.getHeight(); i++) {
            for (int j = 0; j < world.getWidth(); j++) {
                if (world.getTheWorld()[i][j].getSugar().getAmount() != 0 && world.getTheWorld()[i][j].getCell() == null) {
                    gc.setFill(Color.YELLOW);
//                    gc.setFont(new Font(10));
//                    gc.fillText(String.valueOf(world.getTheWorld()[i][j].getSugar()), j*10, i*10);
                    gc.fillRect((j - 0.5) * 10, (i - 0.5) * 10, 10, 10);
                } 
            }
        }
        gc.restore();
    }

    private void paintCells(Cell c) {
        if (c.isAlive()) {
            paintTargetFoodLine(c);
            gc.setStroke(Color.web("#006622"));
            gc.strokeRect(
                    (c.getX() - c.getVision() - 0.5) * 10,
                    (c.getY() - c.getVision() - 0.5) * 10,
                    ((c.getVision() * 2) + 1) * 10,
                    ((c.getVision() * 2) + 1) * 10);

            gc.setFill(Color.GREEN);
            gc.fillRect((c.getX() - 0.5) * 10, (c.getY() - 0.5) * 10, 10, 10);
            
        } else if (!c.isAlive()) {
            gc.setFill(Color.RED);
            gc.fillRect((c.getX() - 0.5) * 10, (c.getY() - 0.5) * 10, 10, 10);
        } else {
            System.out.println("    UNEXPECTED CELL STATE: " + c.getID());
        }
        gc.restore();
    }

    private void paintGrid() {
        gc.setStroke(Color.web("#333333"));
        for (int i = 10; i < width * 10; i += 10) {
            gc.strokeLine(i, 0, i, height * 10);
        }
        for (int i = 10; i < height * 10; i += 10) {
            gc.strokeLine(0, i, width * 10, i);
        }
        gc.restore();
    }

    public void paintTargetFoodLine(Cell c) {
        if (c.getTargetFood() != null) {
            gc.setStroke(Color.RED);
            gc.strokeLine(
                    (c.getX()) * 10, (c.getY()) * 10,
                    (c.getTargetFood()[1]) * 10, (c.getTargetFood()[0]) * 10
            );
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
