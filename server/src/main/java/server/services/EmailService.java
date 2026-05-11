package server.services;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import server.model.Email;
import server.repositories.EmailRepository;

public class EmailService {
    final private EmailRepository emailRepo;
    final private DraftService draftService;

    public EmailService(EmailRepository emailRepo, DraftService draftService) {
        this.emailRepo = emailRepo;
        this.draftService = draftService;
    }

    public ArrayList<Email> getUserEmails(UUID userId) {
        return emailRepo.getUserEmails(userId);
    }

    public Optional<Email> addEmail(Email email, UUID userId, UUID draftId) {
        var optional = emailRepo.addEmail(email);
        if (optional.isPresent()) {
            UUID emailId = optional.get();
            if (draftId != null) {
                draftService.delete(draftId);
            }
            return emailRepo.getEmail(emailId, userId);
        }

        return Optional.empty();
    }

    public Optional<Email> getEmail(UUID emailId, UUID userId) {
        return emailRepo.getEmail(emailId, userId);
    }

    public void deleteUserEmail(UUID userId, UUID emailId) {
        emailRepo.deleteUserEmail(userId, emailId);
    }

}
