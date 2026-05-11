package client.service;

import client.model.Email;
import client.model.User;
import client.network.TcpClient;
import client.storage.DataStorage;
import common.dto.Draft;
import javafx.application.Platform;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DraftService {
    final private TcpClient tcpClient;
    final private SessionService sessionService;
    final private DataStorage storage;

    public DraftService(TcpClient tcpClient, SessionService sessionService, DataStorage storage) {
        this.tcpClient = tcpClient;
        this.sessionService = sessionService;
        this.storage = storage;

        sessionService.addListener(s -> {
            //temp: нужно исключить возможность гонки данных, реализовать возможность отмены задачи
            loadUserDrafts();
        });


    }

    public CompletableFuture<Integer> loadUserDrafts() {
        return tcpClient.requestUserDrafts(sessionService.getCurrentUser().get().getUserId()).thenApply(drafts -> {
            Platform.runLater(() -> {
                storage.setAllDrafts(
                        drafts
                );
            });
            return drafts.size();
        });
    }

    public CompletableFuture<Draft> addDraft(Draft draft) {
        draft.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return tcpClient.requestAddDraft(draft).thenApply(draftId ->
        {
            Platform.runLater(() -> {
                draft.setDraftId(draftId);
                storage.addDraft(draft);
            });
            return draft;
        });
    }

    public CompletableFuture<Boolean> updateDraft(Draft draft) {
        draft.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return tcpClient.requestDraftUpdate(draft).thenApply(b ->
        {
            if (b == true) {
                Platform.runLater(() -> {
                    storage.updateDraft(draft);
                });
            }
            return b;
        });
    }

    // TODO: draft deleting without sending
    public void deleteDraftLocal(UUID draftId) {
        storage.deleteDraft(draftId);
    }


    public void deleteDraft(UUID draftId) {
        var user = sessionService.getCurrentUser().getValue();
        if (user == null) {
            return;
        }

        tcpClient.requestDeleteDraft(draftId).thenAccept(b -> {
            if (b == true) {
                Platform.runLater(() -> {
                    storage.deleteDraft(draftId);
                });
            }
        });
    }
}
