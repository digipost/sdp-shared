package no.posten.dpost.offentlig.api;

import no.difi.begrep.sdp.schema_v10.SDPAvsender;
import no.difi.begrep.sdp.schema_v10.SDPDigitalPost;
import no.difi.begrep.sdp.schema_v10.SDPDigitalPostInfo;
import no.difi.begrep.sdp.schema_v10.SDPMottaker;
import no.difi.begrep.sdp.schema_v10.SDPOrganisasjon;
import no.difi.begrep.sdp.schema_v10.SDPPerson;
import no.difi.begrep.sdp.schema_v10.SDPSikkerhetsnivaa;
import no.difi.begrep.sdp.schema_v10.SDPTittel;
import no.posten.dpost.offentlig.api.interceptors.KeyStoreInfo;
import no.posten.dpost.offentlig.api.representations.Dokumentpakke;
import no.posten.dpost.offentlig.api.representations.EbmsAktoer;
import no.posten.dpost.offentlig.api.representations.EbmsForsendelse;
import no.posten.dpost.offentlig.api.representations.Organisasjonsnummer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.soap.client.SoapFaultClientException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;


public class MessageSenderUtil {

	public static final EbmsAktoer AVSENDER = new EbmsAktoer(new Organisasjonsnummer("984661185"), EbmsAktoer.Rolle.AVSENDER);
	public static final EbmsAktoer MELDINGSFORMIDLER = new EbmsAktoer(new Organisasjonsnummer("984661185"), EbmsAktoer.Rolle.MELDINGSFORMIDLER);
	public static final EbmsAktoer DIGIPOST = new EbmsAktoer(new Organisasjonsnummer("984661185"), EbmsAktoer.Rolle.POSTKASSE);
	public static final EbmsAktoer LOOPBACK = new EbmsAktoer(new Organisasjonsnummer("123456789"), EbmsAktoer.Rolle.POSTKASSE);
	private static AtomicInteger counter = new AtomicInteger();

	public static void main(final String[] args) throws Exception {

		int nbThreads = 1;
		int nbIterations = 1;
		boolean failFast = true;

		if (args.length == 3) {
			nbThreads = Integer.parseInt(args[0]);
			nbIterations = Integer.parseInt(args[1]);
			failFast = Boolean.parseBoolean(args[2]);
		} else {
			System.err.println("Tips! kan kjøres med 3 argumenter, #tråder, #iterasjoner og failFast(true|false)");
		}

		System.err.println(format("Bruker %s tråder, %s iterasjoner og failFast %s", nbThreads, nbIterations, failFast));

		final int finalNbIterations = nbIterations;
		final boolean finalFailFast = failFast;

		KeyStore ks = KeyStore.getInstance("JCEKS");
		ks.load(new ClassPathResource("/meldingsformidler.qa.jce").getInputStream(), "abcd1234".toCharArray());
		KeyStoreInfo keyStoreInfo = new KeyStoreInfo(ks, "meldingsformidler", "abcd1234");
		final MessageSender sender = MessageSender
				.create("https://qaoffentlig.meldingsformidler.digipost.no/api/", keyStoreInfo, AVSENDER, MELDINGSFORMIDLER)
				.withConnectionRequestTimeout(5000)
				.withConnectTimeout(5000)
				.withSocketTimeout(10000)
				.withMaxTotal(100)
				.withDefaultMaxPerRoute(100)
				.build();
//		final MessageSender sender = MessageSender.create("http://localhost:8049", keyStoreInfo, AVSENDER, MELDINGSFORMIDLER).build();
		List<Thread> threads = new ArrayList<Thread>();
		final long now = System.currentTimeMillis();
		for (int i = 0; i < nbThreads; i++) {
			Runnable r = new Runnable() {
				@Override
				public void run() {
					for (int j = 0; j < finalNbIterations; j++) {
						EbmsForsendelse forsendelse = lagForsendelse(AVSENDER, LOOPBACK.orgnr);
						long start = System.currentTimeMillis();
						try {
							sender.send(forsendelse);
						} catch (SoapFaultClientException e) {
							if (finalFailFast) {
								System.err.println(getStringFromDocument(e.getSoapFault().getSource()));
								throw e;
							}
						}
						System.err.println("Tid brukt på melding: " + (System.currentTimeMillis() - start));
						int c = counter.incrementAndGet();
						if (c % 50 == 0) {
							System.err.println(c + " Meldinger i sekundet: " + ((double) c * 1000) / (System.currentTimeMillis() - now));
						}
					}
				}
			};
			Thread t = new Thread(r);
			threads.add(t);
			t.start();
		}
		for (Thread t : threads) {
			t.join();
		}
		long timeTook = System.currentTimeMillis() - now;
		System.err.println(counter.get() + " " + timeTook + "ms " + (double) counter.get() / timeTook * 1000 + "m/s");
	}

	public static EbmsForsendelse lagForsendelse(final EbmsAktoer avsender, final Organisasjonsnummer postkasse) {
		byte[] dataz = new byte[100000];
		SDPDigitalPost sdp = new SDPDigitalPost()
				.withAvsender(new SDPAvsender().withOrganisasjon(new SDPOrganisasjon().withValue(avsender.orgnr.asIso6523())))
				.withMottaker(new SDPMottaker().withPerson(new SDPPerson()
								.withPersonidentifikator("01013300001")
								.withMobiltelefonnummer("12345678")
								.withEpostadresse("ola@nordmann.no")
								.withPostkasseadresse("jarand.bjarte.t.k.grindheim#7RF1"))
				)
				.withDigitalPostInfo(new SDPDigitalPostInfo()
								.withSikkerhetsnivaa(SDPSikkerhetsnivaa.NIVAA_3)
								.withTittel(new SDPTittel("Digital post fra Postmann P@t", "no"))
				);
		return EbmsForsendelse.create(avsender, MELDINGSFORMIDLER, postkasse, sdp, new Dokumentpakke(new ByteArrayInputStream(dataz))).build();
	}


	public static String getStringFromDocument(final Source doc) {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		StringWriter writer = new StringWriter();
		try {
			transformer.transform(doc, new StreamResult(writer));
		} catch (TransformerException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return writer.getBuffer().toString().replaceAll("\n|\r", "");
	}

}
