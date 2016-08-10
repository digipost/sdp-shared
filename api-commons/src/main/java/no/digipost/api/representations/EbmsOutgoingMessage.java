package no.digipost.api.representations;

import no.digipost.api.PMode;
import org.joda.time.DateTime;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo;

public class EbmsOutgoingMessage extends EbmsMessage {

    public final Prioritet prioritet;
    public final PMode.Action action;
    public final String mpcId;
    protected EbmsAktoer ebmsMottaker;
    public EbmsOutgoingMessage(final EbmsAktoer ebmsMottaker, final String messageId, final String refToMessageId,
                               PMode.Action action, final Prioritet prioritet, final String mpcId) {
        super(messageId, refToMessageId);
        this.ebmsMottaker = ebmsMottaker;
        this.action = action;
        this.mpcId = mpcId;
        this.prioritet = prioritet != null ? prioritet : Prioritet.NORMAL;
    }

    public EbmsAktoer getEbmsMottaker() {
        return ebmsMottaker;
    }

    public MessageInfo createMessageInfo() {
        MessageInfo messageInfo = new MessageInfo()
                .withMessageId(messageId)
                .withRefToMessageId(refToMessageId)
                .withTimestamp(DateTime.now());
        return messageInfo;
    }

    public enum Prioritet {
        NORMAL("normal"),
        PRIORITERT("prioritert");
        private final String value;

        Prioritet(final String value) {
            this.value = value;
        }

        public static Prioritet from(final String val) {
            for (Prioritet p : Prioritet.values()) {
                if (p.value.equals(val)) {
                    return p;
                }
            }
            throw new IllegalArgumentException("Invalid Prioritet: " + val);
        }

        public String value() {
            return value;
        }
    }

}
