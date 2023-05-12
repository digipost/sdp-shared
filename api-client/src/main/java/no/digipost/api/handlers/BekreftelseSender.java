package no.digipost.api.handlers;

import no.digipost.api.interceptors.steps.AddReferencesStep;
import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;
import no.digipost.api.xml.JaxbMarshaller;
import no.digipost.org.w3.xmldsig.Reference;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;

import javax.xml.transform.TransformerException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BekreftelseSender extends EbmsContextAware implements WebServiceMessageCallback {

    private final KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering;
    private final JaxbMarshaller jaxb2Marshaller;

    public BekreftelseSender(KanBekreftesSomBehandletKvittering kanBekreftesSomBehandletKvittering, JaxbMarshaller jaxb2Marshaller) {
        this.kanBekreftesSomBehandletKvittering = kanBekreftesSomBehandletKvittering;
        this.jaxb2Marshaller = jaxb2Marshaller;
    }

    @Override
    public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
        List<Reference> references = new ArrayList<Reference>();
        references.add(kanBekreftesSomBehandletKvittering.getReferanseTilMeldingSomKvitteres().getUnmarshalled());

        ebmsContext.addRequestStep(new AddReferencesStep(jaxb2Marshaller, kanBekreftesSomBehandletKvittering.getMeldingsId(), references));
    }

}
