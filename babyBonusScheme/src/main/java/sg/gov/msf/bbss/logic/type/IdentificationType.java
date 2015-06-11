package sg.gov.msf.bbss.logic.type;

import java.lang.reflect.Proxy;

import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;

/**
 * Created by bandaray
 */
public enum IdentificationType {
    SINGAPORE_PINK("SP", "SP", "S/Pink"),
    SINGAPORE_BLUE("SB", "PR", "S/Blue"),
    FOREIGN_PASSPORT("FP", "OT", "Passport Number"),
    FOREIGN_ID("FI", "OT", "Foreign ID");


    private String code;
    private String codeDev;
    private String idType;

    IdentificationType(String code, String codeDev, String idType) {
        this.code = code;
        this.codeDev = codeDev;
        this.idType = idType;
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
        return idType;
    }

    public static IdentificationType parseType(String value){
        for (IdentificationType type : IdentificationType.values()) {
            if(type.getCode().equals(value)) {
                return type;
            }
        }

        return null;
    }
}
