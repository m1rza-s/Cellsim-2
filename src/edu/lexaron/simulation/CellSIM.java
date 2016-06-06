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
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
        window.setTitle("CellSIM V.01");
        window.setOnCloseRequest(e -> Platform.exit());

        BorderPane root = new BorderPane();
        HBox menu = new HBox();
        HBox coord_X = new HBox();
        GridPane grid = new GridPane();

        
        // Structuring
        root.getStyleClass().add("backgroundColor");
        menu.getStyleClass().add("backgroundColorMenu");
        menu.setPadding(new Insets(20));
        menu.setSpacing(20);

        // GUI elements
        Label sugarFactor_L = new Label("Sugar factor: " + String.valueOf(run.getSugarFactor()) + "%");
        Label counter = new Label("0");
        // Adding elements to menu  
        menu.getChildren().addAll(
                sugarFactor_L,
                new Label("World Size: " + run.getWorld().getHeight() + "x" + run.getWorld().getWidth()),
                new Label("No. of Tiles: " + (run.getWorld().getHeight() * run.getWorld().getWidth())),
                counter
        );
        run.setGrid(grid);
        run.setup();
        run.setL(counter);
        run.setMonitor(m);

        // Display!        
        root.setTop(menu);
        root.setCenter(coord_X);
        
        root.setCenter(grid);
        mainScene = new Scene(root, 1000, 1000);
        mainScene.getStylesheets().add("style/style.css");
        window.setScene(mainScene);
        window.setMaximized(true);
        window.show();       
        
        run.startThread(root);

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
