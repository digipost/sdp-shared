package no.digipost.api.security;

import no.digipost.api.representations.Organisasjonsnummer;

import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

public class OrgnummerExtractor {

    private static final Pattern CN_PATTERN = Pattern.compile("CN=([0-9]{9})([^0-9].*)?$");
    private static final Pattern BUYPASS_PATTERN = Pattern.compile("SERIALNUMBER=([0-9]{9})", CASE_INSENSITIVE);
    public static final Collection<Pattern> PATTERNS = Arrays.asList(CN_PATTERN, BUYPASS_PATTERN, Pattern.compile(".*"));

    public Optional<Organisasjonsnummer> tryParse(final X509Certificate cert) {
        String dn = cert.getSubjectDN().getName();
        Matcher matcher = BUYPASS_PATTERN.matcher(dn);
        if (matcher.find()) {
            return Organisasjonsnummer.hvisGyldig(matcher.group(1));
        }
        matcher = CN_PATTERN.matcher(dn);
        if (matcher.find()) {
            return Organisasjonsnummer.hvisGyldig(matcher.group(1));
        }
        return Optional.empty();
    }

    public Organisasjonsnummer from(final X509Certificate cert) {
        return tryParse(cert).orElseThrow(() -> new IllegalArgumentException(
                "Fant ikke organisasjonsnummer i [" + cert.getSubjectDN().getName() + "], " +
                        "issuer=[" + cert.getIssuerDN().getName() + "]"));
    }
}
