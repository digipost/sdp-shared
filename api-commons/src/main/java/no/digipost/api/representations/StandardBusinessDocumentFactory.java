package no.digipost.api.representations;

import no.difi.begrep.sdp.schema_v10.SDPDigitalPost;
import no.difi.begrep.sdp.schema_v10.SDPFeil;
import no.difi.begrep.sdp.schema_v10.SDPFlyttetDigitalPost;
import no.difi.begrep.sdp.schema_v10.SDPKvittering;
import no.difi.begrep.sdp.schema_v10.SDPMelding;
import org.joda.time.DateTime;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.BusinessScope;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.DocumentIdentification;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.Partner;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.PartnerIdentification;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.Scope;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocumentHeader;

public class StandardBusinessDocumentFactory {

    public static final String STANDARD = "urn:no:difi:sdp:1.0";
    public static final String HEADER_VERSION = "1.0";
    public static final String TYPE_VERSION = "1.0";
    public static final String CONVERSATIONID = "ConversationId";

    public static StandardBusinessDocument create(final Organisasjonsnummer avsender, final Organisasjonsnummer mottaker, final String instanceIdentifier, DateTime creationTime, final String conversationId, final SDPMelding body) {
        return new StandardBusinessDocument()
                .withStandardBusinessDocumentHeader(
                        new StandardBusinessDocumentHeader()
                                .withHeaderVersion(HEADER_VERSION)
                                .withSenders(new Partner().withIdentifier(new PartnerIdentification(avsender.getOrganisasjonsnummerMedLandkode(), Organisasjonsnummer.ISO6523_ACTORID)))
                                .withReceivers(new Partner().withIdentifier(new PartnerIdentification(mottaker.getOrganisasjonsnummerMedLandkode(), Organisasjonsnummer.ISO6523_ACTORID)))
                                .withDocumentIdentification(new DocumentIdentification()
                                        .withStandard(STANDARD)
                                        .withTypeVersion(TYPE_VERSION)
                                        .withInstanceIdentifier(instanceIdentifier)
                                        .withType(Type.from(body).toString())
                                        .withCreationDateAndTime(creationTime)
                                )
                                .withBusinessScope(new BusinessScope()
                                        .withScopes(new Scope()
                                                .withIdentifier(STANDARD)
                                                .withType(CONVERSATIONID)
                                                .withInstanceIdentifier(conversationId)
                                        )
                                )
                )
                .withAny(body);
    }

    public enum Type {
        DigitalPost("digitalPost", SDPDigitalPost.class),
        FlyttetDigitalPost("flyttetDigitalPost", SDPFlyttetDigitalPost.class),
        Kvittering("kvittering", SDPKvittering.class),
        Feil("feil", SDPFeil.class);

        private final String name;
        private final Class<? extends SDPMelding> clazz;

        private Type(final String name, final Class<? extends SDPMelding> clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        public static boolean isValid(final String type) {
            for (Type t : Type.values()) {
                if (t.name.equals(type)) {
                    return true;
                }
            }
            return false;
        }

        public static Type from(final SDPMelding melding) {
            for (Type t : Type.values()) {
                if (t.clazz.isInstance(melding)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("Unsupported type of SDPMelding:" + melding.getClass());
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
