import javafx.application.Application;
import javafx.stage.Stage;

public class GuiClient extends Application {

	@Override
	public void start(Stage primaryStage) {
		new LoginScreen(primaryStage);
	}

	public static void main(String[] args) {
		launch(args);
	}
}