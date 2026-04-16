package server.services;

import server.model.Email;
import server.repositories.EmailRepository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class EmailService {
    final private EmailRepository emailRepo;
    public EmailService(EmailRepository emailRepo)
    {
        this.emailRepo = emailRepo;
    }


    public ArrayList<Email> getUserEmails(UUID userId)
    {
        return emailRepo.getUserEmails(userId);
    }


    public Optional<Email> addEmail(Email email) {
        var optional = emailRepo.addEmail(email);
        if (optional.isPresent()) {
            UUID emailId = optional.get();
            return emailRepo.getEmail(emailId);
        }
        return Optional.empty();
    }

    public Optional<Email> getEmail(UUID emailId) {
        return emailRepo.getEmail(emailId);
    }
}
