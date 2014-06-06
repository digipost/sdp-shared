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
package no.digipost.api.xml;

import org.springframework.core.io.ClassPathResource;

public class Schemas {

	public static final ClassPathResource SBDH_SCHEMA = new ClassPathResource("SBDH20040506-02/StandardBusinessDocumentHeader.xsd");
	public static final ClassPathResource SDP_SCHEMA = new ClassPathResource("sdp.xsd");
	public static final ClassPathResource EBMS_SCHEMA = new ClassPathResource("ebxml/ebms-header-3_0-200704.xsd");
	public static final ClassPathResource XMLDSIG_SCHEMA = new ClassPathResource("w3/xmldsig-core-schema.xsd");

}
