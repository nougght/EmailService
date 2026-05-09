package client.view;

import client.model.Email;
import client.service.NavigationService;
import client.viewModel.MainViewModel;
import common.dto.Draft;
import common.dto.EmailItem;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

import java.util.Map;

public class MainController {

    private MainViewModel viewModel;

    public void setViewModel(MainViewModel viewModel) {
        this.viewModel = viewModel;
        userLabel.textProperty().bind(Bindings.selectString(viewModel.getCurrentUser(), "username"));
        emailsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal instanceof Email email) {
                viewModel.onEmailClicked((email).getEmailId());
            } else if (newVal instanceof Draft draft) {
                viewModel.onDraftClicked(draft);
            }
        });
        try {
            emailsList.setCellFactory(lv -> new ListCell<EmailItem>() {
                private final VBox root = new VBox();
                private final HBox row = new HBox();
                private final Label subject = new Label();
                private final Label from = new Label();
                private final Label to = new Label();
                private final String readStyle = "-fx-text-fill:gray";
                private final String newStyle = "-fx-text-fill:blue";

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
                protected void updateItem(EmailItem item, boolean empty) {
                    System.out.println("Update email item:" + item);
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        if (item instanceof Email email) {
                            subject.setText(email.getSubject());
                            from.setText(email.getSender().getUsername());
                            to.setText(email.getRecipients().getFirst().getUsername());


                            if (email.isRead()) {
                                root.setStyle(readStyle);
                            } else {
                                root.setStyle(newStyle);
                            }
                        } else if (item instanceof Draft draft) {
                            subject.setText(draft.getSubject());
                            from.setText("Черновик");
                            var recipients = draft.getRecipients();
                            to.setText(recipients.isEmpty() ? "" : recipients.getFirst());

                        }
                        setGraphic(root);
                    }
                }
            });

            var root = tree.getRoot();
            var foldersRoot = new TreeItem<>("Папки");
            foldersRoot.setExpanded(true);
            var tagsRoot = new TreeItem<>("Теги");
            root.getChildren().addAll(foldersRoot, tagsRoot);
            for (var item : viewModel.getFolderNames().entrySet()) {
                foldersRoot.getChildren().add(new TreeItem<String>(item.getKey()));
            }

            tree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {

                @Override
                public void changed(ObservableValue<? extends TreeItem<String>> observableValue,
                                    TreeItem<String> oldItem, TreeItem<String> newItem) {
                    var item = newItem;
                    var t = item.getValue();
                    emailsList.setItems((FilteredList<EmailItem>) viewModel.getFolderEmails(viewModel.getFolderNames().get(t)));
                }
            });

            tree.setCellFactory(tv -> new TreeCell<String>() {
                private Label name = new Label();

                {
                    name.setFont(new Font(15));
                }

                @Override
                protected void updateItem(String item, boolean b) {
                    super.updateItem(item, b);
                    name.setText(item);
                    setGraphic(name);
                }
            });
            tree.getSelectionModel().select(2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private BorderPane borderPane;

    @FXML
    private SplitPane splitPane;

    @FXML
    private Label userLabel;

    @FXML
    private TreeView<String> tree;

    @FXML
    private ListView<EmailItem> emailsList;

    public BorderPane getBorderPane() {
        return borderPane;
    }

    public SplitPane getSplitPane() {
        return splitPane;
    }

    @FXML
    private void initialize() {
    }

    public void handleClick(ActionEvent actionEvent) {
        viewModel.onRefreshClicked();
    }

    public void handleLogoutClick(ActionEvent actionEvent) {
        viewModel.onLogoutClicked();
    }

    public void handleNewEmailClick(ActionEvent actionEvent) {
        viewModel.onNewEmailClicked();
    }
}
