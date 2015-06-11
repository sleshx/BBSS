package sg.gov.msf.bbss.logic.type;

/**
 * Created by bandaray
 */
public enum EnrolmentAppType {

    NEW("N"),
    EDIT("E"),
    VIEW("V");

    private String code;

    EnrolmentAppType(String code) {
        this.code = code;
    }

    public static EnrolmentAppType parseType(String value){
        for (EnrolmentAppType enrolmentAppType : EnrolmentAppType.values()) {
            if(enrolmentAppType.getCode().equals(value)) {
                return enrolmentAppType;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }
}
