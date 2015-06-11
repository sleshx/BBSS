package sg.gov.msf.bbss.apputils.wizard;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import sg.gov.msf.bbss.apputils.validation.ValidationInfo;
import sg.gov.msf.bbss.apputils.validation.ValidationMessage;
import sg.gov.msf.bbss.apputils.validation.ValidationType;

/**
 * Created by bandaray on 23/2/2015.
 */
public class WizardBase {

    private HashMap<Integer, HashMap<String, ValidationInfo>> wizardValidation;
    private HashMap<String, Integer> sectionPages;

    private boolean displayValidationErrors;

    public WizardBase() {
        this.wizardValidation = new HashMap<Integer, HashMap<String, ValidationInfo>>();
        this.sectionPages = new HashMap<String, Integer>();
    }

    public void addPageValidation(int pageIndex, ValidationInfo validationInfo) {
        if(!wizardValidation.containsKey(pageIndex)) {
            wizardValidation.put(pageIndex, new HashMap<String, ValidationInfo>());
        }

        HashMap<String, ValidationInfo> pageValidations = wizardValidation.get(pageIndex);
        ValidationInfo existingValidationInfo = pageValidations.get(validationInfo.getSectionName());

        if(existingValidationInfo != null) {
            for (ValidationMessage validationMessage : existingValidationInfo.getValidationMessages()) {
                if(validationMessage.getValidationType() == ValidationType.SERVER){
                    validationInfo.addValidationMessage(validationMessage);
                }
            }
        }

        pageValidations.put(validationInfo.getSectionName(), validationInfo);
    }

    public void addPageValidations(String sectionName, ValidationInfo validationInfo) {
        Integer pageIndex = sectionPages.get(sectionName);
        addPageValidation(pageIndex, validationInfo);
    }

    public void clearClientPageValidations(int pageIndex) {
        HashMap<String, ValidationInfo> pageValidations = wizardValidation.get(pageIndex);

        if(pageValidations != null) {
            for (Map.Entry<String, ValidationInfo> entry : pageValidations.entrySet()) {
                entry.getValue().clearClientValidations();

                if (!entry.getValue().hasAnyValidationMessages()) {
                    pageValidations.remove(entry.getKey());
                }
            }

            if (pageValidations.isEmpty()) {
                wizardValidation.remove(pageIndex);
            }
        }
    }

    public void clearAnyPageValidations(int pageIndex) {
        wizardValidation.remove(pageIndex);
    }

    public ValidationInfo getPageSectionValidations(int pageIndex, String section) {
        HashMap<String, ValidationInfo> pageValidations = wizardValidation.get(pageIndex);

        if(pageValidations != null){
            return pageValidations.get(section);
        }

        return new ValidationInfo(section);
    }

    public ValidationInfo getPageSectionArrayValidationsForIndex(int pageIndex, String section, int arrayIndex) {
        HashMap<String, ValidationInfo> pageValidations = wizardValidation.get(pageIndex);

        if(pageValidations != null){
            return pageValidations.get(section).getArrayValidationsForIndex(arrayIndex);
        }

        return new ValidationInfo(section);
    }

    public boolean hasClientValidations() {
        for (HashMap<String, ValidationInfo> pageValidations : wizardValidation.values()) {
            for (ValidationInfo validationInfo : pageValidations.values()) {
                if(validationInfo.hasClientValidationMessages()){
                    return true;
                }
            }
        }

        return false;
    }

    public boolean hasAnyValidations() {
        return wizardValidation.size() > 0;
    }

    public int getFirstErrorPage() {
        if(wizardValidation.size() == 0){
            return -1;
        }

        return Collections.min(wizardValidation.keySet());
    }

    public boolean isDisplayValidationErrors() {
        return displayValidationErrors;
    }

    public void setDisplayValidationErrors(boolean displayValidationErros) {
        this.displayValidationErrors = displayValidationErros;
    }

    public void addSectionPage(String sectionName, Integer pageIndex){
        sectionPages.put(sectionName, pageIndex);
    }
}
