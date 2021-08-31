package no.digipost.api.security;

import no.digipost.api.representations.Organisasjonsnummer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static javax.security.auth.x500.X500Principal.RFC1779;

public class OrgnummerExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(OrgnummerExtractor.class);

    private static final Pattern CN_PATTERN = Pattern.compile("CN=([0-9]{9})([^0-9].*)?$");

    private static final Pattern BUYPASS_PATTERN = Pattern.compile("OID\\.2\\.5\\.4\\.5=([0-9]{9})", CASE_INSENSITIVE);

    private static final Pattern SEID2_PATTERN = Pattern.compile("OID\\.2\\.5\\.4\\.97=(?:NTRNO-)?([0-9]{9})", CASE_INSENSITIVE);


    public static final Collection<Pattern> PATTERNS = Arrays.asList(SEID2_PATTERN, CN_PATTERN, BUYPASS_PATTERN, Pattern.compile(".*"));

    public Optional<Organisasjonsnummer> tryParse(X509Certificate cert) {
        String dn = cert.getSubjectX500Principal().getName(RFC1779);
        return Stream.of(SEID2_PATTERN, BUYPASS_PATTERN, CN_PATTERN)
                .map(pattern -> tryFindOrgnr(dn, pattern))
                .filter(Optional::isPresent).map(Optional::get)
                .findFirst()
                .flatMap(Organisasjonsnummer::hvisGyldig);
    }

    public Organisasjonsnummer from(X509Certificate cert) {
        return tryParse(cert).orElseThrow(() -> new IllegalArgumentException(
                "Fant ikke organisasjonsnummer i [" + cert.getSubjectDN().getName() + "], " +
                        "issuer=[" + cert.getIssuerDN().getName() + "]"));
    }

    private static final Optional<String> tryFindOrgnr(CharSequence text, Pattern extractPattern) {
        Optional<String> extracted = Optional.of(text).map(extractPattern::matcher).filter(Matcher::find).map(m -> m.group(1));
        if (!extracted.isPresent() && LOG.isTraceEnabled()) {
            LOG.trace("Orgnr ikke funnet i '{}' v.h.a. regex '{}'", text, extractPattern);
        }
        return extracted;
    }
}
