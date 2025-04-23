import javax.swing.*;

public class GuiClient {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
	}
}