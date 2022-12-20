package lazy;

import java.util.UUID;

public class UtilShortId {
    static String generateRandomShortId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}




