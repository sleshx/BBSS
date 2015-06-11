package sg.gov.msf.bbss.logic;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.meta.ModelViewSynchronizer;
import sg.gov.msf.bbss.apputils.ui.component.MessageBox;
import sg.gov.msf.bbss.apputils.ui.helper.MessageBoxButtonClickListener;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.apputils.util.ValidationHandler;
import sg.gov.msf.bbss.apputils.validation.ValidationInfo;
import sg.gov.msf.bbss.apputils.validation.ValidationMessage;
import sg.gov.msf.bbss.apputils.validation.ValidationType;
import sg.gov.msf.bbss.logic.adapter.DisplayChildArrayAdapter;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.type.ChildListType;
import sg.gov.msf.bbss.logic.type.IdentificationType;
import sg.gov.msf.bbss.logic.type.RelationshipType;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;
import sg.gov.msf.bbss.model.entity.childdata.ChildNric;
import sg.gov.msf.bbss.model.entity.childdata.ChildRegistration;
import sg.gov.msf.bbss.model.entity.common.BankAccount;
import sg.gov.msf.bbss.model.entity.people.Adult;
import sg.gov.msf.bbss.model.entity.people.Child;

/**
 * Created by bandaray
 */
public class BabyBonusValidationHandler {

    public static  boolean validateSameBankOrAccount(Context context, ChildListType serviceType,
                                                     BankAccount bankAccount,
                                                     ArrayList<ChildItem> childItems,
                                                     DisplayChildArrayAdapter adapter) {

        ArrayList<Integer> childPositions = new ArrayList<Integer>();
        StringBuilder builder = new StringBuilder();
        int displayMessageId = 0;

        for (int position = 0; position < childItems.size(); position++) {
            ChildItem childItem = childItems.get(position);

            switch (serviceType) {
                case CHANGE_NAN:
                    displayMessageId = R.string.error_services_same_nan;
                    if(bankAccount == childItem.getBankAccount()){
                        builder.append(childItem.getChild().getId());
                        builder.append(AppConstants.SYMBOL_COMMA_WITH_SPACE);
                        childPositions.add(position);
                    }
                    break;
                case CHANGE_CDAB:
                    displayMessageId = R.string.error_services_same_cdab;
                    if(bankAccount.getBank() == childItem.getBankAccount().getBank()){
                        builder.append(childItem.getChild().getId());
                        builder.append(AppConstants.SYMBOL_COMMA_WITH_SPACE);
                        childPositions.add(position);
                    }
                    break;
            }
        }

        int builderLength = builder.length();
        adapter.setColorPositions(childPositions);

        if (builderLength > 0 && displayMessageId > 0) {
            builder.deleteCharAt(builderLength - 2);
            createMessageBox(context, context.getString(displayMessageId, builder.toString()));
        } else {
            return false;
        }

        return true;

    }

    public static  boolean validateSameRelationshipType(Context context, ChildListType serviceType,
                                                        RelationshipType selectedRelationship,
                                                        ArrayList<ChildItem> childItems,
                                                        DisplayChildArrayAdapter adapter) {

        ArrayList<Integer> childPositions = new ArrayList<Integer>();
        StringBuilder builder = new StringBuilder();
        int displayMessageId = 0;

        for (int position = 0; position < childItems.size(); position++) {
            ChildItem childItem = childItems.get(position);

            RelationshipType currentRelationship = null;
            switch (serviceType) {
                case CHANGE_NAH:
                    currentRelationship = childItem.getNominatedAccountHolder().getRelationshipType();
                    displayMessageId = R.string.error_services_same_nah;
                    break;
                case CHANGE_CDAT:
                case OPEN_CDA:
                    currentRelationship = childItem.getChildDevAccTrustee().getRelationshipType();
                    displayMessageId = R.string.error_services_same_cdat;
                    break;
            }

            if(selectedRelationship != RelationshipType.TRUSTEE &&
                    selectedRelationship == currentRelationship){
                builder.append(childItem.getChild().getId());
                builder.append(AppConstants.SYMBOL_COMMA_WITH_SPACE);
                childPositions.add(position);
            }
        }

        int builderLength = builder.length();
        adapter.setColorPositions(childPositions);

        if (builderLength > 0 && displayMessageId > 0) {
            builder.deleteCharAt(builderLength - 2);
            createMessageBox(context, context.getString(displayMessageId, builder.toString()));
        } else {
            return false;
        }

        return true;
    }

