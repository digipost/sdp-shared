package no.digipost.api.interceptors;

import no.digipost.api.config.TransaksjonsLogg;
import no.digipost.api.representations.EbmsContext;
import no.digipost.api.xml.JaxbMarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapMessage;

import static no.digipost.api.config.TransaksjonsLogg.Retning.INNKOMMENDE;

public class TransactionLogClientInterceptor implements ClientInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionLogClientInterceptor.class);

    private TransactionLog transactionLog;

    public TransactionLogClientInterceptor(JaxbMarshaller jaxbMarshaller) {
        this.transactionLog = new TransactionLog(jaxbMarshaller);
    }

    @Override
    public boolean handleRequest(final MessageContext messageContext) throws WebServiceClientException {
        try {
            transactionLog.handleOutgoing(EbmsContext.from(messageContext), (SoapMessage) messageContext.getRequest(), "sender");
        } catch (Exception ex) {
            LOG.warn("Feil under klienttransaksjonslogging i handleRequest", ex);
        }
        return true;
    }

    @Override
    public boolean handleResponse(final MessageContext messageContext) throws WebServiceClientException {
        try {
            transactionLog.handleIncoming(EbmsContext.from(messageContext), (SoapMessage) messageContext.getResponse(), "sender");
        } catch (Exception ex) {
            LOG.warn("Feil under klienttransaksjonslogging i handleResponse", ex);
        }
        return true;
    }

    @Override
    public boolean handleFault(final MessageContext messageContext) throws WebServiceClientException {
        try {
            transactionLog.handleFault(INNKOMMENDE, EbmsContext.from(messageContext), (SoapMessage) messageContext.getResponse(), "sender");
        } catch (Exception ex) {
            LOG.warn("Feil under klienttransaksjonslogging i handleFault", ex);
        }
        return true;
    }

    @Override
    public void afterCompletion(final MessageContext messageContext, final Exception ex) throws WebServiceClientException {
    }

    public void setTransaksjonslogg(TransaksjonsLogg transaksjonslogg) {
        this.transactionLog.setTransaksjonslogg(transaksjonslogg);
    }
}
