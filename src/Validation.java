import java.math.BigDecimal;

/**
 * Common input checks.
 * This is a utility class, so it cannot be instantiated.
 */
final class Validation {

    private Validation() {
    }

    static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be empty");
        }
        return value.trim();
    }

    static BigDecimal requireNonNegative(BigDecimal value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        if (value.signum() < 0) {
            throw new IllegalArgumentException(fieldName + " must not be negative: " + value);
        }
        return value;
    }

    static <T> T requireNonNull(T value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        return value;
    }
}
