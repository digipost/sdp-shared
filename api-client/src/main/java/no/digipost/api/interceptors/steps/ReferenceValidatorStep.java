package no.digipost.api.interceptors.steps;

import no.digipost.api.representations.EbmsContext;
import no.digipost.api.representations.EbmsProcessingStep;
import no.digipost.api.xml.JaxbMarshaller;
import no.digipost.org.oasis_open.docs.ebxml_bp.ebbp_signals_2.MessagePartNRInformation;
import no.digipost.org.oasis_open.docs.ebxml_bp.ebbp_signals_2.NonRepudiationInformation;
import no.digipost.org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Messaging;
import no.digipost.org.w3.xmldsig.Reference;
import no.digipost.org.w3.xmldsig.Transform;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;

import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Collection;

public class ReferenceValidatorStep implements EbmsProcessingStep {

    private final Collection<Reference> references;
    private final JaxbMarshaller jaxbMarshaller;

    public ReferenceValidatorStep(JaxbMarshaller jaxbMarshaller, Collection<Reference> references) {
        this.jaxbMarshaller = jaxbMarshaller;
        this.references = references;
    }

    @Override
    public void apply(EbmsContext ebmsContext, SoapHeaderElement ebmsMessaging, SoapMessage soapMessage) {
        Messaging messaging = jaxbMarshaller.unmarshal(ebmsMessaging.getSource(), Messaging.class);
        NonRepudiationInformation nonrep = (NonRepudiationInformation) messaging.getSignalMessages().get(0).getReceipt().getAnies().get(0);
        for (Reference reference : references) {
            boolean found = false;
            for (MessagePartNRInformation npr : nonrep.getMessagePartNRInformations()) {
                Reference ref = npr.getReference();
                if (ref.getURI().equals(reference.getURI())) {
                    found = true;
                    validate(reference, ref);
                    break;
                }
            }
            if (!found) {
                throw new RuntimeException("Missing NonRepudiationInformation for " + reference.getId());
            }
        }
    }

    private void validate(Reference expected, Reference actual) {
        if (!expected.getDigestMethod().getAlgorithm().equals(actual.getDigestMethod().getAlgorithm())) {
            throw new RuntimeException("Unexpected digest method. Expected:" + expected.getDigestMethod().getAlgorithm() + " Actual:" + actual.getDigestMethod().getAlgorithm());
        }
        if (!Arrays.equals(expected.getDigestValue(), actual.getDigestValue())) {
            Encoder base64Encoder = Base64.getEncoder();
            throw new RuntimeException("Unexpected digest value. Expected:" + base64Encoder.encode(expected.getDigestValue()) + " Actual:" + base64Encoder.encode(actual.getDigestValue()));
        }
        validateTransforms(expected, actual);
    }

    private void validateTransforms(Reference expected, Reference actual) {
        boolean expHasTransforms = expected.getTransforms() != null;
        boolean actHasTransforms = actual.getTransforms() != null;
        if (expHasTransforms != actHasTransforms) {
            throw new RuntimeException("Expected to " + (expHasTransforms ? "" : "not ") + "have transforms");
        }
        if (!expHasTransforms) {
            return;
        }

        if (expected.getTransforms().getTransforms().size() != actual.getTransforms().getTransforms().size()) {
            throw new RuntimeException("Unexpected number of transforms. Expected:" + expected.getTransforms().getTransforms().size() + " Actual:" + actual.getTransforms().getTransforms().size());
        }
        for (int i = 0; i < expected.getTransforms().getTransforms().size(); i++) {
            Transform expT = expected.getTransforms().getTransforms().get(i);
            Transform actT = actual.getTransforms().getTransforms().get(i);
            if (!expT.getAlgorithm().equals(actT.getAlgorithm())) {
                throw new RuntimeException("Unexpected transform. Expected:" + expT.getAlgorithm() + " Actual:" + actT.getAlgorithm());
            }
        }
    }

}
