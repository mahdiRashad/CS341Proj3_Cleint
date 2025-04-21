import java.io.Serializable;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    public MessageType type;
    public String message;
    public int recipient;

    public Message(int i, boolean connect){
        if(connect) {
            type = MessageType.NEWUSER;
            message = "User "+i+" has joined!";
            recipient = i;
        } else {
            type = MessageType.DISCONNECT;
            message = "User "+i+" has disconnected!";
            recipient = i;
        }
    }
    public Message(int rec, String mess){
        type = MessageType.TEXT;
        message = mess;
        recipient = rec;
    }
}