    public static  boolean validateSameNric(Context context, ChildListType serviceType,
                                            String nric, ArrayList<ChildItem> childItems,
                                            ModelViewSynchronizer adultModelViewSynchronizer) {

        if (StringHelper.isStringNullOrEmpty(nric)) {
            return  false;
        } else {
            StringBuilder builder = new StringBuilder();
            String displayMessage = AppConstants.EMPTY_STRING;

            for (ChildItem childItem : childItems) {
                String currentNric;
                switch (serviceType) {
                    case CHANGE_NAH:
                        currentNric = childItem.getNominatedAccountHolder().getNric();
                        displayMessage = context.getString(R.string.error_services_same_nah,
                                builder.toString());
                        break;
                    case CHANGE_CDAT:
                        currentNric = childItem.getChildDevAccTrustee().getNric();
                        displayMessage = context.getString(R.string.error_services_same_cdat,
                                builder.toString());
                        break;
                    default:
                        currentNric = null;
                }

                if (!StringHelper.isStringNullOrEmpty(currentNric)) {
                    if (currentNric.equalsIgnoreCase(nric)) {
                        builder.append(childItem.getChild().getId());
                        builder.append(AppConstants.SYMBOL_COMMA_WITH_SPACE);
                    }
                }
            }

            int builderLength = builder.length();

            if (builderLength > 0) {
                builder.deleteCharAt(builderLength - 2);
                createMessageBox(context, displayMessage);

                int color = context.getResources().getColor(R.color.field_error);
                adultModelViewSynchronizer.setFieldBackground(Adult.FIELD_NRIC, color);
                adultModelViewSynchronizer.setFieldBackground(Adult.FIELD_NAME, color);
            } else {
                return false;
            }

            return true;
        }
    }

    //----------------------------------------------------------------------------------------------

    public static void validateCitizenNricPrefix(Context context, Adult adult,
                                                 ValidationInfo validationInfo) {

        if (adult.getIdentificationType() == IdentificationType.SINGAPORE_PINK &&
                !ValidationHandler.isCitizenNricPrefixOk(adult.getNric())) {
            ValidationMessage validationMessage = new ValidationMessage(ValidationType.DATA_FORMAT);

            validationMessage.setSerialName(SerializedNames.SN_PERSON_NRIC);
            validationMessage.setMessage(StringHelper.getStringByResourceId(context,
                    R.string.error_common_invalid_nric_prefix_for_citizen));

            validationInfo.addValidationMessage(validationMessage);
        }
    }

    public static void validateNricFormat(Context context, Adult adult,
                                          ValidationInfo validationInfo) {

        if ((adult.getIdentificationType() == IdentificationType.SINGAPORE_PINK ||
                adult.getIdentificationType() == IdentificationType.SINGAPORE_BLUE) &&
                !StringHelper.isStringNullOrEmpty(adult.getNric()) &&
                !ValidationHandler.isValidNric(adult.getNric())) {

            ValidationMessage validationMessage = new ValidationMessage(ValidationType.DATA_FORMAT);

            validationMessage.setSerialName(SerializedNames.SN_PERSON_NRIC);
            validationMessage.setMessage(StringHelper.getStringByResourceId(context,
                    R.string.error_common_invalid_nric));

            validationInfo.addValidationMessage(validationMessage);
        }
    }

    public static void validateNricFormat(Context context, ChildNric childNric,
                                          ValidationInfo validationInfo) {
        validateNricFormat(context, childNric.getNric1(), validationInfo,
                SerializedNames.SN_CHILD_NRIC1, R.string.label_sibling_check_child_1);

        validateNricFormat(context, childNric.getNric2(), validationInfo,
                SerializedNames.SN_CHILD_NRIC2, R.string.label_sibling_check_child_2);
    }

    private static void validateNricFormat(Context context, String nric, ValidationInfo validationInfo,
                                           String serializedName, int childLabelId) {
        if (!StringHelper.isStringNullOrEmpty(nric) && !ValidationHandler.isValidNric(nric)) {

            ValidationMessage validationMessage = new ValidationMessage(ValidationType.DATA_FORMAT);
            String childLabel = StringHelper.getStringByResourceId(context, childLabelId);
            String message = String.format("Invalid %s format", childLabel);

            validationMessage.setSerialName(serializedName);
            validationMessage.setMessage(message);

            validationInfo.addValidationMessage(validationMessage);
        }
    }

    //----------------------------------------------------------------------------------------------

