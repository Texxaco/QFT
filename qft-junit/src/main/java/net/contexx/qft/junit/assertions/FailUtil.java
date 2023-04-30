package net.contexx.qft.junit.assertions;

import org.opentest4j.AssertionFailedError;

public class FailUtil {

    public static void failNotEqual(String expected, String actual) {
        String msg =  String.format("expected: <%s> but was: <%s>",expected, actual);
        throw new AssertionFailedError(msg, expected, actual);
    }
}
