import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Snake extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Snake.class.getResource("snake.fxml"));
        Parent parent = fxmlLoader.load();
        SnakeController ctrl = fxmlLoader.getController();
        Scene scene = new Scene(parent);
        ctrl.init(parent, scene, stage);
        stage.setTitle("Snake");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}