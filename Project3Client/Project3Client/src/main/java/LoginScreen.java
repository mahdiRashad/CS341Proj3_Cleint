import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;

class LoginScreen {
    private TextField usernameField = new TextField();
    private PasswordField passwordField = new PasswordField();

    public LoginScreen(Stage stage) {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.CENTER);

        GridPane form = new GridPane();
        form.setVgap(10);
        form.setHgap(10);
        form.add(new Label("Username:"), 0, 0);
        form.add(usernameField, 1, 0);
        form.add(new Label("Password:"), 0, 1);
        form.add(passwordField, 1, 1);

        Button loginButton = new Button("Log In");
        Button signupButton = new Button("Sign Up");
        HBox buttons = new HBox(10, loginButton, signupButton);
        buttons.setAlignment(Pos.CENTER);

        root.getChildren().addAll(form, buttons);

        loginButton.setOnAction(e -> authenticate(stage, MessageType.LOGIN));
        signupButton.setOnAction(e -> authenticate(stage, MessageType.CREATE_ACCOUNT));

        stage.setScene(new Scene(root, 300, 200));
        stage.setTitle("Connect Four - Login");
        stage.show();
    }

    private void authenticate(Stage stage, MessageType type) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Username and password must not be empty.");
            return;
        }

        try {
            Socket socket = new Socket("localhost", 5555);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            out.writeObject(new Message(type, username, password));
            out.flush();

            Object response = in.readObject();
            if (response instanceof Message) {
                Message res = (Message) response;
                if ("SUCCESS".equals(res.message)) {
                    new ConnectFourGUI(socket, out, in, username, password);
                    stage.close();
                    return;
                }
            }
            showAlert("Authentication failed. Try again.");
        } catch (Exception ex) {
            showAlert("Connection error: " + ex.getMessage());
        }
    }

    private void showAlert(String message) {
        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, message).showAndWait());
    }
}