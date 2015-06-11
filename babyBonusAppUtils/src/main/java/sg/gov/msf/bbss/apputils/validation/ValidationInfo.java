package sg.gov.msf.bbss.apputils.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by bandaray on 22/2/2015.
 */
public class ValidationInfo {
    private List<ValidationMessage> validationMessages;
    private String sectionName;
    private HashMap<Integer, ValidationInfo> arrayValidationInfo;

    public ValidationInfo(String sectionName){
        this.sectionName = sectionName;
        validationMessages = new ArrayList<ValidationMessage>();
        arrayValidationInfo = new HashMap<Integer, ValidationInfo>();
    }

    public void addValidationMessage(ValidationMessage validationMessage){
        ValidationMessage toRemove = null;

        for (ValidationMessage message : validationMessages) {
            if(message.getSerialName().equals(validationMessage.getSerialName())) {
                validationMessages.remove(message);
                break;
            }
        }

        validationMessages.add(validationMessage);
    }

    public void addArrayValidationInfo(int arrayIndex, ValidationInfo validationInfo) {
        arrayValidationInfo.put(arrayIndex, validationInfo);
    }

    public void clearAllValidationMessages() {
        validationMessages.clear();

        for (ValidationInfo validationInfo : arrayValidationInfo.values()) {
            validationInfo.clearAllValidationMessages();
        }
    }

    public void clearClientValidations() {
        ListIterator<ValidationMessage> iterator = validationMessages.listIterator();

        while(iterator.hasNext()){
            if(iterator.next().getValidationType() != ValidationType.SERVER){
                iterator.remove();
            }
        }

        for (ValidationInfo validationInfo : arrayValidationInfo.values()) {
            validationInfo.clearClientValidations();
        }
    }

    public List<ValidationMessage> getValidationMessages() {
        return validationMessages;
    }

    public String getSectionName() {
        return sectionName;
    }

    public boolean hasAnyValidationMessages() {
        return validationMessages.size() > 0 || arrayValidationInfo.size() > 0;
    }

    public boolean hasClientValidationMessages() {
        for(ValidationMessage validationMessage : validationMessages) {
            if(validationMessage.getValidationType() != ValidationType.SERVER){
                return true;
            }
        }

        for (ValidationInfo validationInfo : arrayValidationInfo.values()) {
            if(validationInfo.hasClientValidationMessages()) {
                return true;
            }
        }

        return false;
    }

    public Integer[] getArrayValidationIndexes() {
        return arrayValidationInfo.keySet().toArray(new Integer[0]);
    }

    public ValidationInfo getArrayValidationsForIndex(int index){
        ValidationInfo validationInfo = arrayValidationInfo.get(index);

        if(validationInfo != null)
            return validationInfo;

        return new ValidationInfo(Integer.toString(index));
    }
}
