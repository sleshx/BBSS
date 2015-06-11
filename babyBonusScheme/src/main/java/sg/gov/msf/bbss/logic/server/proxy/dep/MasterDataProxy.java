package sg.gov.msf.bbss.logic.server.proxy.dep;

import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.connect.HttpJsonCaller;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.logic.server.login.LoginManager;
import sg.gov.msf.bbss.logic.server.SerializedNames;
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
        String entityType = type.getQueryStringPart();
        String entityTypeResponse = type.getJsonRootPropertyName();

        String user = LoginManager.getSessionContainer().getNric();
        String url = AppUrls.MASTER_DATA_URL;

        HttpJsonCaller httpJsonCaller = new HttpJsonCaller(LoginManager.getSessionContainer().getSessionToken());
        ArrayList<GenericDataItem> listItems = new ArrayList<GenericDataItem>();

        try {
            JSONObject jsonFinal = new JSONObject();
            jsonFinal.put(SerializedNames.SN_USER_ID, user);
            jsonFinal.put(SerializedNames.SN_MASTER_DATA_TYPE, entityType);
            jsonFinal.put(SerializedNames.SN_MASTER_DATA_FILTER, new JSONArray());

            String jsonString = httpJsonCaller.post(url, jsonFinal.toString());
            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray(entityTypeResponse);

            int arrayLength  = jsonArray.length();
            JSONObject jsonItem;
            GenericDataItem listItem;

            for (int index = 0; index < arrayLength; index ++) {
                jsonItem = jsonArray.getJSONObject(index);

                listItem = new GenericDataItem();
                listItem.setId(jsonItem.getString(type.getJsonIdPropertyName()));
                listItem.setName(jsonItem.optString(type.getJsonNamePropertyName()));
                listItems.add(listItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listItems.toArray(new GenericDataItem[0]);
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public Bank[] getBanks(MasterDataType type){
        String entityType = type.getQueryStringPart();
        String entityTypeResponse = type.getJsonRootPropertyName();

        String user = LoginManager.getSessionContainer().getNric();
        String url = String.format(AppUrls.MASTER_DATA_URL, type.getQueryStringPart());

        HttpJsonCaller httpJsonCaller = new HttpJsonCaller(LoginManager.getSessionContainer().getSessionToken());
        ArrayList<Bank> banks = new ArrayList<Bank>();

        try {
            JSONObject jsonFinal = new JSONObject();
            jsonFinal.put(SerializedNames.SN_USER_ID, user);
            jsonFinal.put(SerializedNames.SN_MASTER_DATA_TYPE, entityType);
            jsonFinal.put(SerializedNames.SN_MASTER_DATA_FILTER, new JSONArray());

            String jsonString = httpJsonCaller.post(url, jsonFinal.toString());
            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray(entityTypeResponse);

            int arrayLength  = jsonArray.length();
            JSONObject jsonItem;
            Bank bank;

            for (int index = 0; index< arrayLength; index ++) {
                jsonItem = jsonArray.getJSONObject(index);

                bank = new Bank();
                bank.setId(jsonItem.getString(SerializedNames.SN_BANK_ID));
                bank.setName(jsonItem.getString(SerializedNames.SN_BANK_NAME));
                bank.setTermsAndConditionsUrl(jsonItem.optString(SerializedNames.SN_BANK_TERMS_COND_URL));
                banks.add(bank);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return banks.toArray(new Bank[0]);
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public BankAccount getBankAccountBranch(Bank bank, String accountNo){
        String entityType = MasterDataType.BANK_BRANCH.getQueryStringPart();
        String entityTypeResponse = MasterDataType.BANK_BRANCH.getJsonRootPropertyName();

        String user = LoginManager.getSessionContainer().getNric();
        String url = String.format(AppUrls.MASTER_DATA_URL, entityType);

        HttpJsonCaller httpJsonCaller = new HttpJsonCaller(LoginManager.getSessionContainer().getSessionToken());
        BankAccount account = new BankAccount();

        try {
            JSONObject jsonFilterByBank = new JSONObject();
            jsonFilterByBank.put(SerializedNames.SN_COMMON_FILTER_KEY, SerializedNames.SN_BANK_ID);
            jsonFilterByBank.put(SerializedNames.SN_COMMON_FILTER_VALUE, bank.getId());

            JSONObject jsonFilterByBankAcc = new JSONObject();
            jsonFilterByBankAcc.put(SerializedNames.SN_COMMON_FILTER_KEY, SerializedNames.SN_BANK_ACCOUNT_NO);
            jsonFilterByBankAcc.put(SerializedNames.SN_COMMON_FILTER_VALUE, accountNo);

            JSONArray jsonFilters = new JSONArray();
            jsonFilters.put(jsonFilterByBank);
            jsonFilters.put(jsonFilterByBankAcc);

            JSONObject jsonFinal = new JSONObject();
            jsonFinal.put(SerializedNames.SN_USER_ID, user);
            jsonFinal.put(SerializedNames.SN_MASTER_DATA_TYPE, entityType);
            jsonFinal.put(SerializedNames.SN_MASTER_DATA_FILTER, jsonFilters);

            String jsonString = httpJsonCaller.post(url, jsonFinal.toString());
            JSONArray jsonArray = new JSONObject(jsonString).optJSONArray(entityTypeResponse);

            if (jsonArray != null && jsonArray.length() > 0) {
                JSONObject jsonItem = jsonArray.getJSONObject(0);

                account.setBank(bank);
                account.setBankBranchId(jsonItem.getString(SerializedNames.SN_BRANCH_ID));
                account.setBankBranch(jsonItem.getString(SerializedNames.SN_BANK_BRANCH_NAME));
                account.setBankAccountNo(accountNo);
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

        return account;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public Address getLocalAddress(int postalCode){
        String entityType = MasterDataType.LOCAL_ADDRESS.getQueryStringPart();
        String entityTypeResponse = MasterDataType.LOCAL_ADDRESS.getJsonRootPropertyName();

        String user = LoginManager.getSessionContainer().getNric();
        String url = AppUrls.MASTER_DATA_URL;

        HttpJsonCaller httpJsonCaller = new HttpJsonCaller(LoginManager.getSessionContainer().getSessionToken());
        Address address = new Address();

        try {
            JSONObject jsonFilterByPostalCode = new JSONObject();
            jsonFilterByPostalCode.put(SerializedNames.SN_COMMON_FILTER_KEY, SerializedNames.SN_ADDRESS_POSTAL_CODE);
            jsonFilterByPostalCode.put(SerializedNames.SN_COMMON_FILTER_VALUE, postalCode);

            JSONArray jsonFilters = new JSONArray();
            jsonFilters.put(jsonFilterByPostalCode);

            JSONObject jsonFinal = new JSONObject();
            jsonFinal.put(SerializedNames.SN_USER_ID, user);
            jsonFinal.put(SerializedNames.SN_MASTER_DATA_TYPE, entityType);
            jsonFinal.put(SerializedNames.SN_MASTER_DATA_FILTER, jsonFilters);

            String jsonString = httpJsonCaller.post(url, jsonFinal.toString());
            JSONArray jsonArray = new JSONObject(jsonString).optJSONArray(entityTypeResponse);

            if (jsonArray != null && jsonArray.length() > 0) {
                JSONObject jsonItem = jsonArray.getJSONObject(0);

                String unitNo = jsonItem.optString(SerializedNames.SN_ADDRESS_UNIT_NO);

                if (!StringHelper.isStringNullOrEmpty(unitNo)) {
                    String[] splits = unitNo.split(AppConstants.SYMBOL_HYPHEN);
                    int splitCount = splits.length;

                    if (splitCount > 0) {
                        address.setFloorNo(splits[0]);
                    }

                    if (splitCount > 1) {
                        address.setUnitNo(splits[1]);
                    }
                }

                address.setPostalCode(jsonItem.optInt(SerializedNames.SN_ADDRESS_POST_CODE));
                address.setStreetName(jsonItem.optString(SerializedNames.SN_ADDRESS_STREET));
                address.setBuildingName(jsonItem.optString(SerializedNames.SN_ADDRESS_BUILDING));
                address.setBlockHouseNo(jsonItem.optString(SerializedNames.SN_ADDRESS_BLOCK_HOUSE_NO));

            } else {
                address = null;
            }

            return address;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public AccessibleService[] getAccessibleServices(){
        String entityType = MasterDataType.ACCESSIBLE_SERVICES.getQueryStringPart();
        String entityTypeResponse = MasterDataType.ACCESSIBLE_SERVICES.getJsonRootPropertyName();

        String user = LoginManager.getSessionContainer().getNric();
        String url = AppUrls.MASTER_DATA_URL;

        HttpJsonCaller httpJsonCaller = new HttpJsonCaller(LoginManager.getSessionContainer().getSessionToken());
        ArrayList<AccessibleService> accessibleServices = new ArrayList<AccessibleService>();

        try {
            JSONObject jsonFinal = new JSONObject();
            jsonFinal.put(SerializedNames.SN_USER_ID, user);
            jsonFinal.put(SerializedNames.SN_MASTER_DATA_TYPE, entityType);
            jsonFinal.put(SerializedNames.SN_MASTER_DATA_FILTER, new JSONArray());

            String jsonString = httpJsonCaller.post(url, jsonFinal.toString());
            JSONArray jsonArray = new JSONObject(jsonString).getJSONArray(entityTypeResponse);

            int arrayLength  = jsonArray.length();
            JSONObject jsonItem;
            AccessibleService accessibleService;

            for (int index = 0; index < arrayLength; index ++) {
                jsonItem = jsonArray.getJSONObject(index);

                accessibleService = new AccessibleService();
                accessibleService.setIsOutstanding(jsonItem.getInt(SerializedNames.SN_ACCESS_SERVICE_IS_OUTSTANDING) == 1);
                accessibleService.setCode(jsonItem.getString(SerializedNames.SN_ACCESS_SERVICE_SERVICE_CODE));
                accessibleServices.add(accessibleService);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return accessibleServices.toArray(new AccessibleService[accessibleServices.size()]);
    }
}
