package no.digipost.api.xml;

import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class MarshallingException extends RuntimeException {

    public static MarshallingException failedUnmarshal(Class<?> target, Throwable cause) {
        return new MarshallingException("Failed unmarshalling XML to " + target.getName(), cause);
    }

    public static MarshallingException failedMarshal(Object objectFailingToMarshal, Throwable cause) {
        return new MarshallingException(
                "Failed marshalling " + (objectFailingToMarshal != null ? objectFailingToMarshal.getClass().getName() : "null") + " to XML",
                cause);
    }

    public MarshallingException(String message, Throwable cause) {
        super(message + (cause != null ? ", because " + messageIncludingCauses(cause) : ""), cause);
    }

    private static String messageIncludingCauses(Throwable throwable) {
        return causalChainOf(throwable)
                .map(e -> e.getClass().getSimpleName() + ": '" + e.getMessage() + "'")
                .collect(joining(", caused by "));
    }

    private static Stream<Throwable> causalChainOf(Throwable t) {
        Stream.Builder<Throwable> causes = Stream.builder();
        for (Throwable cause = t; cause != null; cause = cause.getCause()) {
            causes.add(cause);
        }
        return causes.build();
    }

}