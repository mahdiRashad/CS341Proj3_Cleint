import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;

    public LoginScreen() {
        setTitle("Connect Four - Login or Sign Up");
        setSize(400, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(2, 2));
        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        loginButton = new JButton("Log In");
        signupButton = new JButton("Sign Up");
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(this::handleLogin);
        signupButton.addActionListener(this::handleSignup);
    }

    private void handleLogin(ActionEvent e) {
        authenticate(MessageType.LOGIN);
    }

    private void handleSignup(ActionEvent e) {
        authenticate(MessageType.CREATE_ACCOUNT);
    }

    private void authenticate(MessageType type) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password must not be empty.");
            return;
        }

        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {
            socket = new Socket("localhost", 5555);

            // Proper stream order
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            Message msg = new Message(type, username, password);
            out.writeObject(msg);
            out.flush();

            Object response = in.readObject();
            if (response instanceof Message) {
                Message res = (Message) response;
                System.out.println("[CLIENT] Got response: " + res);
                if ("SUCCESS".equals(res.message) &&
                        (res.type == MessageType.LOGIN_RESULT || res.type == MessageType.CREATE_ACCOUNT_RESULT)) {

                    // ✅ Now reuse the socket and streams in ConnectFourGUI
                    ObjectOutputStream finalOut = out;
                    ObjectInputStream finalIn = in;
                    new ConnectFourGUI(socket, finalOut, finalIn).setVisible(true);
                    this.dispose();
                    return;
                }
            }

            JOptionPane.showMessageDialog(this, "Authentication failed. Try again.");
            if (socket != null) socket.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Connection error: " + ex.getMessage());
            try {
                if (socket != null) socket.close();
            } catch (Exception ignored) {}
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen().setVisible(true));
    }
}
