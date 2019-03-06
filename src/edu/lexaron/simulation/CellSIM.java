/*g
 *  Project name: CellSIM/CellSIM.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Feb 5, 2016, 8:53:58 PM
 */
package edu.lexaron.simulation;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Timer;

import static edu.lexaron.simulation.WorldPainter.paintCellVision;

/**
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class CellSIM extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    primaryStage.setTitle("CellSIM V.10");
    //noinspection CallToSystemExit
    primaryStage.setOnCloseRequest(e -> System.exit(0));
    // region UI
    VBox infoPanel = new VBox();
    BorderPane root = new BorderPane();
    root.getStyleClass().add("backgroundColor");

    VBox menu = new VBox();
    HBox menuRow1 = new HBox();
    menuRow1.getStyleClass().add("backgroundColorMenu");
//        menuRow1.setPadding(new Insets(5, 15, 15, 15));
    menuRow1.setSpacing(20);
    menuRow1.setAlignment(Pos.CENTER_LEFT);

    HBox stats = new HBox();
    stats.getStyleClass().add("backgroundColorMenu");
    stats.setPadding(new Insets(5, 5, 5, 5));
    stats.setSpacing(20);



    VBox sp = new VBox(new Button("LEGEND"));
    sp.setPadding(new Insets(20));
    sp.setAlignment(Pos.TOP_CENTER);

    Label counter = new Label();
    counter.getStyleClass().add("accentText");

    Label liveCells = new Label();
    liveCells.getStyleClass().addAll("greenText", "bigText");

    Label deadCells = new Label();
    deadCells.getStyleClass().addAll("redText", "bigText");

    Label totalCells = new Label();
    totalCells.getStyleClass().addAll("bigText");

    Label totalSugar = new Label();
    totalSugar.getStyleClass().addAll("bigText", "whiteText");
    // endregion
    Engine engine = new Engine(infoPanel, counter, liveCells, deadCells, totalCells, totalSugar);

    Label sugarFactor_L = new Label("Initial sugar factor: " + String.valueOf(engine.getSugarFactor()) + "%");
    Canvas canvas = new Canvas((double) (engine.getWidth() * 5), (double) (engine.getHeight() * 5));
//    StackPane spc = new StackPane(canvas);
//    spc.setAlignment(Pos.CENTER);
    Button start = new Button("Start");
    start.setOnAction(e -> {
      engine.startThread(canvas, new Timer());
      System.out.println("Simulation started...");
      start.setDisable(true);
    });
    Button generateWorld = new Button("Spawn new cells & reset sugar");
    generateWorld.setOnAction(e -> {
      engine.generateWorld(true, canvas);
      paintCellVision(Engine.getWorld(), canvas);

    });
    // STRUCTURING
    menuRow1.getChildren().addAll(
        sugarFactor_L,
        new Label("World Size: " + Engine.getWorld().getHeight() + "x" + Engine.getWorld().getWidth()),
        new Label("No. of Tiles: " + (Engine.getWorld().getHeight() * Engine.getWorld().getWidth())),
        counter,
        liveCells,
        deadCells,
        totalCells,
        totalSugar
    );
    stats.getChildren().addAll(
        start,
        generateWorld
    );
    menu.getChildren().addAll(
        menuRow1,
        stats
    );

    engine.generateWorld(false, canvas);

    root.setTop(menu);
    root.setCenter(canvas);
    root.setLeft(infoPanel);

    Scene mainScene = new Scene(root, 1000, 800);
    mainScene.getStylesheets().add("style/style.css");

    primaryStage.setScene(mainScene);
    primaryStage.setMaximized(true);
    primaryStage.setFullScreen(true);
    primaryStage.show();

    // LISTENERS
  }
}
