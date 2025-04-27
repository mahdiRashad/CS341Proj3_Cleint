import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class ChatClass {

    private ObjectOutputStream out;
    String username;
    int playerId;
    private TextArea chatArea;

    private StylingClass stylingClass;

    public ChatClass(ObjectOutputStream out, String username, int playerId) {
        this.out = out;
        this.username = username;
        this.playerId = playerId;

        chatArea = new TextArea();
        chatArea.setPrefSize(100, 100);
        chatArea.setEditable(false);


        stylingClass = new StylingClass();
    }




    public VBox getChatPanel() {
        TextField chatInput = new TextField();
        Button sendButton = new Button("Send");
        Button exitButton = new Button("Exit");


        HBox chatBox = new HBox(7, chatInput, sendButton);
        chatBox.setAlignment(Pos.CENTER);

        VBox chatPanel = new VBox(15, chatArea, chatBox, exitButton);
        chatPanel.setAlignment(Pos.CENTER);
        chatPanel.setMaxWidth(500);
        chatPanel.setMinWidth(500);
        chatPanel.setMaxHeight(200);
        chatPanel.setMinHeight(200);
        chatPanel.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-background-radius: 15;" +
                        "-fx-padding: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0.5, 0, 2);"
        );

        chatArea.setStyle(
                "-fx-control-inner-background: #f9f9f9;" +
                        "-fx-background-insets: 0;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-border-color: #e0e0e0;" +
                        "-fx-border-width: 1;"
        );

        chatInput.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-background-radius: 8;" +
                        "-fx-border-radius: 8;" +
                        "-fx-border-color: #cccccc;" +
                        "-fx-border-width: 1;"
        );


        sendButton.setOnAction(e -> sendChat(chatInput, chatArea));

        stylingClass.stylingBtn(sendButton, 75, 30, 10);
        stylingClass.stylingBtn(exitButton, 75, 40, 13);


        chatInput.setPromptText("Send Message");

        chatInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {  // When the field loses focus
                if (chatInput.getText().isEmpty()) {
                    chatInput.setPromptText("Send Message");
                }
            } else {  // When the field gains focus
                chatInput.setPromptText("");  // Clear prompt
            }
        });

        chatInput.setOnMouseClicked(event -> {
            if (chatInput.getText().equals("Send Message")) {
                chatInput.clear();
            }
        });

        exitButton.setOnAction(e -> {
            try {
                Message msg = new Message(playerId, false);
                msg.type = MessageType.CLOSED;
                msg.username = username;
                out.writeObject(msg);
                out.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Platform.exit();
        });

        return chatPanel;
    }


    public TextArea getChatArea() {
        return chatArea;
    }


    private void sendChat(TextField chatInput, TextArea chatArea) {
        String text = chatInput.getText();
        if (!text.isEmpty()) {
            try {
                Message msg = new Message(playerId, text);
                msg.type = MessageType.TEXT;
                msg.username = username;
                out.writeObject(msg);
                out.flush();
                chatInput.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
