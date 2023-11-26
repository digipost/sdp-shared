package no.digipost.api.handlers;

import jakarta.activation.DataHandler;
import no.digipost.api.SdpMeldingSigner;
import no.digipost.api.interceptors.steps.AddUserMessageStep;
import no.digipost.api.representations.EbmsAktoer;
import no.digipost.api.representations.EbmsForsendelse;
import no.digipost.api.representations.Mpc;
import no.digipost.api.xml.JaxbMarshaller;
import no.digipost.api.xml.Marshalling;
import no.digipost.api.xml.TransformerUtil;
import no.digipost.org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import no.digipost.org.w3.xmldsig.DigestMethod;
import no.digipost.org.w3.xmldsig.Reference;
import no.digipost.xsd.types.DigitalPostformidling;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapMessage;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import java.io.IOException;
import java.util.UUID;

public class ForsendelseSender extends EbmsContextAware implements WebServiceMessageCallback {

    private final StandardBusinessDocument doc;
    private final EbmsForsendelse forsendelse;
    private final JaxbMarshaller marshaller;
    private final EbmsAktoer databehandler;
    private final EbmsAktoer tekniskMottaker;
    private final DigitalPostformidling digitalPostformidling;
    private final SdpMeldingSigner signer;

    public ForsendelseSender(final SdpMeldingSigner signer, final EbmsAktoer databehandler, final EbmsAktoer tekniskMottaker, final EbmsForsendelse forsendelse, final JaxbMarshaller marshaller) {
        this.signer = signer;
        this.databehandler = databehandler;
        this.tekniskMottaker = tekniskMottaker;
        this.doc = forsendelse.doc;
        this.forsendelse = forsendelse;
        this.marshaller = marshaller;
        this.digitalPostformidling = (DigitalPostformidling) doc.getAny();
    }

    public static void lagFingeravtrykk(final EbmsForsendelse forsendelse, final DigitalPostformidling digitalPostformidling) throws IOException {
        byte[] hash = forsendelse.getDokumentpakke().getSHA256();
        digitalPostformidling.setDokumentpakkefingeravtrykk(new Reference()
                .withDigestMethod(new DigestMethod().withAlgorithm(javax.xml.crypto.dsig.DigestMethod.SHA256))
                .withDigestValue(hash)
        );
    }

    @Override
    public void doWithMessage(final WebServiceMessage message) throws IOException, TransformerException {
        SoapMessage soapMessage = (SoapMessage) message;
        attachFile(soapMessage);
        Mpc mpc = new Mpc(forsendelse.prioritet, forsendelse.mpcId);
        if (forsendelse.sbdStream != null) {
            TransformerUtil.transform(new StreamSource(forsendelse.sbdStream), soapMessage.getEnvelope().getBody().getPayloadResult(), true);
        } else if (digitalPostformidling.getSignature() == null) {
            Document signedDoc = signer.sign(doc);
            Marshalling.marshal(signedDoc, soapMessage.getEnvelope().getBody().getPayloadResult());
        } else {
            Marshalling.marshal(marshaller, soapMessage.getEnvelope().getBody(), doc);
        }
        ebmsContext.addRequestStep(new AddUserMessageStep(mpc, forsendelse.messageId, forsendelse.action, null, doc, databehandler, tekniskMottaker, marshaller));
    }

    private void attachFile(final SoapMessage soapMessage) throws IOException {
        if (digitalPostformidling.getDokumentpakkefingeravtrykk() == null) {
            lagFingeravtrykk(forsendelse, digitalPostformidling);
        }
        DataHandler handler = new DataHandler(forsendelse.getDokumentpakke());
        soapMessage.addAttachment(generateContentId(), handler);
    }

    private String generateContentId() {
        return "<" + UUID.randomUUID().toString() + "@meldingsformidler.sdp.difi.no>";
    }

}
