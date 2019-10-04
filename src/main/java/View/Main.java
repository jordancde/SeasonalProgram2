package View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {
    Controller controller = new Controller();

    public Main() {
    }

    public void start(Stage stage) throws Exception {
        new GridPane();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui.fxml"));
        Parent root = (Parent)loader.load();
        Scene scene = new Scene(root, 903.0D, 707.0D);
        stage.setTitle("Sector Stats");
        stage.setScene(scene);
        stage.show();
    }


}
