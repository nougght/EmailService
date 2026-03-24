package server.mapper;

import server.dto.EmailDTO;
import server.model.Email;

import java.util.UUID;

public class EmailMapper {

    public static EmailDTO toDTO(Email email) {
        return new EmailDTO(
                email.getEmailId(),
                email.getSenderId(),
                email.getReceiverId(),
                email.getSubject(),
                email.getBody(),
                email.getSentAt()
        );
    }
    public static Email fromDTO(EmailDTO dto) {
        return new Email(
                dto.getEmailId(),
                dto.getSenderId(),
                dto.getReceiverId(),
                dto.getSubject(),
                dto.getBody(),
                dto.getSentAt(),
                null,
                null
        );
    }
}
