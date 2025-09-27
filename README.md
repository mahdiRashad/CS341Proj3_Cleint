A JavaFX client that connects to a Java server so players can play Connect Four online. Players see each other’s moves in real time, chat in an in-app messenger, and get automatic win/loss notifications at game end. The server tracks wins and losses across games.

Features

1-Online multiplayer: Real-time board updates via sockets.

2-Single-player mode: Play against the computer (optional).

3-In-app chat: Message your opponent in a dedicated chat scene.

4-Match results: Automatic winner/loser detection and alerts.

5-Stats tracking: Server maintains win/loss counts per user.

6-Clean UI: Scene-based JavaFX interface (Login → Game → Messenger).

My Contributions

Designed the JavaFX UI flow (login → mode select → board → messenger).

Implemented network protocol, event handling, and thread-safe updates for moves/chat.

Added result detection (horizontal/vertical/diagonal) and win/loss reporting to the server.

Tech Stack

Language: Java

UI: JavaFX

Networking: Java Sockets (multithreaded server with per-client handlers)

Data model: 2D array board representation, server-side game sessions

Wireframe of the project:

<img width="801" height="879" alt="Screenshot 2025-09-06 at 1 34 36 PM" src="https://github.com/user-attachments/assets/a8e7d2c4-7f06-4d42-8df7-458e0da82d2a" />

<img width="777" height="870" alt="Screenshot 2025-09-06 at 1 35 43 PM" src="https://github.com/user-attachments/assets/57d64354-4bfe-412b-bb9e-440ec27c9e60" />

Scene 1 – Login: Username/password; choose Single or Multiplayer; Quit.

Scene 2 – Game: 7×6 grid; place tokens, Play Again, Quit, Records, Messenger.

Scene 3 – Messenger: Chat with opponent; return to Game at any time.

Scene 3 is the game scene where the player can play in the 2d array and can choose where to put the token. They can reset the game from the play again button, quit the game from the quit button, view the record of the game from the record button, and go to the messaging scene to chat from the messenger button. From the messaging scene they can go back to the game scene.

Actual Project:

<img width="1015" height="428" alt="Screenshot 2025-08-29 at 10 28 28 PM" src="https://github.com/user-attachments/assets/339d8225-1ce9-4448-9ef8-aa23d5a52a21" />

<img width="1506" height="826" alt="Screenshot 2025-08-29 at 10 29 23 PM" src="https://github.com/user-attachments/assets/6ba131f7-b62b-4277-8d07-a3bf06c59a2f" />
