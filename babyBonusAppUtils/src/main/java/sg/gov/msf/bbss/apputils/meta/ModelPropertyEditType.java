package sg.gov.msf.bbss.apputils.meta;

import android.text.InputType;

/**
 * Created by bandaray on 17/1/2015.
 */
public enum ModelPropertyEditType {
    TEXT(InputType.TYPE_CLASS_TEXT),
    INTEGER(InputType.TYPE_CLASS_NUMBER),
    CURRENCY(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL),
    DATE(InputType.TYPE_DATETIME_VARIATION_DATE),
    EMAIL(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS),
    PHONE(InputType.TYPE_CLASS_PHONE),
    DROP_DOWN,
    HYPER_LINK;

    private int inputType;

    ModelPropertyEditType(){
    }

    ModelPropertyEditType(int inputType){
        this.inputType = inputType;
    }

    public int getInputType(){
        return inputType;
    }
}
