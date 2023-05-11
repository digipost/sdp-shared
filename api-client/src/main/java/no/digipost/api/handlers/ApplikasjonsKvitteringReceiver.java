package no.digipost.api.handlers;

import no.digipost.api.representations.EbmsAktoer;
import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.xml.JaxbMarshaller;
import no.digipost.api.xml.Marshalling;
import no.digipost.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartyInfo;
import no.digipost.org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapMessage;

import javax.xml.transform.TransformerException;

import java.io.IOException;

public class ApplikasjonsKvitteringReceiver extends EbmsContextAware implements WebServiceMessageExtractor<EbmsApplikasjonsKvittering> {

    private static final String NO_MESSAGE_AVAILABLE_FROM_MPC_ERROR_CODE = "EBMS:0006";

    private final JaxbMarshaller jaxb2Marshaller;

    public ApplikasjonsKvitteringReceiver(JaxbMarshaller jaxb2Marshaller) {
        this.jaxb2Marshaller = jaxb2Marshaller;
    }

    @Override
    public EbmsApplikasjonsKvittering extractData(final WebServiceMessage message) throws IOException, TransformerException {
        SoapBody soapBody = ((SoapMessage) message).getSoapBody();

        if (ebmsContext.warning != null && NO_MESSAGE_AVAILABLE_FROM_MPC_ERROR_CODE.equals(ebmsContext.warning.getErrorCode())) {
            return null;
        }

        StandardBusinessDocument sbd = Marshalling.unmarshal(jaxb2Marshaller, soapBody, StandardBusinessDocument.class);
        PartyInfo partyInfo = ebmsContext.userMessage.getPartyInfo();
        EbmsAktoer avsender = EbmsAktoer.from(partyInfo.getFrom());
        EbmsAktoer mottaker = EbmsAktoer.from(partyInfo.getTo());

        return EbmsApplikasjonsKvittering.create(avsender, mottaker, sbd)
                .withMessageId(ebmsContext.userMessage.getMessageInfo().getMessageId())
                .withRefToMessageId(ebmsContext.userMessage.getMessageInfo().getRefToMessageId())
                .withReferences(ebmsContext.incomingReferences.values())
                .build();
    }

}
