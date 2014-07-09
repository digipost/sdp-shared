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

import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;

public class SoapLogClientInterceptor implements ClientInterceptor {

	private SoapLog soapLog;

	public SoapLogClientInterceptor(SoapLog.LogLevel level) {
		this.soapLog = new SoapLog(level);
	}

	@Override
	public boolean handleRequest(final MessageContext messageContext) throws WebServiceClientException {
		return true;
	}

	@Override
	public boolean handleResponse(final MessageContext messageContext) throws WebServiceClientException {
		if (soapLog.getLogLevel() == SoapLog.LogLevel.ALL || soapLog.isTraceEnabled()) {
			soapLog.logMessageSource("Utgående request: ", soapLog.getSource(messageContext.getRequest()));
			soapLog.logMessageSource("Innkommende response: ", soapLog.getSource(messageContext.getResponse()));
		}
		return true;
	}

	@Override
	public boolean handleFault(final MessageContext messageContext) throws WebServiceClientException {
		if (soapLog.getLogLevel() == SoapLog.LogLevel.FAULTS_ONLY || soapLog.getLogLevel() == SoapLog.LogLevel.ALL || soapLog.isTraceEnabled()) {
			soapLog.logMessageSource("Utgående feilende request: ", soapLog.getSource(messageContext.getRequest()));
			soapLog.logMessageSource("Innkommende fault: ", soapLog.getSource(messageContext.getResponse()));
		}
		return true;
	}

	@Override
	public void afterCompletion(MessageContext messageContext, Exception ex) throws WebServiceClientException {

	}

}
