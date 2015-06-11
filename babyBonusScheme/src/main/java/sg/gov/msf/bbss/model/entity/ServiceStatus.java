package sg.gov.msf.bbss.model.entity;

import java.util.Date;

import sg.gov.msf.bbss.R;
import sg.gov.msf.bbss.apputils.annotation.DisplayNameId;
import sg.gov.msf.bbss.logic.type.ServiceAppStatusType;
import sg.gov.msf.bbss.logic.type.ServiceAppType;

/**
 * Created by chuanhe
 */
public class ServiceStatus {

    public static final String FIELD_SERVICE_APP_ID = "appId";
    public static final String FIELD_SERVICE_APP_TYPE = "appType";
    public static final String FIELD_SERVICE_APP_STATUS = "appStatusType";
    public static final String FIELD_SERVICE_APP_DATE = "appDate";

    @DisplayNameId(R.string.label_service_status_id)
    private String appId;

    @DisplayNameId(R.string.label_service_status_type)
    private ServiceAppType appType;

    @DisplayNameId(R.string.label_service_status_state)
    private ServiceAppStatusType appStatusType;

    @DisplayNameId(R.string.label_service_status_date)
    private Date appDate;


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public ServiceAppType getAppType() {
        return appType;
    }

    public void setAppType(ServiceAppType appType) {
        this.appType = appType;
    }

    public ServiceAppStatusType getAppStatusType() {
        return appStatusType;
    }

    public void setAppStatusType(ServiceAppStatusType appStatusType) {
        this.appStatusType = appStatusType;
    }

    public Date getAppDate() {
        return appDate;
    }

    public void setAppDate(Date appDate) {
        this.appDate = appDate;
    }
}
