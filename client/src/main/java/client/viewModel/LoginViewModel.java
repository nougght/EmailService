package client.viewModel;

import client.service.AuthService;
import client.service.SessionService;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class LoginViewModel {
    final private AuthService authService;
    final private SessionService sessionService;

    public LoginViewModel(AuthService authService, SessionService sessionService) {
        this.authService = authService;
        this.sessionService = sessionService;

    }

    private StringProperty username = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private StringProperty warning = new SimpleStringProperty();

    private ObjectProperty<Object> onToRegistration = new SimpleObjectProperty<>();
    private ObjectProperty<Object> onLoggedIn = new SimpleObjectProperty<>();

    public StringProperty getUsername() {
        return username;
    }

    public StringProperty getPassword() {
        return password;
    }

    public StringProperty getWarning() {
        return warning;
    }

    public ObjectProperty<Object> getOnToRegistration() {
        return onToRegistration;
    }

    public Object getOnLoggedIn() {
        return onLoggedIn.get();
    }

    public void onLoginClicked() {
        authService.login(username.get(), password.get()).thenAccept(s -> {
            Platform.runLater(() -> {
                switch (s) {
                    case "success":
                        onLoggedIn.set(null);
                        break;
                    case "not found":
                        warning.set("Пользователь не найден");
                        break;
                    case "incorrect password":
                        warning.set("Неправильный пароль");
                }
            });
        });
    }

    public void onToRegistrationClicked() {
        onToRegistration.set(null);
    }
}
