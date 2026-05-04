package server.mapper;

import common.dto.EmailRecipientDTO;
import server.model.EmailRecipient;

public class EmailRecipientMapper {
    public static EmailRecipientDTO toDto(EmailRecipient recipient) {
        return new EmailRecipientDTO(recipient.getUserId().orElse(null),
                recipient.getUsername());
    }

    public static EmailRecipient fromDto(EmailRecipientDTO dto) {
        return new EmailRecipient(null, dto.getUserId(), dto.getUsername());
    }
}
