/*
 *  Project name: CellSIM/Monitor.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 2, 2016, 9:15:14 PM
 */
package edu.lexaron.simulation;

import edu.lexaron.cells.*;
import edu.lexaron.world.World;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.math.RoundingMode;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class Monitor {
  private static final Random RANDOM = new SecureRandom(); 

  private final Set<Breed> allBreeds = new TreeSet<>();

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
   * @param w
   * @return
   */
  public int countAllCells(World w) {
    return w.getAllCells().size();
  }

  /**
   * @param w
   * @return
   */
  public long countLiveCells(World w) {
    return w.getAllCells().stream().filter(Cell::isAlive).count();
  }

  /**
   * @param w
   * @return
   */
  public long countDeadCells(World w) {
    return w.getAllCells().stream().filter(cell -> !cell.isAlive()).count();
  }

  /**
   * @param w
   * @return
   */
  public boolean worldHasLiveCells(World w) {
    return w.getAllCells().stream().anyMatch(Cell::isAlive);
  }

  // Promjeni ovaj metod tako da u jednom polju:
  // 1. pokazuje ukupnu i prosječnu energiju za svaku vrstu ćelije
  // 2. pokazuje prosječni FOV
  // 3. prosječnu efikasnost
  // 3. žive, mrtve i ukupno iz iste vrste

  /**
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

    double totalEnergy, avgEnergy, avgEffi = 0, avgSpeed = 0, avgBiteSize = 0;
    int countAlive, totVision = 0;
    String background;

    for (Iterator it = w.getAllCells().iterator(); it.hasNext(); ) {
      Cell c = (Cell) it.next();
      if (!allBreeds.contains(c.getBreed())) {
        allBreeds.add(c.getBreed());
      }

    }

    for (Breed breed : allBreeds) {
      countAlive = 0;
      totalEnergy = 0;
      totVision = 0;
      avgEffi = 0;
      avgSpeed = 0;
      avgBiteSize = 0;

      background = "";
      for (Iterator j = w.getAllCells().iterator(); j.hasNext(); ) {
        Cell c = (Cell) j.next();
        if (c.isAlive() && c.getBreed() == breed) {
          countAlive++;
          totalEnergy += c.getEnergy();
          totVision += (c.getVision() * c.getVision());
          avgEffi += c.getEfficiency();
          avgSpeed += c.getSpeed();
          avgBiteSize += c.getBiteSize();
          background = c.getColor();
        }
      }
      if (countAlive > 0) {
        avgEnergy = totalEnergy / countAlive;
        avgEffi /= countAlive;
        avgSpeed /= countAlive;
        avgBiteSize /= countAlive;

        Label countLive_L = new Label(breed + ", alive: " + countAlive);
        countLive_L.setTextFill(Color.web(background));
        countLive_L.getStyleClass().add("bigText");
        Label totEne_L = new Label("Force: " + (int) totalEnergy);
        totEne_L.getStyleClass().addAll("accentText");

        Label avgEffi_L = new Label("Avg.Eff.: " + df.format(avgEffi));
        avgEffi_L.getStyleClass().add("accentText");

        Label avgSpeed_L = new Label("Avg.Spd.: " + df.format(avgSpeed));
        avgSpeed_L.getStyleClass().add("accentText");

        Label totVision_L = new Label("ZoC: " + totVision);
        totVision_L.getStyleClass().addAll("accentText");

        Label avgBiteSize_L = new Label("Avg.Bite: " + df.format(avgBiteSize));
        avgBiteSize_L.getStyleClass().addAll("accentText");

        ProgressBar avgEne_PB = new ProgressBar();
        avgEne_PB.setMinWidth(200);
        avgEne_PB.setProgress(avgEnergy / 100);

        HBox row1 = new HBox(10);
        row1.getChildren().add(countLive_L);
//        if (background.length() != 0) {
//                    row1.setStyle("-fx-text-fill: " + background);
//        }
        row1.setAlignment(Pos.CENTER);

        HBox row2 = new HBox(10);
        row2.getChildren().addAll(
            //                        new Label("Avg E: "),
            avgEne_PB
        );
        row2.setAlignment(Pos.CENTER);

        HBox row3 = new HBox(10);
        row3.getChildren().addAll(totEne_L, totVision_L, avgBiteSize_L);
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
      }
      else {
//                System.out.println("\tDETECTED EXTINCT CELL: " + breed);
        switch (breed) {
          case VULTURE:
            w.getNewBornCells().add(new Vulture("V", RANDOM.nextInt(w.getWidth()), RANDOM.nextInt(w.getHeight())));
            break;
          case HUNT_FIRST:
            w.getNewBornCells().add(new HuntFirst("F", RANDOM.nextInt(w.getWidth()), RANDOM.nextInt(w.getHeight())));
            break;
          case HUNT_LARGEST:
            w.getNewBornCells().add(new HuntLargest("L", RANDOM.nextInt(w.getWidth()), RANDOM.nextInt(w.getHeight())));
            break;
          case HUNT_CLOSEST:
            w.getNewBornCells().add(new HuntClosest("C", RANDOM.nextInt(w.getWidth()), RANDOM.nextInt(w.getHeight())));
            break;
          case PREDATOR:
            w.getNewBornCells().add(new Predator("P", RANDOM.nextInt(w.getWidth()), RANDOM.nextInt(w.getHeight())));
            break;
          case TREE:
            w.getNewBornCells().add(new Tree("T", RANDOM.nextInt(w.getWidth()), RANDOM.nextInt(w.getHeight())));
          case LEECH:
            w.getNewBornCells().add(new Leech("L", RANDOM.nextInt(w.getWidth()), RANDOM.nextInt(w.getHeight())));
            break;
        }
      }
    }

    return v;
  }

  public void reseed(World w) {
    w.getNewBornCells().add(new Vulture("V", RANDOM.nextInt(w.getWidth()), RANDOM.nextInt(w.getHeight())));
    w.getNewBornCells().add(new Predator("P", RANDOM.nextInt(w.getWidth()), RANDOM.nextInt(w.getHeight())));
    w.getNewBornCells().add(new HuntFirst("F", RANDOM.nextInt(w.getWidth()), RANDOM.nextInt(w.getHeight())));
    w.getNewBornCells().add(new HuntLargest("L", RANDOM.nextInt(w.getWidth()), RANDOM.nextInt(w.getHeight())));
    w.getNewBornCells().add(new HuntClosest("C", RANDOM.nextInt(w.getWidth()), RANDOM.nextInt(w.getHeight())));
    w.getNewBornCells().add(new Tree("T", RANDOM.nextInt(w.getWidth()), RANDOM.nextInt(w.getHeight())));
    w.getNewBornCells().add(new Leech("L", RANDOM.nextInt(w.getWidth()), RANDOM.nextInt(w.getHeight())));
  }

}
