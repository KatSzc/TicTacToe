package pl.sda.poznan.controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import pl.sda.poznan.Message;
import pl.sda.poznan.MessageHeaders;
import pl.sda.poznan.PlayerConstants;
import pl.sda.poznan.Transmission;
import pl.sda.poznan.util.ResourceLoaderUtils;
import pl.sda.poznan.viewmodel.ConnectionDialogViewModel;

public class MainWindowController {

  private final Logger logger = Logger.getLogger(getClass().getName());
  private Transmission transmission;
  private Character playerSign;

  @FXML
  private BorderPane mainWindowBorderPane;
  @FXML
  private Label logTextArea;

  @FXML
  private GridPane gameBoardGridPane;

  public void handleClick(MouseEvent mouseEvent) {
    Label source = (Label) mouseEvent.getSource();
    source.setText(playerSign.toString());
    try {
      transmission.sendObject(Message.builder()
          .header(MessageHeaders.MOVE)
          .data(source.getId())
          .playerSign(playerSign)
          .build());
      appendToLogLabel("Kolej przeciwnika");
      gameBoardGridPane.setDisable(true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Metoda wykonujaca sie przy klinieciu przycisku polacz do serwera z Menu Game
   */
  public void connectToServerAction(ActionEvent actionEvent) {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(ResourceLoaderUtils.getResource("view/ConnectionDialogWindow.fxml"));

    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Połącz do serwera");
    dialog.setHeaderText("Uzupelnij dane");

    try {
      dialog.getDialogPane().setContent(fxmlLoader.load());
    } catch (IOException e) {
      return;
    }
    dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
    dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

    Optional<ButtonType> optionalType = dialog.showAndWait();
    optionalType.ifPresent(buttonType -> {
      ConnectionDialogController controller = fxmlLoader.getController();
      ConnectionDialogViewModel connectionDetails = controller.getConnectionDetails();
      connectToServer(connectionDetails);
    });
  }

  public void connectToServer(ConnectionDialogViewModel viewModel) {
    Thread clientThread = new Thread(() -> {
      logger.log(Level.INFO, String.format(
          "Trying to connect to server at address %s with username %s",
          viewModel.getServerAddress(),
          viewModel.getPlayerName()));
      String[] address = viewModel.getServerAddress().split(":");
      String host = address[0];
      int port = Integer.parseInt(address[1]);
      // todo: add server address validation - display error dialog if sth is wrong
      try {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port));
        this.transmission = new Transmission(socket);

        transmission.sendObject(Message.builder()
            .header(MessageHeaders.CONNECT)
            .data(viewModel.getPlayerName())
            .build());
        while (true) {
          try {
            Object o = transmission.readObject();
            Message message = (Message) o;
            logger.info(String.format("Received message %s", message.getHeader()));

            switch (message.getHeader()) {
              case MessageHeaders.WAITING_FOR_SECOND_CLIENT: {
                appendToLogLabel("Czekam na drugiego gracza");
                transmission.sendObject(Message.builder()
                    .header(MessageHeaders.NOTIFY_ON_SECOND_CLIENT)
                    .build());
                break;
              }
              case MessageHeaders.STARTING_GAME: {
                playerSign = message.getPlayerSign();
                appendToLogLabel("Gra sie rozpoczyna");
                if (playerSign.equals(message.getData().charAt(0))) {
                  appendToLogLabel("Twoja kolej");
                  gameBoardGridPane.setDisable(false);
                } else {
                  appendToLogLabel("Kolej przeciwnika");
                  transmission.sendObject(Message.builder()
                      .header(MessageHeaders.NOTIFY_ON_OPPONENT_MOVE)
                      .build());
                }
                break;
              }
              case MessageHeaders.CORRECT_MOVE: {
                transmission.sendObject(Message.builder()
                    .header(MessageHeaders.NOTIFY_ON_OPPONENT_MOVE)
                    .build());
                appendToLogLabel("Poprawny ruch. Kolej przeciwnika");
                break;
              }
              case MessageHeaders.OPPONENT_MOVED: {
                Platform.runLater(() -> {
                  appendToLogLabel("Przeciwnik się ruszył. Twoja kolej");
                  gameBoardGridPane.setDisable(false);
                  // znajdz pole na ktore wpisal cos przeciwnik
                  String field = message.getData();
                  Label label = (Label) this.mainWindowBorderPane.getScene().lookup("#" + field);
                  label.setText(Character.toString(playerSign == 'X' ? 'O' : 'X'));
                });
                break;
              }
              case MessageHeaders.WINNER: {
                Platform.runLater(() -> {
                  Alert alert = new Alert(AlertType.INFORMATION);
                  alert.setTitle("Wygrales");
                  alert.setContentText("Gratulacje");
                  alert.showAndWait();
                  this.gameBoardGridPane.setDisable(true);
                  this.gameBoardGridPane.setVisible(false);
                });
                break;
              }
              case MessageHeaders.GAME_LOST: {
                Platform.runLater(() -> {
                  Alert alert = new Alert(AlertType.ERROR);
                  alert.setTitle("Przegrales");
                  alert.setContentText("Wygral przeciwnik");
                  alert.showAndWait();
                  this.gameBoardGridPane.setDisable(true);
                  this.gameBoardGridPane.setVisible(false);
                });
                break;
              }

            }
          } catch (ClassNotFoundException e) {
            e.printStackTrace();
          }

        }
      } catch (IOException e) {
        logger.log(Level.INFO, "Cannot connect to server: " + e.getMessage());
        // todo: wyswietl okno z napisem "Nie udalo sie podlaczyc do serwera"
        // Skorzystaj z obiektu Alert
        Platform.runLater(() -> {
          Alert alert = new Alert(AlertType.ERROR);
          alert.setTitle("Błąd");
          alert.setContentText("Nie udało się połączyć z serwerem");
          alert.showAndWait();
        });
      }
    });
    clientThread.start();
  }

  private void appendToLogLabel(String message) {
    Platform.runLater(() -> this.logTextArea.setText(logTextArea.getText() + message + "\n"));
  }

}
