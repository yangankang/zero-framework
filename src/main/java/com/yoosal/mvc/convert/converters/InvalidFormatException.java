package com.yoosal.mvc.convert.converters;

public class InvalidFormatException extends RuntimeException {

    private String invalidValue;

    private String expectedFormat;

    public InvalidFormatException(String invalidValue, String expectedFormat) {
        this(invalidValue, expectedFormat, null);
    }

    public InvalidFormatException(String invalidValue, String expectedFormat, Throwable cause) {
        super("Invalid format for value '" + invalidValue + "'; the expected format was '" + expectedFormat + "'",
                cause);
        this.invalidValue = invalidValue;
        this.expectedFormat = expectedFormat;
    }

    public InvalidFormatException(String invalidValue, String expectedFormat, String message, Throwable cause) {
        super(message, cause);
        this.invalidValue = invalidValue;
        this.expectedFormat = expectedFormat;
    }

    public String getInvalidValue() {
        return invalidValue;
    }

    public String getExpectedFormat() {
        return expectedFormat;
    }
}