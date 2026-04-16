package client.view;

import client.viewModel.EmailFormViewModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class EmailFormController {
    private EmailFormViewModel viewModel;

    public void setViewModel(EmailFormViewModel vm){
        viewModel = vm;

        receiverField.textProperty().bindBidirectional(viewModel.getReceiver());
        subjectField.textProperty().bindBidirectional(viewModel.getSubject());
        bodyArea.textProperty().bindBidirectional(viewModel.getBody());
        warningLabel.textProperty().bind(viewModel.getWarning());
        warningLabel.managedProperty().bind(viewModel.getIsWarningVisible());

    }

    @FXML
    private TextField receiverField;

    @FXML
    private TextField subjectField;

    @FXML
    private TextArea bodyArea;

    @FXML
    private Label warningLabel;

    public boolean isWarningVisible() {
        return viewModel.getWarning().get() != null;
    }

    public void handleSendClick(ActionEvent actionEvent){
        viewModel.onSendClicked();
    }



}
