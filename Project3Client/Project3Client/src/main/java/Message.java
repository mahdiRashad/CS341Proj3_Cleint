import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 42L;
    public MessageType type;
    public String message;
    public int recipient;

    public String username;
    public String password;

    public Message(int i, boolean connect){
        if(connect) {
            type = MessageType.NEW_USER;
            message = "User "+i+" has joined!";
        } else {
            type = MessageType.DISCONNECT;
            message = "User "+i+" has disconnected!";
        }
        recipient = i;
    }
    public Message(int rec, String mess){
        type = MessageType.TEXT;
        message = mess;
        recipient = rec;
    }

    public Message(MessageType type, String username, String password) {
        this.type = type;
        this.username = username;
        this.password = password;
    }

    @Override
    public String toString() {
        return "[type=" + type +
                ", recipient=" + recipient +
                ", message=" + message +
                ", username=" + username + "]";
    }
}
