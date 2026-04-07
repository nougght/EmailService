package client.viewModel;

import client.network.request.RegistrationRequest;
import client.service.AuthService;
import client.service.SessionService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RegistrationViewModel {
    final private AuthService authService;
    final private SessionService sessionService;

    public RegistrationViewModel(AuthService authService, SessionService sessionService)
    {
        this.authService = authService;
        this.sessionService = sessionService;

    }

    private StringProperty username = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();

    private ObjectProperty<Object> onToLogin = new SimpleObjectProperty<>();

    public StringProperty getUsername() {
        return username;
    }

    public StringProperty getPassword() {
        return password;
    }

    public ObjectProperty<Object> getOnToLogin() {
        return onToLogin;
    }

    public void onRegisterClicked() {
        authService.register(username.get(), password.get());
    }

    public void onToLoginClicked()
    {
        onToLogin.set(new Object());
    }
}
