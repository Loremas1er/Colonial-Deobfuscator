package ru.paimon.deobfuscator.colonialdeobfuscator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static Stage stage;

    public void start(Stage stage) throws IOException {
        this.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(),600,400);
        stage.setScene(scene);
        stage.setTitle("Colonial Deobfuscator by Paimon [V1.0]");
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}
