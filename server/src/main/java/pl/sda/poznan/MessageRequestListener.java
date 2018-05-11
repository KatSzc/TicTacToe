package pl.sda.poznan;

import pl.sda.poznan.model.Game;
import pl.sda.poznan.model.GameStatus;

public class MessageRequestListener implements RequestListener {

  private Game game;

  public MessageRequestListener(Game game) {
    this.game = game;
  }

  @Override
  public Message onMessageReceived(Message request) {
    synchronized (game) {
      switch (request.getHeader()) {
        case MessageHeaders.CONNECT: {
          if (!game.isFirstPlayerConnected()) {
            game.setFirstPlayerConnected(true);
            return Message.builder()
                .header(MessageHeaders.WAITING_FOR_SECOND_CLIENT)
                .build();
          } else {
            game.setGameStarted(true);
            game.notify();
            return Message.builder()
                .header(MessageHeaders.STARTING_GAME)
                .playerSign(PlayerConstants.O_PLAYER_SIGN)
                .data(PlayerConstants.X_PLAYER_SIGN.toString())
                .build();
          }
        }
        case MessageHeaders.NOTIFY_ON_SECOND_CLIENT: {
          try {
            while (!game.isGameStarted()) {
              game.wait();
            }
          } catch (InterruptedException e) {
          }
          return Message.builder()
              .header(MessageHeaders.STARTING_GAME)
              .playerSign(PlayerConstants.X_PLAYER_SIGN)
              .data(PlayerConstants.X_PLAYER_SIGN.toString())
              .build();
        }
        case MessageHeaders.NOTIFY_ON_OPPONENT_MOVE: {
          try {
            game.wait();
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          if (game.isGameEnded()) {
            return Message.builder()
                .header(MessageHeaders.GAME_LOST)
                .build();
          }
          return Message.builder()
              .header(MessageHeaders.OPPONENT_MOVED)
              .data(Integer.toString(game.getLastPosition()))
              .build();
        }
        case MessageHeaders.MOVE: {
          Message message = handleGameStatus(request);
          game.notify();
          return message;
        }
      }
      return Message.builder()
          .header("Hello")
          .data("World")
          .build();
    }
  }

  private Message handleGameStatus(Message request) {
    GameStatus gameStatus = game.makeMove(request.getData(), request.getPlayerSign());
    switch (gameStatus){
      case CORRECT_MOVE:
        return Message.builder()
            .header(MessageHeaders.CORRECT_MOVE)
            .build();
      case WINNER:
        return Message.builder()
            .header(MessageHeaders.WINNER)
            .build();
    }
    throw new RuntimeException("Bad game status");
  }
}
