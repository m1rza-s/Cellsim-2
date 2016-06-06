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

    private Monitor m;
    private final Life life = new Life();
    // ID, x, y, energy, vision, movement, efficiency
    Cell_first a = new Cell_first(1, 10, 10, 50, 1, 1, 1);
    Cell_first b = new Cell_first(2, 20, 20, 50, 2, 1, 1);
    Cell_first c = new Cell_first(3, 30, 30, 50, 3, 1, 1);
    // height, width
    volatile World world = new World(50, 50);
    
    GridPane grid;
    VBox infoPanel = new VBox();

    Timer timer;
    Label l;
    int i = 0;
    double sugarFactor = 10;

    public void startThread(BorderPane root) {
        root.setLeft(infoPanel);
        this.timer = new Timer();

        m.getAllCells().add(a);
        m.getAllCells().add(b);
        m.getAllCells().add(c);
        seedCells();
        
        infoPanel.setPadding(new Insets(10));
        infoPanel.setMinWidth(300);
        infoPanel.setMaxWidth(300);
//        infoPanel.getStyleClass().add("backgroundColorAccent");
        infoPanel.getStyleClass().add("accentText");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    synchronized (world) {
                        // UI UPDATE //
                        printWorld();
                        infoPanel.getChildren().clear();
                        if (m.worldHasLiveCells(world)) {
                            infoPanel.getChildren().add(m.refreshLiveCellInfo());
                        } else {
                            Label done = new Label("No live cells!");
                            done.getStyleClass().add("accentText");
                            done.getStyleClass().add("biggerText");
                            infoPanel.getChildren().add(done);
                        }
                        
                        // END OF UI UPDATE //
                        if (!m.worldHasLiveCells(world)) {
                            timer.purge();
                            timer.cancel();
                            System.out.println("Simulation stopped: No more live cells");
                        }
                        // CELL ACTIVITY //
                        life.allLiveCellsHunt(world, m.getAllCells());
                        // CELL ACTIVITY END //
                        i++;
                        l.setText(i + " generations");
                    }
                });
            }
        };
        this.timer.scheduleAtFixedRate(timerTask, 0, 500);
    }

    public void setup() {
        world.generateWorld(sugarFactor);
        grid.setAlignment(Pos.CENTER_LEFT);
//        m.getAllCells().add(a);
//        seedCells();
    }

    public void seedCells() {
        for (Cell c : m.getAllCells()) {
            world.getTheWorld()[c.getY()][c.getX()].setCell(c);
        }
    }

    private void printWorld() {
        grid.getChildren().clear();
        grid.setAlignment(Pos.CENTER);
        grid.getStyleClass().add("backgroundColor");
        grid.setPadding(new Insets(10));
        grid.setHgap(0.5);
        grid.setVgap(0.5);

        for (int i = 0; i < world.getHeight(); i++) {
            for (int j = 0; j < world.getWidth(); j++) {
                if (world.getTheWorld()[j][i].getSugar() != 0) {
                    Rectangle sugar = new Rectangle(10, 10);
                    sugar.setStroke(Color.web("#000000"));
                    switch (world.getTheWorld()[j][i].getSugar()) {
                        case 1:
                            sugar.setFill(Color.web("#808080"));
                            break;
                        case 2:
                            sugar.setFill(Color.web("#ffff00"));
                            break;
                        case 3:
                            sugar.setFill(Color.web("#ff9900"));
                            break;
                        case 4:
                            sugar.setFill(Color.web("#ff0000"));
                            break;
                        case 5:
                            sugar.setFill(Color.web("#ffffff"));
                            break;
                    }
                    Label sugLabel = new Label(String.valueOf(world.getTheWorld()[j][i].getSugar()));
                    
                    sugLabel.getStyleClass().addAll("accentText", "smallText");
                    GridPane.setHalignment(sugLabel, HPos.CENTER);
                    grid.add(sugLabel, j, i);
                } else if (world.getTheWorld()[j][i].getSugar() == 0) {
                    Rectangle empty = new Rectangle(10, 10);
                    empty.setStroke(Color.web("#000000"));
                    if (world.getTheWorld()[j][i].getId() % 2 == 0) {
                        empty.setFill(Color.web("#213300"));
                    } else {
                        empty.setFill(Color.web("#332600"));
                    }
                    grid.add(empty, j, i);
                }
                if (world.getTheWorld()[j][i].getCell() != null) {
//                    printCellVision(world.getTheWorld()[j][i].getCell());
                    Circle cell = world.getTheWorld()[j][i].getCell().drawCell();
                    GridPane.setHalignment(cell, HPos.CENTER);
                    grid.add(cell, j, i);
                }
            }
        }
        for (int i = 0; i < world.getHeight(); i++) {
            for (int j = 0; j < world.getWidth(); j++) {
                if (world.getTheWorld()[j][i].getCell() != null && world.getTheWorld()[j][i].getCell().isAlive()) {
                    printCellVisions(world.getTheWorld()[j][i].getCell());
                                        
                }
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

    public void setMonitor(Monitor m) {
        this.m = m;
    }
}
