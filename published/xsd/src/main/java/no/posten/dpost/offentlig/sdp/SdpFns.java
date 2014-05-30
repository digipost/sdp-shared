package no.posten.dpost.offentlig.sdp;


import no.difi.begrep.sdp.schema_v10.SDPDigitalPostInfo;
import no.difi.begrep.sdp.schema_v10.SDPDokument;
import no.difi.begrep.sdp.schema_v10.SDPEpostVarsel;
import no.difi.begrep.sdp.schema_v10.SDPEpostVarselTekst;
import no.difi.begrep.sdp.schema_v10.SDPManifest;
import no.difi.begrep.sdp.schema_v10.SDPMottaker;
import no.difi.begrep.sdp.schema_v10.SDPPerson;
import no.difi.begrep.sdp.schema_v10.SDPSikkerhetsnivaa;
import no.difi.begrep.sdp.schema_v10.SDPSmsVarsel;
import no.difi.begrep.sdp.schema_v10.SDPTittel;
import no.difi.begrep.sdp.schema_v10.SDPVarsler;
import no.motif.f.Fn;
import no.posten.dpost.offentlig.types.TekstMedSpraak;
import no.posten.dpost.offentlig.types.Varsel;

import static no.motif.Base.first;
import static no.motif.Base.notNull;
import static no.motif.Base.when;


public final class SdpFns {

	public static final Fn<TekstMedSpraak, String> tekst = when(notNull, new Fn<TekstMedSpraak, String>() {
		@Override
		public String $(TekstMedSpraak tekstMedSpraak) {
			return tekstMedSpraak.getValue();
		}
	});

	public static final Fn<TekstMedSpraak, String> spraak = when(notNull, new Fn<TekstMedSpraak, String>() {
		@Override
		public String $(TekstMedSpraak tekstMedSpraak) {
			return tekstMedSpraak.getLang();
		}
	});

	public static final Fn<TekstMedSpraak, SDPTittel> toTittel = new Fn<TekstMedSpraak, SDPTittel>() {
		@Override
		public SDPTittel $(TekstMedSpraak tekstMedSprak) {
			return new SDPTittel(tekstMedSprak.getValue(), tekstMedSprak.getLang());
		}
	};

	public static final Fn<TekstMedSpraak, SDPEpostVarselTekst> toEpostVarselinnhold = new Fn<TekstMedSpraak, SDPEpostVarselTekst>() {
		@Override
		public SDPEpostVarselTekst $(TekstMedSpraak tekstMedSprak) {
			return new SDPEpostVarselTekst(tekstMedSprak.getValue(), tekstMedSprak.getLang());
		}
	};


	public static final Fn<SDPDigitalPostInfo, SDPSikkerhetsnivaa> sikkerhetsnivaa = when(notNull, new Fn<SDPDigitalPostInfo, SDPSikkerhetsnivaa>() {
		@Override
		public SDPSikkerhetsnivaa $(SDPDigitalPostInfo postinfo) {
			return postinfo.getSikkerhetsnivaa();
		}
	});


	public static final Fn<SDPDigitalPostInfo, SDPSmsVarsel> smsvarsel = when(notNull, new Fn<SDPDigitalPostInfo, SDPSmsVarsel>() {
		@Override
		public SDPSmsVarsel $(SDPDigitalPostInfo postinfo) {
			SDPVarsler varsler = postinfo.getVarsler();
			return varsler != null ? varsler.getSmsVarsel() : null;
		}
	});

	public static final Fn<SDPDigitalPostInfo, SDPEpostVarsel> epostvarsel = when(notNull, new Fn<SDPDigitalPostInfo, SDPEpostVarsel>() {
		@Override
		public SDPEpostVarsel $(SDPDigitalPostInfo postinfo) {
			SDPVarsler varsler = postinfo.getVarsler();
			return varsler != null ? varsler.getEpostVarsel() : null;
		}
	});


	public static final Fn<SDPDigitalPostInfo, String> tittel = first(when(notNull, new Fn<SDPDigitalPostInfo, SDPTittel>() {
		@Override
		public SDPTittel $(SDPDigitalPostInfo postinfo) {
			return postinfo.getTittel();
		}
	})).then(when(notNull, tekst));

	public static final Fn<Varsel, Iterable<Integer>> etterDager = when(notNull, new Fn<Varsel, Iterable<Integer>>() {
		@Override
		public Iterable<Integer> $(Varsel varsel) {
			return varsel.getRepetisjoner().getDagerEtters();
		}
	});


	public static final Fn<SDPManifest, SDPDokument> hoveddokument = new Fn<SDPManifest, SDPDokument>() {
		@Override
		public SDPDokument $(SDPManifest manifest) {
			return manifest.getHoveddokument();
		}
	};


	public static final Fn<SDPDokument, String> dokumentHref = new Fn<SDPDokument, String>() {
		@Override
		public String $(SDPDokument dokument) {
			return dokument.getHref();
		}
	};

	public static final Fn<SDPDokument, String> dokumentTittel = first(new Fn<SDPDokument, SDPTittel>() {
		@Override
		public SDPTittel $(SDPDokument dokument) {
			return dokument.getTittel();
		}
	}).then(tekst);


	public static final Fn<SDPManifest, String> hoveddokumentHref = first(hoveddokument).then(when(notNull, dokumentHref));
	public static final Fn<SDPManifest, String> hoveddokumentTittel = first(hoveddokument).then(when(notNull, dokumentTittel));


	public static final Fn<SDPMottaker, String> mottakerAdresse = new Fn<SDPMottaker, String>() {
		@Override
		public String $(SDPMottaker mottaker) {
			SDPPerson person = mottaker.getPerson();
			return (person != null ? person : mottaker.getVirksomhet()).getPostkasseadresse();
		}
	};


	private SdpFns() {
	}
}
