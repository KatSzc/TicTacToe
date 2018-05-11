package pl.sda.poznan.model;

public class Game {

  private GameBoard gameBoard = new GameBoard();
  private boolean isGameStarted = false;
  private boolean isGameEnded = false;
  private boolean isFirstPlayerConnected = false;

  private int lastPosition;
  public Game() {
  }

  public GameStatus makeMove(String move, char sign) {
    int position;
    try {
      position = Integer.parseInt(move);
      this.lastPosition = position;
    } catch (NumberFormatException ex) {
      return GameStatus.INVALID_INPUT;
    }

    if (!gameBoard.isValidMove(position)) {
      return GameStatus.BAD_MOVE;
    }
    boolean isWinner = gameBoard.add(move, sign);
    if (isWinner) {
      isGameEnded = true;
      return GameStatus.WINNER;
    } else {
      return GameStatus.CORRECT_MOVE;
    }
  }

  public boolean isGameStarted() {
    return isGameStarted;
  }

  public void setGameStarted(boolean gameStarted) {
    this.isGameStarted = gameStarted;
  }

  public boolean isGameEnded() {
    return isGameEnded;
  }

  public void setGameEnded(boolean gameEnded) {
    isGameEnded = gameEnded;
  }

  public boolean isFirstPlayerConnected() {
    return isFirstPlayerConnected;
  }

  public void setFirstPlayerConnected(boolean firstPlayerConnected) {
    isFirstPlayerConnected = firstPlayerConnected;
  }

  public int getLastPosition() {
    return lastPosition;
  }
}
