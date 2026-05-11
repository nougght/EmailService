package client.view;

import client.model.Email;
import common.dto.Draft;
import common.dto.EmailItem;
import common.dto.EmailRecipientDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Objects;

/**
 * Разметка строки списка писем; данные задаются через {@link #apply(EmailItem, String)}.
 */
public class EmailListItemController {

    private static final List<String> VARIANT_STYLE_CLASSES = List.of(
            "email-list-item--email",
            "email-list-item--draft",
            "email-list-item--read",
            "email-list-item--unread",
            "email-list-item-folder--inbox",
            "email-list-item-folder--outbox",
            "email-list-item-folder--all",
            "email-list-item-folder--drafts",
            "email-list-item-folder--unknown"
    );

    private static final String EM_DASH = "—";

    @FXML
    private VBox root;

    @FXML
    private Label subjectLabel;

    @FXML
    private Label counterpartyPrefixLabel;

    @FXML
    private Label counterpartyLabel;

    @FXML
    private HBox snippetRow;

    @FXML
    private Label snippetLabel;

    public void apply(EmailItem item, String currentUsername) {
        subjectLabel.setText(item.getSubject() != null ? item.getSubject() : "");

        CounterpartyLine cp = resolveCounterpartyLine(item, currentUsername);
        counterpartyPrefixLabel.setText(cp.prefix());
        counterpartyLabel.setText(cp.value() != null && !cp.value().isBlank() ? cp.value() : EM_DASH);

        String snippet = snippetOf(item.getBody());
        snippetLabel.setText(snippet);
        snippetRow.setManaged(!snippet.isEmpty());
        snippetRow.setVisible(!snippet.isEmpty());

        applyStyleClasses(item);
    }

    private record CounterpartyLine(String prefix, String value) {
    }

    private void applyStyleClasses(EmailItem item) {
        root.getStyleClass().removeAll(VARIANT_STYLE_CLASSES);
        if (item instanceof Draft) {
            root.getStyleClass().addAll("email-list-item--draft", "email-list-item-folder--drafts");
        } else if (item instanceof Email em) {
            root.getStyleClass().add("email-list-item--email");
            root.getStyleClass().add(em.isRead() ? "email-list-item--read" : "email-list-item--unread");
            String folder = em.getFolder();
            if (folder == null || folder.isBlank()) {
                root.getStyleClass().add("email-list-item-folder--unknown");
            } else {
                switch (folder) {
                    case "INBOX" -> root.getStyleClass().add("email-list-item-folder--inbox");
                    case "OUTBOX" -> root.getStyleClass().add("email-list-item-folder--outbox");
                    case "ALL" -> root.getStyleClass().add("email-list-item-folder--all");
                    default -> root.getStyleClass().add("email-list-item-folder--unknown");
                }
            }
        }
    }

    private static CounterpartyLine resolveCounterpartyLine(EmailItem item, String me) {
        if (item instanceof Draft draft) {
            String v = draftCounterpartyValue(draft, me);
            return new CounterpartyLine("Кому:", v);
        }
        if (item instanceof Email email) {
            String senderName = email.getSender() != null ? email.getSender().getUsername() : email.getSenderUsername();
            boolean iAmSender = me != null && me.equals(senderName);
            if (iAmSender) {
                return new CounterpartyLine("Кому:", firstOtherRecipient(email.getRecipients(), me));
            }
            return new CounterpartyLine("От:", senderName != null ? senderName : "");
        }
        return new CounterpartyLine("От:", "");
    }

    private static String firstOtherRecipient(List<EmailRecipientDTO> recipients, String me) {
        if (recipients == null || recipients.isEmpty()) {
            return "";
        }
        for (EmailRecipientDTO r : recipients) {
            String u = r.getUsername();
            if (u != null && !u.isBlank() && !Objects.equals(u, me)) {
                return u;
            }
        }
        return "";
    }

    private static String draftCounterpartyValue(Draft draft, String me) {
        List<String> recipients = draft.getRecipients();
        if (recipients == null || recipients.isEmpty()) {
            return "";
        }
        for (String r : recipients) {
            if (r != null && !r.isBlank() && !Objects.equals(r, me)) {
                return r;
            }
        }
        return "";
    }

    private static String snippetOf(String body) {
        if (body == null || body.isBlank()) {
            return "";
        }
        String oneLine = body.replace('\n', ' ').replace('\r', ' ').replaceAll("\\s+", " ").trim();
        int max = 120;
        if (oneLine.length() <= max) {
            return oneLine;
        }
        return oneLine.substring(0, max).trim() + "…";
    }
}
