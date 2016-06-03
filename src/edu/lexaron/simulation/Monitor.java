/*
 *  Project name: CellSIM/Monitor.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 2, 2016, 9:15:14 PM
 */
package edu.lexaron.simulation;

import edu.lexaron.cells.Cell;
import edu.lexaron.world.World;
import java.util.HashSet;
import java.util.Set;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Monitor {

    private final Set<Cell> allCells = new HashSet();

    public boolean worldHasLiveCells(World w) {
        boolean r = false;
        for (int i = 0; i < w.getWidth(); i++) {
            for (int j = 0; j < w.getHeight(); j++) {
                if (w.getTheWorld()[j][i].getCell() != null && w.getTheWorld()[j][i].getCell().isAlive()) {
                    r = true;
                }
            }
        }
        return r;
    }

    public VBox refreshLiveCellInfo() throws NullPointerException {
        VBox v = new VBox(20);
        v.setPadding(new Insets(10));

        for (Cell c : allCells) {
            if (c.isAlive()) {
                VBox cellBox = new VBox();
                HBox row1 = new HBox(10);
                HBox row2 = new HBox(10);
                row1.setPadding(new Insets(5));
                row1.getStyleClass().add("backgroundColorAccent");                
                
                row2.setPadding(new Insets(5));
                row2.getStyleClass().add("backgroundColorAccent");                
                
                Label cellID = new Label();
                Label energy = new Label();
                Label location = new Label();

                cellID.setText("ID: " + c.getID());
                energy.setText("Energy: " + c.getEnergy());
                location.setText("Location: (" + c.getX() + "," + c.getY() + ")");

                cellID.getStyleClass().addAll("accentText", "bigText");
                energy.getStyleClass().addAll("greenText", "bigText");
                location.getStyleClass().addAll("accentText", "bigText");                
                
                row1.getChildren().addAll(
                        cellID,
                        energy                
                );
                row2.getChildren().addAll(
                        location
                );
                cellBox.getChildren().addAll(row1, row2);
                v.getChildren().add(cellBox);
                v.getStyleClass().add("darkGrayBorder");
            }
        }        
        return v;
    }

    public Set<Cell> getAllCells() {
        return allCells;
    }

}
