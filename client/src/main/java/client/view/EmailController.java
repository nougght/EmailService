package client.view;

import client.viewModel.EmailViewModel;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import  javafx.scene.control.ListView;
import javafx.scene.control.Button;



public class EmailController {
    private EmailViewModel viewModel;

    public void setViewModel(EmailViewModel vm){
        viewModel = vm;

        subjectLabel.textProperty().bind(viewModel.getSubject());
        senderLabel.textProperty().bind(viewModel.getSender());
        receiverLabel.textProperty().bind(viewModel.getReceiver());
        bodyLabel.textProperty().bind(viewModel.getBody());


    }

    @FXML
    private Label subjectLabel;

    @FXML
    private Label senderLabel;

    @FXML
    private Label receiverLabel;

    @FXML
    private Label bodyLabel;


    @FXML
    private Button replyButton;

    @FXML
    private Button forwardButton;

    public void handleReplyClick(ActionEvent actionEvent){
        viewModel.onReplyClicked();
    }

    public void handleForwardClick(ActionEvent actionEvent){
        viewModel.onForwardClicked();
    }


}
