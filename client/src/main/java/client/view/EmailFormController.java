package client.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class EmailFormController {
//    private EmailFormViewModel viewModel;
//
//    public void setViewModel(EmailFormViewModel vm){
//        viewModel = vm;
//
////        receiverField.textProperty().bind(viewModel.getSubject());
////        senderLabel.textProperty().bind(viewModel.getSender());
////        receiverLabel.textProperty().bind(viewModel.getReceiver());
////        bodyLabel.textProperty().bind(viewModel.getBody());
//
//
//
//    }

    @FXML
    private TextField receiverField;

    @FXML
    private TextField subjectField;

    @FXML
    private TextArea bodyArea;



    public void handleSendClick(ActionEvent actionEvent){

//        viewModel.onSendClicked();
    }



}
