package sg.gov.msf.bbss.logic.type;

import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;

/**
 * Created by bandaray
 */
public enum CommunicationType {
    SMS("SMS", "1", "SMS"),
    EMAIL("Email", "2", "Email"),
    LETTER("Letter", "3", "Letter");

    private String code;
    private String codeDev;
    private String modeOfCommunication;

    CommunicationType(String code, String codeDev, String modeOfCommunication) {
        this.code = code;
        this.codeDev = codeDev;
        this.modeOfCommunication = modeOfCommunication;
    }

    public String getCode() {
        if(ProxyFactory.ENVIRONMENT == EnvironmentType.DEP)
            return code;

        if(ProxyFactory.ENVIRONMENT == EnvironmentType.DEV)
            return codeDev;

        return null;
    }

    @Override
    public String toString() {
        return modeOfCommunication;
    }

    public static CommunicationType parseType(String value){
        for (CommunicationType type : CommunicationType.values()) {
            if(type.getCode().equals(value)) {
                return type;
            }
        }

        if(ProxyFactory.ENVIRONMENT == EnvironmentType.DEV) {
            if (value.equalsIgnoreCase("sms"))
                return CommunicationType.SMS;

            if (value.equalsIgnoreCase("email"))
                return CommunicationType.EMAIL;

            if (value.equalsIgnoreCase("letter"))
                return CommunicationType.LETTER;
        }

        return null;
    }
}
