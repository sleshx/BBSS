package sg.gov.msf.bbss.logic.type;

import sg.gov.msf.bbss.view.eservice.ServiceStatusActivity;
import sg.gov.msf.bbss.view.eservice.bo.ChangeBoFragmentContainerActivity;
import sg.gov.msf.bbss.view.eservice.cdab.ChangeCdabFragmentContainerActivity;
import sg.gov.msf.bbss.view.eservice.cdabtc.AcceptCdabTcFragmentContainerActivity;
import sg.gov.msf.bbss.view.eservice.cdat.ChangeCdatFragmentContainerActivity;
import sg.gov.msf.bbss.view.eservice.nah.ChangeNahFragmentContainerActivity;
import sg.gov.msf.bbss.view.eservice.nan.ChangeNanFragmentContainerActivity;
import sg.gov.msf.bbss.view.eservice.opencda.OpenCdaFragmentContainerActivity;
import sg.gov.msf.bbss.view.eservice.psea.TransferPseaFragmentContainerActivity;
import sg.gov.msf.bbss.view.home.UpdateProfileActivity;
import sg.gov.msf.bbss.view.home.familyviewold.FamilyViewFragmentContainerActivity;

/**
 * Created by bandaray
 */
public enum UserRoleType {

    USER_ROLE_USER_PROFIE("DEF-role-userProfile", "Update User Profile",
            UpdateProfileActivity.class),
    USER_ROLE_SERVICE_STATUS("DEF-role-viewServicesStatus", "View Services Application Status",
            ServiceStatusActivity.class),
    USER_ROLE_FAMILY_VIEW("DEF-role-familyView", "Family View",
            FamilyViewFragmentContainerActivity.class),
    USER_ROLE_CHANGE_NAH("DEF-role-ChangeAccHolder", "Request to Change Nominated Account Holder",
            ChangeNahFragmentContainerActivity.class),
    USER_ROLE_CHANGE_NAN("DEF-role-ChangeAcc", "Request to Change Nominated Account Number",
            ChangeNanFragmentContainerActivity.class),
    USER_ROLE_CHANGE_CDAT("DEF-role-ChangeCDATrustee", "Request to Change CDA Trustee",
            ChangeCdatFragmentContainerActivity.class),
    USER_ROLE_CHANGE_CDAB("DEF-role-ChangeMA", "Request to Change CDA Bank",
            ChangeCdabFragmentContainerActivity.class),
    USER_ROLE_CDA_TO_PSEA("DEF-role-TranCDAtoPSEA", "Request to Transfer from CDA to PSEA",
            TransferPseaFragmentContainerActivity.class),
    USER_ROLE_CHANGE_BO("DEF-role-ChangeBO", "Request to Change Birth Order",
            ChangeBoFragmentContainerActivity.class),
    USER_ROLE_TERMS_COND("DEF-role-FATCA", "Acceptance of CDA Bank’s Terms and Conditions",
            AcceptCdabTcFragmentContainerActivity.class),
    USER_ROLE_OPEN_CDA("DEF-role-OpeningofCDA", "Opening of CDA",
            OpenCdaFragmentContainerActivity.class);

    private String code;
    private String rolePurpose;
    private Class classToLoad;

    UserRoleType(String code, String rolePurpose, Class classToLoad) {
        this.code = code;
        this.rolePurpose = rolePurpose;
        this.classToLoad = classToLoad;
    }

    public String getCode(){
        return code;
    }

    public String toString(){
        return code;
    }

    public static UserRoleType parseType(String value){
        for (UserRoleType userRole : UserRoleType.values()) {
            if(userRole.getCode().equals(value)) {
                return userRole;
            }
        }

        return null;
    }

}
