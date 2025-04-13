import java.io.Serializable;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;

    String message;

    public Message(String input){
        message = input;
    }
    public String toString(){
        return message;
    }
}
