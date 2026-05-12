package client.view;

import client.viewModel.EmailViewModel;
import common.dto.EmailRecipientDTO;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

public class EmailController {
    private EmailViewModel viewModel;

    private final ListChangeListener<EmailRecipientDTO> recipientsListener = c -> rebuildRecipients();

    public void setViewModel(EmailViewModel vm) {
        if (viewModel != null && viewModel.getRecipients() != null) {
            viewModel.getRecipients().removeListener(recipientsListener);
        }
        viewModel = vm;

        subjectLabel.textProperty().bind(viewModel.getSubject());
        senderLabel.textProperty().bind(viewModel.getSender());
        bodyLabel.textProperty().bind(viewModel.getBody());

        viewModel.getRecipients().addListener(recipientsListener);
        rebuildRecipients();
    }

    private void rebuildRecipients() {
        recipientsFlow.getChildren().clear();
        if (viewModel == null) {
            return;
        }
        for (EmailRecipientDTO r : viewModel.getRecipients()) {
            Label chip = new Label(r.getUsername());
            chip.getStyleClass().add("recipient-chip");
            chip.setMaxWidth(Region.USE_PREF_SIZE);
            recipientsFlow.getChildren().add(chip);
        }
    }

    @FXML
    private void initialize() {
        recipientsFlow.prefWrapLengthProperty().bind(recipientsScroll.widthProperty().subtract(16));
    }

    @FXML
    private Label subjectLabel;

    @FXML
    private Label senderLabel;

    @FXML
    private ScrollPane recipientsScroll;

    @FXML
    private FlowPane recipientsFlow;

    @FXML
    private Label bodyLabel;

    @FXML
    private Button replyButton;

    @FXML
    private Button forwardButton;

    public void handleReplyClick(ActionEvent actionEvent) {
        viewModel.onReplyClicked();
    }

    public void handleForwardClick(ActionEvent actionEvent) {
        viewModel.onForwardClicked();
    }

}
