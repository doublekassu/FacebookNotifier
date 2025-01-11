package empirenotifier;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class DecodeMessage {
    public static String decodeRawMessage(String rawData) {
        String updatedRawData = rawData.replace('-', '+').replace('_', '/');

        byte[] decodedBytes = Base64.getDecoder().decode(updatedRawData);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}
