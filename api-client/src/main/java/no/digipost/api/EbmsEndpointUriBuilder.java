package no.digipost.api;

import no.digipost.api.representations.EbmsAktoer;

import java.net.URI;

/**
 * Brukes av en {@link MessageSender} til å konstruere faktisk URI <em>per melding</em>
 * som benyttes til å sende EBMS-meldinger til.
 */
public interface EbmsEndpointUriBuilder {

    static EbmsEndpointUriBuilder meldingsformidlerUri(String baseUri) {
        return meldingsformidlerUri(URI.create(baseUri));
    }

    static EbmsEndpointUriBuilder meldingsformidlerUri(URI baseUri) {
        return new MeldingsformidlerUri(baseUri);
    }

    static EbmsEndpointUriBuilder statiskUri(String endpointUri) {
        return statiskUri(URI.create(endpointUri));
    }

    static EbmsEndpointUriBuilder statiskUri(URI endpoint) {
        return new StatiskUri(endpoint);
    }

    URI getBaseUri();

    URI build(EbmsAktoer tekniskAvsender, EbmsAktoer avsender);


    final class StatiskUri implements EbmsEndpointUriBuilder {

        private URI uri;

        private StatiskUri(URI uri) {
            this.uri = uri;
        }

        @Override
        public URI getBaseUri() {
            return uri;
        }

        @Override
        public URI build(EbmsAktoer tekniskAvsender, EbmsAktoer avsender) {
            return getBaseUri();
        }
    }

    final class MeldingsformidlerUri implements EbmsEndpointUriBuilder {

        private URI uri;

        private MeldingsformidlerUri(URI uri) {
            this.uri = uri;
        }

        @Override
        public URI getBaseUri() {
            return uri;
        }

        @Override
        public URI build(EbmsAktoer databehandler, EbmsAktoer avsender) {
            return URI.create(String.format("%s/%s/%s",
                    getBaseUri().toString().replaceFirst("/$", ""),
                    databehandler.orgnr.getOrganisasjonsnummerMedLandkode(),
                    avsender.orgnr.getOrganisasjonsnummerMedLandkode()
            ));
        }
    }

}
