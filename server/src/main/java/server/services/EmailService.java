package server.services;

import server.model.Email;
import server.repositories.EmailRepository;

import java.util.ArrayList;
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
}
