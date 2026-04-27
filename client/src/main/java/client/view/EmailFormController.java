package client.view;

import client.viewModel.EmailFormViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;

public class EmailFormController {
    private EmailFormViewModel viewModel;

    public void setViewModel(EmailFormViewModel vm){
        viewModel = vm;

        receiverField.textProperty().bindBidirectional(viewModel.getReceiver());
        subjectField.textProperty().bindBidirectional(viewModel.getSubject());
        bodyArea.textProperty().bindBidirectional(viewModel.getBody());
        warningLabel.textProperty().bind(viewModel.getWarning());
        warningLabel.managedProperty().bind(viewModel.getIsWarningVisible());

        recipientsList.setItems(viewModel.getRecipients());

        recipientsList.setCellFactory(lv -> new ListCell<String>() {
            private final HBox row = new HBox();
            private final Label username = new Label();
            private final Button removeButton = new Button("x");
            {
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                row.getChildren().addAll(username, spacer, removeButton);
                username.setFont(new Font(15));
                row.setPadding(new Insets(5, 5, 5, 5));

            }

            @Override
            protected void updateItem(String username, boolean empty){
                super.updateItem(username, empty);
                if (empty || username == null) {
                    setGraphic(null);
                } else {
                    this.username.setText(username);
                    setGraphic(row);
                    removeButton.setOnAction(_ -> {
                        Platform.runLater(() -> getListView().getItems().remove(getItem()));

                    });
                }
            }
        });
    }

    @FXML
    private ListView recipientsList;
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

    public void handleAddRecipients(ActionEvent actionEvent) {
        recipientsList.getItems().add(receiverField.getText());
    }

}
