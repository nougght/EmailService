package client.view;

import client.viewModel.LoginViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    private LoginViewModel viewModel;

    public void setViewModel(LoginViewModel viewModel)
    {
        this.viewModel = viewModel;

        usernameInput.textProperty().bindBidirectional(viewModel.getUsername());
        passwordInput.textProperty().bindBidirectional(viewModel.getPassword());
        warningLabel.textProperty().bind(viewModel.getWarning());
    }

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
        viewModel.onLoginClicked();
    }
    public void handleToRegistrationClick(ActionEvent actionEvent)
    {
        viewModel.onToRegistrationClicked();
    }

}
