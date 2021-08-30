package no.digipost.api.interceptors;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.security.KeyStore;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.sameInstance;

public class KeyStoreInfoTest {
    static final String alias = "avsender";
    static final String password = "password1234";

    @Test
    public void keyStoreInfo_withOnlyKeyStore_shouldSetTrustStoreAsSame() {

        KeyStore keystore = getSelfsignedKeyStore();

        KeyStoreInfo keyStoreInfo = new KeyStoreInfo(keystore, alias, password);

        assertThat(keystore, sameInstance(keyStoreInfo.keystore));
        assertThat(keystore, sameInstance(keyStoreInfo.trustStore));
    }

    @Test
    public void keyStoreInfo_withKeyStoreAndTrustStore_areNotEqual() {
        KeyStore keyStore = getSelfsignedKeyStore();
        KeyStore trustStore = getSelfsignedKeyStore();

        KeyStoreInfo keyStoreInfo = new KeyStoreInfo(keyStore, trustStore, alias, password);

        assertThat(keyStoreInfo.keystore, sameInstance(keyStore));
        assertThat(keyStoreInfo.trustStore, sameInstance(trustStore));
        assertThat(keyStoreInfo.keystore, not(equalTo(trustStore)));
    }


    @Test
    public void testGetPrivateKey() throws Exception {
        KeyStoreInfo keyStoreInfo = getKeyStoreInfo();

        assertThat(keyStoreInfo.getPrivateKey(), notNullValue());
    }

    @Test
    public void testGetCertificate() throws Exception {
        KeyStoreInfo keyStoreInfo = getKeyStoreInfo();

        assertThat(keyStoreInfo.getCertificate(), notNullValue());
    }

    @Test
    public void testGetCertificateChain() throws Exception {
        KeyStoreInfo keyStoreInfo = getKeyStoreInfo();

        assertThat(keyStoreInfo.getCertificateChain(), arrayWithSize(1));
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
