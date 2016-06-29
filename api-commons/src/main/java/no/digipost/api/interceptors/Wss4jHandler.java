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

import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.engine.WSSecurityEngineResult;
import org.apache.wss4j.dom.handler.HandlerAction;
import org.apache.wss4j.dom.handler.RequestData;
import org.apache.wss4j.dom.handler.WSHandler;
import org.apache.wss4j.dom.handler.WSHandlerConstants;
import org.springframework.ws.context.MessageContext;
import org.w3c.dom.Document;

import java.util.List;
import java.util.Properties;

public class Wss4jHandler extends WSHandler {

    /** Keys are constants from {@link WSHandlerConstants}; values are strings. */
    private final Properties options = new Properties();

    private String securementPassword;

    private Crypto securementEncryptionCrypto;

    private Crypto securementSignatureCrypto;

    Wss4jHandler() {
        // set up default handler properties
        options.setProperty(WSHandlerConstants.MUST_UNDERSTAND, Boolean.toString(true));
        options.setProperty(WSHandlerConstants.ENABLE_SIGNATURE_CONFIRMATION, Boolean.toString(true));
    }

    @Override
    protected boolean checkReceiverResultsAnyOrder(final List<WSSecurityEngineResult> wsResult, final List<Integer> actions) {
        return super.checkReceiverResultsAnyOrder(wsResult, actions);
    }

    void setOption(final String key, final String value) {
        options.setProperty(key, value);
    }

    void setOption(final String key, final boolean value) {
        options.setProperty(key, Boolean.toString(value));
    }

    @Override
    public Object getOption(final String key) {
        return options.getProperty(key);
    }

    void setSecurementPassword(final String securementPassword) {
        this.securementPassword = securementPassword;
    }

    void setSecurementEncryptionCrypto(final Crypto securementEncryptionCrypto) {
        this.securementEncryptionCrypto = securementEncryptionCrypto;
    }

    void setSecurementSignatureCrypto(final Crypto securementSignatureCrypto) {
        this.securementSignatureCrypto = securementSignatureCrypto;
    }

    @Override
    public String getPassword(final Object msgContext) {
        return securementPassword;
    }

    @Override
    public Object getProperty(final Object msgContext, final String key) {
        return ((MessageContext) msgContext).getProperty(key);
    }

    @Override
    protected Crypto loadEncryptionCrypto(final RequestData reqData) throws WSSecurityException {
        return securementEncryptionCrypto;
    }

    @Override
    public Crypto loadSignatureCrypto(final RequestData reqData) throws WSSecurityException {
        return securementSignatureCrypto;
    }

    @Override
    public void setPassword(final Object msgContext, final String password) {
        securementPassword = password;
    }

    @Override
    public void setProperty(final Object msgContext, final String key, final Object value) {
        ((MessageContext) msgContext).setProperty(key, value);
    }

    @Override
    protected void doSenderAction(final Document doc, final RequestData reqData, final List<HandlerAction> actions, final boolean isRequest)
    		throws WSSecurityException {
    	super.doSenderAction(doc, reqData, actions, isRequest);
    }

}