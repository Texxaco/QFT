package net.contexx.qft.junit;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static net.contexx.qft.junit.assertions.QFTAsserts.assertEquals;


@ExtendWith(QFT.class)
public class SampleTest {

    //https://www.baeldung.com/junit-5-extensions

    @Test
    void simpleTests() {
        assertEquals("a String", "Sample Data");
        assertEquals("a Integer", 12345);
        assertEquals("a Boolean", true);
        assertEquals("a Byte", 256);
        assertEquals("a Short", 256*128);
        assertEquals("a Long", Long.MAX_VALUE);
        assertEquals("a Float", 0.1234f);
        assertEquals("a Double", 128.12398712874d);
        assertEquals("a Character", 'C');
//        QFTAsserts.assertEquals("a BigDecimal", BigDecimal.TEN, Datatypes.);
    }

    @ParameterizedTest()
    @ValueSource(strings = { "racecar", "radar", "able was I ere I saw elba" })
    void parameterizedTests(String candidate) {
        assertEquals("isPalimdrom", candidate);
    }

    @RepeatedTest(3)
    void repeatedTests() {
        assertEquals("a Value", "Repeaded");
    }
}
