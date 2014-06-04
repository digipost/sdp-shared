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
package no.posten.dpost.offentlig.api;

import no.posten.dpost.offentlig.api.interceptors.KeyStoreInfo;
import no.posten.dpost.offentlig.xml.Constants;
import no.posten.dpost.offentlig.xml.Marshalling;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.transform.dom.DOMResult;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.UUID;

public class SdpMeldingSigner {

	private final Jaxb2Marshaller marshaller;
	private final KeyStoreInfo keystoreInfo;

	public SdpMeldingSigner(final KeyStoreInfo keystoreInfo, final Jaxb2Marshaller marshaller) {
		this.keystoreInfo = keystoreInfo;
		this.marshaller = marshaller;
	}

	public Document sign(final StandardBusinessDocument sbd) {
		try {
			PrivateKey privateKey = keystoreInfo.getPrivateKey();
			X509Certificate certificate = keystoreInfo.getCertificate();

			DOMResult result = new DOMResult();
			Marshalling.marshal(marshaller, sbd, result);
			Document doc = (Document)result.getNode();
			String id = "SBD-" + UUID.randomUUID().toString();
			((Element)doc.getFirstChild()).setAttribute("Id", id);
			((Element)doc.getFirstChild()).setIdAttribute("Id", true);

			XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
			Reference ref = fac.newReference("#" + id, fac.newDigestMethod(DigestMethod.SHA256, null),
					Collections.singletonList(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)), null, null);

			SignedInfo si = fac.newSignedInfo(
					fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null),
					fac.newSignatureMethod(Constants.RSA_SHA256, null), Collections.singletonList(ref));
			KeyInfoFactory kif = fac.getKeyInfoFactory();
			X509Data xd = kif.newX509Data(Collections.singletonList(certificate));
			KeyInfo ki = kif.newKeyInfo(Collections.singletonList(xd));
			XMLSignature signature = fac.newXMLSignature(si, ki);

			Node digitalPostNode = doc.getDocumentElement().getFirstChild().getNextSibling();
			Node avsenderNode = digitalPostNode.getFirstChild();

			DOMSignContext dsc = new DOMSignContext(privateKey, doc.getDocumentElement().getLastChild(), avsenderNode);
			signature.sign(dsc);
			return doc;
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		} catch (UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        } catch (XMLSignatureException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (MarshalException e) {
            throw new RuntimeException(e);
        }
    }

}