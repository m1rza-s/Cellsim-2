/*
 *  Project name: CellSIM/Monitor.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 2, 2016, 9:15:14 PM
 */
package edu.lexaron.simulation;

import edu.lexaron.world.Cell;
import edu.lexaron.world.World;
import java.util.Iterator;
import java.util.TreeSet;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.Effect;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Monitor {

    private final NumberAxis x = new NumberAxis();
    private final NumberAxis y = new NumberAxis();
    private final LineChart<Number, Number> chart = new LineChart<>(x, y);
    private XYChart.Series liveSeries = new XYChart.Series();
    private XYChart.Series deadSeries = new XYChart.Series();

    private TreeSet<String> allBreeds = new TreeSet();

    public LineChart<Number, Number> drawChart(World w, int i) {
        liveSeries.getData().add(new XYChart.Data<>(i, countLiveCells(w)));
        deadSeries.getData().add(new XYChart.Data<>(i, countDeadCells(w)));
        return chart;
    }

    ;
    
    public int countAllCells(World w) {
        return w.getAllCells().size();
    }

    public int countLiveCells(World w) {
        int r = 0;
        for (Object o : w.getAllCells()) {
            Cell c = (Cell) o;
            if (c.isAlive()) {
                r++;
            }
        }
        return r;
    }

    public int countDeadCells(World w) {
        int r = 0;
        for (Object o : w.getAllCells()) {
            Cell c = (Cell) o;
            if (!c.isAlive()) {
                r++;
            }
        }
        return r;
    }

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

    // Promjeni ovaj metod tako da u jednom polju:
    // 1. pokazuje ukupnu i prosječnu energiju za svaku vrstu ćelije
    // 2. pokazuje prosječni FOV
    // 3. prosječnu efikasnost
    // 3. žive, mrtve i ukupno iz iste vrste
    public VBox refreshLiveCellInfo(World w) throws NullPointerException {
        allBreeds.clear();
        VBox v = new VBox(20);
        v.setPadding(new Insets(10));
        v.setMinWidth(150);
        v.setSpacing(10);

        double totalEnergy, avgEnergy, avgEffi = 0;
        int countAlive;
        String background;

        for (Iterator it = w.getAllCells().iterator(); it.hasNext();) {
            Cell c = (Cell) it.next();
            allBreeds.add(c.getClass().getSimpleName());
        }

        for (Iterator i = allBreeds.iterator(); i.hasNext();) {
            String breed = (String) i.next();
            totalEnergy = 0;
            avgEnergy = 0;
            countAlive = 0;
            background = "";
            for (Iterator j = w.getAllCells().iterator(); j.hasNext();) {
                Cell c = (Cell) j.next();
                if (c.isAlive() && c.getClass().getSimpleName().equalsIgnoreCase(breed)) {
                    countAlive++;
                    totalEnergy = totalEnergy + c.getEnergy();
                    avgEffi = c.getEfficiency() / countAlive;
                    avgEnergy = totalEnergy / countAlive;
                    background = c.getColor();
                }
            }
            Label countLive = new Label(breed + ", alive: " + countAlive);
            Label totEne = new Label("Total Energy: " + (int) totalEnergy);
            totEne.getStyleClass().add("accentText");
            ProgressBar avgEne = new ProgressBar();

            avgEne.setProgress(avgEnergy / 100);

            HBox row1 = new HBox(10);
            row1.getChildren().add(countLive);
            if (background.length() != 0) {
                row1.setStyle("-fx-background-color: " + background);
            }
            row1.setAlignment(Pos.CENTER);

            HBox row2 = new HBox(10);
            row2.getChildren().addAll(
                    new Label("Energy "),
                    avgEne
            );
            row2.setAlignment(Pos.CENTER);

            HBox row3 = new HBox(10);
            row3.getChildren().add(totEne);
            row3.getStyleClass().add("accentText");

            VBox breedBox = new VBox();
            breedBox.setPadding(new Insets(5));
            breedBox.setSpacing(5);
            breedBox.setMinWidth(150);
            breedBox.getChildren().addAll(row1, row2, row3);
            breedBox.getStyleClass().add("backgroundColorAccent");
            v.getChildren().add(breedBox);
        }

        return v;
    }

    public LineChart<Number, Number> getChart() {
        return chart;
    }

    public XYChart.Series getliveSeries() {
        return liveSeries;
    }

    public XYChart.Series getdeadSeries() {
        return deadSeries;
    }

}
