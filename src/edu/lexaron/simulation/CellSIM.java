/*g
 *  Project name: CellSIM/CellSIM.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Feb 5, 2016, 8:53:58 PM
 */
package edu.lexaron.simulation;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
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
        window.setTitle("CellSIM V.03");
        window.setOnCloseRequest(e -> Platform.exit());

        BorderPane root = new BorderPane();
        VBox menu = new VBox();
        HBox menuRow1 = new HBox();
        HBox menuRow2 = new HBox();
        Canvas canvas = new Canvas(run.getWidth() * 10, run.getHeight() * 10);
        
        StackPane sp = new StackPane(new Button("LEGEND"));

        // Structuring
        root.getStyleClass().add("backgroundColor");
        menuRow1.getStyleClass().add("backgroundColorMenu");
        menuRow1.setPadding(new Insets(5, 15, 15, 15));
        menuRow1.setSpacing(20);
        menuRow2.getStyleClass().add("backgroundColorMenu");
        menuRow2.setPadding(new Insets(15, 15, 5, 15));
        menuRow2.setSpacing(20);

        // GUI elements
        Label sugarFactor_L = new Label("Sugar factor: " + String.valueOf(run.getSugarFactor()) + "%");
        Label counter = new Label();
        counter.getStyleClass().add("accentText");
        Button start = new Button("Start");
        // Adding elements to menuRow1 and 2
        menuRow1.getChildren().addAll(
                start
        );
        menuRow2.getChildren().addAll(
                sugarFactor_L,
                new Label("World Size: " + run.getWorld().getHeight() + "x" + run.getWorld().getWidth()),
                new Label("No. of Tiles: " + (run.getWorld().getHeight() * run.getWorld().getWidth())),
                counter
        );
        menu.getChildren().addAll(menuRow2, menuRow1);
        run.setCanvas(canvas);
        
        run.setup();
        run.setGens(counter);

        // Display!        
        root.setTop(menu);
        root.setCenter(canvas);
        root.setBottom(sp);

        mainScene = new Scene(root, 1000, 800);
        mainScene.getStylesheets().add("style/style.css");
        window.setScene(mainScene);
        window.setMaximized(true);
        window.show();

        start.setOnAction(e -> {
            run.startThread(root);
            System.out.println("Simulation started...");
            start.setDisable(true);
        });       
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
