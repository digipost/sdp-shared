/**
 * Copyright (C) Posten Norge AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.digipost.api.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.xml.transform.TransformerObjectSupport;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import java.io.StringWriter;

public class SoapLoggInterceptor extends TransformerObjectSupport implements ClientInterceptor, EndpointInterceptor {
	private static final Logger LOG = LoggerFactory.getLogger("PAYLOAD_FILE");

	public enum LogLevel {
		NONE,
		FAULTS_ONLY,
		ALL
	}
	private final LogLevel logLevel;

	public SoapLoggInterceptor(final LogLevel logLevel) {
		this.logLevel = logLevel;
	}

	// ClientInterceptor

	@Override
	public boolean handleRequest(final MessageContext messageContext) throws WebServiceClientException {
		if (logLevel == LogLevel.ALL) {
			logMessageSource("Utgående request: ", getSource(messageContext.getRequest()));
		}

		return true;
	}

	@Override
	public boolean handleResponse(final MessageContext messageContext) throws WebServiceClientException {
		if (logLevel == LogLevel.ALL) {
			logMessageSource("Innkommende response: ", getSource(messageContext.getResponse()));
		}
		return true;
	}

    @Override
    public boolean handleFault(final MessageContext messageContext) throws WebServiceClientException {
    		if (logLevel == LogLevel.FAULTS_ONLY) {
    	        logMessageSource("Utgående feilende request: ", getSource(messageContext.getRequest()));
    	        logMessageSource("Innkommende fault: ", getSource(messageContext.getResponse()));
    		}
    		if (logLevel == LogLevel.ALL) {
    			logMessageSource("Innkommende fault: ", getSource(messageContext.getResponse()));
    		}
        return true;
    }

    // Endpoint interceptor
	@Override
	public boolean handleRequest(final MessageContext messageContext, final Object endpoint) throws Exception {
		if (logLevel == LogLevel.ALL) {
			logMessageSource("Innkommende request: ", getSource(messageContext.getRequest()));
		}
		return true;
	}

	@Override
	public boolean handleResponse(final MessageContext messageContext, final Object endpoint) throws Exception {
		if (logLevel == LogLevel.ALL) {
			logMessageSource("Utgående response: ", getSource(messageContext.getResponse()));
		}
		return true;
	}

	@Override
	public boolean handleFault(final MessageContext messageContext, final Object endpoint) throws Exception {
		if (logLevel == LogLevel.FAULTS_ONLY) {
	        logMessageSource("Innkommende feilende request: ", getSource(messageContext.getRequest()));
	        logMessageSource("Utgående fault: ", getSource(messageContext.getResponse()));
		}
		if (logLevel == LogLevel.ALL) {
			logMessageSource("Utgående fault: ", getSource(messageContext.getResponse()));
		}
		return true;
	}

	@Override
	public void afterCompletion(final MessageContext messageContext, final Object endpoint, final Exception ex) throws Exception {
	}


    // Privates

    protected void logMessageSource(final String logMessage, final Source source)  {
    		try {
	        if (source != null) {
	            Transformer transformer = createNonIndentingTransformer();
	            StringWriter writer = new StringWriter();
	            transformer.transform(source, new StreamResult(writer));
	            String message = logMessage + writer.toString();
	            logMessage(message);
	        }
    		} catch (TransformerException e) {
    			LOG.warn("Error logging", e);
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

	@Override
	public void afterCompletion(final MessageContext messageContext, final Exception ex) throws WebServiceClientException {
	}

    protected Source getSource(final WebServiceMessage message) {
        if (message instanceof SoapMessage) {
            SoapMessage soapMessage = (SoapMessage) message;
            return soapMessage.getEnvelope().getSource();
        }
        else {
            return null;
        }
    }



}
