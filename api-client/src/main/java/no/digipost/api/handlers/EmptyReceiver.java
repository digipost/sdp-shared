
package no.digipost.api.handlers;

import no.digipost.api.representations.TransportKvittering;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageExtractor;

import javax.xml.transform.TransformerException;

import java.io.IOException;

public class EmptyReceiver extends EbmsContextAware implements WebServiceMessageExtractor<TransportKvittering> {

	@Override
	public TransportKvittering extractData(final WebServiceMessage message) throws IOException, TransformerException {
		//I know nothing
		return null;
	}

}
