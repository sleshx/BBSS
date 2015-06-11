package sg.gov.msf.bbss.model.entity.people;

import org.json.JSONException;
import org.json.JSONObject;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.annotation.DisplayNameId;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.model.entity.masterdata.GenericDataItem;

/**
 * Created by bandaray
 */
public class CdaTrustee extends Adult {

    public static final String FIELD_CHANGE_REASON = "changeReason";
    public static final String FIELD_CHANGE_REASON_OTHER = "changeReasonDescription";

    /*
     * WARNING : If you refactor any of the property names below,
     *           you have to rename the above constant values as well.
     */

    @DisplayNameId(R.string.label_cda_trustee_change_reason)
    private GenericDataItem changeReason;

    @DisplayNameId(R.string.label_cda_trustee_change_reason_other)
    private String changeReasonDescription;

    //----------------------------------------------------------------------------------------------

    public GenericDataItem getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(GenericDataItem changeReason) {
        this.changeReason = changeReason;
    }

    public String getChangeReasonDescription() {
        return changeReasonDescription;
    }

    public void setChangeReasonDescription(String changeReasonDescription) {
        this.changeReasonDescription = changeReasonDescription;
    }

    public static JSONObject serialize(CdaTrustee cdaTrustee, boolean isAddressRequired) throws JSONException {
        return Adult.serialize(cdaTrustee, isAddressRequired).put(SerializedNames.SN_ADULT_RELATIONSHIP, cdaTrustee.getRelationshipType().getCode());
    }
}
