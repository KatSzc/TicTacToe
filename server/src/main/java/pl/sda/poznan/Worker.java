package pl.sda.poznan;

import java.io.IOException;
import java.net.Socket;

public class Worker {

  private Transmission transmission;
  private RequestListener requestListener;

  public Worker(Socket socket) throws IOException {
    this.transmission = new Transmission(socket);
  }

  /**
   * REQ - REP
   */
  public void startCommunication() throws IOException, ClassNotFoundException {
    while (true) {
      Object obj = transmission.readObject();
      if (obj == null) {
        break;
      }
      if (obj instanceof Message) {
        Message response = requestListener.onMessageReceived((Message) obj);
        transmission.sendObject(response);
      }
    }
  }

  public void setRequestListener(RequestListener requestListener) {
    this.requestListener = requestListener;
  }
}
