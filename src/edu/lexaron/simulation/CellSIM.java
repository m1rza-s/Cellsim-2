/*g
 *  Project name: CellSIM/CellSIM.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Feb 5, 2016, 8:53:58 PM
 */
package edu.lexaron.simulation;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class CellSIM extends Application {

    Stage window;
    Scene mainScene;
    Monitor m = new Monitor();
    Engine run = new Engine();

    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        window = primaryStage;
        window.setTitle("CellSIM V.08");
        window.setOnCloseRequest(e -> Platform.exit());

        BorderPane root = new BorderPane();
        root.getStyleClass().add("backgroundColor");

        VBox menu = new VBox();
        HBox menuRow1 = new HBox();
        menuRow1.getStyleClass().add("backgroundColorMenu");
//        menuRow1.setPadding(new Insets(5, 15, 15, 15));
        menuRow1.setSpacing(20);
        menuRow1.setAlignment(Pos.CENTER);

        HBox stats = new HBox();
        stats.getStyleClass().add("backgroundColorMenu");
        stats.setPadding(new Insets(5, 5, 5, 5));
        stats.setSpacing(20);

        Canvas canvas = new Canvas(run.getWidth() * 5, run.getHeight() * 5);
        StackPane spc = new StackPane(canvas);
        spc.setAlignment(Pos.CENTER);

        VBox sp = new VBox(new Button("LEGEND"));
        sp.setPadding(new Insets(20));
        sp.setAlignment(Pos.TOP_CENTER);

        Label sugarFactor_L = new Label("Initial sugar factor: " + String.valueOf(run.getSugarFactor()) + "%");
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

        Button start = new Button("Start");
        start.setOnAction(e -> {
            run.startThread(root);
            System.out.println("Simulation started...");
            start.setDisable(true);
        });
        Button generateWorld = new Button("Spawn new cells & reset sugar");
        generateWorld.setOnAction(e -> {
            run.setCanvas(canvas);
            run.setup(true);

            run.setGens(counter);
            run.setAlive(liveCells);
            run.setDead(deadCells);
            run.setTotal(totalCells);
            run.setTotalSugar(totalSugar);
            run.paintWorld();
            
        });
        // STRUCTURING
        menuRow1.getChildren().addAll(
                sugarFactor_L,
                new Label("World Size: " + run.getWorld().getHeight() + "x" + run.getWorld().getWidth()),
                new Label("No. of Tiles: " + (run.getWorld().getHeight() * run.getWorld().getWidth())),
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

        run.setCanvas(canvas);
        run.setup(false);

        run.setGens(counter);
        run.setAlive(liveCells);
        run.setDead(deadCells);
        run.setTotal(totalCells);
        run.setTotalSugar(totalSugar);
        // Display!        
        root.setTop(menu);
        root.setCenter(spc);

        mainScene = new Scene(root, 1000, 800);
        mainScene.getStylesheets().add("style/style.css");

        window.setScene(mainScene);
        window.setMaximized(true);
        window.setFullScreen(true);
        window.show();

        // LISTENERS
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
