/*
 *  Project name: CellSIM/Monitor.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 2, 2016, 9:15:14 PM
 */
package edu.lexaron.simulation;

import edu.lexaron.cells.HuntClosest;
import edu.lexaron.cells.HuntFirst;
import edu.lexaron.cells.HuntLargest;
import edu.lexaron.cells.Leech;
import edu.lexaron.cells.Predator;
import edu.lexaron.cells.Tree;
import edu.lexaron.cells.Vulture;
import edu.lexaron.world.Cell;
import edu.lexaron.world.World;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Random;
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
import javafx.scene.paint.Color;

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

    /**
     *
     * @param w
     * @param i
     * @return
     */
    public LineChart<Number, Number> drawChart(World w, int i) {

        liveSeries.getData().add(new XYChart.Data<>(i, countLiveCells(w)));
        deadSeries.getData().add(new XYChart.Data<>(i, countDeadCells(w)));
        return chart;
    }

    ;
    
    public int countSugar(World w) {
        int r = 0;
        for (int i = 0; i < w.getHeight(); i++) {
            for (int j = 0; j < w.getWidth(); j++) {
                r += w.getWorld()[i][j].getSugar().getAmount();
            }
        }
        return r;
    }

    /**
     *
     * @param w
     * @return
     */
    public int countAllCells(World w) {
        return w.getAllCells().size();
    }

    /**
     *
     * @param w
     * @return
     */
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

    /**
     *
     * @param w
     * @return
     */
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

    /**
     *
     * @param w
     * @return
     */
    public boolean worldHasLiveCells(World w) {
        boolean r = false;
        outterloop:
        for (int i = 0; i < w.getWidth(); i++) {
            for (int j = 0; j < w.getHeight(); j++) {
                if (w.getWorld()[j][i].getCell() != null && w.getWorld()[j][i].getCell().isAlive()) {
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
    /**
     *
     * @param w
     * @return
     * @throws NullPointerException
     */
    public VBox refreshLiveCellInfo(World w) throws NullPointerException {
        allBreeds.clear();
        VBox v = new VBox(20);
        v.setPadding(new Insets(10));
        v.setMinWidth(250);
        v.setSpacing(10);

        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);

        double totalEnergy, avgEnergy, avgEffi = 0, avgSpeed = 0;
        int countAlive, totVision = 0;
        String background;

        for (Iterator it = w.getAllCells().iterator(); it.hasNext();) {
            Cell c = (Cell) it.next();
            allBreeds.add(c.getClass().getSimpleName());
        }

        for (Iterator i = allBreeds.iterator(); i.hasNext();) {
            String breed = (String) i.next();
            countAlive = 0;
            totalEnergy = 0;
            totVision = 0;
            avgEffi = 0;
            avgSpeed = 0;

            background = "";
            for (Iterator j = w.getAllCells().iterator(); j.hasNext();) {
                Cell c = (Cell) j.next();
                if (c.isAlive() && c.getClass().getSimpleName().equalsIgnoreCase(breed)) {
                    countAlive++;
                    totalEnergy = totalEnergy + c.getEnergy();
                    totVision += (c.getVision() * c.getVision());
                    avgEffi = avgEffi + c.getEfficiency();
                    avgSpeed = avgSpeed + c.getSpeed();
                    background = c.getColor();
                }
            }
            if (countAlive > 0) {
                avgEnergy = totalEnergy / countAlive;
                avgEffi = avgEffi / countAlive;
                avgSpeed = avgSpeed / countAlive;

                Label countLive_L = new Label(breed + ", alive: " + countAlive);
                countLive_L.setTextFill(Color.web(background));
                countLive_L.getStyleClass().add("bigText");
                Label totEne_L = new Label("Force: " + (int) totalEnergy);
                totEne_L.getStyleClass().addAll("accentText", "bigText");

                Label avgEffi_L = new Label("Efficiency: " + df.format(avgEffi));
                avgEffi_L.getStyleClass().add("accentText");

                Label avgSpeed_L = new Label("Avg. Speed: " + df.format(avgSpeed));
                avgSpeed_L.getStyleClass().add("accentText");

                Label totVision_L = new Label("ZoC: " + totVision);
                totVision_L.getStyleClass().addAll("accentText", "bigText");

                ProgressBar avgEne_PB = new ProgressBar();
                avgEne_PB.setMinWidth(200);
                avgEne_PB.setProgress(avgEnergy / 100);

                HBox row1 = new HBox(10);
                row1.getChildren().add(countLive_L);
                if (background.length() != 0) {
//                    row1.setStyle("-fx-text-fill: " + background);
                }
                row1.setAlignment(Pos.CENTER);

                HBox row2 = new HBox(10);
                row2.getChildren().addAll(
                        //                        new Label("Avg E: "),
                        avgEne_PB
                );
                row2.setAlignment(Pos.CENTER);

                HBox row3 = new HBox(10);
                row3.getChildren().addAll(totEne_L, totVision_L);
                row3.getStyleClass().add("accentText");

                HBox row4 = new HBox(10);
                row4.getChildren().addAll(avgEffi_L, avgSpeed_L);

                VBox breedBox = new VBox();
                breedBox.setPadding(new Insets(5));
                breedBox.setSpacing(5);
                breedBox.setMinWidth(150);
                breedBox.getChildren().addAll(row1, row2, row3, row4);
                breedBox.getStyleClass().add("backgroundColorAccent");
                v.getChildren().add(breedBox);
            } else {
                switch (breed) { // String ID, int x, int y, double energy, int vision, double speed, double efficiency, String color
                    case "Vulture":
                        w.getNewBornCells().add(new Vulture("V", new Random().nextInt(w.getWidth()), new Random().nextInt(w.getHeight()), 95, 5, 1, 0.5, "#33ffff"));
                        break;
                    case "Predator":
                        w.getNewBornCells().add(new Predator("P", new Random().nextInt(w.getWidth()), new Random().nextInt(w.getHeight()), 95, 5, 1, 0.33, "#ff0000"));
                        break;
                    case "HuntFirst":
                        w.getNewBornCells().add(new HuntFirst("F", new Random().nextInt(w.getWidth()), new Random().nextInt(w.getHeight()), 95, 1, 1, 2, "#66ff33"));
                        break;
                    case "HuntLargest":
                        w.getNewBornCells().add(new HuntLargest("L", new Random().nextInt(w.getWidth()), new Random().nextInt(w.getHeight()), 95, 1, 1, 2, "#ffff33"));
                        break;
                    case "HuntClosest":
                        w.getNewBornCells().add(new HuntClosest("C", new Random().nextInt(w.getWidth()), new Random().nextInt(w.getHeight()), 95, 1, 1, 2, "#ff33ff"));
                        break;
                    case "Tree":
                        w.getNewBornCells().add(new Tree("T", new Random().nextInt(w.getWidth()), new Random().nextInt(w.getHeight()), 95, 10, 1, 0.5, "#ffffff"));
                    case "Leech":
                        w.getNewBornCells().add(new Leech("L", new Random().nextInt(w.getWidth()), new Random().nextInt(w.getHeight()), 95, 5, 2, 0.2, "#003366"));
                        break;
                }
            }

        }

        return v;
    }

    /**
     *
     * @return
     */
    public LineChart<Number, Number> getChart() {
        return chart;
    }

    /**
     *
     * @return
     */
    public XYChart.Series getliveSeries() {
        return liveSeries;
    }

    /**
     *
     * @return
     */
    public XYChart.Series getdeadSeries() {
        return deadSeries;
    }

}
