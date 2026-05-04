package client.view;

import client.viewModel.EmailViewModel;
import common.dto.EmailRecipientDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import  javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;


public class EmailController {
    private EmailViewModel viewModel;

    public void setViewModel(EmailViewModel vm){
        viewModel = vm;

        subjectLabel.textProperty().bind(viewModel.getSubject());
        senderLabel.textProperty().bind(viewModel.getSender());
        bodyLabel.textProperty().bind(viewModel.getBody());
        recipientsList.setItems(viewModel.getRecipients());

        recipientsList.setCellFactory(lv -> new ListCell<EmailRecipientDTO>() {
            private final HBox row = new HBox();
            private final Label username = new Label();
            {
                row.getChildren().addAll(username);
                username.setFont(new Font(15));
                row.setPadding(new Insets(5, 5, 5, 5));
                row.setStyle("""
                    -fx-border-color: black;
                    -fx-border-width: 2;
                """);
            }

            @Override
            protected void updateItem(EmailRecipientDTO recipient, boolean empty){
                super.updateItem(recipient, empty);
                if (empty || recipient == null) {
                    setGraphic(null);
                } else {
                    this.username.setText(recipient.getUsername());
                    setGraphic(row);
                }
            }
        });

    }

    @FXML
    private Label subjectLabel;

    @FXML
    private Label senderLabel;

    @FXML
    private ListView recipientsList;

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
