package no.digipost.api.util;

@FunctionalInterface
public interface Converter<I, O> {

    O apply(I value);

}
