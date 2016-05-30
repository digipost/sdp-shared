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

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.security.KeyStore;

import static org.junit.Assert.*;
import static org.fest.assertions.api.Assertions.assertThat;

public class KeyStoreInfoTest {
	static final String alias = "avsender";
	static final String password = "password1234";

	@Test
	public void keyStoreInfo_withOnlyKeyStore_shouldSetTrustStoreAsSame() {

		KeyStore keystore = getSelfsignedKeyStore();

		KeyStoreInfo keyStoreInfo = new KeyStoreInfo(keystore,alias,password);

		assertThat(keystore).isEqualTo(keyStoreInfo.keystore);
		assertThat(keystore).isEqualTo(keyStoreInfo.trustStore);
	}

	@Test
	public void keyStoreInfo_withKeyStoreAndTrustStore_areNotEqual(){
		KeyStore keyStore = getSelfsignedKeyStore();
		KeyStore trustStore = getSelfsignedKeyStore();

		KeyStoreInfo keyStoreInfo = new KeyStoreInfo(keyStore,trustStore,alias,password);

		assertThat(keyStoreInfo.keystore).isEqualTo(keyStore);
		assertThat(keyStoreInfo.trustStore).isEqualTo(trustStore);
		assertThat(keyStoreInfo.keystore).isNotEqualTo(trustStore);
	}


	@Test
	public void testGetPrivateKey() throws Exception {
		KeyStoreInfo keyStoreInfo = getKeyStoreInfo();

		assertThat(keyStoreInfo.getPrivateKey()).isNotNull();
	}

	@Test
	public void testGetCertificate() throws Exception {
		KeyStoreInfo keyStoreInfo = getKeyStoreInfo();

		assertThat(keyStoreInfo.getCertificate()).isNotNull();
	}

	@Test
	public void testGetCertificateChain() throws Exception {
		KeyStoreInfo keyStoreInfo = getKeyStoreInfo();

		assertThat(keyStoreInfo.getCertificateChain()).isNotNull().hasSize(1);
	}

	private KeyStoreInfo getKeyStoreInfo() {
		KeyStore keyStore = getSelfsignedKeyStore();
		return new KeyStoreInfo(keyStore, alias, password);
	}

	private KeyStore getSelfsignedKeyStore() {
		try {
			KeyStore keyStore = KeyStore.getInstance("jks");
			keyStore.load(new ClassPathResource("/selfsigned-keystore.jks").getInputStream(), "password1234".toCharArray());
			return keyStore;

		} catch (Exception e) {
			throw new RuntimeException("Kunne ikke laste keystore", e);
		}
	}
}
