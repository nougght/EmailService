package client.network.notification;

import client.network.message.Message;

// сообщение от сервера к клиенту
public abstract class Notification extends Message {
    public Notification() {
        super();
    }
    public Notification(String type) {
        super("NOTIFICATION", type);
    }
}
