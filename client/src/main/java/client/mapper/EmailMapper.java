package client.mapper;

import client.model.Email;
import common.dto.EmailDTO;

public class EmailMapper {

    public static Email fromDTO(EmailDTO dto) {
        return new Email(
                dto.getEmailId(),
                dto.getSenderId(),
                dto.getSenderUsername(),
                dto.getSubject(),
                dto.getBody(),
                dto.getSentAt(),
                null,
                dto.getRecipients(),
                dto.getFolder(),
                dto.isRead()
        );
    }
}

