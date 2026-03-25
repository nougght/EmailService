package client.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    public boolean isWarningVisible = false;

    @FXML
    private Label loginLabel;

    @FXML
    private TextField usernameInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private Label warningLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Button toRegistrationButton;

    public void handleLoginClick(ActionEvent actionEvent) {

    }
    public void handleToRegistrationClick(ActionEvent actionEvent)
    {
        
    }

}
