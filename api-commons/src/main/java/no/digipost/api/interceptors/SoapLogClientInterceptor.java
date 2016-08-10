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
