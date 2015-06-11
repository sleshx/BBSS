package sg.gov.msf.bbss.logic.type;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.util.StringHelper;

/**
 * Created by bandaray
 */
public enum ServiceAppType {

    CHANGE_NAH("PP_ES_CHG_NAH",
            "NHA", R.string.label_service_status_change_nah,
            R.string.label_service_change_nah, R.drawable.ic_service_change_nah),
    CHANGE_NAN("PP_ES_CHG_NAN",
            "NAN", R.string.label_service_status_change_nan,
            R.string.label_service_change_nan, R.drawable.ic_service_change_nan),
    CHANGE_CDAT("PP_ES_CDA",
            "CDAT", R.string.label_service_status_change_cdat,
            R.string.label_service_change_cdat, R.drawable.ic_service_change_cdat),
    CHANGE_CDAB("PP_ES_CDA_BANK",
            "MA", R.string.label_service_status_change_cdab,
            R.string.label_service_change_cdab, R.drawable.ic_service_change_cdab),
    CDA_TO_PSEA("PP_ES_TR_CDA_PSEA",
            "PSEA", R.string.label_service_status_cda_psea,
            R.string.label_service_cda_psea, R.drawable.ic_service_cda_psea),
    CHANGE_BO("PP_ES_CHG_BO",
            "BO", R.string.label_service_status_change_bo,
            R.string.label_service_change_bo, R.drawable.ic_service_change_bo),
    TERMS_AND_COND("PP_ES_ACCP_TC",
            "CDABTC", R.string.label_service_status_cdab_tc,
            R.string.label_service_cdab_tc, R.drawable.ic_service_cdab_tc),
    OPEN_CDA("PP_ES_OPEN_CDA",
            "OPENCDA", R.string.label_service_status_open_cda,
            R.string.label_service_open_cda, R.drawable.ic_service_open_cda);

    private String code;
    private String codeDev;
    private int statusResourceId;
    private int titleResourceId;
    private int imageResourceId;

    ServiceAppType(String code, String codeDev, int statusResourceId,
                   int titleResourceId, int imageResourceId) {
        this.code = code;
        this.codeDev = codeDev;
        this.statusResourceId = statusResourceId;
        this.titleResourceId = titleResourceId;
        this.imageResourceId = imageResourceId;
    }

    //----------------------------------------------------------------------------------------------

    public String getCodeDev(){
        return codeDev;
    }

    public String getCode(){
        return code;
    }

    public String getStatusLabel(Context context) {
        return StringHelper.getStringByResourceId(context, statusResourceId);
    }
    public String getStatusTitle(Context context) {
        return StringHelper.getStringByResourceId(context, titleResourceId);
    }

    public Bitmap getImageBitmap(Context context) {
        return BitmapFactory.decodeResource(context.getResources(), imageResourceId);
    }

    //----------------------------------------------------------------------------------------------

    public static ServiceAppType parseType(String value){
        for (ServiceAppType serviceAppType : ServiceAppType.values()) {
            if(serviceAppType.getCode().equals(value)) {
                return serviceAppType;
            }
        }
        return null;
    }

    public static ServiceAppType parseTypeDev(String value){
        for (ServiceAppType serviceAppType : ServiceAppType.values()) {
            if(serviceAppType.getCodeDev().equals(value)) {
                return serviceAppType;
            }
        }
        return null;
    }
}
