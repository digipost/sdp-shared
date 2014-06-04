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
package no.digipost.api.api.security;

import no.digipost.api.api.representations.Organisasjonsnummer;
import no.digipost.api.api.security.OrgnummerExtractor;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import static org.junit.Assert.assertEquals;

public class OrgnummerExtractorTest {
	private static final String PROD_VIRKSOMHETSSERTIFIKAT = "-----BEGIN CERTIFICATE-----" + "\n"
			+ "MIIElTCCA32gAwIBAgIDDUL8MA0GCSqGSIb3DQEBBQUAMEsxCzAJBgNVBAYTAk5P" + "\n"
			+ "MR0wGwYDVQQKDBRCdXlwYXNzIEFTLTk4MzE2MzMyNzEdMBsGA1UEAwwUQnV5cGFz" + "\n"
			+ "cyBDbGFzcyAzIENBIDEwHhcNMTEwNTEzMTI0MjU3WhcNMTQwNTEzMTI0MjUzWjBo" + "\n"
			+ "MQswCQYDVQQGEwJOTzEYMBYGA1UECgwPUE9TVEVOIE5PUkdFIEFTMREwDwYDVQQL" + "\n"
			+ "DAhEaWdpcG9zdDEYMBYGA1UEAwwPUE9TVEVOIE5PUkdFIEFTMRIwEAYDVQQFEwk5" + "\n"
			+ "ODQ2NjExODUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC68mb3nPMY" + "\n"
			+ "KPN+jOLJCQ8x2it9PxMh8eiZ0cr5oK7C2fPs/3ywwWpav1yQdsLc5zSMQupzmqXN" + "\n"
			+ "OoAirP42sO+MonAmsBbgAmJfKepQCBg5CX3qZUuId6kn7BZcPIMzTWF44wc5UrF1" + "\n"
			+ "TlE2Ib/1ZS05lJCyLkdqpobBBKXoh2PP3xai5hFo8i+bohhyR+RWNxDnjPyp1mMX" + "\n"
			+ "2bLjmD+/g/0QQM3SbwKdjmy7Ylr/2EDrAQ+S0OSjR3G0BnpRCOvhVLcXfjDM1GWq" + "\n"
			+ "KIWKnQYFSTMB1cSpkqk9AxMY3MNT/DwUFWOihVlWvVWGjKrTBJC7FZpIqJBchNF4" + "\n"
			+ "hiWv4iNI7vYfAgMBAAGjggFjMIIBXzAJBgNVHRMEAjAAMB8GA1UdIwQYMBaAFDgU" + "\n"
			+ "5sjwqaQD9E4+IqNb8tbgrUB0MB0GA1UdDgQWBBQQPwlTXTPvyhFL30Sf3TxwhBHM" + "\n"
			+ "KjAOBgNVHQ8BAf8EBAMCBLAwFQYDVR0gBA4wDDAKBghghEIBGgEDAjCBpQYDVR0f" + "\n"
			+ "BIGdMIGaMC+gLaArhilodHRwOi8vY3JsLmJ1eXBhc3Mubm8vY3JsL0JQQ2xhc3Mz" + "\n"
			+ "Q0ExLmNybDBnoGWgY4ZhbGRhcDovL2xkYXAuYnV5cGFzcy5uby9kYz1CdXlwYXNz" + "\n"
			+ "LGRjPU5PLENOPUJ1eXBhc3MlMjBDbGFzcyUyMDMlMjBDQSUyMDE/Y2VydGlmaWNh" + "\n"
			+ "dGVSZXZvY2F0aW9uTGlzdDBDBggrBgEFBQcBAQQ3MDUwMwYIKwYBBQUHMAGGJ2h0" + "\n"
			+ "dHA6Ly9vY3NwLmJ1eXBhc3Mubm8vb2NzcC9CUENsYXNzM0NBMTANBgkqhkiG9w0B" + "\n"
			+ "AQUFAAOCAQEAivJd3hg5+16QOv638JlKixMivlZjbtAj8TGDKhnB6sXBw4bNbHQS" + "\n"
			+ "GDVdO07JKzBeYcohiYKPSn6+6NAEhJaetwVrhZgMQxNluUSOj+KSxzVVD6NLC3ga" + "\n"
			+ "wswK6i3OruBvpynXViNVCTjlmQzi/4pp5NjRNFcbJrfeONZwzClmIqhJorDqhw2T" + "\n"
			+ "/55OBEC+FxtQ9bEFBwHT0Qrx4L+HHJP7Vkk0CLWY5Ib89PZHFke/X/ad/HEla8F3" + "\n"
			+ "UjKB02xxs2OKEmE0gQrn3SYjOtONfQDK377RiQPiY3eKV4CBVUcidbfqPyjY/rbZ" + "\n" //
			+ "TaPOXIYao3VFv6RCTO80zn1qw1JqGvDgNg==" + "\n" //
			+ "-----END CERTIFICATE-----";

	@Test
	public void skal_hente_ut_riktig_org_nummer_fra_X509sertifikat() throws Exception {
		Security.addProvider(new BouncyCastleProvider());
		CertificateFactory cf = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
		X509Certificate certificate = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(PROD_VIRKSOMHETSSERTIFIKAT.getBytes()));
		OrgnummerExtractor extractor = new OrgnummerExtractor();
		Organisasjonsnummer orgNr = extractor.from(certificate);
		assertEquals("984661185", orgNr.toString());
	}

}
