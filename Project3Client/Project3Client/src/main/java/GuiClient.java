import java.io.*;
import java.net.Socket;

public class GuiClient {
	public static void main(String[] args) throws IOException {
		Socket socket = new Socket("127.0.0.1", 5555);
		ConnectFourGUI gui = new ConnectFourGUI(socket);
		gui.setVisible(true);
	}
}
