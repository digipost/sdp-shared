package no.digipost.api;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;


@FunctionalInterface
public interface MessageFactorySupplier {

    /**
     * This resolves the {@link com.sun.xml.messaging.saaj.soap.ver1_2.SOAPMessageFactory1_2Impl} from the
     * <a href="https://javaee.github.io/metro-saaj/">Eclipse Enterprise for Java (EE4J) Metro SAAJ RI</a>.
     * <p>
     * This is the default SAAJ implementation used by this library, and is preferred because the implementation
     * bundled in the JDK has certain concurrency issues.
     */
    static MessageFactorySupplier metroSaajRI() {
        return com.sun.xml.messaging.saaj.soap.ver1_2.SOAPMessageFactory1_2Impl::new;
    }

    /**
     * This resolves which MessageFactory implementation to use using standard JDK mechanism by
     * invoking {@link MessageFactory#newInstance(String)} with {@link SOAPConstants#SOAP_1_2_PROTOCOL} as
     * argument. This may be apropriate if you use an application server runtime environment, and you want
     * to use the SAAJ implementation supplied by it.
     *
     * <h2>Note</h2>
     * The SAAJ implementation bundled with the JDK has certain performance and concurrency issues:
     * <ul>
     *  <li><a href="https://github.com/eclipse-ee4j/metro-saaj/issues/73">Issue #73: Make EnvelopeFactory ParserPool capacity configurable</a></li>
     *  <li><a href="https://github.com/eclipse-ee4j/metro-saaj/pull/117">Pull-request #117: Fix stability and security problem</a></li>
     * </ul>
     * You should not use this MessageFactoryResolver unless you have a good reason to, either if
     * you have configured your runtime environment to create a certain SAAJ implementation specified
     * by the {@code javax.xml.soap.MessageFactory} system property, or this is preconfigured
     * by your runtime environment, e.g. an application server.
     */
    static MessageFactorySupplier jdkFactory() {
        return () -> MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
    }

    /**
     * Get the default {@code MessageFactorySupplier} (i.e. {@link #metroSaajRI()})
     * if given {@code null} as argument.
     *
     * @param messageFactory the factory to check if it is {@code null}.
     * @return the given {@code messageFactory} or if it is {@code null}, the result
     *         from {@link #metroSaajRI()}.
     */
    static MessageFactorySupplier defaultIfNull(MessageFactorySupplier messageFactory) {
        return messageFactory != null ? messageFactory : metroSaajRI();
    }



    MessageFactory createMessageFactory() throws SOAPException;

}