    public static void validateMobileNumberPrefix(Context context, Adult adult,
                                                  ValidationInfo validationInfo) {
        String mobileNo = adult.getMobileNumber();
        if (mobileNo != null) {
            if (mobileNo.length() > 0) {
                char firstDigit = adult.getMobileNumber().charAt(0);
                if (firstDigit < '8') {
                    ValidationMessage validationMessage = new ValidationMessage(ValidationType.DATA_FORMAT);

                    validationMessage.setSerialName(SerializedNames.SN_ADULT_MOBILE);
                    validationMessage.setMessage(StringHelper.getStringByResourceId(context,
                            R.string.error_common_invalid_mobile_number));

                    validationInfo.addValidationMessage(validationMessage);
                }
            }
        }
    }

    public static void validateEstimatedDeliveryDate(Context context, Date date,
                                                     ValidationInfo validationInfo) {
        if (date != null) {
            //Date date = StringHelper.parseDate(eddValue);
            Date now = new Date();

            GregorianCalendar from = new GregorianCalendar();
            from.setTime(now);
            from.add(GregorianCalendar.WEEK_OF_MONTH, -2);

            GregorianCalendar to = new GregorianCalendar();
            to.setTime(now);
            to.add(GregorianCalendar.WEEK_OF_MONTH, 8);

            if (date.compareTo(from.getTime()) < 0) {
                ValidationMessage validationMessage = new ValidationMessage(ValidationType.DATA_FORMAT);

                validationMessage.setSerialName(SerializedNames.SN_CHILD_REG_EST_DELIVERY_DATE);
                validationMessage.setMessage(StringHelper.getStringByResourceId(context,
                        R.string.error_enrolment_estimated_delivery_back_dated));

                validationInfo.addValidationMessage(validationMessage);
            }

            if (date.compareTo(to.getTime()) > 0) {
                ValidationMessage validationMessage = new ValidationMessage(ValidationType.DATA_FORMAT);

                validationMessage.setSerialName(SerializedNames.SN_CHILD_REG_EST_DELIVERY_DATE);
                validationMessage.setMessage(StringHelper.getStringByResourceId(context,
                        R.string.error_enrolment_estimated_delivery_future_dated));

                validationInfo.addValidationMessage(validationMessage);
            }
        }
    }

    public static void validateIfParentsMarried(Context context, boolean isMarried,
                                                ValidationInfo validationInfo) {
        if (!isMarried) {
            ValidationMessage validationMessage = new ValidationMessage(ValidationType.ITEMS_MISSING);

            validationMessage.setSerialName(SerializedNames.SN_CHILD_REG_IS_MARRIED);
            validationMessage.setMessage(StringHelper.getStringByResourceId(context,
                    R.string.error_enrolment_you_should_be_married));

            validationInfo.addValidationMessage(validationMessage);
        }
    }

    public static void validateIfAtLeastOneChildRegistrationAdded(Context context,
                                                                  ValidationInfo validationInfo) {
        ValidationMessage validationMessage = new ValidationMessage(ValidationType.ITEMS_MISSING);

        validationMessage.setSerialName(SerializedNames.SN_CHILD_BIRTH_CERT_NO);
        validationMessage.setMessage(StringHelper.getStringByResourceId(context,
                R.string.error_enrolment_required_atleast_one_registration));

        validationInfo.addValidationMessage(validationMessage);
    }

    public static void validateIfAtLeastOneDocAttached(Context context, LayoutInflater inflater,
                                                       View rootView, ValidationInfo validationInfo) {
        SupportingDocumentsHelper docHelper = new SupportingDocumentsHelper(rootView,
                context, inflater);
        if (docHelper.getAttachedDocCount() <= 0) {
            ValidationMessage validationMessage = new ValidationMessage(ValidationType.ITEMS_MISSING);

            validationMessage.setSerialName(SerializedNames.SN_COMMON_SUPPORTING_DOC_ID);
            validationMessage.setMessage(StringHelper.getStringByResourceId(context,
                    R.string.error_enrolment_attach_marriage_certificate));

            validationInfo.addValidationMessage(validationMessage);
        }
    }

    //----------------------------------------------------------------------------------------------

    private static void createMessageBox(Context context, String message) {

        MessageBox.show(context, message, false,
                true, R.string.btn_ok, false, 0, new MessageBoxButtonClickListener() {
                    @Override
                    public void onClickPositiveButton(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickNegativeButton(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
    }
}
