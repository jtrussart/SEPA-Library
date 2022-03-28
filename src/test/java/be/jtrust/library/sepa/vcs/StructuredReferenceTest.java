package be.jtrust.library.sepa.vcs;

import be.jtrust.library.exception.VCSFormatException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

class StructuredReferenceTest {

    @ParameterizedTest
    @ValueSource(strings = {"FULLTXT", "76655YH", "UHY654", "145,876"})
    void checkOnlyNumber(String reference) {
        Assertions.assertThrows(VCSFormatException.class, () -> StructuredReference.buildWithYourReference(reference));
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345678901", "12345678902145"})
    void checkLength(String reference) {
        Assertions.assertThrows(VCSFormatException.class, () -> StructuredReference.buildWithYourReference(reference));
    }

    @ParameterizedTest
    @MethodSource("buildValidReferenceSource")
    void buildValidReference(String reference, String expectedValue) throws VCSFormatException {
        Assertions.assertEquals(expectedValue, StructuredReference.buildWithYourReference(reference).getRawReference());
    }

    private static Stream<Arguments> buildValidReferenceSource() {
        return Stream.of(
                Arguments.of("97","000000009797"),
                Arguments.of("100","000000010003")
        );
    }

    @Test
    void getPrettyPrintTests() throws VCSFormatException {
        Assertions.assertTrue(StructuredReference.buildWithYourReference("1234567890").getPrettyPrint().matches("[+]{3}[0-9]{3}/[0-9]{4}/[0-9]{5}[+]{3}"));
        Assertions.assertTrue(StructuredReference.buildWithFullReference("123456789002").getPrettyPrint().matches("[+]{3}[0-9]{3}/[0-9]{4}/[0-9]{5}[+]{3}"));
    }

    @Test
    void buildWithFullReference() {
        Assertions.assertThrows(VCSFormatException.class, () -> StructuredReference.buildWithFullReference("2345678"));
        Assertions.assertThrows(VCSFormatException.class, () -> StructuredReference.buildWithFullReference("123456789012"));
        Assertions.assertThrows(VCSFormatException.class, () -> StructuredReference.buildWithFullReference("DFGHJKJKHKJH"));
        Assertions.assertDoesNotThrow(() -> StructuredReference.buildWithFullReference("000000010003"));
        Assertions.assertDoesNotThrow(() -> StructuredReference.buildWithFullReference("+++000/0000/10003+++"));
    }
}
