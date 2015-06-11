package sg.gov.msf.bbss.logic.server.proxy.dev;

/**
 * Created by bandaray
 * Modified to add more URL by chuanhe
 */
public class AppUrls {

    //"http://192.168.0.13:6060/bbss-ws";

    public static String SERVICE_URL = "http://192.168.0.10:6060/bbss-ws";

    // ------------------ Common
    public static String MASTER_DATA_URL = SERVICE_URL + "/getMasterData?type=%s";
    public static String UPLOAD_FILE_URL = SERVICE_URL + "/addFile";
    public static String CHILD_ITEM_LIST_URL = SERVICE_URL + "/getChildList";

    // ------------------ Home
    public static String CHECK_SIBLINGHOOD = SERVICE_URL+"/getSiblingCheck";
    public static String UPDATE_USER_PROFILE = SERVICE_URL + "/updateUserProfile";
    public static String GET_USER_PROFILE = SERVICE_URL + "/getUserProfile";
    public static String GET_SERVICE_STATUS = SERVICE_URL + "/getAppStatus";

    // ------------------ Home : Family View
    public static String CHILD_LIST_URL = SERVICE_URL + "/getChilds";
    public static String CHILD_STATEMENT_URL = SERVICE_URL + "/getChildDetails?ChildId=%s";

    // ------------------ Enrolment
    public static String GET_ENROLMENT_PRE_POPULATED_DATA = SERVICE_URL + "/getApplicationPrePoulated";
    public static String GET_ENROLMENT_SAVED_DATA = SERVICE_URL + "/getApplicationEditView";
    public static String GET_ENROLMENT_STATUS = SERVICE_URL + "/getEnrollmentStatus";
    public static String UPDATE_ENROLMENT_APPLICATION = SERVICE_URL + "/updateApplication";

    // ------------------ Services
    public static String NAH_UPDATE_URL = SERVICE_URL + "/updateNominatedAccHolder";
    public static String NAN_UPDATE_URL = SERVICE_URL + "/updateNominatedAccNo";
    public static String CDAT_UPDATE_URL = SERVICE_URL + "/updateChilDevAccTrustee";
    public static String CDAB_UPDATE_URL = SERVICE_URL + "/updateChildsCDABank";
    public static String PSEA_UPDATE_URL = SERVICE_URL + "/updateChildsCDAtoPSEA";
    public static String CDABTC_UPDATE_URL = SERVICE_URL + "/updateCDABankTC";
    public static String OPEN_CDA_UPDATE_URL = SERVICE_URL + "/updateOpenCDA";
    public static String CHANGE_BO_URL = SERVICE_URL + "/updateChildsBrithOrder";

    //--Others

    public static String GET_CHILD_LIST = SERVICE_URL + "/getChildList";

}
