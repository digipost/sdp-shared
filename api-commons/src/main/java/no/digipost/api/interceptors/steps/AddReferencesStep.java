package no.digipost.api.interceptors.steps;

import no.digipost.api.representations.EbmsContext;
import no.digipost.api.representations.EbmsProcessingStep;
import no.digipost.api.xml.Constants;
import no.digipost.api.xml.JaxbMarshaller;
import no.digipost.api.xml.Marshalling;
import no.digipost.org.oasis_open.docs.ebxml_bp.ebbp_signals_2.MessagePartNRInformation;
import no.digipost.org.oasis_open.docs.ebxml_bp.ebbp_signals_2.NonRepudiationInformation;
import no.digipost.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo;
import no.digipost.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Receipt;
import no.digipost.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage;
import no.digipost.org.w3.xmldsig.Reference;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class AddReferencesStep implements EbmsProcessingStep {

    public final Collection<Reference> references;
    private final JaxbMarshaller jaxb2Marshaller;
    private final String messageId;

    public AddReferencesStep(JaxbMarshaller jaxbMarshaller, String messageId, Collection<Reference> references) {
        this.jaxb2Marshaller = jaxbMarshaller;
        this.messageId = messageId;
        this.references = references == null ? new ArrayList<>() : references;
    }

    @Override
    public void apply(EbmsContext ebmsContext, SoapHeaderElement ebmsMessaging, SoapMessage soapMessage) {
        List<MessagePartNRInformation> nrInfos = new ArrayList<MessagePartNRInformation>();
        for (Reference ref : references) {
            nrInfos.add(new MessagePartNRInformation().withReference(ref));
        }

        Receipt receipt = new Receipt()
                .withAnies(new NonRepudiationInformation()
                        .withMessagePartNRInformations(nrInfos));
        SignalMessage signalMessage = new SignalMessage()
                .withMessageInfo(new MessageInfo(
                        ZonedDateTime.now(),
                        UUID.randomUUID().toString(),
                        messageId))
                .withReceipt(receipt);
        Marshalling.marshal(jaxb2Marshaller, ebmsMessaging, Constants.SIGNAL_MESSAGE_QNAME, signalMessage);
    }

}
