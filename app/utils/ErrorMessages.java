package utils;

public class ErrorMessages {
    public static String ACCEPT_TC = "errorAcceptTC";
    public static String ALREADY_EXISTS = "errorAlreadyExists";
    public static String MISSING_VALUE = "errorMissingValue";
    public static String MUST_SET_DATE_AND_TIME = "errorMustSetDateAndTime";
    public static String INVALID_VALUE = "errorInvalidValue";
    public static String INVALID_PHOTO_ID = "errorInvalidPhotoId";
    public static String INVALID_EMAIL = "errorInvalidEmail";
    public static String INVALID_EMAIL_OR_PASSWORD = "errorInvalidEmailOrPassword";
    public static String INVALID_URL = "errorInvalidUrl";
    public static String INVALID_EMAIL_DOES_NOT_MATCH = "errorEmailDoesNotMatch";
    public static String PHOTO_INVALID_EXIF = "errorPhotoExif";
    public static String PHOTO_TOO_LARGE = "errorPhotoSize";
    public static String PHOTO_WRONG_FORMAT = "errorPhotoFormat";

    public static String MIN_VALUE_IS(String minValue) {
        return "Minimum value is " + minValue;
    }
    public static String MAX_VALUE_IS(String maxValue) {
        return "Maximum value is " + maxValue;
    }
}
