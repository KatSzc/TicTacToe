package pl.sda.poznan;

import java.io.IOException;

public class TicTacToe {

  private Server server;

  public void startServer() {
    try {
      this.server = new Server();
      this.server.waitForConnection();
    } catch (IOException e) {
      stopServer();
    }
  }

  private void stopServer() {
    if (server != null) {
      try {
        server.close();
      } catch (IOException e) {
        //todo : log this exception
        e.printStackTrace();
      }
    }
  }


}
