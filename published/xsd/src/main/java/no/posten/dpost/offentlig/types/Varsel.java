package no.posten.dpost.offentlig.types;

import no.difi.begrep.sdp.schema_v10.SDPRepetisjoner;

public interface Varsel {
	TekstMedSpraak getTekst();
	SDPRepetisjoner getRepetisjoner();
}
