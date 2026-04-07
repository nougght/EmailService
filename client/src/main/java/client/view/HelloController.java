package client.view;

import client.model.Email;
import client.viewModel.EmailViewModel;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class HelloController {

    private EmailViewModel viewModel;

    public void setViewModel(EmailViewModel viewModel) {
        this.viewModel = viewModel;
        userLabel.textProperty().bind(Bindings.selectString(viewModel.getCurrentUser(), "username"));
        emailsList.setItems(viewModel.getEmails());

        emailsList.setCellFactory(lv -> new ListCell<Email>(){
            private final VBox root = new VBox();
            private final HBox row = new HBox();
            private final Label subject = new Label();
            private final Label from = new Label();
            private final Label to = new Label();

            {
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                row.getChildren().addAll(from, spacer, to);
                subject.setFont(new Font(15));
                from.setFont(new Font(12));
                to.setFont(new Font(12));
                root.getChildren().addAll(subject, row);
                root.setAlignment(Pos.CENTER);
                root.setPadding(new Insets(3, 0, 3, 0));
                VBox.setMargin(root, new Insets(13, 10, 3, 0));

            }

            @Override
            protected  void updateItem(Email email, boolean empty)
            {
                super.updateItem(email, empty);


                if (empty || email == null){
                    setGraphic(null);
                }
                else{
                    subject.setText(email.getSubject());
                    from.setText(email.getSender().getUsername());
                    to.setText(email.getReceiver().getUsername());
                    setGraphic(root);
                }
            }
        });
    }

    @FXML
    private Label userLabel;

    @FXML
    private ListView emailsList;



    @FXML
    private void initialize(){
    }
//    @FXML
//    protected void onHelloButtonClick() {
//        welcomeText.setText("Welcome to JavaFX Application!");
//    }

    public void handleClick(ActionEvent actionEvent) {
        viewModel.onRefreshClicked();

    }
}
