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
