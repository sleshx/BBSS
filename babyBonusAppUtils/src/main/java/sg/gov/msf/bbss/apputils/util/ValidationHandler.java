package sg.gov.msf.bbss.apputils.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sg.gov.msf.bbss.apputils.AppConstants;

/**
 * Created by bandaray on 25/2/2015.
 */
public class ValidationHandler {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final String POSTFIX_STRING_ST = "ABCDEFGHIZJ";
    private static final String POSTFIX_STRING_FG = "KLMNPQRTUWX";

    public static boolean isValidEmail(String emailAddress) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(emailAddress);
        return matcher.matches();
    }

    public static boolean isCitizenNricPrefixOk(String nric) {
        String firstChar;

        if (!StringHelper.isStringNullOrEmpty(nric)) {
            firstChar = Character.toString(nric.charAt(0));
            return (firstChar.equalsIgnoreCase("S") || firstChar.equalsIgnoreCase("T"));
        }
        return true;
    }

    public static boolean isValidNric(String nric) {
        if (nric == null || nric == "" || nric.length() != 9) {
            return false;
        }
        return true;
    }

    public static boolean isValidNricComplex(String nric) {
        String prefix, digit, postfix, correctPostfix;

        int checkDigitPosition, sum, remainder;
        boolean valid = false;

        if (nric == null || nric == "" || nric.length() != 9) {
            return false;
        }

        try
        {
            prefix = nric.trim().toUpperCase(AppConstants.APP_LOCALE).substring(0, 1);
            postfix = nric.trim().toUpperCase(AppConstants.APP_LOCALE).substring(8, 9);

            digit = nric.trim().substring(1, 8);

            if (digit.length() == 7)
            {
                sum = (Integer.parseInt(nric.substring(1, 2)) * 2) +
                        (Integer.parseInt(nric.substring(2, 3)) * 7) +
                        (Integer.parseInt(nric.substring(3, 4)) * 6) +
                        (Integer.parseInt(nric.substring(4, 5)) * 5) +
                        (Integer.parseInt(nric.substring(5, 6)) * 4) +
                        (Integer.parseInt(nric.substring(6, 7)) * 3) +
                        (Integer.parseInt(nric.substring(7, 8)) * 2);

                remainder = 0;

                if (prefix.equals("S") || prefix.equals("F")) {
                    remainder = (sum + 0) % 11;

                } else if (prefix.equals("T") || prefix.equals("G")){
                    remainder = (sum + 4) % 11;

                } else {
                    // Invalid prefix, must be S,T,F or G
                    valid = false;
                }

                checkDigitPosition = (11 - remainder) ;
                //position starts from 0, therefore subtract 1 (<-- this removed)

                if (prefix.equals("S") || prefix.equals("T")) {
                    correctPostfix = POSTFIX_STRING_ST.substring(checkDigitPosition - 1,
                            checkDigitPosition);

                } else if (prefix.equals("F") || prefix.equals("G")) {
                    correctPostfix = POSTFIX_STRING_FG.substring(checkDigitPosition - 1,
                            checkDigitPosition);

                } else {
                    correctPostfix = null;
                }

                if (correctPostfix != null && postfix.equals(correctPostfix)) {
                    valid = true;
                }
            }
            else {
                // Invalid length, must be 7.
                valid = false;
            }
        } catch(Exception e) {
            valid = false;
        }
        return valid;
    }
}
