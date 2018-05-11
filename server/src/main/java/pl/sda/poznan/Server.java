package pl.sda.poznan;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.sda.poznan.model.Game;

public class Server {

  private final Logger logger = Logger.getLogger(getClass().getName());

  public static final int DEFAULT_PORT = 5000;
  private static final String DEFAULT_HOST_NAME = "localhost";
  private ServerSocket serverSocket;
  private Game game = new Game();

  /**
   * Creates server with default port = 5000 and listens on localhost
   */
  public Server() throws IOException {
    logger.log(Level.INFO,
        String.format("Creating socket on address %s on port %d", DEFAULT_HOST_NAME, DEFAULT_PORT));
    this.serverSocket = new ServerSocket(DEFAULT_PORT, 10,
        InetAddress.getByName(DEFAULT_HOST_NAME));
  }

  /**
   * Creates new server
   *
   * @param address - server address
   * @param port - server port
   * @param maxConnection - max clients number accepted by server
   */
  public Server(String address, int port, int maxConnection) throws IOException {
    this.serverSocket = new ServerSocket(port, maxConnection, InetAddress.getByName(address));
  }

  public void waitForConnection() throws IOException {
    logger.info(
        String.format("pl.sda.poznan.Server is listening on port %d", serverSocket.getLocalPort()));
    Socket socket = null;
    while ((socket = serverSocket.accept()) != null) {
      talkWithClient(socket);
    }
  }

  private void talkWithClient(Socket socket) {
    // commucation with client
    logger.info("Client connected... Talking with client");
    Thread clientThread = new Thread(() -> {
    try {
      Worker worker = new Worker(socket);
      worker.setRequestListener(new MessageRequestListener(game));
      worker.startCommunication();
    } catch (IOException | ClassNotFoundException e) {
      logger.info("Error on client thread" + e.getMessage());
    }
  });
    clientThread.start();
  }

  public void close() throws IOException {
    this.serverSocket.close();
  }
}
