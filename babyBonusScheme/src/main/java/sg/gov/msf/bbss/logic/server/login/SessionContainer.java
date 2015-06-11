package sg.gov.msf.bbss.logic.server.login;

import sg.gov.msf.bbss.logic.type.LoginUserType;
import sg.gov.msf.bbss.logic.type.UserRoleType;
import sg.gov.msf.bbss.model.entity.masterdata.AccessibleService;
import sg.gov.msf.bbss.model.entity.masterdata.GenericDataItem;

/**
 * Created by bandaray on 20/5/2015.
 */
public class SessionContainer {
    private String nric;
    private String sessionToken;
    private LoginUserType loginType;
    private GenericDataItem[] userRoles;
    private AccessibleService[] accessibleServices;

    private boolean canAccessFamilyView;
    private boolean canAccessUpdateProfile;
    private boolean canAccessServiceStatus;

    public SessionContainer(String nric, String sessionToken, LoginUserType loginType) {
        this.nric = nric;
        this.sessionToken = sessionToken;
        this.loginType = loginType;
    }

    public String getNric() {
        return nric;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public LoginUserType getLoginType() {
        return loginType;
    }

    public void setUserRoles(GenericDataItem[] userRoles) {
        this.userRoles = userRoles;
        setUserAccesibles();
    }

    public GenericDataItem[] getUserRoles() {
        return userRoles;
    }

    public AccessibleService[] getAccessibleServices() {
        return accessibleServices;
    }

    public void setAccessibleServices(AccessibleService[] accessibleServices) {
        this.accessibleServices = accessibleServices;
    }

    public boolean isCanAccessFamilyView() {
        return canAccessFamilyView;
    }

    public boolean isCanAccessUpdateProfile() {
        return canAccessUpdateProfile;
    }

    public boolean isCanAccessServiceStatus() {
        return canAccessServiceStatus;
    }


    //-- User Accesibles
    private void setUserAccesibles() {
        for (GenericDataItem userRole : userRoles) {
            if(userRole.getId().equals(UserRoleType.USER_ROLE_FAMILY_VIEW.getCode())) {
                canAccessFamilyView = true;
            } else if(userRole.getId().equals(UserRoleType.USER_ROLE_USER_PROFIE.getCode())) {
                canAccessUpdateProfile = true;
            } else if(userRole.getId().equals(UserRoleType.USER_ROLE_SERVICE_STATUS.getCode())) {
                canAccessServiceStatus = true;
            }
        }

    }
}
