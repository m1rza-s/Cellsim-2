package edu.lexaron.simulation;

import edu.lexaron.cells.*;
import edu.lexaron.world.World;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class looks at average values of a entire {@link Breed}.
 * While the {@link Monitor} is looking at the {@link Breed}s, it is in a good position to see a {@link Breed}'s
 * population and thus, if the last member of a {@link Breed} dies, a new default member is created and inserted into the
 * {@link World}.
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 * Date & time: Jun 2, 2016, 9:15:14 PM
 *
 * Refactored: 24.04.2018
 */
class Monitor {
  private Monitor() {}

  /**
   * Adds {@link GridPane}s into a {@link VBox}.
   * Each {@link GridPane} contains information about each {@link Breed}.
   *
   * @param world     where the {@link Cell}s live
   * @param infoPanel container of each {@link Breed}s {@link GridPane}
   */
  @SuppressWarnings ({"ConstantConditions", "MagicNumber"})
  static void refreshCellInformation(World world, VBox infoPanel) {
    infoPanel.getChildren().clear();

    DecimalFormat df = new DecimalFormat("0.00");
    Map<Breed, Set<Cell>> breedPopulations = world.getAllCells().parallelStream().collect(Collectors.groupingBy(Cell::getBreed, Collectors.toSet()));

    breedPopulations.keySet().stream()
        .sorted(Comparator.comparingLong(breed -> breedPopulations.get(breed).parallelStream().filter(Cell::isAlive).count()).reversed())
        .forEach(sortedBreed -> {
          reviveIfExtinct(world, sortedBreed, breedPopulations);

          Label countLive_L     = new Label(                     breedPopulations.get(sortedBreed).parallelStream().filter(Cell::isAlive).count() + " " + sortedBreed);
          Label avgZoC_L        = new Label("FoV : " + df.format(breedPopulations.get(sortedBreed).parallelStream().filter(Cell::isAlive).mapToDouble(Cell::getVision)    .average().orElse(0.0)));
          Label avgEfficiency_L = new Label("Eff.: " + df.format(breedPopulations.get(sortedBreed).parallelStream().filter(Cell::isAlive).mapToDouble(Cell::getEfficiency).average().orElse(0.0)));
          Label avgSpeed_L      = new Label("Spd.: " + df.format(breedPopulations.get(sortedBreed).parallelStream().filter(Cell::isAlive).mapToDouble(Cell::getSpeed)     .average().orElse(0.0)));
          Label avgBite_L       = new Label("Bite: " + df.format(breedPopulations.get(sortedBreed).parallelStream().filter(Cell::isAlive).mapToDouble(Cell::getBiteSize)  .average().orElse(0.0)));

          countLive_L.setTextFill(Color.web(sortedBreed.getColorCode()));
          applyStyleClass("bigText", countLive_L);
          applyStyleClass("accentText", avgZoC_L, avgEfficiency_L, avgSpeed_L, avgBite_L);

          ProgressBar avgEne_PB = new ProgressBar();
          avgEne_PB.setMinWidth(200.0);
          avgEne_PB.setProgress(breedPopulations.get(sortedBreed).parallelStream().filter(Cell::isAlive).mapToDouble(Cell::getEnergy).average().orElse(0.0) / 100.0);

          GridPane grid = new GridPane();
          grid.setMinWidth(150.0);
          grid.setHgap(5.0);
          grid.setAlignment(Pos.CENTER);
          grid.getStyleClass().addAll("backgroundColorAccent", "accentText");
          grid.add(countLive_L, 0, 0, 2, 1);
          grid.add(avgZoC_L   , 0, 1);  grid.add(avgEfficiency_L, 1, 1);
          grid.add(avgBite_L  , 0, 2);  grid.add(avgSpeed_L     , 1, 2);
          grid.add(avgEne_PB  , 0, 3, 2, 1);
          infoPanel.getChildren().add(grid);
        });
  }

  private static void applyStyleClass(String styleClass, Label... labels) {
    Stream.of(labels).forEach(label -> label.getStyleClass().add(styleClass));
  }

  private static void reviveIfExtinct(World world, Breed breed, Map<Breed, Set<Cell>> population) {
    // todo Mirza : get rid of this SWITCH
    switch (breed) {
      case HUNT_CLOSEST:
        if (population.get(Breed.HUNT_CLOSEST).parallelStream().noneMatch(Cell::isAlive)) {
          world.getNewBornCells().add(new HuntClosest(world));
        }
        break;
      case HUNT_FIRST:
        if (population.get(Breed.HUNT_FIRST).parallelStream().noneMatch(Cell::isAlive)) {
          world.getNewBornCells().add(new HuntFirst(world));
        }
        break;
      case HUNT_MAX:
        if (population.get(Breed.HUNT_MAX).parallelStream().noneMatch(Cell::isAlive)) {
          world.getNewBornCells().add(new HuntMax(world));
        }
        break;
      case LEECH:
        if (population.get(Breed.LEECH).parallelStream().noneMatch(Cell::isAlive)) {
          world.getNewBornCells().add(new Leech(world));
        }
        break;
      case SPIDER:
        if (population.get(Breed.SPIDER).parallelStream().noneMatch(Cell::isAlive)) {
          world.getNewBornCells().add(new Spider(world));
        }
        break;
      case TREE:
        if (population.get(Breed.TREE).parallelStream().noneMatch(Cell::isAlive)) {
          world.getNewBornCells().add(new Tree(world));
        }
        break;
      case VULTURE:
        if (population.get(Breed.VULTURE).parallelStream().noneMatch(Cell::isAlive)) {
          world.getNewBornCells().add(new Vulture(world));
        }
        break;
    }
  }

}
