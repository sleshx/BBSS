package sg.gov.msf.bbss.model.wizardbase.eservice;

import sg.gov.msf.bbss.apputils.wizard.WizardBase;
import sg.gov.msf.bbss.model.entity.ServiceStatus;

/**
 * Created by chuanhe
 */
public class ServiceAppStatus extends WizardBase {
    private ServiceStatus[] serviceStatusList;

    public ServiceStatus[] getServiceStatusList() {
        return serviceStatusList;
    }

    public void setServiceStatusList(ServiceStatus[] serviceStatusList) {
        this.serviceStatusList = serviceStatusList;
    }
}