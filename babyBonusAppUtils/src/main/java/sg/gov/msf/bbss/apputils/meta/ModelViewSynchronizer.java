package sg.gov.msf.bbss.apputils.meta;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.R;
import sg.gov.msf.bbss.apputils.annotation.DisplayHeaderNameId;
import sg.gov.msf.bbss.apputils.ui.DatePickerPopupCreator;
import sg.gov.msf.bbss.apputils.ui.helper.ViewHolder;
import sg.gov.msf.bbss.apputils.util.FieldHandler;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.apputils.util.ValidationHandler;
import sg.gov.msf.bbss.apputils.validation.ValidationInfo;
import sg.gov.msf.bbss.apputils.validation.ValidationMessage;
import sg.gov.msf.bbss.apputils.validation.ValidationType;

/**
 * Created by bandaray on 15/12/2014.
 * Added method adjustToAllowOnlyTwoDecimalPoints() by chuanhe
 */
public class ModelViewSynchronizer<T> {

    private static final double CURRENCY_DEFAULT = 0.0;
    Class<T> typeClass;
    private ModelPropertyViewMetaList viewMetaList;
    private View view;
    private T dataObject;
    private ValidationInfo validationInfo;

    public ModelViewSynchronizer(Class<T> typeClass, ModelPropertyViewMetaList viewMetaList,
                                 View view, String sectionName){
        this.typeClass = typeClass;
        this.viewMetaList = viewMetaList;
        this.view = view;
        this.validationInfo = new ValidationInfo(sectionName);
    }

    //--- Data Object ------------------------------------------------------------------------------

