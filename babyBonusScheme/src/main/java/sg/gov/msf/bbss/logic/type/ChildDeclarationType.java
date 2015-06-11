package sg.gov.msf.bbss.logic.type;

import android.content.Context;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.util.StringHelper;

/**
 * Created by bandaray
 */
public enum ChildDeclarationType {

    HAS_GIVEN_UP_FOR_ADOPTION_CHILD("a", R.string.label_child_dec_has_given_up_for_adaption),
    HAS_ADOPTED_CHILD("b", R.string.label_child_dec_has_adopted),
    HAS_NON_SINGAPORE_CHILD("c", R.string.label_child_dec_has_non_singapore_child),
    HAS_SINGAPORE_BORN_CHILD("d", R.string.label_child_dec_has_singapore_born_child),
    HAS_DECEASED_CHILD("e", R.string.label_child_dec_has_deceased);

    private String code;
    private int registrationType;

    ChildDeclarationType(String code, int registrationType) {
        this.code = code;
        this.registrationType = registrationType;
    }

    public String getString(Context context) {
        return StringHelper.getStringByResourceId(context, registrationType);
    }

    public String getCode(){
        return code;
    }

    public String toString(){
        return code;
    }

    public static ChildDeclarationType parseType(String value){
        for (ChildDeclarationType declarationType : ChildDeclarationType.values()) {
            if(declarationType.getCode().equals(value)) {
                return declarationType;
            }
        }

        return null;
    }
}
