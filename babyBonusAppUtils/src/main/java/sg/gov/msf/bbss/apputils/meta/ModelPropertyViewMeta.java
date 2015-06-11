package sg.gov.msf.bbss.apputils.meta;

import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SpinnerAdapter;

/**
 * Created by bandaray on 15/12/2014.
 */
public class ModelPropertyViewMeta {

    private String propertyName;
    private String serialName;
    private String labelString;
    private int labelResourceId;
    private int includeTagId;
    private int tagNameId;
    private int maxLength;
    private boolean isMandatory;
    private boolean isEditable;
    private boolean isDecimalNo;
    private boolean isBold = false;
    private boolean isFocusable = true;
    private boolean isFutureDateRequired = false;
    private ModelPropertyEditType editType;
    private SpinnerAdapter dropDownAdapter;
    private AdapterView.OnItemSelectedListener dropDownItemSelectedListener;
    private View.OnClickListener viewClickListener;
    private TextWatcher textChangeListener;
    private View.OnFocusChangeListener textFocusChangeListener;
    private ViewPositionType viewPositionType = ViewPositionType.NONE;

    public ModelPropertyViewMeta(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getSerialName() {
        return serialName;
    }

    public void setSerialName(String serialName) {
        this.serialName = serialName;
    }

    public String getLabelString() {
        return labelString;
    }

    public void setLabelString(String labelString) {
        this.labelString = labelString;
    }

    public int getLabelResourceId() {
        return labelResourceId;
    }

    public void setLabelResourceId(int labelResourceId) {
        this.labelResourceId = labelResourceId;
    }

    public int getIncludeTagId() {
        return includeTagId;
    }

    public void setIncludeTagId(int includeTagId) {
        this.includeTagId = includeTagId;
    }

    public int getTagNameId() {
        return tagNameId;
    }

    public void setTagNameId(int tagNameId) {
        this.tagNameId = tagNameId;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean isMandatory) {
        this.isMandatory = isMandatory;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    public boolean isDecimalNo() {
        return isDecimalNo;
    }

    public void setDecimalNo(boolean isDecimalNo) {
        this.isDecimalNo = isDecimalNo;
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBold(boolean isBold) {
        this.isBold = isBold;
    }

    public boolean isFocusable() {
        return isFocusable;
    }

    public void setFocusable(boolean isFocusable) {
        this.isFocusable = isFocusable;
    }

    public boolean isFutureDateRequired() {
        return isFutureDateRequired;
    }

    public void setFutureDateRequired(boolean isFutureDateRequired) {
        this.isFutureDateRequired = isFutureDateRequired;
    }

    public ModelPropertyEditType getEditType() {
        return editType;
    }

    public void setEditType(ModelPropertyEditType editType) {
        this.editType = editType;
    }

    public SpinnerAdapter getDropDownAdapter() {
        return dropDownAdapter;
    }

    public void setDropDownAdapter(SpinnerAdapter dropDownAdapter) {
        this.dropDownAdapter = dropDownAdapter;
    }

    public AdapterView.OnItemSelectedListener getDropDownItemSelectedListener() {
        return dropDownItemSelectedListener;
    }

    public void setDropDownItemSelectedListener(AdapterView.OnItemSelectedListener
                                                        dropDownItemSelectedListener) {
        this.dropDownItemSelectedListener = dropDownItemSelectedListener;
    }

    public View.OnClickListener getViewClickListener() {
        return viewClickListener;
    }

    public void setViewClickListener(View.OnClickListener viewClickListener) {
        this.viewClickListener = viewClickListener;
    }

    public ViewPositionType getViewPositionType() {
        return viewPositionType;
    }

    public void setViewPositionType(ViewPositionType viewPositionType) {
        this.viewPositionType = viewPositionType;
    }

    public View.OnFocusChangeListener getTextFocusChangeListener() {
        return textFocusChangeListener;
    }

    public void setTextFocusChangeListener(View.OnFocusChangeListener textFocusChangeListener) {
        this.textFocusChangeListener = textFocusChangeListener;
    }

    public TextWatcher getTextChangeListener() {
        return textChangeListener;
    }

    public void setTextChangeListener(TextWatcher textChangeListener) {
        this.textChangeListener = textChangeListener;
    }
}
