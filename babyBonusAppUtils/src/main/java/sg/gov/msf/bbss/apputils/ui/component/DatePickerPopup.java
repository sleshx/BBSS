package sg.gov.msf.bbss.apputils.ui.component;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Date;

import sg.gov.msf.bbss.apputils.R;

/**
 * Created by bandaray on 16/3/2015.
 */
public class DatePickerPopup  extends DatePickerDialog {

    private final DatePicker mDatePicker;
    private final OnDateSetListener mCallBack;

    public DatePickerPopup(Context context, OnDateSetListener callBack,
                           int year, int monthOfYear, int dayOfMonth,
                           boolean isFutureDateRequired, int titleResourceId) {
        super(context, 0, callBack, year, monthOfYear, dayOfMonth);

        mCallBack = callBack;

        Context themeContext = getContext();
        setButton(BUTTON_POSITIVE,
                themeContext.getText(R.string.date_picker_button_set), this);
        setButton(BUTTON_NEUTRAL,
                themeContext.getText(R.string.date_picker_button_clear), this);
        setButton(BUTTON_NEGATIVE,
                themeContext.getText(R.string.date_picker_button_cancel), this); //last param Null?
        setIcon(android.R.drawable.ic_menu_today);
        setTitle(titleResourceId);

        LayoutInflater inflater = (LayoutInflater)
                themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.date_picker_popup, null);
        setView(view);
        mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);
        if (!isFutureDateRequired) {
            mDatePicker.setMaxDate(new Date().getTime());
        }
        mDatePicker.init(year, monthOfYear, dayOfMonth, this);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (mCallBack != null) {
            if (which == BUTTON_POSITIVE) {
                mDatePicker.clearFocus();
                mCallBack.onDateSet(mDatePicker, mDatePicker.getYear(),
                        mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
            } else if (which == BUTTON_NEUTRAL) {
                mDatePicker.clearFocus();
                mCallBack.onDateSet(mDatePicker, 0, 0, 0);
            }
        }
    }
}
