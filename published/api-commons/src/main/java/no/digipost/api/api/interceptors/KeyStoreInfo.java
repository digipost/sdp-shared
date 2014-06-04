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
package no.digipost.api.api.interceptors;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

public class KeyStoreInfo {
	public final KeyStore keystore;
	public final String alias;
	public final String password;

	public KeyStoreInfo(final KeyStore keystore, final String alias, final String password) {
		this.keystore = keystore;
		this.alias = alias;
		this.password = password;
	}

	public PrivateKey getPrivateKey() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
		return (PrivateKey)keystore.getKey(alias, password.toCharArray());
	}

	public X509Certificate getCertificate() throws KeyStoreException {
		return (X509Certificate)keystore.getCertificate(alias);
	}
}
