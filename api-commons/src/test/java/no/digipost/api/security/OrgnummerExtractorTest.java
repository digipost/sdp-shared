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
package no.digipost.api.security;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import no.digipost.api.representations.Organisasjonsnummer;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

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

	private final String DIFICERT = "-----BEGIN CERTIFICATE-----\n" +
			"MIIFOjCCBCKgAwIBAgIKGQqI22LuZ+0U6TANBgkqhkiG9w0BAQsFADBRMQswCQYD\n" +
			"VQQGEwJOTzEdMBsGA1UECgwUQnV5cGFzcyBBUy05ODMxNjMzMjcxIzAhBgNVBAMM\n" +
			"GkJ1eXBhc3MgQ2xhc3MgMyBUZXN0NCBDQSAzMB4XDTE0MDYxNjA4NTYyNloXDTE3\n" +
			"MDYxNjIxNTkwMFowgaAxCzAJBgNVBAYTAk5PMSwwKgYDVQQKDCNESVJFS1RPUkFU\n" +
			"RVQgRk9SIEZPUlZBTFROSU5HIE9HIElLVDEhMB8GA1UECwwYU0RQIC0gbWVsZGlu\n" +
			"Z3N1dHZla3NsaW5nMSwwKgYDVQQDDCNESVJFS1RPUkFURVQgRk9SIEZPUlZBTFRO\n" +
			"SU5HIE9HIElLVDESMBAGA1UEBRMJOTkxODI1ODI3MIIBIjANBgkqhkiG9w0BAQEF\n" +
			"AAOCAQ8AMIIBCgKCAQEAx6IPA2KSAkSupen5fFM1LEnW6CRqSK20wjpBnXf414W0\n" +
			"3eWUvBlw97c6k5sl2tYdn4aVb6Z9GeDaz1bLKN3XwhFGPk9PnjSIhrFJNAPnWVEB\n" +
			"DqGqfeMrEsYdOEgM2veBZDYkhVwipjr8AesmptTRAat61q+6hCJe8UZqjXb4Mg6Y\n" +
			"KSTAHfJdthAG06weBMgVouQkTkeIIawM+QPcKQ3Wao0gIZi17V0+8xzgDu1PXr90\n" +
			"eJ/Xjsw9t0C8Ey/3N7n3j3hplsZkjOJMBNHzbeBG/doroC6uzVURiuEn9Bc9Nk22\n" +
			"4b+7lOBZ1FvNNrJVUu5Ty3xyMDseCV7z1QTwW7wcpwIDAQABo4IBwjCCAb4wCQYD\n" +
			"VR0TBAIwADAfBgNVHSMEGDAWgBQ/rvV4C5KjcCA1X1r69ySgUgHwQTAdBgNVHQ4E\n" +
			"FgQU6JguiqDjkgjEGRHhzkbeKeqyWQEwDgYDVR0PAQH/BAQDAgSwMBYGA1UdIAQP\n" +
			"MA0wCwYJYIRCARoBAAMCMIG7BgNVHR8EgbMwgbAwN6A1oDOGMWh0dHA6Ly9jcmwu\n" +
			"dGVzdDQuYnV5cGFzcy5uby9jcmwvQlBDbGFzczNUNENBMy5jcmwwdaBzoHGGb2xk\n" +
			"YXA6Ly9sZGFwLnRlc3Q0LmJ1eXBhc3Mubm8vZGM9QnV5cGFzcyxkYz1OTyxDTj1C\n" +
			"dXlwYXNzJTIwQ2xhc3MlMjAzJTIwVGVzdDQlMjBDQSUyMDM/Y2VydGlmaWNhdGVS\n" +
			"ZXZvY2F0aW9uTGlzdDCBigYIKwYBBQUHAQEEfjB8MDsGCCsGAQUFBzABhi9odHRw\n" +
			"Oi8vb2NzcC50ZXN0NC5idXlwYXNzLm5vL29jc3AvQlBDbGFzczNUNENBMzA9Bggr\n" +
			"BgEFBQcwAoYxaHR0cDovL2NydC50ZXN0NC5idXlwYXNzLm5vL2NydC9CUENsYXNz\n" +
			"M1Q0Q0EzLmNlcjANBgkqhkiG9w0BAQsFAAOCAQEAKOTM1zSdGHWUBKPzDPYCcci9\n" +
			"cpbktd2WuBg028bRC0NwKSWUKeuUfWesTiu/P4UlYGe86qd/+z3MNpN89aGA8pr0\n" +
			"E0WpI+NM+v+Cb0dQwxHASHtrkVo9CVx6V6/QSBqIUEMfNquDHzxB2/mXbv6GuO5e\n" +
			"Il3OSVKg7Ffd/1wdE6zeMmHQO+zRpfj+OVEhNPb5cLa13Ah9+JrMkr1O7VUFbozL\n" +
			"QgFPhuI8/5+u8U/6cDOOmcFV4f4IYUmhbcLiW5MQnvaJ8044+uInOQTNtSkKmZAo\n" +
			"7Jnm4KUyhFXftJOStOHSlODOQcepVS7csszO5yWQRMTV8doEsaH5p/LBXYF56Q==\n" +
			"-----END CERTIFICATE-----";

	private final String EBOKSCERT = "-----BEGIN CERTIFICATE-----\n" +
			"MIIE+DCCA+CgAwIBAgIKGQiM/jonpcG0VTANBgkqhkiG9w0BAQsFADBRMQswCQYD\n" +
			"VQQGEwJOTzEdMBsGA1UECgwUQnV5cGFzcyBBUy05ODMxNjMzMjcxIzAhBgNVBAMM\n" +
			"GkJ1eXBhc3MgQ2xhc3MgMyBUZXN0NCBDQSAzMB4XDTE0MDYxMjEzNTYzOFoXDTE3\n" +
			"MDYxMjIxNTkwMFowXzELMAkGA1UEBhMCTk8xEjAQBgNVBAoMCUUtQk9LUyBBUzEU\n" +
			"MBIGA1UECwwLT3BlcmF0aW9uIDExEjAQBgNVBAMMCUUtQk9LUyBBUzESMBAGA1UE\n" +
			"BRMJOTk2NDYwMzIwMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArDwI\n" +
			"/8AEOlml4abZt+zXRTxQuzuWTVx8QS2a2zE0BdUE+PO3K8QQpfPzIZVHSrhiDr03\n" +
			"VRW2zJ5qz2peGhwNw1BRBltndJLuSJBqSdfJ2TbayoBQoHJkg7YvPi11LsM2aYE7\n" +
			"5tiKN/FUqKIgqMiOz0rbTyjOcNI1cD6ZC0xskZN1ONJqG5Jxqc3NOpPTco/YA7s4\n" +
			"1v1gUPdPfoXlu5tgnmiMh4Ixwr7x7FK80aj3Akg0eWmHI8P1IxJU8hJI6sthYO0Z\n" +
			"2d8RCLeXIc4pXAkRBvgKC8I8HEYk6pDxR3UvFlwC96Mj4Ne0EN8yo3ODtT1chPp7\n" +
			"iyUPiDvNhqSRrp8GEQIDAQABo4IBwjCCAb4wCQYDVR0TBAIwADAfBgNVHSMEGDAW\n" +
			"gBQ/rvV4C5KjcCA1X1r69ySgUgHwQTAdBgNVHQ4EFgQUBL6S6KHLV/uxUDs5bB6n\n" +
			"3jZUP/4wDgYDVR0PAQH/BAQDAgSwMBYGA1UdIAQPMA0wCwYJYIRCARoBAAMCMIG7\n" +
			"BgNVHR8EgbMwgbAwN6A1oDOGMWh0dHA6Ly9jcmwudGVzdDQuYnV5cGFzcy5uby9j\n" +
			"cmwvQlBDbGFzczNUNENBMy5jcmwwdaBzoHGGb2xkYXA6Ly9sZGFwLnRlc3Q0LmJ1\n" +
			"eXBhc3Mubm8vZGM9QnV5cGFzcyxkYz1OTyxDTj1CdXlwYXNzJTIwQ2xhc3MlMjAz\n" +
			"JTIwVGVzdDQlMjBDQSUyMDM/Y2VydGlmaWNhdGVSZXZvY2F0aW9uTGlzdDCBigYI\n" +
			"KwYBBQUHAQEEfjB8MDsGCCsGAQUFBzABhi9odHRwOi8vb2NzcC50ZXN0NC5idXlw\n" +
			"YXNzLm5vL29jc3AvQlBDbGFzczNUNENBMzA9BggrBgEFBQcwAoYxaHR0cDovL2Ny\n" +
			"dC50ZXN0NC5idXlwYXNzLm5vL2NydC9CUENsYXNzM1Q0Q0EzLmNlcjANBgkqhkiG\n" +
			"9w0BAQsFAAOCAQEARj4WegvcMeqvt8R2BxB/uoNIjATmoUxlUc1f/vLkqq0fNGMt\n" +
			"RDAJWlQJ26P6Q+05G+85mK0DkRNWEjZNnX/NzMijygYwgHc0KukMoIVfYngc02Vn\n" +
			"p2QNk5YC+EGF3WjtuD9D653WkA/eKXNGEkyKPO4Okgr5akDWqUORH2ZvgyIg+r/f\n" +
			"AScTxj8YhAdooXBh5TSQqWyyCLxspY7TY/qiQ5Yk1nQTUIkrBh3UD2VSeR+ymozO\n" +
			"9DxzboFRh87BgoT0c9scVo7yWpEkMcjUdZnpvqDQ0vtKFHz/VR7JfRFWpx7JG4Cs\n" +
			"xDCnMjfCd/jSllWUjrUmKVj7es8CqXcQnjTUZg==\n" +
			"-----END CERTIFICATE-----";


	@Test
	public void skal_hente_ut_riktig_org_nummer_fra_X509sertifikat() throws Exception {
		sjekkOrgnummer(PROD_VIRKSOMHETSSERTIFIKAT, "984661185");
	}

	@Test
	public void skal_hente_ut_riktig_org_nummer_fra_X509sertifikat_for_difi() throws Exception {
		sjekkOrgnummer(DIFICERT, "991825827");
	}

	@Test
	public void skal_hente_ut_riktig_org_nummer_fra_X509sertifikat_for_eboks() throws Exception {
		sjekkOrgnummer(EBOKSCERT, "996460320");
	}


	private void sjekkOrgnummer(final String prodVirksomhetssertifikat, final String orgnummer) throws CertificateException, NoSuchProviderException {
		Security.addProvider(new BouncyCastleProvider());
		CertificateFactory cf = CertificateFactory.getInstance("X.509", BouncyCastleProvider.PROVIDER_NAME);
		X509Certificate certificate = (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(prodVirksomhetssertifikat.getBytes()));
		OrgnummerExtractor extractor = new OrgnummerExtractor();
		Organisasjonsnummer orgNr = extractor.from(certificate);
		assertEquals(orgnummer, orgNr.toString());
	}

}
