package no.digipost.api.representations;

import no.digipost.api.PMode;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.w3.xmldsig.Reference;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

public class EbmsApplikasjonsKvittering extends EbmsOutgoingMessage implements KanBekreftesSomBehandletKvittering {

    public final StandardBusinessDocument sbd;
    public final List<Reference> references = new ArrayList<Reference>();
    public final InputStream sbdStream;
    public EbmsAktoer avsender = null;

    private EbmsApplikasjonsKvittering(final EbmsAktoer avsender, final String mpcId, final EbmsAktoer mottaker, final Prioritet prioritet,
                                       final String messageId, final PMode.Action action, final String refToMessageId, final StandardBusinessDocument sbd, final InputStream sbdStream) {
        super(mottaker, messageId, refToMessageId, action, prioritet, mpcId);
        this.sbd = sbd;
        this.avsender = avsender;
        this.sbdStream = sbdStream;
    }

    public static Builder create(final EbmsAktoer avsender, final EbmsAktoer mottaker, final StandardBusinessDocument sbd) {
        return new Builder(avsender, mottaker, sbd);
    }


    public SimpleStandardBusinessDocument getStandardBusinessDocument() {
        return new SimpleStandardBusinessDocument(sbd);
    }

    public SimpleStandardBusinessDocument.SimpleKvittering getKvittering() {
        return new SimpleStandardBusinessDocument(sbd).getKvittering();
    }

    @Override
    public String getMeldingsId() {
        return messageId;
    }

    @Override
    public KvitteringsReferanse getReferanseTilMeldingSomKvitteres() {
        Reference reference = references.get(0);
        return KvitteringsReferanse.builder(reference).build();
    }

    public static class Builder {

        private final EbmsAktoer avsender;
        private final EbmsAktoer mottaker;
        private final StandardBusinessDocument sbd;
        private Prioritet prioritet = Prioritet.NORMAL;
        private String messageId = null;
        private String refToMessageId = null;
        private Collection<Reference> references = new ArrayList<Reference>();
        private InputStream sbdStream = null;
        private String mpcId;
        private PMode.Action action = PMode.Action.KVITTERING;

        public Builder(final EbmsAktoer avsender, final EbmsAktoer mottaker, final StandardBusinessDocument sbd) {
            this.mottaker = mottaker;
            this.avsender = avsender;
            this.sbd = sbd;
        }

        public Builder withMessageId(final String messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder withPrioritet(final Prioritet prioritet) {
            this.prioritet = prioritet;
            return this;
        }

        public Builder withAction(final PMode.Action action) {
            this.action = action;
            return this;
        }

        public Builder withRefToMessageId(final String refToMessageId) {
            this.refToMessageId = refToMessageId;
            return this;
        }

        public Builder withReferences(final Collection<Reference> incomingReferences) {
            references = incomingReferences;
            return this;
        }

        public Builder withMpcId(final String mpcId) {
            this.mpcId = mpcId;
            return this;
        }

        public Builder withSbdStream(final InputStream sbdStream) {
            this.sbdStream = sbdStream;
            return this;
        }


        public EbmsApplikasjonsKvittering build() {
            String id = hasText(messageId) ? messageId : newId();
            EbmsApplikasjonsKvittering kvittering = new EbmsApplikasjonsKvittering(avsender, mpcId, mottaker, prioritet, id, action, refToMessageId, sbd, sbdStream);
            kvittering.references.addAll(references);
            return kvittering;
        }
    }
}
