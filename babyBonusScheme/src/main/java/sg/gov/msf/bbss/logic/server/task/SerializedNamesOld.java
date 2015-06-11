package sg.gov.msf.bbss.logic.server.task;

public class SerializedNamesOld {

    //-- Person
    public static final String SN_PERSON_ID = "id";
    public static final String SN_PERSON_SEQ = "seq";
    public static final String SN_PERSON_NAME = "name";
    public static final String SN_PERSON_NRIC = "nric";
    public static final String SN_PERSON_BIRTHDAY = "dob";

    //-- Adult
    public static final String SN_ADULT_RELATIONSHIP = "type";
    public static final String SN_ADULT_ID_TYPE = "idType";
    public static final String SN_ADULT_ID_NO = "idNo";
    public static final String SN_ADULT_NATIONALITY = "nationalityId";
    public static final String SN_ADULT_MODE_OF_COMM = "commType";
    public static final String SN_ADULT_MOBILE = "mobileNo";
    public static final String SN_ADULT_EMAIL = "email";
    public static final String SN_ADULT_ADDR_TYPE = "addrType";
    public static final String SN_ADULT_ADDRESS = "address";
    public static final String SN_ADULT_OCCUPATION = "occupation";
    public static final String SN_ADULT_INCOME = "income";

    //-- CDA Trustee
    public static final String SN_ADULT_CDAT_CHANGE_REASON = "reasonId";
    public static final String SN_ADULT_CDAT_CHANGE_REASON_OTHER = "reasonDesc";

    //-- Child
    public static final String SN_CHILD_BIRTH_CERT_NO = "birthCertNo";
    public static final String SN_CHILD_BIRTH_ORDER_NO = "brithOrderNo";
    public static final String SN_CHILD_IS_BORN_OVERSEAS = "bornOverseas";

    //-- Child Items
    public static final String SN_CHILD_ITEM_CG_AMT = "cgAmt";
    public static final String SN_CHILD_ITEM_CS_AMT = "childcareSubsidyAmt";
    public static final String SN_CHILD_ITEM_GM_AMT = "govMatchingAmt";

    //-- Child Declaration
    public static final String SN_CHILD_DEC_TYPE = "typeNo";
    public static final String SN_CHILD_DEC_ADOPTION_GIVEN_DATE = "adoptionGivenDate";
    public static final String SN_CHILD_DEC_ADOPTION_ORDER_DATE = "adoptionOrderDate";
    public static final String SN_CHILD_DEC_COUNTRY = "countryId";
    public static final String SN_CHILD_DEC_SINGA_CITIZEN_NO = "nric";
    public static final String SN_CHILD_DEC_DE_DATE = "decreasedDate";
    public static final String SN_CHILD_DEC_REMARK = "remark";

    //-- Sibling Check
    public static final String SN_CHILD_NRIC1 = "nric1";
    public static final String SN_CHILD_NRIC2 = "nric2";

    //-- Bank
    public static final String SN_BANK_ROOT = "bank";
    public static final String SN_BANK_ID = "bankId";
    public static final String SN_BANK_NAME = "bankName";
    public static final String SN_BRANCH_ID = "branchId";
    public static final String SN_BANK_ACC_NO = "bankAccNo";
    public static final String SN_BANK_TC_URL = "bankT&Curl";
    public static final String SN_BANK_BRANCH = "branch";

    //-- CDA Bank
    public static final String SN_CDAB_ID = "cdaBankId";
    public static final String SN_CDAB_CHANGE_REASON = "reasonId";
    public static final String SN_CDAB_CHANGE_REASON_OTHER = "reasonDesc";
    public static final String SN_CDAB_NETS_CARD_NAME = "netsCardName";

    //-- Address
    public static final String SN_ADDRESS_LOCAL_ROOT = "localAddress";
    public static final String SN_ADDRESS_POSTAL_CODE = "postCode";
    public static final String SN_ADDRESS_UNIT_NO = "unitNo";
    public static final String SN_ADDRESS_BLOCK_HOUSE_NO = "blkhseNo";
    public static final String SN_ADDRESS_STREET = "street";
    public static final String SN_ADDRESS_BUIDING = "building";
    public static final String SN_ADDRESS_ADDRESS2 = "addr1";
    public static final String SN_ADDRESS_ADDRESS1 = "addr2";

