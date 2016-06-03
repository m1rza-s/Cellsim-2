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
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Engine {

    // ID, x, y, energy, vision, movement, efficiency
    volatile Cell_first a = new Cell_first(1, 10, 10, 20, 2, 1, 1);
    // height, width
    volatile World world = new World(80, 80);

    GridPane grid;
    Timer timer;
    Label l;
    int i = 0;
    double sugarFactor = 3.5;

    public void setL(Label l) {
        this.l = l;
    }

    public void startThread() {
        this.timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                world.getTheWorld()[a.getX()][a.getY()].setCell(a);
                Platform.runLater(() -> {
                    synchronized (world) {
                        printWorld();
                        if (!worldHasLiveCells()) {
                            timer.purge();
                            timer.cancel();
                            System.out.println("Simulation stopped: No more live cells");
                        }
                        a.hunt(world);
                        a.moveDown(world);
                        i++;
                        l.setText("Counter: " + i + " cycles");
                    }
                });
            }
        };
        this.timer.scheduleAtFixedRate(timerTask, 0, 500);
    }

    public void setup() {
        world.generateWorld(sugarFactor);
    }

    public void printWorld() {
        grid.getChildren().clear();
        grid.setAlignment(Pos.CENTER);
        grid.getStyleClass().add("backgroundColor");
        grid.setPadding(new Insets(10));
        grid.setHgap(0.5);
        grid.setVgap(0.5);
        for (int i = 1; i < world.getWidth() - 1; i++) {
            for (int j = 1; j < world.getHeight() - 1; j++) {
                if (world.getTheWorld()[i][j].getSugar() != 0) {
                    Rectangle sugar = new Rectangle(10, 10);
                    sugar.setStroke(Color.web("#000000"));
                    switch (world.getTheWorld()[i][j].getSugar()) {
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
                    grid.add(sugar, i, j);
                } else if (world.getTheWorld()[i][j].getSugar() == 0) {
                    Rectangle empty = new Rectangle(10, 10);
                    empty.setStroke(Color.web("#000000"));
                    grid.add(empty, i, j);
                }
                if (world.getTheWorld()[i][j].getCell() != null) {
//                    printCellVision(world.getTheWorld()[i][j].getCell());
                    Circle cell = world.getTheWorld()[i][j].getCell().drawCell();
                    GridPane.setHalignment(cell, HPos.CENTER);
                    grid.add(cell, i, j);
                }
            }
        }
        for (int i = 1; i < world.getWidth() - 1; i++) {
            for (int j = 1; j < world.getHeight() - 1; j++) {
                if (world.getTheWorld()[i][j].getCell() != null && world.getTheWorld()[i][j].getCell().isAlive()) {
                    printCellVisions(world.getTheWorld()[i][j].getCell());
                }
            }
        }

    }

    public boolean worldHasLiveCells() {
        boolean r = false;
        for (int i = 1; i < world.getWidth() - 1; i++) {
            for (int j = 1; j < world.getHeight() - 1; j++) {
                if (world.getTheWorld()[i][j].getCell() != null && world.getTheWorld()[i][j].getCell().isAlive()) {
                    r = true;
                }
            }
        }
        return r;
    }

    public void printCellVisions(Cell c) {
        if (c.isAlive()) {
            for (int i = (c.getX() - c.getVision()); i <= (c.getX() + c.getVision()); i++) {
                for (int j = (c.getY() - c.getVision()); j <= (c.getY() + c.getVision()); j++) {
                    if (getNodeFromGridPane(i, j) != null) {
                        ((Rectangle) getNodeFromGridPane(i, j)).setStroke(Color.web("#404040"));
                    }
                }
            }
        }
    }

    private Node getNodeFromGridPane(int col, int row) {
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
