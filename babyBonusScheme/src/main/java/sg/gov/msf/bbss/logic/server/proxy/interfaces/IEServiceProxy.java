package sg.gov.msf.bbss.logic.server.proxy.interfaces;

import android.app.Service;

import sg.gov.msf.bbss.logic.server.response.ServerResponse;
import sg.gov.msf.bbss.logic.type.ServiceAppType;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceCdabTc;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceChangeBo;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceChangeCdab;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceChangeCdat;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceChangeNah;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceChangeNan;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceOpenCda;
import sg.gov.msf.bbss.model.wizardbase.eservice.ServiceTransferToPsea;

/**
 * Created by bandaray
 */
public interface IEServiceProxy {
    ChildItem[] getChildItemList(ServiceAppType serviceType);

    ServerResponse updateNominatedAccountHolder(ServiceChangeNah changeNah);

    ServerResponse updateNominatedAccountNumber(ServiceChangeNan changeNan);

    ServerResponse updateChildDevAccountTrustee(ServiceChangeCdat changeCdat);

    ServerResponse updateChildDevAccountBank(ServiceChangeCdab changeCdab);

    ServerResponse updateTransferCdaToPsea(ServiceTransferToPsea transferToPsea);

    ServerResponse updateBirthOrder(ServiceChangeBo changeBo);

    ServerResponse updateCdaBankTermsAndCond(ServiceCdabTc cdaBankTc);

    ServerResponse updateOpenCDA(ServiceOpenCda openCda);
}
