package client.view;

import client.model.Email;
import client.service.NavigationService;
import client.viewModel.MainViewModel;
import common.dto.Draft;
import common.dto.EmailItem;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.Map;

public class MainController {

    private MainViewModel viewModel;


    public void setViewModel(MainViewModel viewModel) {
        this.viewModel = viewModel;

        // подключаем доступность кнопки удаления к свойству из viewmodel
        deleteButton.disableProperty().bind(viewModel.isDeleteDisabledProperty());

        userLabel.textProperty().bind(Bindings.selectString(viewModel.getCurrentUser(), "username"));
        emailsList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            viewModel.onEmailsListSelected(newVal);
        });

        try {
            emailsList.setCellFactory(lv -> {
                FXMLLoader loader = new FXMLLoader(MainController.class.getResource("/client/email-list-item.fxml"));
                final VBox rowRoot;
                final EmailListItemController rowPresenter;
                try {
                    rowRoot = loader.load();
                    rowPresenter = loader.getController();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                var cell = new ListCell<EmailItem>() {
                    @Override
                    protected void updateItem(EmailItem item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            var user = viewModel.getCurrentUser().get();
                            String me = user != null ? user.getUsername() : null;
                            rowPresenter.apply(item, me);
                            setGraphic(rowRoot);
                        }
                    }
                };


                ContextMenu menu = new ContextMenu();
//                MenuItem reply   = new MenuItem("Ответить");
                MenuItem delete = new MenuItem("Удалить");
                menu.getItems().addAll(delete);

//                reply.setOnAction(e -> System.out.println("Ответ: " + cell.getItem()));
                cell.setOnMouseClicked(e -> {
                    if (cell.getItem() instanceof Email email) {
                        viewModel.onEmailClicked((email).getEmailId());
                    } else if (cell.getItem() instanceof Draft draft) {
                        viewModel.onDraftClicked(draft);
                    }
                });
                cell.setOnContextMenuRequested(e -> {
                    if (!cell.isEmpty()) {
                        menu.show(cell, e.getScreenX(), e.getScreenY());
                        delete.setOnAction(de -> viewModel.onDeleteClicked(cell.getItem()));
                        e.consume();
                    }
                });
                return cell;
            });


            var root = tree.getRoot();
//            var foldersRoot = new TreeItem<>("Папки");
//            foldersRoot.setExpanded(true);
            for (var item : viewModel.getFolderOrder()) {
                root.getChildren().add(new TreeItem<String>(item));
            }
            var tagsRoot = new TreeItem<>("Теги");
            tagsRoot.getChildren().add(new TreeItem<>("Тег 1"));
            root.getChildren().add(tagsRoot);


            tree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {
                @Override
                public void changed(ObservableValue<? extends TreeItem<String>> observableValue,
                                    TreeItem<String> oldItem, TreeItem<String> newItem) {
                    if (newItem == oldItem) {
                        return;
                    }
                    var t = newItem.getValue();
                    var folder = viewModel.getFolderNames().get(t);
                    var oldFolder = "";
                    if (oldItem != null) {
                        oldFolder = viewModel.getFolderNames().get(oldItem.getValue());
                    }
                    viewModel.switchFolder(folder);

                    if (oldItem == null || folder.equals("DRAFTS") || oldFolder.equals("DRAFTS")) {
                        emailsList.setItems((SortedList<EmailItem>) viewModel.getEmailList());
                    }
                }
            });

            tree.setCellFactory(tv -> new TreeCell<String>() {
                private final Label name = new Label();

                @Override
                protected void updateItem(String item, boolean b) {
                    super.updateItem(item, b);
                    name.setText(item);
                    setGraphic(name);
                }
            });

            tree.getSelectionModel().select(0);


            sortChoice.setItems(viewModel.getSortList());
            sortChoice.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

                public void changed(ObservableValue ov, Number value, Number new_value) {
                    viewModel.switchSort(new_value.intValue());
                }
            });
            sortChoice.getSelectionModel().select(0);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void handleDeleteClick(ActionEvent actionEvent) {
        var item = emailsList.getSelectionModel().getSelectedItem();
        var ind = emailsList.getSelectionModel().getSelectedIndex();

        if (item != null) {
            viewModel.onDeleteClicked(item);

//            emailsList.getSelectionModel().select(ind + 1);
        } else {
            System.out.println("delete clicked but selected item == null");
        }
    }



    @FXML
    private BorderPane borderPane;

    @FXML
    private Button deleteButton;

    @FXML
    private SplitPane splitPane;

    @FXML
    private Label userLabel;

    @FXML
    private TreeView<String> tree;

    @FXML
    private ListView<EmailItem> emailsList;

    @FXML
    private Button refreshButton;
    @FXML
    private Button sortButton;

    @FXML
    private ChoiceBox<String> sortChoice;

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

    public void handleSort(ActionEvent actionEvent) {
        ContextMenu menu = new ContextMenu();
        MenuItem ascDate = new MenuItem("Сначала новые");
        MenuItem descDate = new MenuItem("Сначала старые");
        menu.getItems().addAll(ascDate, descDate);

        ascDate.setOnAction(e -> {
            viewModel.sortAscDate();
            menu.hide();
        });
        descDate.setOnAction(e -> {
            viewModel.sortDescDate();
            menu.hide();
        });
        menu.show(sortButton, sortButton.getScaleX(), sortButton.getScaleY());
    }

    public void handleLogoutClick(ActionEvent actionEvent) {
        viewModel.onLogoutClicked();
    }

    public void handleNewEmailClick(ActionEvent actionEvent) {
        viewModel.onNewEmailClicked();
    }
}
