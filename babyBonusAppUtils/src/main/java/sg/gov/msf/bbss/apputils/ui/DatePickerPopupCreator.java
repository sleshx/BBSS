package sg.gov.msf.bbss.apputils.ui;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.EditText;

import java.util.Calendar;

import sg.gov.msf.bbss.apputils.ui.component.DatePickerPopup;
import sg.gov.msf.bbss.apputils.util.StringHelper;

/**
 * Created by bandaray 16/03/2015
 * Fixed a bug by chuanhe
 */
public class DatePickerPopupCreator {

    public static void loadDatePickerDialog(Context context, final EditText etValue,
                                            boolean isFutureDateRequired, int titleResourceId) {
        final Calendar calender = Calendar.getInstance();

        DatePickerPopup dateDialog = new DatePickerPopup(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        calender.set(Calendar.YEAR, year);
                        calender.set(Calendar.MONTH, monthOfYear);
                        calender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        if (year <= 0 || monthOfYear < 0 || dayOfMonth <= 0) {
                            etValue.setText(null);
                        } else {
                            etValue.setText(StringHelper.formatDate(calender.getTime()));
                        }
                    }
                },
                calender.get(Calendar.YEAR),
                calender.get(Calendar.MONTH),
                calender.get(Calendar.DAY_OF_MONTH),
                isFutureDateRequired, titleResourceId);

        dateDialog.show();
    }
}
