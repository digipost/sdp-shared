package no.digipost.api.handlers;

import no.digipost.api.interceptors.steps.AddReferencesStep;
import no.digipost.api.representations.EbmsContext;
import no.digipost.api.representations.EbmsProcessingStep;
import no.digipost.api.representations.EbmsPullRequest;
import no.digipost.api.representations.KanBekreftesSomBehandletKvittering;
import no.digipost.api.representations.Mpc;
import no.digipost.api.xml.Constants;
import no.digipost.api.xml.Marshalling;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PullRequest;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.SignalMessage;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.w3.xmldsig.Reference;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PullRequestSender extends EbmsContextAware implements WebServiceMessageCallback {

    private final EbmsPullRequest pullRequest;
    private final Jaxb2Marshaller marshaller;
    private final KanBekreftesSomBehandletKvittering tidligereKvitteringSomSkalBekreftes;

    public PullRequestSender(final EbmsPullRequest pullRequest, final Jaxb2Marshaller marshaller, final KanBekreftesSomBehandletKvittering tidligereKvitteringSomSkalBekreftes) {
        this.pullRequest = pullRequest;
        this.marshaller = marshaller;
        this.tidligereKvitteringSomSkalBekreftes = tidligereKvitteringSomSkalBekreftes;
    }

    @Override
    public void doWithMessage(final WebServiceMessage message) throws IOException, TransformerException {
        if (tidligereKvitteringSomSkalBekreftes != null) {
            List<Reference> referenceToMessageToBeConfirmed = new ArrayList<Reference>();
            referenceToMessageToBeConfirmed.add(tidligereKvitteringSomSkalBekreftes.getReferanseTilMeldingSomKvitteres().getUnmarshalled());

            ebmsContext.addRequestStep(new AddReferencesStep(marshaller, tidligereKvitteringSomSkalBekreftes.getMeldingsId(), referenceToMessageToBeConfirmed));
        }

        ebmsContext.addRequestStep(new EbmsProcessingStep() {

            @Override
            public void apply(final EbmsContext ebmsContext, final SoapHeaderElement ebmsMessaging, final SoapMessage soapMessage) {
                Mpc mpc = new Mpc(pullRequest.prioritet, pullRequest.mpcId);
                SignalMessage signalMessage = new SignalMessage()
                        .withMessageInfo(pullRequest.createMessageInfo())
                        .withPullRequest(new PullRequest().withMpc(mpc.toString())
                        );
                Marshalling.marshal(marshaller, ebmsMessaging, Constants.SIGNAL_MESSAGE_QNAME, signalMessage);
            }

        });
    }

}