package client.view;

import client.viewModel.RegistrationViewModel;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;


public class RegistrationController {

    private RegistrationViewModel viewModel;

    public void setViewModel(RegistrationViewModel viewModel) {
        this.viewModel = viewModel;

        usernameInput.textProperty().bindBidirectional(viewModel.getUsername());
        passwordInput.textProperty().bindBidirectional(viewModel.getPassword());

    }

    @FXML
    private Label registrationLabel;

    @FXML
    private TextField usernameInput;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private Label warningLabel;

    @FXML
    private Button registrationButton;

    @FXML
    private Button toLoginButton;


    public void handleRegistrationClick(ActionEvent actionEvent) {
        viewModel.onRegisterClicked();
    }

    public void handleToLoginClick(ActionEvent actionEvent) {
        viewModel.onToLoginClicked();
    }
}
