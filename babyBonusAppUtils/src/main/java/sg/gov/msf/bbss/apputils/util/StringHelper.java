package sg.gov.msf.bbss.apputils.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.R;

/**
 * Created by bandaray on 11/12/2014.
 */
public class StringHelper {

    private static String EMPTY = "";

    public static String getStringByResourceId(Context context, int stringResourceId) {
        return context.getResources().getString(stringResourceId);
    }

    public static boolean isStringNullOrEmpty(String str) {
        boolean isNull = false;
        if (str == null) {
            isNull = true;
        } else if (str.length() <= 0) {
            isNull = true;
        }
        //return str.isEmpty() || str == null;
        return isNull;
    }

    public static String getStringFromExtras(Activity activity, String nameOfExtra) {
        String valueOfExtra = null;
        Bundle extras = activity.getIntent().getExtras();
        if (extras!= null) {
            valueOfExtra = extras.getString(nameOfExtra);
        }
        return valueOfExtra;
    }

    public static String formatCurrencyNumber(String decimalNo) {
        DecimalFormat formatter = new DecimalFormat("#,###.##");
        formatter.setDecimalSeparatorAlwaysShown(true);
        formatter.setMinimumFractionDigits(2);
        String value = formatter.format(Double.parseDouble(decimalNo));
        return value;
    }

    public static String getJustifiedErrorString(Context context, String message, int bgColorId) {
        String color;
        if (bgColorId == R.color.theme_creme) {
            color = "#F4F8DF";
        } else {
            color = "#F5F5F5";
        }

        return "<html><head>"
                + "<style type=\"text/css\">body{color: #ff0000; background-color: "+ color + ";}"
                + "</style></head>"
                + "<body>"
                + "<p align=\"justify\">"
                + message
                + "</p> "
                + "</body></html>";
    }

    public static String getJustifiedString(Context context, int stringResourceId, int bgColorId) {
        return getJustifiedString(context, getStringByResourceId(context, stringResourceId), bgColorId);
    }

    public static String getJustifiedString(Context context, String message, int bgColorId) {
        String color;
        if (bgColorId == R.color.theme_creme) {
            color = "#F4F8DF";
        } else {
            color = "#F5F5F5";
        }
        return "<html><head>"
                + "<style type=\"text/css\">body{background-color: "+ color + ";}"
                + "</style></head>"
                + "<body>"
                + "<p align=\"justify\">"
                + message
                + "</p> "
                + "</body></html>";
    }

    public static Date parseDate(String dateString){
        if(dateString != null && !dateString.isEmpty()) {
            SimpleDateFormat formatter = new SimpleDateFormat(AppConstants.APP_DATE_FORMAT);

            try {
                return formatter.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static String formatDate(Date date) {
        return date == null ? EMPTY :
                new SimpleDateFormat(AppConstants.APP_DATE_FORMAT).format(date);
    }

    public static String getCapitalizedNric(String nric) {
        return  nric.trim().toUpperCase(AppConstants.APP_LOCALE).substring(0, 1) +
                nric.trim().substring(1, 8) +
                nric.trim().toUpperCase(AppConstants.APP_LOCALE).substring(8, 9);
    }
}
