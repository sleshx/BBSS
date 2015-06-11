package sg.gov.msf.bbss.logic.type;

/**
 * Created by bandaray
 */
public enum YesNoType {
    YES("1", "Yes"),
    NO("0", "No");

    private String code;
    private String type;

    YesNoType(String code, String type)
    {
        this.code = code;
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return type;
    }

    public static YesNoType parseType(String value){
        for (YesNoType yesNoType : YesNoType.values()) {
            if(yesNoType.getCode().equals(value)) {
                return yesNoType;
            }
        }

        return null;
    }
}
