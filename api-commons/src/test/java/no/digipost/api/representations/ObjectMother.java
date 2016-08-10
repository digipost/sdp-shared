package no.digipost.api.representations;

import org.w3.xmldsig.DigestMethod;
import org.w3.xmldsig.Reference;
import org.w3.xmldsig.Transform;

import java.util.ArrayList;
import java.util.List;

class ObjectMother {

    public static Reference getReference() {
        Reference reference = new Reference();
        reference.setURI("#id-f2ecf3b2-101e-433b-a30d-65a9b6779b5a");

        List<Transform> transforms = new ArrayList<Transform>();
        Transform transform = new Transform();
        transform.setAlgorithm("http://www.w3.org/2001/10/xml-exc-c14n#");
        transforms.add(transform);

        DigestMethod digestMethod = new DigestMethod();
        digestMethod.setAlgorithm("http://www.w3.org/2001/04/xmlenc#sha256");
        reference.setDigestMethod(digestMethod);
        reference.setDigestValue("xQbKUtuEGSrsgZsSAT5rF+/yflr+hl2cUC4cKyiMxRM=".getBytes());

        return reference;
    }
}
