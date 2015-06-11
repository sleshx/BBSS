package sg.gov.msf.bbss.logic.server.proxy.dev;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.connect.HttpJsonCaller;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.server.login.LoginManager;
import sg.gov.msf.bbss.logic.server.proxy.interfaces.IMasterDataProxy;
import sg.gov.msf.bbss.logic.type.MasterDataType;
import sg.gov.msf.bbss.model.entity.common.Address;
import sg.gov.msf.bbss.model.entity.common.BankAccount;
import sg.gov.msf.bbss.model.entity.masterdata.AccessibleService;
import sg.gov.msf.bbss.model.entity.masterdata.Bank;
import sg.gov.msf.bbss.model.entity.masterdata.GenericDataItem;

/**
 * Created by bandaray
 */
public class MasterDataProxy implements IMasterDataProxy {

    @Override
    public GenericDataItem[] getGenericDataItems(MasterDataType type) {
        HttpJsonCaller httpJsonCaller = new HttpJsonCaller(LoginManager.getSessionContainer().getSessionToken());
        ArrayList<GenericDataItem> listItems = new ArrayList<GenericDataItem>();

        if (type == MasterDataType.OCCUPATION) {
            type = MasterDataType.DEV_OCCUPATION;
        } else if (type == MasterDataType.CDA_BANK_CHANGE_REASON) {
            type = MasterDataType.DEV_CDA_BANK_CHANGE_REASON;
        }else if (type == MasterDataType.TRUSTEE_BANK_CHANGE_REASON) {
            type = MasterDataType.DEV_TRUSTEE_BANK_CHANGE_REASON;
        }

        try {
            String maserDataUrl = String.format(AppUrls.MASTER_DATA_URL, type.getQueryStringPart());

            String jsonString = httpJsonCaller.get(maserDataUrl);

            //TODO: remove after singpass ok
            if(type == MasterDataType.USER_ROLE) {
                //jsonString = "{\"userRole\":[{\"roleId\":\"DEF-role-viewServicesStatus\",\"roleName\":\"Role-Trustee of viewServicesStatus\"},{\"roleId\":\"DEF-role-userProfile\",\"roleName\":\"Role-Trustee of userProfile\"},{\"roleId\":\"DEF-role-familyView\",\"roleName\":\"Role-Trustee of familyView\"}]}";
                jsonString = "{\"userRole\":[{\"roleId\":\"DEF-role-userProfile\",\"roleName\":\"Role-Trustee of userProfile\"},{\"roleId\":\"DEF-role-familyView\",\"roleName\":\"Role-Trustee of familyView\"}]}";
            }

            JSONArray jsonArray;
            if(type == MasterDataType.USER_ROLE) {
                jsonArray = new JSONObject(jsonString).getJSONArray("userRole");
            } else {
                jsonArray = new JSONObject(jsonString)
                        .getJSONObject(SerializedNames.SEC_COMMON_MASTER_DATA_ROOT)
                        .getJSONArray(type.getJsonRootPropertyName());
            }

            int arrayLength  = jsonArray.length();
            JSONObject jsonItem;
            GenericDataItem listItem;

            for (int index = 0; index< arrayLength; index ++) {
                jsonItem = jsonArray.getJSONObject(index);

                listItem = new GenericDataItem();
                listItem.setId(jsonItem.getString(type.getJsonIdPropertyName()));
                listItem.setName(jsonItem.getString(type.getJsonNamePropertyName()));
                listItems.add(listItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listItems.toArray(new GenericDataItem[0]);
    }

    @Override
    public Bank[] getBanks(MasterDataType type){
        HttpJsonCaller httpJsonCaller = new HttpJsonCaller(LoginManager.getSessionContainer().getSessionToken());
        ArrayList<Bank> banks = new ArrayList<Bank>();

        try {
            String maserDataUrl = String.format(AppUrls.MASTER_DATA_URL, type.getQueryStringPart());
            String jsonString = httpJsonCaller.get(maserDataUrl);
            JSONArray jsonArray = new JSONObject(jsonString)
                                        .getJSONObject(SerializedNames.SEC_COMMON_MASTER_DATA_ROOT)
                                        .getJSONArray(type.getJsonRootPropertyName());
            int arrayLength  = jsonArray.length();
            JSONObject jsonItem;
            Bank bank;

            for (int index = 0; index< arrayLength; index ++) {
                jsonItem = jsonArray.getJSONObject(index);

                bank = new Bank();
                bank.setId(jsonItem.getString(SerializedNames.SN_BANK_ID));
                bank.setName(jsonItem.getString(SerializedNames.SN_BANK_NAME));
                bank.setTermsAndConditionsUrl(jsonItem.getString(SerializedNames.SN_BANK_TC_URL));
                banks.add(bank);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return banks.toArray(new Bank[0]);
    }

    @Override
    public BankAccount getBankAccountBranch(Bank bank, String accountNo){
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBank(bank);
        bankAccount.setBankBranch("some branch name");
        bankAccount.setBankBranchId("123");
        bankAccount.setBankAccountNo(accountNo);
        return bankAccount;
    }

    @Override
    public Address getLocalAddress(int postalCode){
        HttpJsonCaller httpJsonCaller = new HttpJsonCaller(LoginManager.getSessionContainer().getSessionToken());
        MasterDataType type = MasterDataType.LOCAL_ADDRESS;

        try {
            String maserDataUrl = String.format(AppUrls.MASTER_DATA_URL, type.getQueryStringPart());
            String jsonString = httpJsonCaller.get(maserDataUrl);
            JSONObject jsonItem = new JSONObject(jsonString)
                                        .getJSONObject(SerializedNames.SEC_COMMON_MASTER_DATA_ROOT)
                                        .getJSONObject(type.getJsonRootPropertyName());
            Address address = new Address();

            String unitNo = jsonItem.getString(SerializedNames.SN_ADDRESS_UNIT_NO);

            if(!StringHelper.isStringNullOrEmpty(unitNo)){
                String[] splits = unitNo.split(AppConstants.SYMBOL_HYPHEN);
                int splitCount = splits.length;

                if(splitCount > 0){
                    address.setFloorNo(splits[0]);
                }

                if(splitCount > 1){
                    address.setUnitNo(splits[1]);
                }
            }

            address.setPostalCode(jsonItem.getInt(SerializedNames.SN_ADDRESS_POST_CODE));
            address.setStreetName(jsonItem.getString(SerializedNames.SN_ADDRESS_STREET));
            address.setBuildingName(jsonItem.getString(SerializedNames.SN_ADDRESS_BUILDING));
            address.setBlockHouseNo(jsonItem.getString(SerializedNames.SN_ADDRESS_BLOCK_HOUSE_NO));

            return address;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public AccessibleService[] getAccessibleServices(){
        HttpJsonCaller httpJsonCaller = new HttpJsonCaller(LoginManager.getSessionContainer().getSessionToken());
        ArrayList<AccessibleService> accessibleServices = new ArrayList<AccessibleService>();
        MasterDataType type = MasterDataType.ACCESSIBLE_SERVICES;


        try {
            String maserDataUrl = String.format(AppUrls.MASTER_DATA_URL, "accessibleSerivce");
            String jsonString = httpJsonCaller.get(maserDataUrl);

            JSONArray jsonArray = new JSONObject(jsonString)
                    .getJSONObject(SerializedNames.SEC_COMMON_MASTER_DATA_ROOT)
                    .getJSONArray(type.getJsonRootPropertyName());
            int arrayLength  = jsonArray.length();
            JSONObject jsonItem;
            AccessibleService accessibleService;

            for (int index = 0; index< arrayLength; index ++) {
                jsonItem = jsonArray.getJSONObject(index);

                accessibleService = new AccessibleService();
                accessibleService.setIsOutstanding(jsonItem.getInt(SerializedNames.SN_ACCESS_SERVICE_IS_OUTSTANDING)==1);
                accessibleService.setCode(jsonItem.getString(SerializedNames.SN_ACCESS_SERVICE_SERVICE_CODE));
                accessibleServices.add(accessibleService);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return accessibleServices.toArray(new AccessibleService[0]);
    }
}
