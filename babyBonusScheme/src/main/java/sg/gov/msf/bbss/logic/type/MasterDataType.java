package sg.gov.msf.bbss.logic.type;

import sg.gov.msf.bbss.logic.server.SerializedNames;

/**
 * Created by bandaray
 */
public enum MasterDataType {

    COUNTRY("country", SerializedNames.SN_COUNTRY_ROOT,
            SerializedNames.SN_COUNTRY_ID, SerializedNames.SN_COUNTRY_NAME),

    BANK_BRANCH("bank branch", SerializedNames.SN_BANK_BRANCH_ROOT,
            SerializedNames.SN_BANK_BRANCH_ID, SerializedNames.SN_BANK_BRANCH_NAME),

    USER_ROLE("userRole", SerializedNames.SN_USER_ROLE_ROOT,
            SerializedNames.SN_USER_ROLE_ID, SerializedNames.SN_USER_ROLE_NAME),

    NATIONALITY("nationality", SerializedNames.SN_NATIONALITY_ROOT,
            SerializedNames.SN_NATIONALITY_ID, SerializedNames.SN_NATIONALITY_NAME),

    OCCUPATION("occupation", SerializedNames.SN_OCCUPATION_ROOT,
            SerializedNames.SN_OCCUPATION_ID, SerializedNames.SN_OCCUPATION_NAME),

    CDA_BANK_CHANGE_REASON("cda bank change reason", SerializedNames.SN_CDA_BANK_CHANGE_REASON_ROOT,
            SerializedNames.SN_CDA_BANK_CHANGE_REASON_ID, SerializedNames.SN_CDA_BANK_CHANGE_REASON_NAME),

    TRUSTEE_BANK_CHANGE_REASON("trustee change reason", SerializedNames.SN_TRUSTEE_BANK_CHANGE_REASON_ROOT,
            SerializedNames.SN_TRUSTEE_BANK_CHANGE_REASON_ID, SerializedNames.SN_TRUSTEE_BANK_CHANGE_REASON_NAME),

    BANK_MA("bankMA", SerializedNames.SN_BANK_ROOT, SerializedNames.SN_BANK_ID, SerializedNames.SN_BANK_NAME),

    BANK("bank", SerializedNames.SN_BANK_ROOT, SerializedNames.SN_BANK_ID, SerializedNames.SN_BANK_NAME),

    LOCAL_ADDRESS("local address", SerializedNames.SN_ADDRESS_LOCAL_ROOT),

    ACCESSIBLE_SERVICES("accessibleServices", SerializedNames.SN_ACCESS_SERVICE_ROOT),

    DEV_OCCUPATION("occupation", SerializedNames.SN_OCCUPATION_ROOT,"Id", "name"),

    DEV_CDA_BANK_CHANGE_REASON("cdaBankChangeReason", SerializedNames.SN_CDA_BANK_CHANGE_REASON_ROOT,
            SerializedNames.SN_CDA_BANK_CHANGE_REASON_ID, SerializedNames.SN_CDA_BANK_CHANGE_REASON_NAME),

    DEV_TRUSTEE_BANK_CHANGE_REASON("trusteeChangeReason", SerializedNames.SN_TRUSTEE_BANK_CHANGE_REASON_ROOT,
            SerializedNames.SN_TRUSTEE_BANK_CHANGE_REASON_ID, SerializedNames.SN_TRUSTEE_BANK_CHANGE_REASON_NAME),;

    private String queryStringPart;
    private String jsonRootPropertyName;
    private String jsonIdPropertyName;
    private String jsonNamePropertyName;


    MasterDataType(String queryStringPart, String jsonRootPropertyName){
        this.queryStringPart = queryStringPart;
        this.jsonRootPropertyName = jsonRootPropertyName;
    }

    MasterDataType(String queryStringPart, String jsonRootPropertyName, String jsonIdPropertyName,
                   String jsonNamePropertyName) {

        this.queryStringPart = queryStringPart;
        this.jsonRootPropertyName = jsonRootPropertyName;
        this.jsonIdPropertyName = jsonIdPropertyName;
        this.jsonNamePropertyName = jsonNamePropertyName;
    }

    public String getJsonRootPropertyName() {
        return jsonRootPropertyName;
    }

    public String getJsonIdPropertyName() {
        return jsonIdPropertyName;
    }

    public String getJsonNamePropertyName() {
        return jsonNamePropertyName;
    }

    public String getQueryStringPart() {
        return queryStringPart;
    }
}
