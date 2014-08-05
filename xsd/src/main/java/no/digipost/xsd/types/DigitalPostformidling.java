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
package no.digipost.xsd.types;

import no.difi.begrep.sdp.schema_v10.*;
import org.w3.xmldsig.Reference;
import org.w3.xmldsig.Signature;


/**
 * Felles type for meldinger som har med formidling av post å gjøre, hvor det
 * vanligste er {@link SDPDigitalPost vanlig post som sendes fra avsender til mottaker},
 * men også {@link SDPFlyttetDigitalPost flyttet post fra en postkasseleverandør til en annen}.
 */
public interface DigitalPostformidling {

	Signature getSignature();
	SDPAvsender getAvsender();
	SDPMottaker getMottaker();
	SDPDigitalPostInfo getDigitalPostInfo();

	Reference getDokumentpakkefingeravtrykk();
	void setDokumentpakkefingeravtrykk(Reference fingeravtrykk);

}
