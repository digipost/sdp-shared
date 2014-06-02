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
package no.posten.dpost.offentlig.api.config;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Error;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.MessageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.soap.SoapFault;

import static no.posten.dpost.offentlig.api.config.TransaksjonsLogg.Retning.INNKOMMENDE;
import static no.posten.dpost.offentlig.api.config.TransaksjonsLogg.Retning.UTGÅENDE;
import static no.posten.dpost.offentlig.api.config.TransaksjonsLogg.Type.EBMSFEIL;
import static no.posten.dpost.offentlig.api.config.TransaksjonsLogg.Type.SOAPFAULT;
import static no.posten.dpost.offentlig.api.exceptions.ebms.standard.processing.EmptyMessagePartitionChannelException.EMPTY_MPC_EBMS_CODE;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

public class TransaksjonsLogg {

	private static final String N_A = "";
	public static final String MF_LOGGER_TRANSLOG = "mf.logger.translog";
	private static final Logger LOG = LoggerFactory.getLogger(MF_LOGGER_TRANSLOG);

	public void innkommende(final String endpoint, final String orgnr, final Type type, final String mpc, final String conversationId, final String instanceIdentifier, final String messageId, final String refToMessageId) {
		log(endpoint, orgnr, INNKOMMENDE, type, mpc, conversationId, instanceIdentifier, messageId, refToMessageId, "");
	}

	public void utgående(final String endpoint, final String orgnr, final Type type, final String mpc, final String conversationId, final String instanceIdentifier, final String messageId, final String refToMessageId) {
		log(endpoint, orgnr, UTGÅENDE, type, mpc, conversationId, instanceIdentifier, messageId, refToMessageId, "");
	}

	public void log(final String endpoint, final String orgnr, final Retning retning, final Type type, final String mpc, final String conversationId, final String instanceIdentifier, final String messageId, final String refToMessageId, final String extra) {
		String ref = refToMessageId != null ? "ref:[" + refToMessageId + "]" : "";
		LOG.info("[{}] [{}] {} {} mpc:[{}] conversationId:[{}] instanceId:[{}] messageId:[{}] {} {}",
				endpoint, toLoggable(orgnr), retning, type, toLoggable(mpc), toLoggable(conversationId), toLoggable(instanceIdentifier), messageId, ref, extra);
	}

	public void soapfault(final String endpoint, final String orgnr, final Retning retning, final SoapFault soapFault) {
		LOG.warn("[{}] [{}] {} fault:[{}]", endpoint, toLoggable(orgnr), SOAPFAULT, soapFault.getFaultStringOrReason());
	}
	private String toLoggable(final String s) {
		return defaultIfEmpty(s, N_A);
	}


	public void ebmserror(final String endpoint, final String orgnr, final Retning retning, final Error error, final MessageInfo messageInfo, final String mpc, final String conversationId, final String instanceIdentifier) {
		if (EMPTY_MPC_EBMS_CODE.equals(error.getErrorCode())) {
			log(endpoint, orgnr, retning, EBMSFEIL, mpc, conversationId, instanceIdentifier, messageInfo.getMessageId(), messageInfo.getRefToMessageId(), "");
			return;
		}
		String errorMsg = String.format("error:[%s][%s]", error.getShortDescription(), error.getDescription().getValue());
		log(endpoint, orgnr, retning, EBMSFEIL, mpc, conversationId, instanceIdentifier, messageInfo.getMessageId(), messageInfo.getRefToMessageId(), errorMsg);
	}



	public enum Retning {
		INNKOMMENDE, UTGÅENDE
	}

	public enum Type {
		USERMESSAGE, PULLREQUEST, TRANSPORTKVITTERING, APPLIKASJONSKVITTERING, TOMKØ, EBMSFEIL, SOAPFAULT
	}
}
