package no.digipost.api.representations;

import no.difi.begrep.sdp.schema_v10.SDPMelding;
import no.digipost.api.PMode;
import no.digipost.xsd.types.DigitalPostformidling;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;

import java.io.InputStream;
import java.time.ZonedDateTime;

public class EbmsForsendelse extends EbmsOutgoingMessage {
    public final String conversationId;
    public final String instanceIdentifier;
    public final StandardBusinessDocument doc;
    public final InputStream sbdStream;
    private final Dokumentpakke dokumentpakke;
    private final EbmsAktoer ebmsMottaker;
    private final EbmsAktoer ebmsAvsender;
    private final Organisasjonsnummer sbdhMottaker;

    private EbmsForsendelse(String messageId, PMode.Action action, EbmsAktoer ebmsMottaker, EbmsAktoer ebmsAvsender, String mpcId, Organisasjonsnummer sbdhMottaker, Prioritet prioritet,
                            String conversationId, String instanceIdentifier, StandardBusinessDocument doc, Dokumentpakke dokumentpakke, InputStream sbdStream) {
        super(ebmsMottaker, messageId, null, action, prioritet, mpcId);
        this.ebmsMottaker = ebmsMottaker;
        this.ebmsAvsender = ebmsAvsender;
        this.sbdhMottaker = sbdhMottaker;
        this.conversationId = conversationId;
        this.instanceIdentifier = instanceIdentifier;
        this.doc = doc;
        this.dokumentpakke = dokumentpakke;
        this.sbdStream = sbdStream;
    }

    public static <P extends SDPMelding & DigitalPostformidling> Builder create(EbmsAktoer avsender, EbmsAktoer mottaker, Organisasjonsnummer sbdhMottaker,
                                                                                P digitalPostformidling, Dokumentpakke dokumentpakke) {
        Builder builder = new Builder();
        builder.avsender = avsender;
        builder.mottaker = mottaker;
        builder.sbdhMottaker = sbdhMottaker;
        builder.digitalPost = digitalPostformidling;
        builder.dokumentpakke = dokumentpakke;
        return builder;
    }

    public static Builder create(EbmsAktoer avsender, EbmsAktoer mottaker, Organisasjonsnummer sbdhMottaker, StandardBusinessDocument sbd, Dokumentpakke dokumentpakke) {
        SimpleStandardBusinessDocument sdoc = new SimpleStandardBusinessDocument(sbd);
        Builder builder = new Builder();
        builder.dokumentpakke = dokumentpakke;
        builder.avsender = avsender;
        builder.mottaker = mottaker;
        builder.sbdhMottaker = sbdhMottaker;
        builder.conversationId = sdoc.getConversationId();
        builder.instanceIdentifier = sdoc.getInstanceIdentifier();
        builder.doc = sbd;
        builder.digitalPost = (SDPMelding) sbd.getAny();
        return builder;
    }

    public static EbmsForsendelse from(EbmsAktoer avsender, EbmsAktoer mottaker, StandardBusinessDocument sbd, Dokumentpakke dokumentpakke) {
        return create(avsender, mottaker, new SimpleStandardBusinessDocument(sbd).getReceiver(), sbd, dokumentpakke).build();
    }

    public static Builder builderFrom(final EbmsAktoer avsender, final EbmsAktoer mottaker, final StandardBusinessDocument sbd, final Dokumentpakke dokumentpakke) {
        return create(avsender, mottaker, new SimpleStandardBusinessDocument(sbd).getReceiver(), sbd, dokumentpakke);
    }

    public Dokumentpakke getDokumentpakke() {
        return dokumentpakke;
    }

    public EbmsAktoer getMottaker() {
        return ebmsMottaker;
    }

    public EbmsAktoer getAvsender() {
        return ebmsAvsender;
    }

    public Organisasjonsnummer getSbdhMottaker() {
        return sbdhMottaker;
    }

    public static class Builder {
        public InputStream sbdStream = null;
        private Dokumentpakke dokumentpakke;
        private Organisasjonsnummer sbdhMottaker;
        private EbmsAktoer mottaker;
        private EbmsAktoer avsender;
        private String conversationId = newId();
        private String instanceIdentifier = newId();
        private ZonedDateTime creationTime = ZonedDateTime.now();
        private StandardBusinessDocument doc;
        private SDPMelding digitalPost;
        private String messageId = newId();
        private Prioritet prioritet = Prioritet.NORMAL;
        private String mpcId = null;
        private PMode.Action action = PMode.Action.FORMIDLE_DIGITAL;

        private Builder() {
        }

        public Builder withMessageId(final String messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder withAction(PMode.Action action) {
            this.action = action;
            return this;
        }

        public Builder withSbdStream(final InputStream sbdStream) {
            this.sbdStream = sbdStream;
            return this;
        }

        public Builder withConversationId(final String conversationId) {
            this.conversationId = conversationId;
            return this;
        }

        public Builder withMpcId(final String mpcId) {
            this.mpcId = mpcId;
            return this;
        }

        public Builder withPrioritet(final Prioritet prioritet) {
            this.prioritet = prioritet;
            return this;
        }

        public EbmsForsendelse build() {
            if (doc == null) {
                doc = StandardBusinessDocumentFactory
                        .create(avsender.orgnr, sbdhMottaker, instanceIdentifier, creationTime, conversationId, digitalPost);
            }
            return new EbmsForsendelse(messageId, action, mottaker, avsender, mpcId, sbdhMottaker, prioritet, conversationId, instanceIdentifier, doc, dokumentpakke, sbdStream);
        }
    }

}
