package no.digipost.api;

import no.digipost.api.interceptors.KeyStoreInfo;
import no.digipost.api.xml.Constants;
import no.digipost.api.xml.JaxbMarshaller;
import no.digipost.api.xml.Marshalling;
import no.digipost.org.unece.cefact.namespaces.standardbusinessdocumentheader.StandardBusinessDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.transform.dom.DOMResult;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collections;

public class SdpMeldingSigner {
    private final JaxbMarshaller marshaller;
    private final KeyStoreInfo keystoreInfo;

    public SdpMeldingSigner(KeyStoreInfo keystoreInfo, JaxbMarshaller marshaller) {
        this.keystoreInfo = keystoreInfo;
        this.marshaller = marshaller;
    }

    public Document sign(StandardBusinessDocument sbd) {
        try {
            PrivateKey privateKey = keystoreInfo.getPrivateKey();
            X509Certificate certificate = keystoreInfo.getCertificate();

            DOMResult result = new DOMResult();
            Marshalling.marshal(marshaller, sbd, result);
            Document doc = (Document) result.getNode();
            Marshalling.trimNamespaces(doc);

            XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
            Reference ref = fac.newReference("", fac.newDigestMethod(DigestMethod.SHA256, null),
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

            DOMSignContext dsc = new DOMSignContext(privateKey, digitalPostNode, avsenderNode);
            signature.sign(dsc);

            doc.normalizeDocument();
            return doc;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
