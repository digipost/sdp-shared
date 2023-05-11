package no.digipost.api.handlers;

import no.digipost.api.SdpMeldingSigner;
import no.digipost.api.interceptors.steps.AddUserMessageStep;
import no.digipost.api.representations.EbmsAktoer;
import no.digipost.api.representations.EbmsApplikasjonsKvittering;
import no.digipost.api.representations.Mpc;
import no.digipost.api.representations.SimpleStandardBusinessDocument;
import no.digipost.api.xml.JaxbMarshaller;
import no.digipost.api.xml.Marshalling;
import no.digipost.api.xml.TransformerUtil;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapMessage;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import java.io.IOException;

public class KvitteringSender extends EbmsContextAware implements WebServiceMessageCallback {

    private final EbmsApplikasjonsKvittering appKvittering;
    private final JaxbMarshaller marshaller;
    private final EbmsAktoer databehandler;
    private final EbmsAktoer tekniskMottaker;
    private final SdpMeldingSigner signer;

    public KvitteringSender(SdpMeldingSigner signer, EbmsAktoer databehandler, EbmsAktoer tekniskMottaker, EbmsApplikasjonsKvittering appKvittering, JaxbMarshaller marshaller) {
        this.signer = signer;
        this.databehandler = databehandler;
        this.tekniskMottaker = tekniskMottaker;
        this.appKvittering = appKvittering;
        this.marshaller = marshaller;
    }

    @Override
    public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
        SoapMessage soapMessage = (SoapMessage) message;
        SimpleStandardBusinessDocument simple = new SimpleStandardBusinessDocument(appKvittering.sbd);
        if (appKvittering.sbdStream != null) {
            TransformerUtil.transform(new StreamSource(appKvittering.sbdStream), soapMessage.getEnvelope().getBody().getPayloadResult(), true);
        } else if (simple.getMelding().getSignature() == null) {
            Document signedDoc = signer.sign(appKvittering.sbd);
            Marshalling.marshal(signedDoc, soapMessage.getEnvelope().getBody().getPayloadResult());
        } else {
            Marshalling.marshal(marshaller, soapMessage.getEnvelope().getBody(), appKvittering.sbd);
        }

        Mpc mpc = new Mpc(appKvittering.prioritet, appKvittering.mpcId);
        ebmsContext.addRequestStep(new AddUserMessageStep(
                mpc, appKvittering.messageId, appKvittering.action, null, appKvittering.sbd, databehandler, tekniskMottaker, marshaller));
    }

}
