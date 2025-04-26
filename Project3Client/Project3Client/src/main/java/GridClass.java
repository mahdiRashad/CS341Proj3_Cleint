import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.function.BooleanSupplier;

public class GridClass {
    private final int ROWS = 6;
    private final int COLUMNS = 7;
    private final int SIZE = 80;
    private final Circle[][] board = new Circle[ROWS][COLUMNS];
    private final ObjectOutputStream out;

    private final BooleanSupplier isMyTurn;

    public GridClass(ObjectOutputStream out, BooleanSupplier isMyTurn) {
        this.out = out;
        this.isMyTurn = isMyTurn;
    }


    public GridPane createGrid(int playerId) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setStyle("-fx-background-color: #9aa1ec");

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                StackPane cell = new StackPane();
                cell.setPrefSize(SIZE, SIZE);
                Circle circle = new Circle(SIZE / 2 - 5);
                circle.setFill(Color.WHITE);
                board[row][col] = circle;
                cell.getChildren().add(circle);
                final int column = col;
                int finalCol = col;
                cell.setOnMouseClicked(e -> {
                    if (isMyTurn.getAsBoolean()) {
                        try {
                            Message move = new Message(playerId, String.valueOf(finalCol));
                            move.type = MessageType.MOVE;
                            out.writeObject(move);
                            out.flush();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                    }
                });
                grid.add(cell, col, row);
            }
        }
        return grid;
    }

    public void updateBoard(int player, int col) {
            for (int row = ROWS - 1; row >= 0; row--) {
                if (board[row][col].getFill().equals(Color.WHITE)) {
                    board[row][col].setFill(player == 0 ? Color.RED : Color.YELLOW);
                    break;
                }
            }
    }


    public void askReplay(String username, String password, int playerId, Stage stage) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Play again?", ButtonType.YES, ButtonType.NO);
            alert.setTitle("Game Over");
            // 🧩 Change font for the whole dialog
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 16px;");

            // Optional: you can also style buttons separately if you want
            for (ButtonType buttonType : alert.getButtonTypes()) {
                Button button = (Button) dialogPane.lookupButton(buttonType);
                button.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 16px;");
            }
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        stage.close();
                        Socket newSocket = new Socket("127.0.0.1", 5555);
                        ObjectOutputStream newOut = new ObjectOutputStream(newSocket.getOutputStream());
                        newOut.flush();
                        ObjectInputStream newIn = new ObjectInputStream(newSocket.getInputStream());

                        Message login = new Message(MessageType.LOGIN, username, password);
                        newOut.writeObject(login);
                        newOut.flush();

                        Object responseMsg = newIn.readObject();
                        Message msg = (Message) responseMsg;
                        if ("SUCCESS".equals(msg.message) &&
                                msg.type == MessageType.LOGIN_RESULT) {
                            new ConnectFourGUI(newSocket, newOut, newIn, username, password);
                        } else {
                            new Alert(Alert.AlertType.ERROR, "Replay failed: unable to reconnect.").showAndWait();
                            Platform.exit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        new Alert(Alert.AlertType.ERROR, "Error while restarting game: " + e.getMessage()).showAndWait();
                        Platform.exit();
                    }
                } else {
                    try {
                        Message disconnect = new Message(playerId, false);
                        disconnect.type = MessageType.DISCONNECT;
                        disconnect.username = username;
                        out.writeObject(disconnect);
                        out.flush();
                    } catch (IOException ignored) {}
                    stage.close();
                    Platform.exit();
                }
            });
        });
    }
}