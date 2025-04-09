package no.digipost.api.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static no.digipost.api.xml.SchemaResources.createSchema;

public class JaxbMarshaller {

    public static JaxbMarshaller validatingMarshallerForPackages(Collection<Package> packages, Collection<SchemaResource> schemaResources) {
        return new JaxbMarshaller(initContextFromPackages(packages.stream()), createSchema(schemaResources));
    }

    public static JaxbMarshaller validatingMarshallerForClasses(Collection<Class<?>> classes, Collection<SchemaResource> schemaResources) {
        return new JaxbMarshaller(initContext(classes), createSchema(schemaResources));
    }

    private final JAXBContext jaxbContext;
    private final Schema schema;

    public JaxbMarshaller(JAXBContext jaxbContext, Schema schema) {
        this.jaxbContext = jaxbContext;
        this.schema = schema;
    }

    public JAXBContext getJaxbContext() {
        return jaxbContext;
    }

    public void marshal(Object object, Result result) {
        doWithMarshaller(object, (o, marshaller) -> marshaller.marshal(o, result));
    }
    
    @FunctionalInterface
    private interface ThrowingFunction<T, R> {
        R apply(T t) throws Exception;
    }

    @FunctionalInterface
    private interface ThrowingBiConsumer<T, S> {
        void accept(T t, S s) throws Exception;
    }

    private <T> void doWithMarshaller(T object, ThrowingBiConsumer<? super T, ? super Marshaller> operation) {
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            if (schema != null) {
                marshaller.setSchema(schema);
            }
            operation.accept(object, marshaller);
        } catch (Exception e) {
            throw MarshallingException.failedMarshal(object, e);
        }
    }


    public <T> T unmarshal(InputStream inputStream, Class<T> type) {
        return unmarshal(unmarshaller -> unmarshaller.unmarshal(inputStream), type);
    }

    public <T> T unmarshal(byte[] bytes, Class<T> type) {
        return unmarshal(new ByteArrayInputStream(bytes), type);
    }

    public <T> T unmarshal(Source source, Class<T> type) {
        return unmarshal(unmarshaller -> unmarshaller.unmarshal(source), type);
    }

    private <T> T unmarshal(ThrowingFunction<? super Unmarshaller, ?> operation, Class<T> type) {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            if (schema != null) {
                unmarshaller.setSchema(schema);
            }
            return type.cast(operation.apply(unmarshaller));
        } catch (Exception e) {
            throw MarshallingException.failedUnmarshal(type, e);
        }
    }

    private static JAXBContext initContextFromPackages(Stream<Package> packages) {
        return initContextFromPackageNames(packages.map(Package::getName));
    }

    private static JAXBContext initContextFromPackageNames(Stream<String> packageNames) {
        String jaxbContextPath = packageNames.collect(joining(":"));
        try {
            return JAXBContext.newInstance(jaxbContextPath);
        } catch (JAXBException e) {
            throw new RuntimeException("Could not create JAXBContext for contextPath [" + jaxbContextPath + "]" , e);
        }
    }

    private static JAXBContext initContext(Collection<Class<?>> classes) {
        Class<?>[] classesToBeBound = classes.toArray(new Class[classes.size()]);
        try {
            return JAXBContext.newInstance(classesToBeBound);
        } catch (JAXBException e) {
            throw new RuntimeException("Could not create JAXBContext for classes [" + Stream.of(classesToBeBound).map(Class::getSimpleName).collect(joining(",")) + "]" , e);
        }
    }

}
