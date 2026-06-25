package com.jarvis.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class JarvisApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/jarvis/ui/hud.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1000, 600);
        // Hacer el fondo de la ventana transparente
        scene.setFill(Color.TRANSPARENT);

        // Quitar los bordes de la ventana y ponerla siempre visible
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setTitle("J.A.R.V.I.S. HUD");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void lanzar(String[] args) {
        launch(args);
    }
}