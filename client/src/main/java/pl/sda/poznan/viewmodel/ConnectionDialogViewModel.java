package pl.sda.poznan.viewmodel;

public class ConnectionDialogViewModel {

  private String serverAddress;
  private String playerName;

  public ConnectionDialogViewModel(String serverAddress, String playerName) {
    this.serverAddress = serverAddress;
    this.playerName = playerName;
  }

  public String getServerAddress() {
    return serverAddress;
  }

  public void setServerAddress(String serverAddress) {
    this.serverAddress = serverAddress;
  }

  public String getPlayerName() {
    return playerName;
  }

  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }
}
