package client.network.response;


import client.dto.EmailDTO;

import java.util.UUID;

public class SendEmailResponse extends Response {
    private EmailDTO emailDTO;

    public SendEmailResponse() {
        super();
    }

    public SendEmailResponse(UUID requestId, String status, EmailDTO emailDTO) {
        super(requestId, "SendEmail", status);
        this.emailDTO = emailDTO;
    }

    public EmailDTO getEmailDTO() {
        return emailDTO;
    }

    public void setEmailDTO(EmailDTO emailDTO) {
        this.emailDTO = emailDTO;
    }
}