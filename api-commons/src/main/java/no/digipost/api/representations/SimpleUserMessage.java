package no.digipost.api.representations;

import no.digipost.api.PMode;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;

import java.time.ZonedDateTime;

public class SimpleUserMessage {

    private final UserMessage message;

    public SimpleUserMessage(final UserMessage message) {
        this.message = message;
    }

    public Organisasjonsnummer getTo() {
        return Organisasjonsnummer.of(message.getPartyInfo().getTo().getPartyIds().get(0).getValue());
    }

    public Organisasjonsnummer getFrom() {
        return Organisasjonsnummer.of(message.getPartyInfo().getFrom().getPartyIds().get(0).getValue());
    }

    public String getMessageId() {
        return message.getMessageInfo().getMessageId();
    }

    public String getRefToMessageId() {
        return message.getMessageInfo().getRefToMessageId();
    }

    public ZonedDateTime getTimestamp() {
        return message.getMessageInfo().getTimestamp();
    }

    public String getAction() {
        return message.getCollaborationInfo().getAction();
    }

    public String getMpc() {
        return message.getMpc();
    }

    public boolean erFlytt() {
        return getAction().equals(PMode.Action.FLYTT.value);
    }
}
