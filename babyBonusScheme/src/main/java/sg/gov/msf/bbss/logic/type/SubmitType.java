package sg.gov.msf.bbss.logic.type;

/**
 * Created by bandaray on 10/5/2015.
 */
public enum SubmitType {
    DRAFT("D"),
    SAVE("S");

    private String code;

    SubmitType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}