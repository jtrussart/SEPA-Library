package be.jtrust.library.sepa.vcs;

import be.jtrust.library.exception.VCSFormatException;

import java.math.BigInteger;
import java.text.DecimalFormat;

public class StructuredReference {

    private static final BigInteger _97 = BigInteger.valueOf(97);

    private String rawReference;

    /**
     * Build the StructuredReference based on the business reference (without checkdigit) <br/>
     * @param yourReference : The String can only contain digit and cant be longer than 10 char
     * @throws VCSFormatException
     */
    public static StructuredReference buildWithYourReference(String yourReference) throws VCSFormatException {
        BigInteger numericReference;
        try {
            numericReference = new BigInteger(yourReference);
        } catch (NumberFormatException e) {
            throw new VCSFormatException();
        }
        // There is a maximum size : 10
        if(yourReference.length() > 10) {
            throw new VCSFormatException();
        }

        // Compute the check digit
        BigInteger remainder = computeCheckDigit(numericReference);

        DecimalFormat twoPositionFormat = new DecimalFormat("00");
        DecimalFormat tenPositionFormat = new DecimalFormat("0000000000");

        final var structuredReference = new StructuredReference();
        structuredReference.setRawReference(tenPositionFormat.format(numericReference) + twoPositionFormat.format(remainder));

        return structuredReference;
    }

    /**
     * Build StructuredReference based on a full structured communication (including checkdigit) <br/>
     * @param fullReference : "+" and "/" will be ignored
     * @throws VCSFormatException
     */
    public static StructuredReference buildWithFullReference(String fullReference) throws VCSFormatException {
        fullReference = fullReference.replace("+","");
        fullReference = fullReference.replace("/","");
        if(fullReference.length() != 12) {
            throw new VCSFormatException();
        }
        // Check digit
        BigInteger reference;
        BigInteger checkDigit;
        try {
            reference = new BigInteger(fullReference.substring(0, 10));
            checkDigit = new BigInteger(fullReference.substring(10, 12));
        } catch (NumberFormatException e) {
            throw new VCSFormatException();
        }

        BigInteger remainder = computeCheckDigit(reference);

        if(remainder.compareTo(checkDigit) == 0) {
            final var structuredReference = new StructuredReference();
            structuredReference.setRawReference(fullReference);
            return structuredReference;
        } else {
            throw new VCSFormatException();
        }

    }

    private static BigInteger computeCheckDigit(BigInteger numericReference) {
        BigInteger remainder = numericReference.remainder(_97);
        if(remainder.compareTo(BigInteger.ZERO) == 0) {
            remainder = _97;
        }
        return remainder;
    }

    public String getRawReference() {
        return rawReference;
    }

    public String getPrettyPrint() {
        return String.format("+++%s/%s/%s+++", getRawReference().substring(0,3), getRawReference().substring(3,7), getRawReference().substring(7,12));
    }

    private void setRawReference(String rawReference) {
        this.rawReference = rawReference;
    }
}
