package sg.gov.msf.bbss.apputils.validation;

import sg.gov.msf.bbss.apputils.validation.ValidationType;

/**
 * Created by bandaray on 22/2/2015.
 */
public class ValidationMessage {
    private ValidationType validationType;
    private String serialName;
    private String message;

    public ValidationMessage(ValidationType validationType) {
        this.validationType = validationType;
    }

    public ValidationType getValidationType() {
        return validationType;
    }

    public String getSerialName() {
        return serialName;
    }

    public void setSerialName(String serialName) {
        this.serialName = serialName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
