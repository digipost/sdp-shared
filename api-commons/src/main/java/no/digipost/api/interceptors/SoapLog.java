
package no.digipost.api.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.xml.transform.TransformerObjectSupport;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class SoapLog extends TransformerObjectSupport {

	private static final Logger LOG = LoggerFactory.getLogger("PAYLOAD_FILE");

	private final LogLevel logLevel;

	public enum LogLevel {
		NONE,
		FAULTS_ONLY,
		ALL
	}

	public SoapLog(final LogLevel logLevel) {
		this.logLevel = logLevel;
	}

	public LogLevel getLogLevel() {
		return logLevel;
	}

	public boolean isTraceEnabled() {
		return LOG.isTraceEnabled();
	}

	public void logMessageSource(final String logMessage, final Source source) {
		try {
			if (source != null) {
				Transformer transformer = createNonIndentingTransformer();
				StringWriter writer = new StringWriter();
				transformer.transform(source, new StreamResult(writer));
				String message = logMessage + writer.toString();
				logMessage(message);
			}
		} catch (TransformerException e) {
			LOG.warn("Error logging message source", e);
		}
	}

	public Source getSource(final WebServiceMessage message) {
		if (message instanceof SoapMessage) {
			SoapMessage soapMessage = (SoapMessage) message;
			return soapMessage.getEnvelope().getSource();
		} else {
			return null;
		}
	}

	private void logMessage(final String message) {
		LOG.info(message);
	}

	private Transformer createNonIndentingTransformer() throws TransformerConfigurationException {
		Transformer transformer = createTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.INDENT, "no");
		return transformer;
	}

}