    public T getDataObject() {
        List<Field> fields = FieldHandler.getAllNonStaticFields(typeClass);
        ModelPropertyViewMeta propertyViewMeta;
        LinearLayout linearLayout;

        clearValidations();

        for (Field field : fields) {
            propertyViewMeta = viewMetaList.getByPropertyName(field.getName());

            if (propertyViewMeta == null || !propertyViewMeta.isEditable())
                continue;

            linearLayout = (LinearLayout) view.findViewById(propertyViewMeta.getIncludeTagId());
            field.setAccessible(true);

            try {
                getFieldValue(field, linearLayout, propertyViewMeta);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return dataObject;
    }

    public void displayDataObject(T dataObject, ViewHolder viewHolder) {
        this.dataObject = dataObject;
        List<Field> fields = FieldHandler.getAllNonStaticFields(typeClass);
        ModelPropertyViewMeta propertyViewMeta;
        LinearLayout linearLayout;

        for (Field field : fields) {
            if ((propertyViewMeta = viewMetaList.getByPropertyName(field.getName())) == null)
                continue;

            linearLayout = (LinearLayout) view.findViewById(propertyViewMeta.getIncludeTagId());
            field.setAccessible(true);

            try {
                setFieldValue(field, linearLayout, propertyViewMeta, viewHolder);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void displayDataObject(T displayDataObject) {
        displayDataObject(displayDataObject, null);
    }

    //--- Validations ------------------------------------------------------------------------------

    public ValidationInfo getValidationInfo() {
        return validationInfo;
    }

    public void displayValidationErrors(List<ValidationMessage> validationMessages) {
        ModelPropertyViewMeta viewMeta;

        for (ValidationMessage validationMessage : validationMessages) {
            viewMeta = viewMetaList.getBySerialName(validationMessage.getSerialName());

            if(viewMeta != null) {
                LinearLayout layout = (LinearLayout) view.findViewById(viewMeta.getIncludeTagId());
                layout.setBackgroundColor(view.getContext().getResources()
                        .getColor(R.color.field_error));

                switch (viewMeta.getEditType()) {
                    case TEXT:
                    case INTEGER:
                    case CURRENCY:
                    case DATE:
                    case PHONE:
                    case EMAIL:
                        EditText etValue = (EditText) layout.findViewById(R.id.etValue);
                        etValue.setBackgroundColor(view.getContext().getResources()
                                .getColor(R.color.theme_gray_default_bg));
                        //To set the error in a bubble
                        /*
                        if (viewMeta.getTagNameId() > 0) {
                            etValue.setError(validationMessage.getMessage());
                        }
                        */
                        break;
                    case DROP_DOWN:
                        Spinner spValue = (Spinner) layout.findViewById(R.id.spValue);
                        spValue.setBackgroundColor(view.getContext().getResources()
                                .getColor(R.color.theme_gray_default_bg));
                        break;
                }
            }
        }
    }

    //--- Header Title -----------------------------------------------------------------------------

    public void setHeaderTitle(int headerLayoutId, int headerTitleId) {
        DisplayHeaderNameId annotation = typeClass.getAnnotation(DisplayHeaderNameId.class);

        if (annotation != null) {
            headerTitleId = annotation.value();
        }

        if (headerLayoutId > 0) {
            ((TextView) view.findViewById(headerLayoutId)).setText(headerTitleId);
        }
    }

    //--- Labels -----------------------------------------------------------------------------------

    private void setFieldLabel(ModelPropertyViewMeta propertyViewMeta) {
        LinearLayout layout = (LinearLayout) view.findViewById(propertyViewMeta.getIncludeTagId());
        TextView tvLabel = (TextView) layout.findViewById(R.id.tvLabel);
        String labelValue = propertyViewMeta.getLabelString();

        if (labelValue == null) {
            labelValue =  StringHelper.getStringByResourceId(
                    view.getContext(), propertyViewMeta.getLabelResourceId());
        }

        if(tvLabel != null) {
            if (propertyViewMeta.isMandatory()) {
                tvLabel.setText(labelValue + AppConstants.SYMBOL_ASTRIX);
            } else {
                tvLabel.setText(labelValue);
            }
        }
    }

    public void setFieldMandatory(String propertyName, boolean isMandatory){
        Field field = FieldHandler.getField(typeClass, propertyName);

        if(field != null) {
            ModelPropertyViewMeta viewMeta = viewMetaList.getByPropertyName(propertyName);
            viewMeta.setMandatory(isMandatory);
            setFieldLabel(viewMeta);
        }
    }

    public void setFieldBackground(String propertyName, int color){
        Field field = FieldHandler.getField(typeClass, propertyName);

        if(field != null) {
            ModelPropertyViewMeta viewMeta = viewMetaList.getByPropertyName(propertyName);
            LinearLayout layout = (LinearLayout) view.findViewById(viewMeta.getIncludeTagId());
            layout.setBackgroundColor(color);
            setFieldLabel(viewMeta);
        }
    }

    public void setLabels(){
        List<Field> fields = FieldHandler.getAllNonStaticFields(typeClass);
        ModelPropertyViewMeta viewMeta;

        for (Field field : fields) {
            if (((viewMeta = viewMetaList.getByPropertyName(field.getName())) == null))
                continue;

            field.setAccessible(true);
            setFieldLabel(viewMeta);
        }
    }

    //--- TextView Values --------------------------------------------------------------------------

    private TextView setTextViewValue(LinearLayout linearLayout, Field field,
                                      ModelPropertyViewMeta viewMeta, ViewHolder viewHolder)
            throws IllegalAccessException {
        TextView tvValue = null;

        if(dataObject != null){
            if (viewHolder != null) {
                tvValue = viewHolder.get(linearLayout, R.id.tvValue);
            } else {
                tvValue = (TextView) linearLayout.findViewById(R.id.tvValue);
            }

            if(field.get(dataObject) == null) {
                tvValue.setText(AppConstants.SYMBOL_HYPHEN);
            } else {
                tvValue.setText(field.get(dataObject).toString());
            }

            if (viewMeta.isBold()) {
                tvValue.setTypeface(null, Typeface.BOLD);
            }
        }

        return tvValue;
    }

    //--- EditText Values --------------------------------------------------------------------------

    private EditText setEditTextValue(LinearLayout linearLayout, Field field,
                                      ModelPropertyViewMeta viewMeta) throws IllegalAccessException {
        EditText etValue = getTextEditor (linearLayout,viewMeta);

        etValue.setInputType(viewMeta.getEditType().getInputType());

        if(!viewMeta.isFocusable()) {
            etValue.setLongClickable(false);
            etValue.setFocusable(false);
            etValue.setKeyListener(null);
            etValue.setBackgroundColor(view.getContext().getResources().getColor(
                    R.color.theme_gray_dark));
        }

        if(viewMeta.getMaxLength() > 0) {
            InputFilter[] FilterArray = new InputFilter[1];
            FilterArray[0] = new InputFilter.LengthFilter(viewMeta.getMaxLength());
            etValue.setFilters(FilterArray);
        }

        if(viewMeta.getViewClickListener() != null) {
            ImageView ivButton = (ImageView) linearLayout.findViewById(R.id.ivBtn);
            ivButton.setOnClickListener(viewMeta.getViewClickListener());
        }

        if(viewMeta.getTextChangeListener() != null) {
            etValue.addTextChangedListener(viewMeta.getTextChangeListener());
        }

        if(viewMeta.getTextFocusChangeListener() != null) {
            etValue.setOnFocusChangeListener(viewMeta.getTextFocusChangeListener());
        }

        if(dataObject != null){
            if(field.get(dataObject) == null) {
                etValue.setText(AppConstants.EMPTY_STRING);
            } else {
                etValue.setText(field.get(dataObject).toString());
            }
        }
        return etValue;
    }

    private void setEditTextDateValue(final EditText etValue, final ModelPropertyViewMeta viewMeta) {
        etValue.setFocusable(false);

        if(viewMeta.isFocusable()) {
            etValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerPopupCreator.loadDatePickerDialog(view.getContext(), etValue,
                            viewMeta.isFutureDateRequired(), viewMeta.getLabelResourceId());
                }
            });
        } else {
            etValue.setBackgroundColor(view.getContext().getResources().getColor(
                    R.color.theme_gray_dark));
        }
    }

    //--- Field Values -----------------------------------------------------------------------------

    private void setFieldValue(Field field, LinearLayout layout, ModelPropertyViewMeta viewMeta,
                               ViewHolder viewHolder) throws IllegalAccessException {
        switch (viewMeta.getEditType()) {
            case TEXT: setString(field, layout, viewMeta, viewHolder); break;
            case INTEGER: setInteger(field, layout, viewMeta, viewHolder); break;
            case CURRENCY: setCurrency(field, layout, viewMeta, viewHolder); break;
            case DATE: setDate(field, layout, viewMeta, viewHolder); break;
            case PHONE: setPhone(field, layout, viewMeta, viewHolder); break;
            case EMAIL: setEmail(field, layout, viewMeta, viewHolder); break;
            case DROP_DOWN: setSpinner(field, layout, viewMeta); break;
            case HYPER_LINK: setHyperLink(field, layout); break;
        }
    }

    private void getFieldValue(Field field, LinearLayout layout, ModelPropertyViewMeta viewMeta)
            throws IllegalAccessException {
        EditText etValue = null;
        Spinner spValue = null;

        switch (viewMeta.getEditType()) {
            case TEXT:
            case PHONE:
            case EMAIL:
            case INTEGER:
            case CURRENCY:
            case DATE: etValue = getTextEditor(layout, viewMeta); break;
            case DROP_DOWN: spValue = (Spinner) layout.findViewById(R.id.spValue); break;
        }

        switch (viewMeta.getEditType()) {
            case TEXT:
            case PHONE: field.set(dataObject, getString(etValue, viewMeta)); break;
            case EMAIL: field.set(dataObject, getEmail(etValue, viewMeta)); break;
            case INTEGER: field.set(dataObject, getInteger(etValue, viewMeta)); break;
            case CURRENCY: field.set(dataObject, getCurrency(etValue, viewMeta)); break;
            case DATE: field.set(dataObject, getDate(etValue, viewMeta)); break;
            case DROP_DOWN: field.set(dataObject, getSelectedListItem(spValue, viewMeta)); break;
        }
    }

    private EditText getTextEditor(LinearLayout layout, ModelPropertyViewMeta viewMeta){
        switch (viewMeta.getViewPositionType()){
            case NONE: return (EditText) layout.findViewById(R.id.etValue);
            case ONE: return (EditText) layout.findViewById(R.id.etValue1);
            case TWO: return (EditText) layout.findViewById(R.id.etValue2);
        }

        return null;
    }

    //--- Field Values : Strings/Text --------------------------------------------------------------

    private void setString(Field field, LinearLayout layout, ModelPropertyViewMeta viewMeta,
                           ViewHolder viewHolder) throws IllegalAccessException {
        if (viewMeta.isEditable()) {
            setEditTextValue(layout, field, viewMeta);
        } else {
            setTextViewValue(layout, field, viewMeta, viewHolder);
        }
    }

    private String getString(EditText editor, ModelPropertyViewMeta viewMeta){
        String editValue = editor.getText().toString();

        if(viewMeta.isMandatory() && StringHelper.isStringNullOrEmpty(editValue)){
            ValidationMessage validationMessage = new ValidationMessage(ValidationType.MANDATORY);

            validationMessage.setSerialName(viewMeta.getSerialName());
            validationMessage.setMessage(String.format("%s is mandatory.",
                    getTagValue(viewMeta.getTagNameId())));

            validationInfo.addValidationMessage(validationMessage);
        }

        return editValue;
    }

    //--- Field Values : Integers ------------------------------------------------------------------

    private void setInteger(Field field, LinearLayout layout, ModelPropertyViewMeta viewMeta,
                            ViewHolder viewHolder) throws IllegalAccessException {
        if (viewMeta.isEditable()) {
            EditText etValue = setEditTextValue(layout, field, viewMeta);

            if(dataObject != null && field.get(dataObject) != null){
                if(etValue != null && field.get(dataObject).equals(0)) {
                    etValue.setText(AppConstants.EMPTY_STRING);
                }
            }
        } else {
            setTextViewValue(layout, field, viewMeta, viewHolder);
        }
    }

    private int getInteger(EditText editor, ModelPropertyViewMeta viewMeta){
        String editValue = editor.getText().toString();
        int value = 0;

        try{
            value = Integer.parseInt(editValue);
        } catch (NumberFormatException ex) {
            if(viewMeta.isMandatory()){
                ValidationMessage validationMessage = new ValidationMessage(ValidationType.MANDATORY);

                validationMessage.setSerialName(viewMeta.getSerialName());
                validationMessage.setMessage(String.format("%s is mandatory.",
                        getTagValue(viewMeta.getTagNameId())));

                validationInfo.addValidationMessage(validationMessage);
            }
        }
        return value;
    }

    //--- Field Values : Currency ------------------------------------------------------------------

    private void setCurrency(Field field, LinearLayout layout, ModelPropertyViewMeta viewMeta,
                             ViewHolder viewHolder) throws IllegalAccessException {
        TextView tvDollar;
        if(viewHolder != null) {
            tvDollar = viewHolder.get(layout, R.id.tvDollar);
        } else {
            tvDollar = (TextView) layout.findViewById(R.id.tvDollar);
        }
        tvDollar.setText(AppConstants.SYMBOL_DOLLAR);

        if (viewMeta.isEditable()) {
            EditText etValue = setEditTextValue(layout, field, viewMeta);
            if (dataObject != null && field.get(dataObject) != null) {
                if (etValue != null && field.get(dataObject).equals(CURRENCY_DEFAULT)) {
                    etValue.setText(AppConstants.EMPTY_STRING);
                }
            }
            adjustToAllowOnlyTwoDecimalPoints(etValue);
        } else {
            TextView tvValue = setTextViewValue(layout, field, viewMeta, viewHolder);
            tvValue.setGravity(Gravity.RIGHT);
            if (dataObject != null) {
                tvValue.setText(StringHelper.formatCurrencyNumber(
                        field.get(dataObject).toString()));
            }
        }
    }

    private double getCurrency(EditText editor, ModelPropertyViewMeta viewMeta){
        String editValue = editor.getText().toString();
        double value = 0;

        try{
            value = Double.parseDouble(editValue);
        } catch (NumberFormatException ex) {
            if(viewMeta.isMandatory()){
                ValidationMessage validationMessage = new ValidationMessage(ValidationType.MANDATORY);

                validationMessage.setSerialName(viewMeta.getSerialName());
                validationMessage.setMessage(String.format("%s is mandatory.",
                        getTagValue(viewMeta.getTagNameId())));

                validationInfo.addValidationMessage(validationMessage);
            }
        }
        return value;
    }

    private void adjustToAllowOnlyTwoDecimalPoints(final EditText editor) {
        editor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        editor.setText(s);
                        editor.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    editor.setText(s);
                    editor.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        editor.setText(s.subSequence(0, 1));
                        editor.setSelection(1);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    String str = editor.getText().toString();
                    if (str.indexOf(".") > 0){
                        if (str.length() - str.indexOf(".") == 1){
                            editor.setText(str + "00");
                        } else if (str.length()- str.indexOf(".") == 2){
                            editor.setText(str + "0");
                        }
                    } else {
                        editor.setText(str + ".00");
                    }
                }
            }
        });
    }

    //--- Field Values : Date ----------------------------------------------------------------------

    private void setDate(Field field, LinearLayout layout, ModelPropertyViewMeta viewMeta,
                         ViewHolder viewHolder) throws IllegalAccessException {
        if (viewMeta.isEditable()) {
            EditText etValue = setEditTextValue(layout, field, viewMeta);
            setEditTextDateValue(etValue, viewMeta);

            if(dataObject != null && field.get(dataObject) != null){
                etValue.setText(StringHelper.formatDate((Date)field.get(dataObject)));
            }
        } else {
            TextView tvValue = setTextViewValue(layout, field, viewMeta, viewHolder);
            if (dataObject != null) {
                tvValue.setText(StringHelper.formatDate((Date) field.get(dataObject)));
            }
        }
    }

    private Date getDate(EditText editor, ModelPropertyViewMeta viewMeta){
        String editValue = editor.getText().toString();
        Date value = StringHelper.parseDate(editValue);

        if(value == null && viewMeta.isMandatory()) {
            ValidationMessage validationMessage = new ValidationMessage(ValidationType.MANDATORY);

            validationMessage.setSerialName(viewMeta.getSerialName());
            validationMessage.setMessage(String.format("%s is mandatory.",
                    getTagValue(viewMeta.getTagNameId())));

            validationInfo.addValidationMessage(validationMessage);
        }
        return value;
    }

    //--- Field Values : Phone ---------------------------------------------------------------------

    private void setPhone(Field field, LinearLayout layout, ModelPropertyViewMeta viewMeta,
                          ViewHolder viewHolder) throws IllegalAccessException {
        if (viewMeta.isEditable()) {
            setEditTextValue(layout, field, viewMeta);
        } else {
            setTextViewValue(layout, field, viewMeta, viewHolder);
        }
    }

    //--- Field Values : Email ---------------------------------------------------------------------

    private void setEmail(Field field, LinearLayout layout, ModelPropertyViewMeta viewMeta,
                          ViewHolder viewHolder) throws IllegalAccessException {
        if (viewMeta.isEditable()) {
            setEditTextValue(layout, field, viewMeta);
        } else {
            setTextViewValue(layout, field, viewMeta, viewHolder);
        }
    }

    private String getEmail(EditText editor, ModelPropertyViewMeta viewMeta) {
        String editValue = editor.getText().toString();

        if(StringHelper.isStringNullOrEmpty(editValue)){
            if(viewMeta.isMandatory()) {
                ValidationMessage validationMessage = new ValidationMessage(ValidationType.MANDATORY);

                validationMessage.setSerialName(viewMeta.getSerialName());
                validationMessage.setMessage(String.format("%s is mandatory.",
                        getTagValue(viewMeta.getTagNameId())));

                validationInfo.addValidationMessage(validationMessage);
            }
        } else if(!ValidationHandler.isValidEmail(editValue)){
            ValidationMessage validationMessage = new ValidationMessage(ValidationType.DATA_FORMAT);

            validationMessage.setSerialName(viewMeta.getSerialName());
            validationMessage.setMessage(String.format("%s is invalid.",
                    getTagValue(viewMeta.getTagNameId())));

            validationInfo.addValidationMessage(validationMessage);
        }

        return editValue;
    }

    //--- Field Values : Spinner -------------------------------------------------------------------

    private void setSpinner(Field field, LinearLayout layout, ModelPropertyViewMeta viewMeta)
            throws IllegalAccessException {
        Spinner spValue = (Spinner) layout.findViewById(R.id.spValue);
        SpinnerAdapter adapter = viewMeta.getDropDownAdapter();

        if(!viewMeta.isFocusable()){
            spValue.setClickable(false);
            spValue.setBackgroundColor(view.getContext().getResources().getColor(
                    R.color.theme_gray_dark));
        }

        spValue.setAdapter(adapter);
        spValue.setOnItemSelectedListener(viewMeta.getDropDownItemSelectedListener());

        if(adapter == null)
            return;

        int itemCount = adapter.getCount();

        for(int position = 0; position < itemCount; position++) {
            if(adapter.getItem(position).equals(field.get(dataObject))){
                spValue.setSelection(position);
                break;
            }
        }
    }

    private Object getSelectedListItem(Spinner editor, ModelPropertyViewMeta viewMeta){
        if(viewMeta.isMandatory() && editor.getSelectedItem() == null){
            ValidationMessage validationMessage = new ValidationMessage(ValidationType.MANDATORY);

            validationMessage.setSerialName(viewMeta.getSerialName());
            validationInfo.addValidationMessage(validationMessage);
        }
        return editor.getSelectedItem();
    }

    //--- Field Values : Hyperlink -----------------------------------------------------------------

    private void setHyperLink(final Field field, LinearLayout layout) throws IllegalAccessException {
        TextView tvValue = (TextView) layout.findViewById(R.id.tvValue);

        if(dataObject != null && field.get(dataObject) != null) {
            final String url = field.get(dataObject).toString();
            tvValue.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            tvValue.setTextColor(Color.BLUE);
            tvValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.getContext().startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse(url)));
                }
            });
        }
    }

    //--- Validations ------------------------------------------------------------------------------

    private void clearValidations(){
        validationInfo.clearAllValidationMessages();
        clearValidationErrorTemplates();
    }

    private void clearValidationErrorTemplates(){
        String[] propertyNames = viewMetaList.getByPropertyNames();
        ModelPropertyViewMeta viewMeta;

        for (String propertyName : propertyNames){
            viewMeta = viewMetaList.getByPropertyName(propertyName);

            if(!viewMeta.isEditable())
                continue;

            LinearLayout layout = (LinearLayout) view.findViewById(viewMeta.getIncludeTagId());
            layout.setBackgroundColor(Color.WHITE);

            switch (viewMeta.getEditType()) {
                case TEXT:
                case INTEGER:
                case CURRENCY:
                case DATE:
                case PHONE:
                case EMAIL:
                    if (viewMeta.getTagNameId() > 0) {
                        EditText etValue = getTextEditor(layout, viewMeta);
                        etValue.setError(null);
                    }
                    break;
                case DROP_DOWN:
                    Spinner spValue = (Spinner) layout.findViewById(R.id.spValue);
                    spValue.setBackgroundColor(Color.WHITE);
                    break;
            }
        }
    }

    //--- Tag Values -------------------------------------------------------------------------------

    private String getTagValue(int resourceId) {
        return StringHelper.getStringByResourceId(view.getContext(), resourceId);
    }
}
