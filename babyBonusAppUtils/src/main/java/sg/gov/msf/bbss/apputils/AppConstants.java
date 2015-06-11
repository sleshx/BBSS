package sg.gov.msf.bbss.apputils;

import java.util.Locale;

/**
 * Created by bandaray on 3/2/2015.
 */
public class AppConstants {

    public static String EMPTY_STRING = "";
    //-- Symbols
    public static String SYMBOL_SPACE = " ";
    public static String SYMBOL_DOLLAR = "$";
    public static String SYMBOL_HYPHEN = "-";
    public static String SYMBOL_ASTRIX = "*";
    public static String SYMBOL_BREAK_LINE = "<br>";
    public static String SYMBOL_COMMA_WITH_SPACE = ", ";

    //----- APP constants
    public static boolean APP_IS_PAGING_ENABLED = false;
    public static final Locale APP_LOCALE = Locale.ENGLISH;
    public static final String APP_DATE_FORMAT = "dd/MM/yyyy";
    public static final String APP_DATE_TIME_FORMAT = "yyyy-MM-dd'T'hh:mm:ss";
    public static final int APP_MAX_UPLOAD_FILE_SIZE = 262144;
    public static final String APP_MAX_UPLOAD_FILE_SIZE_NAME = "2MB";

    //----- URLs
    public static final String AUTHENTICATION_URL = "http://192.168.204.82:8080/iframe-cxf-sample/services/itrust/authentication/login";



}
