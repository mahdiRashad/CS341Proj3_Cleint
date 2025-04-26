import javafx.application.Platform;
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

    public ChatClass(ObjectOutputStream out, String username, int playerId) {
        this.out = out;
        this.username = username;
        this.playerId = playerId;

        chatArea = new TextArea();
        chatArea.setEditable(false);
    }




    public VBox getChatPanel() {
        TextField chatInput = new TextField();
        Button sendButton = new Button("Send");
        Button exitButton = new Button("Exit"); // <-- New Exit button

        HBox chatBox = new HBox(5, chatInput, sendButton, exitButton);

        VBox chatPanel = new VBox(5, chatArea, chatBox);

        sendButton.setOnAction(e -> sendChat(chatInput, chatArea));

        exitButton.setOnAction(e -> {
            try {
                Message msg = new Message(playerId, false); // false for disconnect
                msg.type = MessageType.CLOSED;          // Set proper message type
                msg.username = username;                    // Include username
                out.writeObject(msg);
                out.flush(); // Always flush after writing an object
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Platform.exit(); // Close the application cleanly
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
