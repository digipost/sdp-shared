package no.posten.dpost.offentlig.api;

import no.posten.dpost.offentlig.api.interceptors.KeyStoreInfo;
import no.posten.dpost.offentlig.api.representations.ApplikasjonsKvitteringBuilder;
import no.posten.dpost.offentlig.api.representations.EbmsApplikasjonsKvittering;
import no.posten.dpost.offentlig.api.representations.Organisasjonsnummer;
import org.springframework.core.io.ClassPathResource;

import java.security.KeyStore;
import java.util.UUID;

public class ReceiptSenderUtil {
	public static final Organisasjonsnummer AVSENDER = new Organisasjonsnummer("889640782");
	public static final Organisasjonsnummer MELDINGSFORMIDLER = new Organisasjonsnummer("984661185");
	public static final Organisasjonsnummer DIGIPOST = new Organisasjonsnummer("984661185");


	public static void main(final String[] args) throws Exception {
		KeyStore ks = KeyStore.getInstance("JCEKS");
		ks.load(new ClassPathResource("/meldingsformidler.qa.jce").getInputStream(), "abcd1234".toCharArray());
		KeyStoreInfo keyStoreInfo = new KeyStoreInfo(ks, "meldingsformidler", "abcd1234");
		final MessageSender sender = MessageSender.create("https://qaoffentlig.meldingsformidler.digipost.no/api/", keyStoreInfo, DIGIPOST, MELDINGSFORMIDLER).build();
		EbmsApplikasjonsKvittering leveranse = ApplikasjonsKvitteringBuilder.create(DIGIPOST, AVSENDER, UUID.randomUUID().toString(), "31415926535987", UUID.randomUUID().toString()).build();
		sender.send(leveranse);
	}
}
