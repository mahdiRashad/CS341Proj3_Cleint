import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ConnectFourGUI extends JFrame {
	private JButton[] columnButtons = new JButton[7];
	private JLabel[][] boardLabels = new JLabel[6][7];
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private int playerId;
	private boolean myTurn = false;
	private String username;
	private String password;

	public ConnectFourGUI(Socket socket, ObjectOutputStream out, ObjectInputStream in, String password) {
		super("Connect Four");
		this.out = out;
		this.in = in;
		this.password = password;

		setSize(700, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		JPanel boardPanel = new JPanel(new GridLayout(6, 7));
		for (int r = 0; r < 6; r++) {
			for (int c = 0; c < 7; c++) {
				boardLabels[r][c] = new JLabel(" ", SwingConstants.CENTER);
				boardLabels[r][c].setBorder(BorderFactory.createLineBorder(Color.BLACK));
				boardLabels[r][c].setOpaque(true);
				boardLabels[r][c].setBackground(Color.WHITE);
				boardPanel.add(boardLabels[r][c]);
			}
		}

		JTextArea chatArea = new JTextArea(10, 30);
		JTextField chatInput = new JTextField();
		JButton sendButton = new JButton("Send");
		chatArea.setEditable(false);

		JPanel chatPanel = new JPanel(new BorderLayout());
		chatPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);

		JPanel inputPanel = new JPanel(new BorderLayout());
		inputPanel.add(chatInput, BorderLayout.CENTER);
		inputPanel.add(sendButton, BorderLayout.EAST);
		chatPanel.add(inputPanel, BorderLayout.SOUTH);

		add(chatPanel, BorderLayout.SOUTH);

		JPanel controlPanel = new JPanel(new GridLayout(1, 7));
		for (int c = 0; c < 7; c++) {
			final int col = c;
			columnButtons[c] = new JButton("↓");
			columnButtons[c].addActionListener(e -> {
				if (myTurn) {
					try {
						Message move = new Message(playerId, String.valueOf(col));
						move.type = MessageType.MOVE;
						out.writeObject(move);
						out.flush();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			});
			controlPanel.add(columnButtons[c]);
		}

		add(controlPanel, BorderLayout.NORTH);
		add(boardPanel, BorderLayout.CENTER);

		sendButton.addActionListener(e -> {
			String text = chatInput.getText();
			if (!text.isEmpty()) {
				try {
					Message msg = new Message(playerId, text);
					msg.type = MessageType.TEXT;
					msg.username = username;
					out.writeObject(msg);
					out.flush();
					chatInput.setText("");
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});

		new Thread(() -> {
			try {
				while (true) {
					Object obj = in.readObject();
					if (!(obj instanceof Message)) continue;
					Message msg = (Message) obj;

					switch (msg.type) {
						case NEW_USER:
							playerId = msg.recipient;
							myTurn = (playerId == 0);
							username = msg.username;
							break;
						case TEXT:
							chatArea.append(msg.username + ": " + msg.message + "\n");
							break;
						case MOVE:
							String[] parts = msg.message.split(":");
							int p = Integer.parseInt(parts[0]);
							int c = Integer.parseInt(parts[1]);
							updateBoard(p, c);
							myTurn = (p != playerId);
							break;
						case WIN:
							int winner = msg.recipient;
							JOptionPane.showMessageDialog(this, winner == playerId ? "You win!" : "You lose.");
							askReplay();
							break;
						case DRAW:
							JOptionPane.showMessageDialog(this, "Game is a draw.");
							askReplay();
							break;
						case INVALID:
							JOptionPane.showMessageDialog(this, "Invalid move. Try another column.");
							break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void updateBoard(int player, int col) {
		for (int row = 5; row >= 0; row--) {
			if (boardLabels[row][col].getBackground() == Color.WHITE) {
				boardLabels[row][col].setBackground(player == 0 ? Color.RED : Color.YELLOW);
				break;
			}
		}
	}

	private void askReplay() {
		int res = JOptionPane.showConfirmDialog(this, "Play again?", "Game Over", JOptionPane.YES_NO_OPTION);
		try {
			if (res == JOptionPane.YES_OPTION) {
				// Dispose old game window
				this.dispose();

				// Reconnect to server
				Socket newSocket = new Socket("localhost", 5555);
				ObjectOutputStream newOut = new ObjectOutputStream(newSocket.getOutputStream());
				newOut.flush();
				ObjectInputStream newIn = new ObjectInputStream(newSocket.getInputStream());

				// Re-send login message
				Message login = new Message(MessageType.LOGIN, username, password); // use saved username and empty pw
				newOut.writeObject(login);
				newOut.flush();

				Object response = newIn.readObject();
				Message resMsg = (Message) response;
				System.out.println(resMsg);
				if ("SUCCESS".equals(resMsg.message) &&
						resMsg.type == MessageType.LOGIN_RESULT) {

					// Launch new game window
					new ConnectFourGUI(newSocket, newOut, newIn, password).setVisible(true);
				} else {
					JOptionPane.showMessageDialog(null, "Replay failed: unable to reconnect.");
					System.exit(0);
				}

			} else {
				Message disconnect = new Message(playerId, false);
				disconnect.type = MessageType.DISCONNECT;
				disconnect.username = username;
				out.writeObject(disconnect);
				out.flush();
				this.dispose();
				System.exit(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error while restarting game: " + e.getMessage());
			System.exit(1);
		}
	}

}
