package sg.gov.msf.bbss.logic.server.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.connect.ConnectionHelper;
import sg.gov.msf.bbss.apputils.ui.component.MessageBox;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.masterdata.MasterDataCache;
import sg.gov.msf.bbss.logic.masterdata.MasterDataListener;
import sg.gov.msf.bbss.logic.server.ServerConnectionHelper;
import sg.gov.msf.bbss.logic.server.proxy.ProxyFactory;
import sg.gov.msf.bbss.logic.type.EnvironmentType;
import sg.gov.msf.bbss.logic.type.LoginModeType;
import sg.gov.msf.bbss.logic.type.LoginUserType;
import sg.gov.msf.bbss.logic.type.MasterDataType;
import sg.gov.msf.bbss.model.entity.masterdata.AccessibleService;
import sg.gov.msf.bbss.model.entity.masterdata.GenericDataItem;
import sg.gov.msf.bbss.view.eservice.ServiceStatusActivity;
import sg.gov.msf.bbss.view.eservice.ServicesHomeActivity;
import sg.gov.msf.bbss.view.home.LoginActivity;
import sg.gov.msf.bbss.view.home.UpdateProfileActivity;
import sg.gov.msf.bbss.view.home.familyview.FamilyViewMainActivity;
import sg.gov.msf.bbss.view.home.familyviewold.FamilyViewFragmentContainerActivity;

/**
 * Created by bandaray
 */
public class LoginManager {

    private  static String CURRENT_USER_DEV = "S7064368E";

    //"S7064368E" -- For family view, update profile and sibling check
    //"S7235299H" -- For enrolment status, enrolment
    //"S7309665J" -- For eServices and its status

    private static SessionContainer sessionContainer;
    private static Activity fromActivity;

    private static Class<?> destinationClass;
    private static Intent destinationIntent;
    private static boolean isLogoutClicked;

    public static void login(Activity fromActivity, Class<?> toActivityClass, Bundle bundle) {
        LoginManager.fromActivity = fromActivity;
        destinationClass = toActivityClass;
        destinationIntent = new Intent(fromActivity, toActivityClass);

        if (bundle != null) {
            destinationIntent.putExtras(bundle);
            isLogoutClicked = bundle.getBoolean(BabyBonusConstants.IS_LOGOUT_CLICKED);
        }

        if(ProxyFactory.ENVIRONMENT == EnvironmentType.DEV) {
            sessionContainer = new SessionContainer(CURRENT_USER_DEV, AppConstants.EMPTY_STRING, LoginUserType.PARENT);
        }

        //TODO : comment after proper singpass
        //sessionContainer = new SessionContainer(CURRENT_USER_DEV, "TOKEN1", LoginUserType.PARENT);

        if (isLogoutClicked) {
            Intent loginIntent = new Intent(fromActivity, LoginActivity.class);
            loginIntent.putExtras(bundle);

            fromActivity.startActivity(loginIntent);
            fromActivity.finish();
        } else {
            if (sessionContainer != null) {
                if (ConnectionHelper.isNetworkConnected(LoginManager.fromActivity)) {
                    fetchUserSessionRoles();
                    fetchUserAccessibleServices();
                } else {
                    ServerConnectionHelper.createDeviceOfflineMessageBox(LoginManager.fromActivity, null);
                }
            } else {
                Intent loginIntent = new Intent(fromActivity, LoginActivity.class);
                loginIntent.putExtra(BabyBonusConstants.LOGIN_TO_ACTIVITY_CLASS, toActivityClass);
                loginIntent.putExtras(bundle);

                fromActivity.startActivity(loginIntent);
                fromActivity.finish();
            }
        }
    }

    public static SessionContainer getSessionContainer() {
        return sessionContainer;
    }

    public static void setSessionContainer(SessionContainer sessionContainer) {
        LoginManager.sessionContainer = sessionContainer;
    }

    //----------------------------------------------------------------------------------------------

    private static void fetchUserSessionRoles() {
        MasterDataCache masterDataCache = new MasterDataCache(fromActivity);
        masterDataCache.getGenericDataItems(MasterDataType.USER_ROLE,
                new MasterDataListener<GenericDataItem[]>() {
                    @Override
                    public void onMasterData(GenericDataItem[] masterDataItems) {
                        sessionContainer.setUserRoles(masterDataItems);

                        String errorMessage = getAccessDeniedErrorMessages();
                        if (errorMessage == AppConstants.EMPTY_STRING) {
                            fromActivity.startActivity(destinationIntent);
                            fromActivity.finish();
                        } else {
                            MessageBox.show(fromActivity, errorMessage, false, true,
                                    R.string.btn_ok, false, 0, null);
                        }
                    }
                });
    }

    private static void fetchUserAccessibleServices(){
        MasterDataCache masterDataCache = new MasterDataCache(fromActivity);
        masterDataCache.getAccessibleServices(new MasterDataListener<AccessibleService[]>() {
            @Override
            public void onMasterData(AccessibleService[] accessibleServices) {
                sessionContainer.setAccessibleServices(accessibleServices);

                if (destinationClass == ServicesHomeActivity.class) {
                    if (accessibleServices.length > 0) {
                        fromActivity.startActivity(destinationIntent);
                        fromActivity.finish();
                    } else {
                        MessageBox.show(fromActivity, StringHelper.getStringByResourceId(fromActivity,
                                        R.string.error_common_not_allowed_service),
                                false, true, R.string.btn_ok, false, 0, null);
                    }
                }
            }
        });
    }

    //----------------------------------------------------------------------------------------------

    private static String getAccessDeniedErrorMessages() {
        int errorMessageResourceId = 0;

        SessionContainer sessionContainer = LoginManager.getSessionContainer();

        if (destinationClass == FamilyViewMainActivity.class &&
                !sessionContainer.isCanAccessFamilyView()) {
            errorMessageResourceId = R.string.error_common_not_allowed_family_view;

        } else if (destinationClass == UpdateProfileActivity.class &&
                !sessionContainer.isCanAccessUpdateProfile()) {
            errorMessageResourceId = R.string.error_common_not_allowed_update_profile;

        } else if (destinationClass == ServiceStatusActivity.class &&
                !sessionContainer.isCanAccessServiceStatus()) {
            errorMessageResourceId = R.string.error_common_not_allowed_service_status;
        }

        if (errorMessageResourceId > 0) {
            return StringHelper.getStringByResourceId(fromActivity, errorMessageResourceId);
        }

        return  AppConstants.EMPTY_STRING;
    }
}
