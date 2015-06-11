package sg.gov.msf.bbss.logic.server.proxy.interfaces;

import sg.gov.msf.bbss.logic.type.MasterDataType;
import sg.gov.msf.bbss.model.entity.common.Address;
import sg.gov.msf.bbss.model.entity.common.BankAccount;
import sg.gov.msf.bbss.model.entity.masterdata.AccessibleService;
import sg.gov.msf.bbss.model.entity.masterdata.Bank;
import sg.gov.msf.bbss.model.entity.masterdata.GenericDataItem;

/**
 * Created by bandaray on 12/5/2015.
 */
public interface IMasterDataProxy {
    GenericDataItem[] getGenericDataItems(MasterDataType type);

    Bank[] getBanks(MasterDataType type);

    BankAccount getBankAccountBranch(Bank bank, String accountNo);

    Address getLocalAddress(int postalCode);

    AccessibleService[] getAccessibleServices();
}
