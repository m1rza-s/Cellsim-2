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

    Engine run = new Engine();

    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        window = primaryStage;
        window.setTitle("CellSIM V.01");
        window.setOnCloseRequest(e -> Platform.exit());
        
        
        
        // Prepwork
        run.setup();

        // Structuring
        BorderPane root = new BorderPane();
        root.getStyleClass().add("backgroundColor");

        HBox menu = new HBox();
        menu.getStyleClass().add("backgroundColorMenu");
        menu.setPadding(new Insets(20));
        menu.setSpacing(20);

        GridPane grid = new GridPane();
        // GUI elements
        Label sugarFactor_L = new Label("Sugar factor: " + String.valueOf(run.getSugarFactor()) + "%");
        Label counter = new Label("0");
        // Adding elements to menu  
        menu.getChildren().addAll(
                sugarFactor_L,
                new Label("Size: " + run.getWorld().getHeight()+ "x" + run.getWorld().getWidth()),
                new Label("Grid size: " + (run.getWorld().getHeight() * run.getWorld().getWidth())),
                counter
        );

        // Painting grid
        run.setGrid(grid);        

        // Display!        
        root.setTop(menu);
        root.setCenter(grid);
        mainScene = new Scene(root, 640, 1000);
        mainScene.getStylesheets().add("style/style.css");
        window.setScene(mainScene);
        window.setMaximized(false);
        window.show();
        
        run.setL(counter);
        run.startThread();
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
