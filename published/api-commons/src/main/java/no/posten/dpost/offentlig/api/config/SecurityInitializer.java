package no.posten.dpost.offentlig.api.config;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class SecurityInitializer {

	public static void ensureBC() {
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
	}

	public static void ensureOcspEnabled() {
		Security.setProperty("ocsp.enable", "true");
	}
}
