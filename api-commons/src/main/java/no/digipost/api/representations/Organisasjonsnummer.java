package no.digipost.api.representations;

import no.digipost.api.PMode;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;


public final class Organisasjonsnummer {

    public static final String ISO6523_ACTORID = PMode.PARTY_ID_TYPE;
    public static final String ISO6523_ACTORID_OLD = "iso6523-actorid-upis";
    static final String COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY_OLD = "9908";
    static final String COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY_NEW = "0192";
    private static final Pattern ORGANIZATION_NUMBER_PATTERN = Pattern.compile("^((" + COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY_OLD + "|" + COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY_NEW + "):)?([0-9]{9})$");
    private final String organisasjonsnummer;
    private final String landkode;


    private Organisasjonsnummer(MatchResult matchedOrganisasjonsnummer) {
        int groupOfOrganizationNumber = matchedOrganisasjonsnummer.groupCount();
        this.organisasjonsnummer = matchedOrganisasjonsnummer.group(groupOfOrganizationNumber);
        this.landkode = Optional.ofNullable(matchedOrganisasjonsnummer.group(groupOfOrganizationNumber - 1))
            .orElse(COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY_OLD);
    }

    public static boolean erGyldig(String organisasjonsnummer) {
        return ORGANIZATION_NUMBER_PATTERN.matcher(organisasjonsnummer).matches();
    }

    public static Optional<Organisasjonsnummer> hvisGyldig(String organisasjonsnummer) {
        return Optional.of(ORGANIZATION_NUMBER_PATTERN.matcher(organisasjonsnummer))
                .filter(Matcher::matches)
                .map(Organisasjonsnummer::new);
    }

    public static Organisasjonsnummer of(String organisasjonsnummer) {
        Matcher matcher = ORGANIZATION_NUMBER_PATTERN.matcher(organisasjonsnummer);
        if (matcher.matches()) {
            return new Organisasjonsnummer(matcher);
        } else {
            throw new IllegalArgumentException(
                    "Ugyldig organisasjonsnummer. Forventet format er ISO 6523, men fikk følgende nummer: '" +
                            organisasjonsnummer + "'. Organisasjonsnummeret skal være 9 siffer og kan prefikses med " +
                            "enten landkode 9908 eller 0192. Eksempler på dette er '9908:984661185', '0192:984661185' " +
                            "og '984661185'.");
        }
    }

    public String getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }

    public String getOrganisasjonsnummerMedLandkode() {
        return landkode + ":" + organisasjonsnummer;
    }

    public boolean er(String organisasjonsnummerString) {
        return hvisGyldig(organisasjonsnummerString).filter(this::equals).isPresent();
    }

    public boolean erEnAv(Organisasjonsnummer... kandidater) {
        return erEnAv(asList(kandidater));
    }

    public boolean erEnAv(Collection<Organisasjonsnummer> kandidater) {
        return kandidater.contains(this);
    }

    @Override
    public String toString() {
        return organisasjonsnummer;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Organisasjonsnummer) {
            Organisasjonsnummer that = (Organisasjonsnummer) obj;
            return Objects.equals(this.organisasjonsnummer, that.organisasjonsnummer) && Objects.equals(this.landkode, that.landkode);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(organisasjonsnummer, landkode);
    }

}
