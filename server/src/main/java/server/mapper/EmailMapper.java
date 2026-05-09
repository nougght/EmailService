package server.mapper;

import common.dto.EmailDTO;
import server.mapper.EmailRecipientMapper;
import server.model.Email;

public class EmailMapper {

    public static EmailDTO toDTO(Email email) {
        if (email == null) return null;
        var dto = new EmailDTO(
                email.getEmailId(),
                email.getSenderId().orElse(null),
                email.getRecipients().stream().map(EmailRecipientMapper::toDto).toList(),
                email.getSubject(),
                email.getBody(),
                email.getSentAt(),
                null,
                false
        );
        email.getDetails().ifPresent(d -> {
            dto.setFolder(d.getFolder());
            dto.setRead(d.isRead());
        });
        return dto;
    }

    public static Email fromDTO(EmailDTO dto) {
        return new Email(
                dto.getEmailId(),
                dto.getSenderId(),
                dto.getSenderUsername(),
                dto.getSubject(),
                dto.getBody(),
                dto.getSentAt(),
                null,
                dto.getRecipients().stream().map(EmailRecipientMapper::fromDto).toList(),
                null
        );
    }
}
