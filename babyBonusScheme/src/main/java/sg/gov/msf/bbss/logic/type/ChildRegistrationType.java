package sg.gov.msf.bbss.logic.type;

import android.content.Context;

import java.io.Serializable;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.logic.server.SerializedNames;

/**
 * Created by bandaray
 */
public enum ChildRegistrationType implements Serializable {

    PRE_BIRTH("Pre-Birth", R.string.label_child_reg_type_pre_birth),
    POST_BIRTH("Post-Birth", R.string.label_child_reg_type_post_birth),
    CITIZENSHIP("Citizenship", R.string.label_child_reg_type_citizenship),
    ADOPTION("Adoption" ,R.string.label_child_reg_type_adoption);

    private String code;
    private int registrationType;
    private String enrolmentSectionName;

    ChildRegistrationType(String code, int registrationType) {
        this.code = code;
        this.registrationType = registrationType;
    }

    public String getDisplayName(Context context) {
        return StringHelper.getStringByResourceId(context, registrationType);
    }

    public String getCode() {
        return code;
    }

    public static ChildRegistrationType parseType(String value){
        for (ChildRegistrationType relationshipType : ChildRegistrationType.values()) {
            if(relationshipType.getCode().equals(value)) {
                return relationshipType;
            }
        }

        return null;
    }
}
