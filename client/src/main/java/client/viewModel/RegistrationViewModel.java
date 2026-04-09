package client.viewModel;

import client.network.request.RegistrationRequest;
import client.service.AuthService;
import client.service.SessionService;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RegistrationViewModel {
    final private AuthService authService;
    final private SessionService sessionService;

    public RegistrationViewModel(AuthService authService, SessionService sessionService) {
        this.authService = authService;
        this.sessionService = sessionService;

    }

    private StringProperty username = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private StringProperty warning = new SimpleStringProperty();

    private ObjectProperty<Object> onToLogin = new SimpleObjectProperty<>();
    private ObjectProperty<Object> onRegistrated = new SimpleObjectProperty<>();

    public StringProperty getUsername() {
        return username;
    }

    public StringProperty getPassword() {
        return password;
    }

    public StringProperty getWarning() {
        return warning;
    }

    public ObjectProperty<Object> getOnToLogin() {
        return onToLogin;
    }

    public ObjectProperty<Object> getOnRegistered() {
        return onRegistrated;
    }

    public void onRegisterClicked() {
        authService.register(username.get(), password.get()).thenAccept(s -> {
            Platform.runLater(()  -> {
                switch(s) {
                    case "success":
                        onRegistrated.set(new Object());
                        break;
                    case "duplicate":
                        warning.set("Имя пользователя занято");
                        break;
                }
            });
        });
    }

    public void onToLoginClicked() {
        onToLogin.set(new Object());
    }
}
