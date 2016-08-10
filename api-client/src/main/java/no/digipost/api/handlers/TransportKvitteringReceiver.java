package no.digipost.api.handlers;

import no.digipost.api.representations.TransportKvittering;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageExtractor;

import javax.xml.transform.TransformerException;
import java.io.IOException;

public class TransportKvitteringReceiver extends EbmsContextAware implements WebServiceMessageExtractor<TransportKvittering> {

    @Override
    public TransportKvittering extractData(final WebServiceMessage message) throws IOException, TransformerException {
        MessageInfo messageInfo = ebmsContext.receipts.get(0).getMessageInfo();
        return new TransportKvittering(messageInfo.getMessageId(), messageInfo.getRefToMessageId());
    }

}