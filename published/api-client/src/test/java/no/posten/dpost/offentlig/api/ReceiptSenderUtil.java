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
package no.posten.dpost.offentlig.api;

import no.posten.dpost.offentlig.api.interceptors.KeyStoreInfo;
import no.posten.dpost.offentlig.api.representations.ApplikasjonsKvitteringBuilder;
import no.posten.dpost.offentlig.api.representations.EbmsApplikasjonsKvittering;
import org.springframework.core.io.ClassPathResource;

import java.security.KeyStore;
import java.util.UUID;

public class ReceiptSenderUtil {


	public static void main(final String[] args) throws Exception {
		KeyStore ks = KeyStore.getInstance("JCEKS");
		ks.load(new ClassPathResource("/meldingsformidler.qa.jce").getInputStream(), "abcd1234".toCharArray());
		KeyStoreInfo keyStoreInfo = new KeyStoreInfo(ks, "meldingsformidler", "abcd1234");
		final MessageSender sender = MessageSender.create("https://qaoffentlig.meldingsformidler.digipost.no/api/", keyStoreInfo, MessageSenderUtil.DIGIPOST, MessageSenderUtil.MELDINGSFORMIDLER).build();
		EbmsApplikasjonsKvittering leveranse = ApplikasjonsKvitteringBuilder.create(MessageSenderUtil.DIGIPOST, MessageSenderUtil.MELDINGSFORMIDLER, MessageSenderUtil.AVSENDER.orgnr, UUID.randomUUID().toString(), "31415926535987", UUID.randomUUID().toString()).build();
		sender.send(leveranse);
	}
}
