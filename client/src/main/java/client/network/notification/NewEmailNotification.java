package client.network.notification;

import client.dto.EmailDTO;

public class NewEmailNotification extends Notification{
    EmailDTO emailDTO;

    public NewEmailNotification() {super("NewEmail");}
    public NewEmailNotification(EmailDTO emailDTO) {
        super("NewEmail");
        this.emailDTO = emailDTO;
    }

    public EmailDTO getEmailDTO() {
        return emailDTO;
    }

    public void setEmailDTO(EmailDTO emailDTO) {
        this.emailDTO = emailDTO;
    }
}
