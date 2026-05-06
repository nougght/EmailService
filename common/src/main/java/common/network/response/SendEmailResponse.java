package common.network.response;

import java.util.UUID;

import common.dto.EmailDTO;

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