    //-- Generic Data Item
    public static final String SN_COUNTRY_ROOT = "country";
    public static final String SN_COUNTRY_ID = "countryId";
    public static final String SN_COUNTRY_NAME = "countryName";
    public static final String SN_BANK_BRANCH_ROOT = "bankBranch";
    public static final String SN_BANK_BRANCH_ID = "bankBranchId";
    public static final String SN_BANK_BRANCH_NAME = "bankBranchName";
    public static final String SN_USER_ROLE_ROOT = "userRole";
    public static final String SN_USER_ROLE_ID = "roleId";
    public static final String SN_USER_ROLE_NAME = "roleName";
    public static final String SN_NATIONALITY_ROOT = "nationality";
    public static final String SN_NATIONALITY_ID = "nationalityId";
    public static final String SN_NATIONALITY_NAME = "nationalityName";
    public static final String SN_OCCUPATION_ROOT = "occupation";
    public static final String SN_OCCUPATION_ID = "Id";
    public static final String SN_OCCUPATION_NAME = "name";
    public static final String SN_CDA_BANK_CHANGE_REASON_ROOT = "cdaBankChangeReason";
    public static final String SN_CDA_BANK_CHANGE_REASON_ID = "resonId";
    public static final String SN_CDA_BANK_CHANGE_REASON_NAME = "reasonName";
    public static final String SN_TRUSTEE_BANK_CHANGE_REASON_ROOT = "trusteeChangeReason";
    public static final String SN_TRUSTEE_BANK_CHANGE_REASON_ID = "resonId";
    public static final String SN_TRUSTEE_BANK_CHANGE_REASON_NAME = "reasonName";

    //-- Cash Gift
    public static final String SN_CG_ID  = "cgId";
    public static final String SN_CG_AMT = "amt";
    public static final String SN_CG_PAID_DATE = "paidDate";
    public static final String SN_CG_SCHEDULED_DATE = "scheduledDate";

    //-- Child Development Account
    public static final String SN_CDA_CAP_AMT = "capAmt";
    public static final String SN_CDA_REMAIN_CAP_AMT = "remainCapAmt";
    public static final String SN_CDA_TOT_GOV_MATCH_AMT = "totalGovMatchingAmt";
    public static final String SN_CDA_TOT_DEPO_AMT = "totalDepositAmt";
    public static final String SN_CDA_EXP_DATE = "expDate";
    
    //-- Child Cate Subsidy
    public static final String SN_CC_SUBSIDY_ID = "subsidyId";
    public static final String SN_CC_SUBSIDY_NAME = "name";
    public static final String SN_CC_SUBSIDY_MONTH = "month";
    public static final String SN_CC_SUBSIDY_AMOUNT = "amount";
    public static final String SN_CC_SUBSIDY_TOT_AMOUNT = "totalAmt";

    //-- Child Development Account Matching History
    public static final String SN_CDA_HISTORY_ID = "cdaMatchingId";
    public static final String SN_CDA_HISTORY_DEP_AMT = "depositAmt";
    public static final String SN_CDA_HISTORY_MATCH_AMT = "govMatchingAmt";
    public static final String SN_CDA_HISTORY_MATCH_DATE = "govMatchingDate";
    public static final String SN_CDA_HISTORY_DEP_DATE = "depostDate";

    //-- Child Registration
    public static final String SN_CHILD_REG_EST_DELIVERY_DATE = "estDateDelivery";
    public static final String SN_CHILD_REG_IS_MARRIED = "isMarried";
    public static final String SN_CHILD_REG_IS_MARRIAGE_REG_IN_SINGAPORE= "isMarriedRegSg";
    public static final String SN_CHILD_REG_TYPE= "enrolType";

    //-- Accessible Services
    public static final String SN_ACCESS_SERVICE_ROOT = "accessibleSerivce";
    public static final String SN_ACCESS_SERVICE_IS_OUTSTANDING = "isOutstanding";
    public static final String SN_ACCESS_SERVICE_SERVICE_CODE = "serviceCode";

    //-- Service Statuses
    public static final String SN_SERVICE_APP_ID = "serviceId";
    public static final String SN_SERVICE_APP_TYPE = "serviceType";
    public static final String SN_SERVICE_APP_STATUS = "serviceStatus";
    public static final String SN_SERVICE_APP_DATE = "date";

    //-- Enrolment
    public static final String SN_ENROLMENT_APP_ID = "appId";
    public static final String SN_ENROLMENT_SUBMIT_TYPE = "submitType";
    public static final String SN_ENROLMENT_IS_PRE_POPULATED = "isPrePopulated";
    public static final String SN_ENROLMENT_IS_IS_DECLARE1 = "isdeclare1";
    public static final String SN_ENROLMENT_IS_IS_DECLARE2 = "isdeclare2";
    public static final String SN_ENROLMENT_IS_IS_DECLARE3 = "isdeclare3";
    public static final String SN_ENROLMENT_IS_IS_DECLARE4 = "isdeclare4";

    //------------------------SECTIONS -------------------------------------------------------------

    //-- Common
    public static final String SEC_COMMON_MASTER_DATA_ROOT = "masterData";
    public static final String SEC_ADDRESS = "address";
    public static final String SEC_SUPPORTING_FILES = "supFiles";

    //-- Common : Response
    public static final String SEC_RESPONSE_STATUS = "status";
    public static final String SEC_RESPONSE_CODE = "Code";
    public static final String SEC_RESPONSE_DATA = "data";
    public static final String SEC_RESPONSE_SERVICE_APP_ID = "serviceAppId";
    public static final String SEC_RESPONSE_MESSAGE = "message";
    public static final String SEC_RESPONSE_APPLICATION = "application";

    //-- Common : Child Lists
    public static final String SEC_CHILD_LIST_ROOT = "child";
    public static final String SEC_CHILD_LIST_NAH = "naHolder";
    public static final String SEC_CHILD_LIST_CDA_TRUSTEE = "cdaTrustee";
    public static final String SEC_CHILD_LIST_BANK_ACCOUNT = "bankAccount";

