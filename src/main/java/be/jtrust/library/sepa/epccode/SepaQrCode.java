package be.jtrust.library.sepa.epccode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.Builder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Base64;

@Builder
public class SepaQrCode {


    private String recipient;
    private String iban;
    private String currency;
    private BigDecimal amount;
    private String communication;


    public String generateBase64Jpeg() {

        var content = "BCD\n002\n1\nSCT\n\n%s\n%s\n%s%s\n\n%s";

        var qrCodeStringValue = String.format(content, recipient, iban, currency, amount.toPlainString(), communication);

        try(ByteArrayOutputStream image = new ByteArrayOutputStream()) {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeStringValue, BarcodeFormat.QR_CODE, 150, 150);
            MatrixToImageWriter.writeToStream(bitMatrix,"jpeg", image);
            return Base64.getEncoder().encodeToString(image.toByteArray());
        } catch (IOException | WriterException e) {
            throw new RuntimeException(e);
        }
    }
}
