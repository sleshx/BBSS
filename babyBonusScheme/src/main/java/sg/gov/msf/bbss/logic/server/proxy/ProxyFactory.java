package sg.gov.msf.bbss.logic.server.proxy;


import sg.gov.msf.bbss.logic.server.proxy.interfaces.IEServiceProxy;
import sg.gov.msf.bbss.logic.server.proxy.interfaces.IEnrolmentProxy;
import sg.gov.msf.bbss.logic.server.proxy.interfaces.IMasterDataProxy;
import sg.gov.msf.bbss.logic.server.proxy.interfaces.IOtherProxy;
import sg.gov.msf.bbss.logic.type.EnvironmentType;


/**
 * Created by bandaray
 */
public class ProxyFactory {
    public static final EnvironmentType ENVIRONMENT = EnvironmentType.DEP;

    public static IEnrolmentProxy getEnrolmentProxy() {
        if (ENVIRONMENT == ENVIRONMENT.DEV)
            return new sg.gov.msf.bbss.logic.server.proxy.dev.EnrolmentProxy();
        else if (ENVIRONMENT == ENVIRONMENT.DEP)
            return new sg.gov.msf.bbss.logic.server.proxy.dep.EnrolmentProxy();
        else
            return null;
    }

    public static IMasterDataProxy getMasterDataProxy() {
        if (ENVIRONMENT == ENVIRONMENT.DEV)
            return new sg.gov.msf.bbss.logic.server.proxy.dev.MasterDataProxy();
        else if (ENVIRONMENT == ENVIRONMENT.DEP)
            return new sg.gov.msf.bbss.logic.server.proxy.dep.MasterDataProxy();
        else
            return null;
    }

    public static IEServiceProxy getEServiceProxy(){
        if (ENVIRONMENT == ENVIRONMENT.DEV)
            return new sg.gov.msf.bbss.logic.server.proxy.dev.EServiceProxy();
        else if (ENVIRONMENT == ENVIRONMENT.DEP)
            return new sg.gov.msf.bbss.logic.server.proxy.dep.EServiceProxy();
        else
            return null;
    }

    public static IOtherProxy getOtherProxy() {
        if (ENVIRONMENT == ENVIRONMENT.DEV)
            return new sg.gov.msf.bbss.logic.server.proxy.dev.OtherProxy();
        else if (ENVIRONMENT == ENVIRONMENT.DEP)
            return new sg.gov.msf.bbss.logic.server.proxy.dep.OtherProxy();
        else
            return null;
    }
}
