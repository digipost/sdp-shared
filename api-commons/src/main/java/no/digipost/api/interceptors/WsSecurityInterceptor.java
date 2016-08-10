package no.digipost.api.interceptors;

import no.digipost.api.xml.Constants;
import org.apache.wss4j.common.crypto.Merlin;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointExceptionResolver;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.server.SoapEndpointInterceptor;

import javax.xml.namespace.QName;

public class WsSecurityInterceptor implements ClientInterceptor, SoapEndpointInterceptor, InitializingBean {

    private final Wss4jInterceptor interceptor;
    private final KeyStoreInfo keystoreInfo;

    public WsSecurityInterceptor(final KeyStoreInfo keystoreInfo, final EndpointExceptionResolver exceptionResolver) {
        this(keystoreInfo, exceptionResolver, new LogFault.LogFaultsAsWarn(Wss4jInterceptor.LOG));
    }

    public WsSecurityInterceptor(final KeyStoreInfo keystoreInfo, final EndpointExceptionResolver exceptionResolver, LogFault logFault) {
        this(keystoreInfo, new Wss4jInterceptor(logFault, exceptionResolver));
    }

    private WsSecurityInterceptor(final KeyStoreInfo keystoreInfo, final Wss4jInterceptor interceptor) {
        this.keystoreInfo = keystoreInfo;
        this.interceptor = interceptor;
    }

    public static String getSignParts() {
        StringBuilder builder = new StringBuilder();
        builder.append("{}{}Body").append(";");
        builder.append(partFromQName(Constants.TIMESTAMP)).append(";");
        builder.append(partFromQName(Constants.MESSAGING_QNAME)).append(";");
        return builder.toString();
    }

    private static String partFromQName(final QName qname) {
        return "{}{" + qname.getNamespaceURI() + "}" + qname.getLocalPart();
    }

    public static WsSecurityInterceptor forWssecTest(final KeyStoreInfo keystoreInfo, final Wss4jInterceptor interceptor) throws Exception {
        WsSecurityInterceptor ic = new WsSecurityInterceptor(keystoreInfo, interceptor);
        ic.afterPropertiesSet();
        return ic;
    }

    @Override
    public void afterPropertiesSet() {
        Merlin crypto = new Merlin();
        crypto.setCryptoProvider(BouncyCastleProvider.PROVIDER_NAME);

        crypto.setKeyStore(keystoreInfo.keystore);
        crypto.setTrustStore(keystoreInfo.trustStore);

        interceptor.setSecurementSignatureParts(getSignParts());
        interceptor.setSecurementSignatureIfPresentParts("{}cid:Attachments");
        interceptor.setSecurementSignatureCrypto(crypto);
        interceptor.setSecurementSignatureUser(keystoreInfo.alias);
        interceptor.setSecurementPassword(keystoreInfo.password);
        interceptor.setValidationSignatureCrypto(crypto);

    }

    @Override
    public boolean handleRequest(final MessageContext messageContext) throws WebServiceClientException {
        return interceptor.handleRequest(messageContext);
    }

    @Override
    public boolean handleResponse(final MessageContext messageContext) throws WebServiceClientException {
        return interceptor.handleResponse(messageContext);
    }

    @Override
    public boolean handleFault(final MessageContext messageContext) throws WebServiceClientException {
        return interceptor.handleFault(messageContext);
    }

    @Override
    public boolean handleRequest(final MessageContext messageContext, final Object endpoint) throws Exception {
        return interceptor.handleRequest(messageContext, endpoint);
    }

    @Override
    public boolean handleResponse(final MessageContext messageContext, final Object endpoint) throws Exception {
        return interceptor.handleResponse(messageContext, endpoint);
    }

    @Override
    public boolean handleFault(final MessageContext messageContext, final Object endpoint) throws Exception {
        return interceptor.handleFault(messageContext, endpoint);

    }

    @Override
    public void afterCompletion(final MessageContext messageContext, final Object endpoint, final Exception ex) throws Exception {
        interceptor.afterCompletion(messageContext, endpoint, ex);
    }

    @Override
    public boolean understands(final SoapHeaderElement header) {
        return interceptor.understands(header);
    }

    @Override
    public void afterCompletion(MessageContext messageContext, Exception ex) throws WebServiceClientException {

    }

}
