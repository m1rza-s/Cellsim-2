/*
 *  Project name: CellSIM/Monitor.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 2, 2016, 9:15:14 PM
 */
package edu.lexaron.simulation;

import edu.lexaron.cells.Cell;
import edu.lexaron.world.World;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Monitor {

    private volatile List allCells = new LinkedList();

    public boolean worldHasLiveCells(World w) {
        boolean r = false;
        outterloop:
        for (int i = 0; i < w.getWidth(); i++) {
            for (int j = 0; j < w.getHeight(); j++) {
                if (w.getTheWorld()[j][i].getCell() != null && w.getTheWorld()[j][i].getCell().isAlive()) {
                    r = true;
                    break outterloop;
                }
            }
        }
        return r;
    }

    public VBox refreshLiveCellInfo() throws NullPointerException {
        VBox v = new VBox(20);
        v.setPadding(new Insets(10));

        for (Iterator it = allCells.iterator(); it.hasNext();) {
            Cell c = (Cell) it.next();
            if (c.isAlive()) {
                VBox cellBox = new VBox();
                HBox row1 = new HBox(10);
                HBox row2 = new HBox(10);
                row1.setPadding(new Insets(5, 5, 0, 5));
                row1.getStyleClass().add("backgroundColorAccent");

                row2.setPadding(new Insets(5, 0, 5, 5));
                row2.getStyleClass().add("backgroundColorAccent");

                Label cellID = new Label();
                ProgressBar energy = new ProgressBar();
                Label location = new Label();
                Label vision = new Label();
                Label efficiency = new Label();
                

                cellID.setText("ID: " + c.getID());
                energy.setProgress(c.getEnergy() / 100);
                energy.setScaleY(0.5);                
                location.setText("L: (" + c.getX() + "," + c.getY() + ")");
                vision.setText("V: " + c.getVision());
                efficiency.setText("E: " + c.getEfficiency());
                
                location.setTooltip(new Tooltip("Location"));
                vision.setTooltip(new Tooltip("Vision"));
                efficiency.setTooltip(new Tooltip("Efficiency"));
                
                cellID.getStyleClass().addAll("accentText", "bigText");
                energy.getStyleClass().addAll("greenText", "bigText");
                location.getStyleClass().addAll("accentText", "bigText");
                vision.getStyleClass().addAll("accentText", "bigText");
                efficiency.getStyleClass().addAll("accentText", "bigText");
                        
                
                row1.getChildren().addAll(
                        cellID,
                        energy,
                        efficiency
                );
                row2.getChildren().addAll(
                        vision,
                        location
                );
                cellBox.getChildren().addAll(row1, row2);
                v.getChildren().add(cellBox);
                v.getStyleClass().add("darkGrayBorder");
            }
        }
        return v;
    }

    public List<Cell> getAllCells() {
        return allCells;
    }

}
