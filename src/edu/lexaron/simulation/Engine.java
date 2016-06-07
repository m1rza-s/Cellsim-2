/*
 *  Project name: CellSIM/Engine.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Feb 6, 2016, 3:41:07 AM
 */
package edu.lexaron.simulation;

import edu.lexaron.cells.Cell;
import edu.lexaron.cells.Cell_first;
import edu.lexaron.world.World;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Engine {

    private final Life life = new Life();
    // ID, x, y, energy, vision, movement, efficiency
    Cell_first a = new Cell_first(1, 25, 25, 50, 3, 1, 1);
    
    // height, width
    volatile World world = new World(80, 80);

    GridPane grid;
    VBox infoPanel = new VBox();

    Timer timer;
    Label l;
    int generations = 0;
    double sugarFactor = 15;

    public void startThread(BorderPane root) {
        root.setLeft(infoPanel);
        this.timer = new Timer();
        grid.setAlignment(Pos.CENTER);
        grid.getStyleClass().add("backgroundColor");
        grid.setPadding(new Insets(10));
        grid.setHgap(0.5);
        grid.setVgap(0.5);
        grid.setCache(true);
        
        world.getMonitor().getAllCells().add(a);
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
                    printWorld();
                    infoPanel.getChildren().clear();
                    if (world.getMonitor().worldHasLiveCells(world)) {
                        infoPanel.getChildren().add(world.getMonitor().refreshLiveCellInfo());
                    } else {
                        Label done = new Label("No live cells!");
                        done.getStyleClass().add("accentText");
                        done.getStyleClass().add("biggerText");
                        infoPanel.getChildren().add(done);
                    }

                    // END OF UI UPDATE //
                    if (!world.getMonitor().worldHasLiveCells(world)) {
                        timer.cancel();
                        timer.purge();

                        System.out.println("Simulation stopped: No more live cells");
                    }
                    // CELL ACTIVITY //
                    life.allLiveCellsHunt(world, world.getMonitor().getAllCells());
                    // CELL ACTIVITY END //
                    generations++;
                    l.setText(generations + " generations");
                });
            }
        };
        this.timer.scheduleAtFixedRate(timerTask, 0, 250);
    }

    public void setup() {
        world.generateWorld(sugarFactor);
        grid.setAlignment(Pos.CENTER_LEFT);
    }

    public void seedCells() {
        for (Cell c : world.getMonitor().getAllCells()) {
            world.getTheWorld()[c.getY()][c.getX()].setCell(c);
        }
    }

    private void printWorld() {
        grid.getChildren().clear();

        for (int i = 0; i < world.getHeight(); i++) {
            for (int j = 0; j < world.getWidth(); j++) {
                if (world.getTheWorld()[i][j].getSugar() != 0) {
                    Label sugLabel = new Label(String.valueOf(world.getTheWorld()[i][j].getSugar()));
                    sugLabel.setPadding(new Insets(0));
                    sugLabel.getStyleClass().addAll("accentText", "smallText");
                    GridPane.setHalignment(sugLabel, HPos.CENTER);
                    grid.add(sugLabel, i, j);
                } else if (world.getTheWorld()[i][j].getSugar() == 0) {
                    grid.add(new Rectangle(10, 10), i, j);
                }
                if (world.getTheWorld()[i][j].getCell() != null) {
                    printCellVisions(world.getTheWorld()[i][j].getCell());
                    Circle cell = world.getTheWorld()[i][j].getCell().drawCell();
                    GridPane.setHalignment(cell, HPos.CENTER);
                    grid.add(cell, i, j);
                }
            }
        }
        for (Cell temp : world.getMonitor().getAllCells()) {
            if (temp.isAlive()) {
                printCellVisions(temp);
            }
        }
    }

    private void printCellVisions(Cell c) {
        if (c.isAlive()) {
            for (int i = (c.getX() - c.getVision()); i <= (c.getX() + c.getVision()); i++) {
                for (int j = (c.getY() - c.getVision()); j <= (c.getY() + c.getVision()); j++) {
                    if (getNodeFromGridPane(j, i) != null) {
                        try {
                            ((Rectangle) getNodeFromGridPane(j, i)).setFill(Color.web("#404040"));
                        } catch (Exception ex) {
                            ((Label) getNodeFromGridPane(j, i)).setStyle("-fx-background-color: #404040");
                        }

                    }
                }
            }
        }
    }

    private Node getNodeFromGridPane(int row, int col) {
        for (Node node : grid.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    public void setGrid(GridPane grid) {
        this.grid = grid;
    }

    public void setL(Label l) {
        this.l = l;
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
