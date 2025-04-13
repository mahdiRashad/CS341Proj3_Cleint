import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class ConnectFourGUI extends JFrame {
	private JButton[] columnButtons = new JButton[7];
	private JLabel[][] boardLabels = new JLabel[6][7];
	private PrintWriter out;
	private BufferedReader in;
	private int playerId; // 0 for red, 1 for yellow
	private boolean myTurn = false;

	public ConnectFourGUI(Socket socket) throws IOException {
		super("Connect Four");
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

		JPanel controlPanel = new JPanel(new GridLayout(1, 7));
		for (int c = 0; c < 7; c++) {
			final int col = c;
			columnButtons[c] = new JButton("↓");
			columnButtons[c].addActionListener(e -> {
				if (myTurn) {
					out.println("MOVE:" + col);
				}
			});
			controlPanel.add(columnButtons[c]);
		}

		add(controlPanel, BorderLayout.NORTH);
		add(boardPanel, BorderLayout.CENTER);

		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);

		new Thread(() -> {
			try {
				String msg;
				while ((msg = in.readLine()) != null) {
					if (msg.startsWith("START")) {
						playerId = msg.endsWith("RED") ? 0 : 1;
						myTurn = playerId == 0;
					} else if (msg.startsWith("MOVE")) {
						String[] parts = msg.split(":");
						int p = Integer.parseInt(parts[1]);
						int c = Integer.parseInt(parts[2]);
						updateBoard(p, c);
						myTurn = (p != playerId);
					} else if (msg.startsWith("WIN")) {
						int winner = Integer.parseInt(msg.split(":")[1]);
						JOptionPane.showMessageDialog(this, winner == playerId ? "You win!" : "You lose.");
						askReplay();
					} else if (msg.equals("DRAW")) {
						JOptionPane.showMessageDialog(this, "Game is a draw.");
						askReplay();
					}
				}
			} catch (IOException e) {
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
		if (res == JOptionPane.YES_OPTION) {
			System.exit(0); // for now just close; we can implement replay logic later
		} else {
			out.println("QUIT");
			System.exit(0);
		}
	}
}
