
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;


class ConnectFourGUI {
	private GridPane board = new GridPane();
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private int playerId;
	private boolean myTurn;
	private String username;
	private String password;
	private Stage stage;
	private GridClass gclass;
	private VBox root;
	private ChatClass chat;


	private final int ROWS = 6;
	private final int COLUMNS = 7;
	private final int SIZE = 80;
	private final Circle[][] board1 = new Circle[ROWS][COLUMNS];

	public ConnectFourGUI(Socket socket, ObjectOutputStream out, ObjectInputStream in, String username, String password) {
		this.out = out;
		this.in = in;
		this.username = username;
		this.password = password;

		stage = new Stage();
		gclass = new GridClass(out, () -> myTurn);
		ChatClass chat = new ChatClass(out, username, playerId);

		root = new VBox(10);
		root.setPadding(new Insets(10));


		VBox chatPanel = chat.getChatPanel();
		root.getChildren().addAll(chatPanel);

		stage.setScene(new Scene(root, 500, 600));
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
//				VBox chatPanel = chat.getChatPanel();
				root.getChildren().add(0, grid);
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
		}
	}

	private void showAlert(String message) {
		new Alert(Alert.AlertType.INFORMATION, message).showAndWait();
	}

}