    //-- Enrolment
    public static final String SEC_ENROLMENT_ROOT = "enrolDetail";
    public static final String SEC_ENROLMENT_FATHER_PARTICULARS = "father";
    public static final String SEC_ENROLMENT_MOTHER_PARTICULARS = "mother";
    public static final String SEC_ENROLMENT_ENROL_CHILD = "enrolChild";
    public static final String SEC_ENROLMENT_CHILD_DETAILS = "childsDetail";
    public static final String SEC_ENROLMENT_CHILD = "child";
    public static final String SEC_ENROLMENT_NAH_PARTICULARS = "naHolder";
    public static final String SEC_ENROLMENT_NAH_ADDRESS = "naHolder.address";
    public static final String SEC_ENROLMENT_CDAT_PARTICULARS = "cdaTrustee";
    public static final String SEC_ENROLMENT_CDAT_ADDRESS = "cdaTrustee.address";
    public static final String SEC_ENROLMENT_CHILD_DEV_ACCOUNT = "cda";
    public static final String SEC_ENROLMENT_CHILD_DEV_ACCOUNT_BANK = "cdaBank";
    public static final String SEC_ENROLMENT_CASH_GIFT_ACCOUNT = "cgAuthorizer";
    public static final String SEC_ENROLMENT_MOTHER_DECLARE = "motherDeclare";
    public static final String SEC_ENROLMENT_MOTHER_DECLARE_CHILD = "motherDeclare.child";
    public static final String SEC_ENROLMENT_DECLARE = "declaration";

    //-- Enrolment: Status
    public static final String SEC_ENROLMENT__APP_STATUS = "enrollStatus";

    //-- Services : Status
    public static final String SEC_SERVICE_APP_STATUS_ROOT = "appStatusDetail";

    //-- Services : Common
    public static final String SEC_SERVICE_COMMON_CHILD_IDS = "childId";

    //-- Services : Section Change NA Holder
    public static final String SEC_SERVICE_CHANGE_NAH_ROOT = "childsNAHolderDetail";
    public static final String SEC_SERVICE_CHANGE_NAH_PARTICULARS = "naHolder";
    public static final String SEC_SERVICE_CHANGE_NAH_ADDRESS = "naHolder.address";
    public static final String SEC_SERVICE_CHANGE_NAH_CG_AUTHORIZER = "cgAuthorizer";
    public static final String SEC_SERVICE_CHANGE_NAH_IS_DECLARED = "isDeclared";

    //-- Services : Section Change NA Number
    public static final String SEC_SERVICE_CHANGE_NAN_ROOT = "childsNANumberDetail";
    public static final String SEC_SERVICE_CHANGE_NAN_CG_AUTHORIZER="cgAuthorizer";
    public static final String SEC_SERVICE_CHANGE_NAN_IS_DECLARED = "isDeclared";

    //-- Services : Section Change CDA Trustee
    public static final String SEC_SERVICE_CHANGE_CDAT_ROOT = "childsCDATrusteeDetail";
    public static final String SEC_SERVICE_CHANGE_CDAT_PARTICULARS = "cdaTrustee";
    public static final String SEC_SERVICE_CHANGE_CDAT_ADDRESS = "cdaTrustee.address";
    public static final String SEC_SERVICE_CHANGE_CDAT_CHANGE_REASON = "reasonId";
    public static final String SEC_SERVICE_CHANGE_CDAT_REASON_OTHER = "reasonDesc";
    public static final String SEC_SERVICE_CHANGE_CDAT_IS_DECLARED_1 = "isDeclared1";
    public static final String SEC_SERVICE_CHANGE_CDAT_IS_DECLARED_2 = "isDeclared2";

    // Services : Section Change CDA Bank
    public static final String SEC_SERVICE_CHANGE_CDAB_ROOT = "childCDABankDetail";
    public static final String SEC_SERVICE_CHANGE_CDAB_CHANGE_REASON = "reasonId";
    public static final String SEC_SERVICE_CHANGE_CDAB_REASON_OTHER = "reasonDesc";
    public static final String SEC_SERVICE_CHANGE_CDAB_IS_DECLARED_1 = "isDeclared1";
    public static final String SEC_SERVICE_CHANGE_CDAB_IS_DECLARED_2 = "isDeclared2";

    // Services : Section Change CDA Bank T&C
    public static final String SEC_SERVICE_CHANGE_CDABTC_ROOT = "childsCDABankTC";
    public static final String SEC_SERVICE_CHANGE_CDABTC_IS_DECLARED_1 = "isDeclared1";
    public static final String SEC_SERVICE_CHANGE_CDABTC_IS_DECLARED_2 = "isDeclared2";

    // Home
    public static final String SEC_HOME_SIBLING_CHECK = "siblingCheck";
    public static final String SEC_HOME_UPDATE_PROFILE = "userDetail";
    public static final String SEC_HOME_UPDATE_PROFILE_ADDRESS = "userDetail.address";



}
