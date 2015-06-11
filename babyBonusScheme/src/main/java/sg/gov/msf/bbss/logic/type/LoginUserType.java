package sg.gov.msf.bbss.logic.type;

/**
 * Created by bandaray
 */
public enum LoginUserType {
    PARENT("Parent"),
    NOMINATED_ACCOUNT_HOLDER("Nominated Account Holder"),
    CDA_TRUSTEE("CDA Trustee");

    private String userType;

    LoginUserType(String userType) {
        this.userType = userType;
    }

    @Override public String toString() {
        return userType;
    }
}
