package no.digipost.api.util;

import java.util.function.Function;

/**
 * Utility for resolving one of two mutual exclusive instances, typically from
 * a JAXB-representation of an xsd:choice element. If given two actual instances
 * (non-<code>null</code> references) it will fail-fast with an IllegalArgumentException.
 */
public final class Choice<T> {

    public static <T> T choice(T first, T second) {
        return choice(first, second, Function.identity());
    }

    public static <T, S> T choice(T first, S second, Function<? super S, ? extends T> secondChoiceConverter) {
        if (first != null && second != null) {
            throw new IllegalArgumentException("Can only specify one of the arguments, not both. Got first arg:" + first + ", second: " + second);
        } else if (first != null) {
            return first;
        } else if (second != null) {
            return secondChoiceConverter.apply(second);
        } else {
            return null;
        }
    }


    private Choice() { }
}
