package server.services;

import common.dto.Draft;
import server.repositories.DraftRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DraftService {
    private DraftRepository repo;

    public DraftService(DraftRepository repo) {
        this.repo = repo;
    }

    public Optional<UUID> addDraft(Draft draft) {
        return repo.addDraft(draft);
    }

    public List<Draft> getDrafts(UUID userId) {
        return repo.getDraftsByUserId(userId);
    }

    public void updateDraft(Draft draft) {
        if (repo.draftExists(draft.getDraftId())) {
            repo.updateDraft(draft);
        }
    }

    public void delete(UUID draftId) {
        repo.delete(draftId);
    }
}
