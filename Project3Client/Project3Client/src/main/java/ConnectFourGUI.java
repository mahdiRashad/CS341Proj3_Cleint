
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;


class ConnectFourGUI {
	private ObjectInputStream in;
	private int playerId;
	private boolean myTurn;
	private String username;
	private String password;
	private Stage stage;
	private GridClass gclass;
	private VBox root;


	public ConnectFourGUI(Socket socket, ObjectOutputStream out, ObjectInputStream in, String username, String password) {

		this.in = in;
		this.username = username;
		this.password = password;

		stage = new Stage();
		gclass = new GridClass(out, () -> myTurn);
		ChatClass chat = new ChatClass(out, username, playerId);
		root = new VBox(10);
		root.setPadding(new Insets(10));
		VBox chatPanel = chat.getChatPanel();
		root.setAlignment(Pos.CENTER);
		root.getChildren().addAll(chatPanel);
		stage.setScene(new Scene(root, 750, 800));
		root.setStyle("-fx-font-family: 'Helvetica';");
		root.setStyle("-fx-background-color: linear-gradient(to bottom, #ECE9E6, #FFFFFF);");
		stage.setTitle("Connect Four - " + username);
		stage.show();

		new Thread(() -> listenForMessages(chat.getChatArea())).start();
	}

	private void listenForMessages(TextArea chatArea) {
		try {
			while (true) {
				Object obj = in.readObject();
				if (!(obj instanceof Message)) continue;
				Message msg = (Message) obj;

				System.out.println("Received: " + msg);

				Platform.runLater(() -> handleMessage(msg, chatArea));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleMessage(Message msg, TextArea chatArea) {
		switch (msg.type) {
			case NEW_USER:
				playerId = msg.recipient;
				myTurn = (playerId == 0);
				GridPane grid = gclass.createGrid(playerId);
				StackPane stackPane = new StackPane();
				stackPane.getChildren().add(grid);
				stackPane.setMaxWidth(550);
				stackPane.setMinWidth(550);
				stackPane.setMinHeight(450);
				stackPane.maxHeight(450);
				stackPane.setAlignment(Pos.CENTER);
				root.getChildren().add(0, stackPane);
				break;
			case TEXT:
				chatArea.appendText(msg.username + ": " + msg.message + "\n");
				break;
			case MOVE:
				String[] parts = msg.message.split(":");
				int p = Integer.parseInt(parts[0]);
				int c = Integer.parseInt(parts[1]);
				gclass.updateBoard(p, c);
				myTurn = (p != playerId);
				break;
			case WIN:
				showAlert(msg.recipient == playerId ? "You win!" : "You lose.");
				gclass.askReplay(username, password, playerId, stage);
				break;
			case DRAW:
				showAlert("Game is a draw.");
				gclass.askReplay(username, password, playerId, stage);
				break;
			case INVALID:
				showAlert("Invalid move. Try another column.");
				break;
			case CLOSED:
				Platform.exit();
				System.exit(0);
				break;
		}
	}
	//new alert
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