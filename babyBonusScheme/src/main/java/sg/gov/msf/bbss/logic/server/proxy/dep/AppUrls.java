package sg.gov.msf.bbss.logic.server.proxy.dep;

/**
 * Created by bandaray
 * Modified to add more URL by chuanhe
 */
public class AppUrls {

//  SIT
    public static String SERVICE_URL = "http://192.168.204.214/parent-rs/services/rest";

//  UAT
//  public static String SERVICE_URL = "http://118.201.152.13/parent-rs/services/rest/";

    // ------------------ Common
    public static String MASTER_DATA_URL = SERVICE_URL + "/home/common/getMasterData";

    // ------------------ Home
    public static String HOME_CHECK_SIBLING_HOOD = SERVICE_URL+"/home/familyView/getSiblingCheck";
    public static String HOME_GET_USER_PROFILE = SERVICE_URL + "/home/userProfile/getUserProfile?userId=%s";
    public static String HOME_UPDATE_USER_PROFILE = SERVICE_URL + "/home/userProfile/updateUserProfile";

    // ------------------ Home : Family View
    public static String HOME_FAMILYVIEW_CHILD_LIST = SERVICE_URL + "/home/familyView/getChilds?userId=%s";
    public static String HOME_FAMILYVIEW_CHILD_STATEMENT = SERVICE_URL + "/home/familyView/getChildDetails?userId=%s&childId=%s";

    // ------------------ E-Services
    public static String SERVICES_STATUS = SERVICE_URL + "/home/viewAppStatus/getAppStatus?userId=%s";
    public static String SERVICES_CHILD_ITEM_LIST_URL = SERVICE_URL + "/eservice/getChilds?userId=%s&serviceCode=%s";
    public static String SERVICES_UPDATE_URL_NAH = SERVICE_URL + "/eservice/updateNominatedAccHolder";
    public static String SERVICES_UPDATE_URL_NAN = SERVICE_URL + "/eservice/updateNominatedAccNo";
    public static String SERVICES_UPDATE_URL_CDAT = SERVICE_URL + "/eservice/updateChilDevAccTrustee";
    public static String SERVICES_UPDATE_URL_CDAB = SERVICE_URL + "/eservice/updateChildsCDABank";
    public static String SERVICES_UPDATE_URL_PSEA = SERVICE_URL + "/eservice/updateChildsCDAtoPSEA";
    public static String SERVICES_UPDATE_URL_BO = SERVICE_URL + "/eservice/updateChildsBrithOrder";
    public static String SERVICES_UPDATE_URL_CDABTC = SERVICE_URL + "/eservice/updateCDABankTC";
    public static String SERVICES_UPDATE_URL_OPENCDA = SERVICE_URL + "/eservice/updateOpenCDA";

    // ------------------ Enrolment
    public static String ENROLMENT_GET_STATUS = SERVICE_URL + "/enrollment/getEnrollmentStatus?userId=%s";
}
