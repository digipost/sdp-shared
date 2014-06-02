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
package no.posten.dpost.offentlig.api.interceptors;

import no.posten.dpost.offentlig.api.exceptions.ebms.standard.processing.OtherException;
import no.posten.dpost.offentlig.xml.Constants;
import org.apache.wss4j.common.ConfigurationConstants;
import org.apache.wss4j.common.crypto.AlgorithmSuite;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.WSDataRef;
import org.apache.wss4j.dom.WSSConfig;
import org.apache.wss4j.dom.WSSecurityEngine;
import org.apache.wss4j.dom.WSSecurityEngineResult;
import org.apache.wss4j.dom.handler.RequestData;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.apache.wss4j.dom.handler.WSHandlerResult;
import org.apache.wss4j.dom.message.token.Timestamp;
import org.apache.wss4j.dom.util.WSSecurityUtil;
import org.apache.wss4j.dom.validate.Credential;
import org.apache.wss4j.dom.validate.SignatureTrustValidator;
import org.apache.wss4j.dom.validate.TimestampValidator;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointExceptionResolver;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.soap.security.AbstractWsSecurityInterceptor;
import org.springframework.ws.soap.security.WsSecurityFaultException;
import org.springframework.ws.soap.security.WsSecuritySecurementException;
import org.springframework.ws.soap.security.WsSecurityValidationException;
import org.springframework.ws.soap.security.callback.CleanupCallback;
import org.springframework.ws.soap.security.wss4j.Wss4jSecuritySecurementException;
import org.springframework.ws.soap.security.wss4j.Wss4jSecurityValidationException;
import org.w3c.dom.Document;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;


public class Wss4jInterceptor extends AbstractWsSecurityInterceptor {

	public static final String SECUREMENT_USER_PROPERTY_NAME = "Wss4jSecurityInterceptor.securementUser";

	public static final String INCOMING_CERTIFICATE = "Wss4jInterceptor.incoming.certificate";


    private String securementActions;

    private List<Integer> securementActionsVector;

    private String securementUsername;

    private CallbackHandler validationCallbackHandler;


    private String validationActions;

    private List<Integer> validationActionsVector;

    private String validationActor;

    private Crypto validationSignatureCrypto;

    private final boolean timestampStrict = true;

    private boolean enableSignatureConfirmation;

    private int validationTimeToLive = 120;

    private int securementTimeToLive = 120;

    private WSSConfig wssConfig;

    private final WSSecurityEngine securityEngine = new WSSecurityEngine();

    private boolean enableRevocation;


    private final Wss4jHandler handler = new Wss4jHandler();

	private EndpointExceptionResolver exceptionResolver;

	private String digestAlgorithm;

	private String securementSignatureAlgorithm;

    public Wss4jInterceptor() {
    	setSecurementSignatureAlgorithm(Constants.RSA_SHA256);
    	setSecurementSignatureDigestAlgorithm(DigestMethod.SHA256);
    	setSecurementSignatureKeyIdentifier("DirectReference");
    	setSecurementActions("Timestamp Signature");
    	setValidationActions("Timestamp Signature");

    }






