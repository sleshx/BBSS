package sg.gov.msf.bbss.logic;

import sg.gov.msf.bbss.logic.server.login.SessionContainer;

/**
 * Created by bandaray
 */
public class BabyBonusConstants {

    //--- Adult
    public static int LENGTH_ADULT_NRIC_FIN = 9;
    public static int LENGTH_ADULT_ID_NO = 20;
    public static int LENGTH_ADULT_NAME = 66;
    public static int LENGTH_ADULT_MOBILE = 16;
    public static int LENGTH_ADULT_EMAIL = 320;
    public static int LENGTH_ADULT_INCOME = 23;

    //--- Address
    public static int LENGTH_ADDRESS_POSTAL_CODE = 6;
    public static int LENGTH_ADDRESS_UNIT_NO = 5;
    public static int LENGTH_ADDRESS_BLK_HOUSE_NO = 10;
    public static int LENGTH_ADDRESS_STREET = 32;
    public static int LENGTH_ADDRESS_BUILDING = 32;
    public static int LENGTH_ADDRESS_FOREIGN_1 = 60;
    public static int LENGTH_ADDRESS_FOREIGN_2 = 60;

    //--- URL
    public static String FEEDBACK_URL = "http://www.babybonus.gov.sg/bbss/html/feedback.html";
    public static String FAQ_URL = "http://192.168.204.214/parent-home/faq.html";
    public static String ABOUT_URL = "http://192.168.204.214/parent-home/about.html";

    public static String LOGIN_URL = "http://192.168.204.214/parent-rs/SingpassRequestServlet";
    public static String LOGOUT_URL = "http://192.168.204.214/parent-rs/services/itrust/authentication/logout/";

    public static String MSF_CDA_URL = "http://www.babybonus.gov.sg/bbss/html/index.html";
    public static String MSF_PSEA_URL = "http://www.babybonus.gov.sg/bbss/html/index.html";

    public static String ELIGIBILITY_CHECK_URL = "http://192.168.204.214/parent-helper/screen/BBSS/en-US/summary?user=guest";
    public static String AI_LOCATOR_URL = "http://192.168.204.214/parent-home/approved-institutions.html";

    //--- BUTTON IMAGES
    public static int BUTTON_DELETE = android.R.drawable.ic_menu_delete;
    public static int BUTTON_VIEW = android.R.drawable.ic_menu_view;
    public static int BUTTON_EDIT = android.R.drawable.ic_menu_edit;

    //--- ENROLMENT
    public static String ENROLMENT_CHILD_REGISTRATION_TYPE = "CHILD_REGISTRATION_TYPE";
    public static String ENROLMENT_IS_VIEW_EDIT_MODE = "IS_VIEW_EDIT_MODE";
    public static String ENROLMENT_SELECTED_LIST_POSITION = "SELECTED_LIST_POSITION";
    public static String ENROLMENT_IS_PRE_PO = "IS_PRE_PO";
    public static String ENROLMENT_APP_MODE = "APP_MODE";

    //--- SERVICES
    public static String SERVICES_APP_ID = "SERVICES_APP_ID";

    //-- COMMON
    public static String LOGIN_TO_ACTIVITY_CLASS = "LOGIN_TO_ACTIVITY_CLASS";
    public static String CURRENT_FRAGMENT_POSITION = "CURRENT_FRAGMENT_POSITION";

    //-- HTTP CODES
    public static String HTTP_OK = "200";

    //-- SINGPASS
    public static String IS_LOGOUT_CLICKED = "IS_LOGOUT_CLICKED";

}
