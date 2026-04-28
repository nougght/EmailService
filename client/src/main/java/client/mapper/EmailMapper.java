package client.mapper;


import client.dto.EmailDTO;
import client.model.Email;

public class EmailMapper {
    public static EmailDTO toDTO(Email email) {
        return new EmailDTO(
                email.getEmailId(),
                email.getSenderId(),
                email.getRecipientIds(),
                email.getSubject(),
                email.getBody(),
                email.getSentAt()
        );
    }

    public static Email fromDTO(EmailDTO dto) {
        return new Email(
                dto.getEmailId(),
                dto.getSenderId(),
                dto.getRecipientIds(),
                dto.getSubject(),
                dto.getBody(),
                dto.getSentAt(),
                null,
                null,
                null
        );
    }
}