    public void setSecurementActions(final String actions) {
        securementActions = actions;
        try {
        	securementActionsVector = WSSecurityUtil.decodeAction(securementActions);
        }
        catch (WSSecurityException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public void setSecurementActor(final String securementActor) {
        handler.setOption(WSHandlerConstants.ACTOR, securementActor);
    }

    public void setSecurementPassword(final String securementPassword) {
        handler.setSecurementPassword(securementPassword);
    }

    public void setSecurementSignatureAlgorithm(final String securementSignatureAlgorithm) {
        handler.setOption(WSHandlerConstants.SIG_ALGO, securementSignatureAlgorithm);
        this.securementSignatureAlgorithm = securementSignatureAlgorithm;
    }
    public void setSecurementSignatureDigestAlgorithm(final String digestAlgorithm) {
        handler.setOption(WSHandlerConstants.SIG_DIGEST_ALGO, digestAlgorithm);
        this.digestAlgorithm = digestAlgorithm;
    }
    public void setSecurementSignatureCrypto(final Crypto securementSignatureCrypto) {
        handler.setSecurementSignatureCrypto(securementSignatureCrypto);
    }
    public void setSecurementSignatureKeyIdentifier(final String securementSignatureKeyIdentifier) {
        handler.setOption(WSHandlerConstants.SIG_KEY_ID, securementSignatureKeyIdentifier);
    }

    public void setSecurementSignatureParts(final String securementSignatureParts) {
        handler.setOption(WSHandlerConstants.SIGNATURE_PARTS, securementSignatureParts);
    }
    public void setSecurementSignatureIfPresentParts(final String securementSignatureParts) {
        handler.setOption(ConfigurationConstants.OPTIONAL_SIGNATURE_PARTS, securementSignatureParts);
    }


    public void setSecurementSignatureUser(final String securementSignatureUser) {
        handler.setOption(WSHandlerConstants.SIGNATURE_USER, securementSignatureUser);
    }

    /** Sets the time to live on the outgoing message */
    public void setSecurementTimeToLive(final int ttl) {
        if (ttl <= 0) {
            throw new IllegalArgumentException("timeToLive must be positive");
        }
        securementTimeToLive = ttl;
    }


    public void setValidationTimeToLive(final int ttl) {
        if (ttl <= 0) {
            throw new IllegalArgumentException("timeToLive must be positive");
        }
        validationTimeToLive = ttl;
    }

    public void setValidationActions(final String actions) {
        validationActions = actions;
        try {
            validationActionsVector = WSSecurityUtil.decodeAction(actions);
        }
        catch (WSSecurityException ex) {
            throw new IllegalArgumentException(ex);
        }
    }



    public void setValidationSignatureCrypto(final Crypto signatureCrypto) {
        validationSignatureCrypto = signatureCrypto;
    }






    public void setEnableRevocation(final boolean enabled) {
        enableRevocation = enabled;
    }

    public void setBspCompliant(final boolean compliant) {
	    handler.setOption(WSHandlerConstants.IS_BSP_COMPLIANT, compliant);
    }


    public void afterPropertiesSet() throws Exception {
        Assert.isTrue(validationActions != null || securementActions != null,
                "validationActions or securementActions are required");
        if (validationActions != null) {
        	if (validationActionsVector.contains(WSConstants.UT)) {
                Assert.notNull(validationCallbackHandler, "validationCallbackHandler is required");
            }

            if (validationActionsVector.contains(WSConstants.SIGN)) {
                Assert.notNull(validationSignatureCrypto, "validationSignatureCrypto is required");
            }
        }

    }


	@Override
	protected boolean handleFaultException(final WsSecurityFaultException ex, final MessageContext messageContext) {
		if (logger.isWarnEnabled()) {
			logger.warn("Could not handle request: " + ex.getMessage());
		}
		if (exceptionResolver != null) {
			exceptionResolver.resolveException(messageContext, null, ex);
		} else {
			logger.error("Exception resolver ikke satt");
			throw new OtherException();
		}
		return false;
	}


    @Override
    protected void secureMessage(final SoapMessage soapMessage, final MessageContext messageContext)
            throws WsSecuritySecurementException {
    	boolean noSecurity = securementActionsVector.isEmpty() || securementActionsVector.contains(0);
		if (noSecurity && !enableSignatureConfirmation){
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Securing message [" + soapMessage + "] with actions [" + securementActions + "]");
        }
        RequestData requestData = initializeRequestData(messageContext);
        requestData.setAttachmentCallbackHandler(new AttachmentCallbackHandler((SaajSoapMessage)soapMessage));

        Document envelopeAsDocument = soapMessage.getDocument();
        try {
            // In case on signature confirmation with no other securement
            // action, we need to pass an empty securementActionsVector to avoid
            // NPE
            if (noSecurity) {
                securementActionsVector = new ArrayList<Integer>(0);
            }

            handler.doSenderAction(envelopeAsDocument, requestData, WSSecurityUtil.decodeHandlerAction(securementActions, wssConfig), true);
        }
        catch (WSSecurityException ex) {
            throw new Wss4jSecuritySecurementException(ex.getMessage(), ex);
        }

        soapMessage.setDocument(envelopeAsDocument);
    }

    /**
     * Creates and initializes a request data for the given message context.
     *
     * @param messageContext the message context
     * @return the request data
     */
    protected RequestData initializeRequestData(final MessageContext messageContext) {
        RequestData requestData = new RequestData();
        requestData.setMsgContext(messageContext);

        // reads securementUsername first from the context then from the property
        String contextUsername = (String) messageContext.getProperty(SECUREMENT_USER_PROPERTY_NAME);
        if (StringUtils.hasLength(contextUsername)) {
            requestData.setUsername(contextUsername);
        }
        else {
            requestData.setUsername(securementUsername);
        }
        requestData.setAppendSignatureAfterTimestamp(true);
        requestData.setTimeToLive(securementTimeToLive);

        requestData.setWssConfig(wssConfig);
        return requestData;
    }



    @Override
    protected void validateMessage(final SoapMessage soapMessage, final MessageContext messageContext)
            throws WsSecurityValidationException {
        if (logger.isDebugEnabled()) {
            logger.debug("Validating message [" + soapMessage + "] with actions [" + validationActions + "]");
        }

        if (validationActionsVector.isEmpty() || validationActionsVector.contains(WSConstants.NO_SECURITY)) {
            return;
        }

        Document envelopeAsDocument = soapMessage.getDocument();

        // Header processing

        try {
            RequestData requestData = new RequestData();
            requestData.setAttachmentCallbackHandler(new AttachmentCallbackHandler((SaajSoapMessage)soapMessage));
            requestData.setWssConfig(wssConfig);
            requestData.setSigVerCrypto(validationSignatureCrypto);
            requestData.setCallbackHandler(validationCallbackHandler);
            AlgorithmSuite algorithmSuite = new AlgorithmSuite();
            algorithmSuite.addDigestAlgorithm(digestAlgorithm);
            algorithmSuite.addSignatureMethod(securementSignatureAlgorithm);
            algorithmSuite.addC14nAlgorithm(CanonicalizationMethod.EXCLUSIVE);
            //algorithmSuite.addTransformAlgorithm("http://www.w3.org/2001/10/xml-exc-c14n#");
            //algorithmSuite.addTransformAlgorithm("http://docs.oasis-open.org/wss/2004/XX/oasis-2004XX-wss-swa-profile-1.0#Attachment-Complete-Transform");
			requestData.setAlgorithmSuite(algorithmSuite);
			requestData.setEnableTimestampReplayCache(false);


        	List<WSSecurityEngineResult> results = securityEngine
                    .processSecurityHeader(envelopeAsDocument, validationActor, requestData );

            // Results verification
            if (CollectionUtils.isEmpty(results)) {
                throw new Wss4jSecurityValidationException("No WS-Security header found");
            }

            updateMessageContextWithCertificate(messageContext, results);

            checkResults(results, validationActionsVector);

            validateEbmsMessagingIsSigned(envelopeAsDocument.getDocumentElement().getPrefix(), results);

            // puts the results in the context
            // useful for Signature Confirmation
            updateContextWithResults(messageContext, results);

            verifyCertificateTrust(results);

            verifyTimestamp(results);
    	}
        catch (WSSecurityException ex) {
            throw new Wss4jSecurityValidationException(ex.getMessage(), ex);
        }

        soapMessage.setDocument(envelopeAsDocument);

        soapMessage.getEnvelope().getHeader().removeHeaderElement(WS_SECURITY_NAME);
    }






	private void updateMessageContextWithCertificate(final MessageContext messageContext, final List<WSSecurityEngineResult> results) {
		WSSecurityEngineResult signResult = WSSecurityUtil.fetchActionResult(results, WSConstants.SIGN);
		if (signResult != null) {
			X509Certificate cert = (X509Certificate) signResult.get(WSSecurityEngineResult.TAG_X509_CERTIFICATE);
			if (cert != null) {
				messageContext.setProperty(INCOMING_CERTIFICATE, cert);
			}
		}
	}






	//Dette burde ikke være her....
	private void validateEbmsMessagingIsSigned(final String envelopePrefix, final List<WSSecurityEngineResult> results) {
    	for (WSSecurityEngineResult r : results) {
    		if (r.containsKey("data-ref-uris")) {
    			List<WSDataRef> refs = (List<WSDataRef>)r.get("data-ref-uris");
    			for(WSDataRef ref : refs) {
    				if (ref.getName().equals(Constants.MESSAGING_QNAME)) {
    					if (ref.getXpath().equals("/" + envelopePrefix + ":Envelope/" + envelopePrefix + ":Header/" + ref.getName().getPrefix() + ":" + ref.getName().getLocalPart())) {
    						return;
    					}
    				}
    			}
    		}
    	}
		throw new Wss4jSecurityValidationException("ebms:Messaging was not signed");
	}

    /**
     * Checks whether the received headers match the configured validation actions. Subclasses could override this method
     * for custom verification behavior.
     *
     *
     * @param results the results of the validation function
     * @param validationActions the decoded validation actions
     * @throws Wss4jSecurityValidationException if the results are deemed invalid
     */
    protected void checkResults(final List<WSSecurityEngineResult> results, final List<Integer> validationActions)
            throws Wss4jSecurityValidationException {
        if (!handler.checkReceiverResultsAnyOrder(results, validationActions)) {
            throw new Wss4jSecurityValidationException("Security processing failed (actions mismatch)");
        }
    }

    /**
     * Puts the results of WS-Security headers processing in the message context. Some actions like Signature
     * Confirmation require this.
     */
    @SuppressWarnings("unchecked")
    private void updateContextWithResults(final MessageContext messageContext, final List<WSSecurityEngineResult> results) {
        List<WSHandlerResult> handlerResults;
        if ((handlerResults = (List<WSHandlerResult>) messageContext.getProperty(WSHandlerConstants.RECV_RESULTS)) == null) {
            handlerResults = new ArrayList<WSHandlerResult>();
            messageContext.setProperty(WSHandlerConstants.RECV_RESULTS, handlerResults);
        }
        WSHandlerResult rResult = new WSHandlerResult(validationActor, results);
        handlerResults.add(0, rResult);
        messageContext.setProperty(WSHandlerConstants.RECV_RESULTS, handlerResults);
    }

    /** Verifies the trust of a certificate. */
    protected void verifyCertificateTrust(final List<WSSecurityEngineResult> results) throws WSSecurityException {
        WSSecurityEngineResult actionResult = WSSecurityUtil.fetchActionResult(results, WSConstants.SIGN);

        if (actionResult != null) {
            X509Certificate returnCert =
                    (X509Certificate) actionResult.get(WSSecurityEngineResult.TAG_X509_CERTIFICATE);
            Credential credential = new Credential();
            credential.setCertificates(new X509Certificate[] { returnCert});

            RequestData requestData = new RequestData();
            requestData.setSigVerCrypto(validationSignatureCrypto);
            requestData.setEnableRevocation(enableRevocation);

            SignatureTrustValidator validator = new SignatureTrustValidator();
            validator.validate(credential, requestData);
        }
    }

    /** Verifies the timestamp. */
    protected void verifyTimestamp(final List<WSSecurityEngineResult> results) throws WSSecurityException {
        WSSecurityEngineResult actionResult = WSSecurityUtil.fetchActionResult(results, WSConstants.TS);

        if (actionResult != null) {
            Timestamp timestamp = (Timestamp) actionResult.get(WSSecurityEngineResult.TAG_TIMESTAMP);
            if (timestamp != null && timestampStrict) {
                Credential credential = new Credential();
                credential.setTimestamp(timestamp);

                RequestData requestData = new RequestData();
                WSSConfig config = WSSConfig.getNewInstance();
                config.setTimeStampTTL(validationTimeToLive);
                config.setTimeStampStrict(timestampStrict);
                requestData.setWssConfig(config);

                TimestampValidator validator = new TimestampValidator();
                validator.validate(credential, requestData);
            }
        }
    }



    @Override
    protected void cleanUp() {
        if (validationCallbackHandler != null) {
            try {
                CleanupCallback cleanupCallback = new CleanupCallback();
                validationCallbackHandler.handle(new Callback[]{cleanupCallback});
            }
            catch (IOException ex) {
                logger.warn("Cleanup callback resulted in IOException", ex);
            }
            catch (UnsupportedCallbackException ex) {
                // ignore
            }
        }
    }





}
