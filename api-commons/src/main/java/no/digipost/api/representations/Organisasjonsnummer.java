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
    private static final String COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY = "9908";
    private static final Pattern ORGANIZATION_NUMBER_PATTERN = Pattern.compile("^(" + COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY + ":)?([0-9]{9})$");
    private final String organisasjonsnummer;


    private Organisasjonsnummer(MatchResult matchedOrganisasjonsnummer) {
        int groupOfOrganizationNumber = matchedOrganisasjonsnummer.groupCount();
        this.organisasjonsnummer = matchedOrganisasjonsnummer.group(groupOfOrganizationNumber);
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
                            "landkode 9908. Eksempler på dette er '9908:984661185' og '984661185'.");
        }
    }

    public String getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }

    public String getOrganisasjonsnummerMedLandkode() {
        return COUNTRY_CODE_ORGANIZATION_NUMBER_NORWAY + ":" + organisasjonsnummer;
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
            return Objects.equals(this.organisasjonsnummer, that.organisasjonsnummer);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(organisasjonsnummer);
    }

}
