package sg.gov.msf.bbss.logic.type;

import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;

/**
 * Created by bandaray
 */
public enum AddressType {
    LOCAL("L", "Local"),
    FOREIGN("F", "Foreign");

    private String code;
    private String addrType;

    AddressType(String code, String addrType) {
        this.code = code;
        this.addrType = addrType;
    }

    @Override public String toString() {
        return addrType;
    }

    public String getCode() {
        return code;
    }

    public static AddressType parseType(String value){
        for (AddressType type : AddressType.values()) {
            if(type.getCode().equals(value)) {
                return type;
            }
        }

        if(ProxyFactory.ENVIRONMENT == EnvironmentType.DEV) {
            if (value.equalsIgnoreCase("local"))
                return AddressType.LOCAL;

            if (value.equalsIgnoreCase("foreign"))
                return AddressType.LOCAL;
        }

        return null;
    }
}
