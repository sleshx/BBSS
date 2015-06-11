package sg.gov.msf.bbss.logic.type;

import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;

/**
 * Created by bandaray
 */
public enum RelationshipType {
    FATHER("F", "Father", "Father"),
    MOTHER("M", "Mother", "Mother"),
    TRUSTEE("O", "Trustee", "Third Party");

    private String code;
    private String codeDev;
    private String relationship;

    RelationshipType(String code, String codeDev, String relationship) {
        this.code = code;
        this.codeDev = codeDev;
        this.relationship = relationship;
    }

    public String getCode(){
        if(ProxyFactory.ENVIRONMENT == EnvironmentType.DEP)
            return code;

        if(ProxyFactory.ENVIRONMENT == EnvironmentType.DEV)
            return codeDev;
        return null;
    }

    @Override public String toString() {
        return relationship;
    }

    public static RelationshipType parseType(String value){
        for (RelationshipType relationshipType : RelationshipType.values()) {
            if(relationshipType.getCode().equals(value)) {
                return relationshipType;
            }
        }

        return null;
    }
}
