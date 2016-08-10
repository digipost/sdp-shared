
package no.digipost.api.util;

public interface Converter<I, O> {

	O apply(I value);

}
