package pl.sda.poznan.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import pl.sda.poznan.viewmodel.ConnectionDialogViewModel;

public class ConnectionDialogController {
  @FXML
  public TextField serverAddressTextField;

  @FXML
  public TextField playerNameTextField;

  public ConnectionDialogViewModel getConnectionDetails(){
    return new ConnectionDialogViewModel(serverAddressTextField.getText(),
            playerNameTextField.getText());
  }

}
