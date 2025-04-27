import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;

class LoginScreen {
    private TextField usernameField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private StylingClass stylingClass;

    public LoginScreen(Stage stage) {

        Image image = new Image(getClass().getResource("/four.png").toExternalForm());
        ImageView connectImage = new ImageView(image);
        connectImage.setFitWidth(58);
        connectImage.setFitHeight(58);

        VBox imageBox = new VBox(connectImage);

        imageBox.setAlignment(Pos.CENTER);

        imageBox.setMaxHeight(158);
        imageBox.setMinHeight(158);


        usernameField.setPromptText("Username");

        usernameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {  // When the field loses focus
                if (usernameField.getText().isEmpty()) {
                    usernameField.setPromptText("Username");
                }
            } else {  // When the field gains focus
                usernameField.setPromptText("");  // Clear prompt
            }
        });

        usernameField.setOnMouseClicked(event -> {
            if (usernameField.getText().equals("Username")) {
                usernameField.clear();
            }
        });

        usernameField.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-color: #cccccc;" +
                        "-fx-border-width: 1;"
        );

        passwordField.setPromptText("Password");

        passwordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {  // When the field loses focus
                if (passwordField.getText().isEmpty()) {
                    passwordField.setPromptText("Password");
                }
            } else {  // When the field gains focus
                passwordField.setPromptText("");  // Clear prompt
            }
        });

        passwordField.setOnMouseClicked(event -> {
            if (passwordField.getText().equals("Password")) {
                passwordField.clear();
            }
        });

        passwordField.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-color: #cccccc;" +
                        "-fx-border-width: 1;"
        );

        stylingClass = new StylingClass();

        VBox root = new VBox(30);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #1CD8D2, #93EDC7);");

        HBox form = new HBox(10, usernameField, passwordField);
        form.setAlignment(Pos.CENTER);

        Button loginButton = new Button("Log In");
        Button signupButton = new Button("Sign Up");

        stylingClass.stylingBtn(loginButton, 75, 40, 13);
        stylingClass.stylingBtn(signupButton, 75, 40, 13);

        HBox buttons = new HBox(10, loginButton, signupButton);
        buttons.setAlignment(Pos.CENTER);

        root.getChildren().addAll(imageBox, form, buttons);

        loginButton.setOnAction(e -> authenticate(stage, MessageType.LOGIN));
        signupButton.setOnAction(e -> authenticate(stage, MessageType.CREATE_ACCOUNT));

        stage.setScene(new Scene(root, 500, 400));
        root.setStyle("-fx-font-family: 'Helvetica';");
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
            Socket socket = new Socket("127.0.0.1", 5555);
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
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);

            Label label = new Label(message);
            label.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 16px;");

            alert.getDialogPane().setContent(label);
            alert.getDialogPane().lookupButton(ButtonType.OK).setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px;");
            alert.showAndWait();
        });
    }
}