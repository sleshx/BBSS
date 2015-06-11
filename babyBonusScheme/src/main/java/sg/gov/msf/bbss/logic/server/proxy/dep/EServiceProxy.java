package sg.gov.msf.bbss.logic.server.proxy.dep;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.server.login.LoginManager;
import sg.gov.msf.bbss.logic.server.proxy.BaseProxy;
import sg.gov.msf.bbss.logic.server.proxy.interfaces.IEServiceProxy;
import sg.gov.msf.bbss.logic.server.response.ServerResponse;
import sg.gov.msf.bbss.logic.server.response.ServerResponseType;
import sg.gov.msf.bbss.logic.type.RelationshipType;
import sg.gov.msf.bbss.logic.type.ServiceAppType;
import sg.gov.msf.bbss.model.entity.childdata.ChildItem;
import sg.gov.msf.bbss.model.entity.common.Address;
import sg.gov.msf.bbss.model.entity.common.BankAccount;
import sg.gov.msf.bbss.model.entity.common.CdaBankAccount;
import sg.gov.msf.bbss.model.entity.masterdata.Bank;
import sg.gov.msf.bbss.model.entity.masterdata.GenericDataItem;
import sg.gov.msf.bbss.model.entity.people.Adult;
import sg.gov.msf.bbss.model.entity.people.CdaTrustee;
import sg.gov.msf.bbss.model.entity.people.Child;
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
 * Modified to add more methods by chuanhe
 * Fixed bugs and did code refactoring by bandaray
 */
public class EServiceProxy extends BaseProxy implements IEServiceProxy {

    @Override
    public ChildItem[] getChildItemList(ServiceAppType serviceType) {
        String user = LoginManager.getSessionContainer().getNric();
        String url = String.format(AppUrls.SERVICES_CHILD_ITEM_LIST_URL,user, serviceType.getCode());

        ArrayList<ChildItem> childItems = new ArrayList<ChildItem>();

        try {
            String jsonString  = httpJsonCaller.get(url);
            JSONArray jsonArray = new JSONArray(jsonString);

            JSONObject jsonChild;
            JSONObject jsonNaHolder;
            JSONObject jsonCdaTrustee;
            JSONObject jsonBankAccount;

            int arrayLength  = jsonArray.length();
            ChildItem childItem;

            Child child;
            Adult naHolder;
            Adult cdaTrustee;
            Bank bank;
            BankAccount bankAccount;

            for (int index = 0; index < arrayLength; index ++){
                childItem = new ChildItem();
                jsonChild = jsonArray.getJSONObject(index);
                jsonNaHolder = jsonChild.getJSONObject(SerializedNames.SEC_CHILD_LIST_NAH);
                jsonCdaTrustee = jsonChild.getJSONObject(SerializedNames.SEC_CHILD_LIST_CDA_TRUSTEE);
                jsonBankAccount = jsonChild.getJSONObject(SerializedNames.SEC_CHILD_LIST_BANK_ACCOUNT);

                child = new Child();
                child.setId(jsonChild.getString(SerializedNames.SN_PERSON_ID));
                child.setName(jsonChild.getString(SerializedNames.SN_PERSON_NAME));
                child.setBirthOrder(jsonChild.optInt(SerializedNames.SN_CHILD_BIRTH_ORDER_NO));

                naHolder = new Adult();
                naHolder.setId(jsonNaHolder.getString(SerializedNames.SN_PERSON_ID));
                naHolder.setName(jsonNaHolder.getString(SerializedNames.SN_PERSON_NAME));
                naHolder.setNric(jsonNaHolder.getString(SerializedNames.SN_PERSON_NRIC));
                naHolder.setDateOfBirth(StringHelper.parseDate(jsonNaHolder.getString(SerializedNames.SN_PERSON_BIRTHDAY)));
                naHolder.setRelationshipType(RelationshipType.parseType(jsonNaHolder.getString(SerializedNames.SN_ADULT_RELATIONSHIP)));

                cdaTrustee = new Adult();
                cdaTrustee.setId(jsonCdaTrustee.getString(SerializedNames.SN_PERSON_ID));
                cdaTrustee.setName(jsonCdaTrustee.getString(SerializedNames.SN_PERSON_NAME));
                cdaTrustee.setNric(jsonCdaTrustee.getString(SerializedNames.SN_PERSON_NRIC));
                cdaTrustee.setDateOfBirth(StringHelper.parseDate(jsonCdaTrustee.getString(SerializedNames.SN_PERSON_BIRTHDAY)));
                cdaTrustee.setRelationshipType(RelationshipType.parseType(jsonNaHolder.getString(SerializedNames.SN_ADULT_RELATIONSHIP)));

                bankAccount = new BankAccount();
                bankAccount.setBankAccountNo(jsonBankAccount.getString(SerializedNames.SN_BANK_ACC_NO));

                bank = new Bank();
                bank.setId(jsonBankAccount.getString(SerializedNames.SN_BANK_ID));
                bank.setName(jsonBankAccount.getString(SerializedNames.SN_BANK_NAME));
                bank.setTermsAndConditionsUrl(jsonBankAccount.optString(SerializedNames.SN_BANK_TNC_URL));
                bankAccount.setBank(bank);

                childItem.setChild(child);
                childItem.setNominatedAccountHolder(naHolder);
                childItem.setChildDevAccTrustee(cdaTrustee);
                childItem.setBankAccount(bankAccount);
                childItems.add(childItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return childItems.toArray(new ChildItem[childItems.size()]);
    }

    //----------- NAH ------------------------------------------------------------------------------

    @Override
    public ServerResponse updateNominatedAccountHolder(ServiceChangeNah changeNah) {
        //--Change nah - Success
        //testOutputJsonString = "{\"status\":{\"code\":\"200\",\"message\":\"Nominated Account Holder is updated successfully\"},\"data\":{\"childsNAHolderDetail\":{\"status\":\"\",\"message\":\"\",\"serviceAppId\":\"APP_123\"}}}";
        //--Change Nah -Error
        //testOutputJsonString = "{\"status\":{\"code\":\"900005\",\"message\":\"Invalid input\"},\"data\":{\"childsNAHolderDetail\":{\"childId\":\"\",\"naHolder\":{\"nric\":\"Invalid NRIC input\",\"email\":\"Invalid email\"},\"cgAuthorizer\":{\"bankAccNo\":\"Invalid Bank Account No\"}}}}";

        String user = LoginManager.getSessionContainer().getNric();
        String url = AppUrls.SERVICES_UPDATE_URL_NAH;

        ServerResponse serverResponse = new ServerResponse();

        try {
            JSONObject root = new JSONObject();
            JSONArray childIds = new JSONArray();

            for (ChildItem childItem : changeNah.getChildItems()) {
                childIds.put(childItem.getChild().getId());
            }

            Adult naHolder = changeNah.getNominatedAccHolder();
            JSONObject jsonNah = Adult.serialize(naHolder, true);
            JSONObject jsonCgAuthorize = BankAccount.serialize(changeNah.getNewBankAccount());

            jsonNah.put(SerializedNames.SN_ADULT_RELATIONSHIP, naHolder.getRelationshipType().getCode());

            root.put(SerializedNames.SEC_SERVICE_COMMON_CHILD_IDS, childIds);
            root.put(SerializedNames.SEC_SERVICE_CHANGE_NAH_PARTICULARS, jsonNah);
            root.put(SerializedNames.SEC_SERVICE_CHANGE_NAH_CG_AUTHORIZER, jsonCgAuthorize);
            root.put(SerializedNames.SEC_SERVICE_CHANGE_NAH_IS_DECLARED, changeNah.isDeclared());

            JSONObject jsonFinal = new JSONObject();
            jsonFinal.put(SerializedNames.SEC_SERVICE_CHANGE_NAH_ROOT, root);

            serverResponse = post(url, jsonFinal);

            JSONObject jsonChildNaHolderDetail = serverResponse.getJsonDataResponse().getJSONObject(SerializedNames.SEC_SERVICE_CHANGE_NAH_ROOT);

            if(jsonChildNaHolderDetail != null) {
                if (serverResponse.getResponseType() == ServerResponseType.SUCCESS) {
                    serverResponse.setAppId(jsonChildNaHolderDetail.getString(SerializedNames.SEC_RESPONSE_SERVICE_APP_ID));
                } else if (serverResponse.getResponseType() == ServerResponseType.SERVICE_ERROR) {
                    addValidationInfo(SerializedNames.SEC_SERVICE_CHANGE_NAH_PARTICULARS, Adult.SERIAL_NAMES, jsonChildNaHolderDetail, changeNah, AppConstants.EMPTY_STRING);
                    addValidationInfo(SerializedNames.SEC_ADDRESS, Address.SERIAL_NAMES, getJSONObject(jsonChildNaHolderDetail, SerializedNames.SEC_SERVICE_CHANGE_NAH_PARTICULARS, SerializedNames.SEC_ADDRESS), changeNah, AppConstants.EMPTY_STRING);
                    addValidationInfo(SerializedNames.SEC_SERVICE_CHANGE_NAH_CG_AUTHORIZER, BankAccount.SERIAL_NAMES, jsonChildNaHolderDetail, changeNah, AppConstants.EMPTY_STRING);
                }
            }
        } catch (Exception e) {
            serverResponse.setResponseType(ServerResponseType.APPLICATION_ERROR);
            serverResponse.setMessage(e.getMessage());
        }

        return serverResponse;
    }

    //----------- NAN ------------------------------------------------------------------------------

    @Override
    public ServerResponse updateNominatedAccountNumber(ServiceChangeNan changeNan){
        //--Change nan - Success
        //testOutputJsonString = "{\"status\":{\"code\":\"200\",\"message\":\"Nominated Account Number updated successfully\"},\"data\":{\"childsNAHolderDetail\":{\"status\":\"\",\"message\":\"\",\"serviceAppId\":\"APP_456\"}}}";
        //--Change cdat - Error
        //testOutputJsonString = "{\"status\":{\"code\":\"900005\",\"message\":\"Invalid input\"},\"data\":{\"childsNAHolderDetail\":{\"cgAuthorizer\":{\"bankId\":\"Invalid Bank\",\"branchId\":\"Invalid Bank Branch\",\"bankAccNo\":\"Invalid Bank Account No\"}}}}";

        String user = LoginManager.getSessionContainer().getNric();
        String url = AppUrls.SERVICES_UPDATE_URL_NAN;

        ServerResponse serverResponse = new ServerResponse();

        try {
            JSONObject root = new JSONObject();
            JSONArray childIds = new JSONArray();

            for (ChildItem childItem : changeNan.getChildItems()) {
                childIds.put(childItem.getChild().getId());
            }

            JSONObject jsonCgAuthorize = BankAccount.serialize(changeNan.getNewBankAccount());

            root.put(SerializedNames.SEC_SERVICE_COMMON_CHILD_IDS, childIds);
            root.put(SerializedNames.SEC_SERVICE_CHANGE_NAN_CG_AUTHORIZER, jsonCgAuthorize);
            root.put(SerializedNames.SEC_SERVICE_CHANGE_NAN_IS_DECLARED, changeNan.isDeclared());

            JSONObject jsonFinal = new JSONObject();
            jsonFinal.put(SerializedNames.SEC_SERVICE_CHANGE_NAN_ROOT_DEV, root);
            serverResponse = post(url, jsonFinal);
            JSONObject jsonUpdateNaNumber = serverResponse.getJsonDataResponse().getJSONObject(SerializedNames.SEC_SERVICE_CHANGE_NAN_ROOT_DEV);

            if(jsonUpdateNaNumber != null){
                if (serverResponse.getResponseType() == ServerResponseType.SUCCESS) {
                    serverResponse.setAppId(jsonUpdateNaNumber.getString(SerializedNames.SEC_RESPONSE_SERVICE_APP_ID));
                }else if (serverResponse.getResponseType() == ServerResponseType.SERVICE_ERROR) {
                    addValidationInfo(SerializedNames.SEC_SERVICE_CHANGE_NAN_CG_AUTHORIZER, BankAccount.SERIAL_NAMES, jsonUpdateNaNumber, changeNan, AppConstants.EMPTY_STRING);
                }
            }

        } catch (Exception e) {
            serverResponse.setResponseType(ServerResponseType.APPLICATION_ERROR);
            serverResponse.setMessage(e.getMessage());
        }

        return serverResponse;
    }

    //----------- CDAT ------------------------------------------------------------------------------

    @Override
    public ServerResponse updateChildDevAccountTrustee(ServiceChangeCdat changeCdaTrustee) {
        //--Change cdat - Success
        //testOutputJsonString = "{\"status\":{\"code\":\"200\",\"message\":\"Child Development Account is updated successfully\"},\"data\":{\"childsCDATrusteeDetail\":{\"status\":\"\",\"message\":\"\",\"serviceAppId\":\"APP_789\"}}}";
        //--Change cdat - Error
        //testOutputJsonString = "{\"status\":{\"code\":\"900005\",\"message\":\"Invalid input\"},\"data\":{\"childsCDATrusteeDetail\":{\"cdaTrustee\":{\"nric\":\"Invalid NRIC input\",\"email\":\"Invalid email\",\"dob\":\"Invalid date format\"}}}}";

        String user = LoginManager.getSessionContainer().getNric();
        String url = AppUrls.SERVICES_UPDATE_URL_CDAT;

        ServerResponse serverResponse = new ServerResponse();

        try {
            JSONObject root = new JSONObject();
            JSONArray childIds = new JSONArray();

            for (ChildItem childItem : changeCdaTrustee.getChildItems()) {
                childIds.put(childItem.getChild().getId());
            }

            CdaTrustee cdaTrustee = changeCdaTrustee.getCdaTrustee();
            GenericDataItem genericDataItem = cdaTrustee.getChangeReason();
            JSONObject jsonCdaTrustee = Adult.serialize(changeCdaTrustee.getCdaTrustee(), true);

            jsonCdaTrustee.put(SerializedNames.SN_ADULT_RELATIONSHIP, cdaTrustee.getRelationshipType().getCode());

            if (genericDataItem != null) {
                root.put(SerializedNames.SEC_SERVICE_CHANGE_CDAT_CHANGE_REASON, genericDataItem.getId());
                root.put(SerializedNames.SEC_SERVICE_CHANGE_CDAT_REASON_OTHER, genericDataItem.getName());
            }

            root.put(SerializedNames.SEC_SERVICE_COMMON_CHILD_IDS, childIds);
            root.put(SerializedNames.SEC_SERVICE_CHANGE_CDAT_PARTICULARS, jsonCdaTrustee);
            root.put(SerializedNames.SEC_SERVICE_CHANGE_CDAT_IS_DECLARED_1, changeCdaTrustee.isDeclared1());
            root.put(SerializedNames.SEC_SERVICE_CHANGE_CDAT_IS_DECLARED_2, changeCdaTrustee.isDeclared2());

            JSONObject jsonFinal = new JSONObject();
            jsonFinal.put(SerializedNames.SEC_SERVICE_CHANGE_CDAT_ROOT, root);
            serverResponse = post(url, jsonFinal);
            JSONObject jsonUpdateCdaTrustee = serverResponse.getJsonDataResponse().getJSONObject(SerializedNames.SEC_SERVICE_CHANGE_CDAT_ROOT);

            if(jsonUpdateCdaTrustee != null){
                if (serverResponse.getResponseType() == ServerResponseType.SUCCESS) {
                    serverResponse.setAppId(jsonUpdateCdaTrustee.getString(SerializedNames.SEC_RESPONSE_SERVICE_APP_ID));
                }else if (serverResponse.getResponseType() == ServerResponseType.SERVICE_ERROR) {
                    addValidationInfo(SerializedNames.SEC_SERVICE_CHANGE_CDAT_PARTICULARS, Adult.SERIAL_NAMES, jsonUpdateCdaTrustee, changeCdaTrustee, AppConstants.EMPTY_STRING);
                    addValidationInfo(SerializedNames.SEC_ADDRESS, Address.SERIAL_NAMES, getJSONObject(jsonUpdateCdaTrustee, SerializedNames.SEC_SERVICE_CHANGE_CDAT_PARTICULARS, SerializedNames.SEC_ADDRESS), changeCdaTrustee, AppConstants.EMPTY_STRING);
                }
            }
        } catch (Exception e) {
            serverResponse.setResponseType(ServerResponseType.APPLICATION_ERROR);
            serverResponse.setMessage(e.getMessage());
        }

        return serverResponse;
    }

    //-------------------------- CDAB --------------------------------------------------------------

    @Override
    public ServerResponse updateChildDevAccountBank(ServiceChangeCdab changeCdaBank) {
        //--Change cdab - Success
        //testOutputJsonString = "{\"status\":{\"code\":\"200\",\"message\":\"Child Development Account Bank updated successfully\"},\"data\":{\"childsNAHolderDetail\":{\"status\":\"\",\"message\":\"\",\"serviceAppId\":\"APP_012\"}}}";
        //--Change cdab - Error
        //testOutputJsonString = "{\"status\":{\"code\":\"900005\",\"message\":\"Invalid input\"},\"data\":{\"childsNAHolderDetail\":{\"reasonDesc\":\"Invalid reason\"}}}";

        String user = LoginManager.getSessionContainer().getNric();
        String url = AppUrls.SERVICES_UPDATE_URL_CDAB;

        ServerResponse serverResponse = new ServerResponse();

        try {
            JSONObject root = new JSONObject();
            JSONArray childIds = new JSONArray();

            for (ChildItem childItem : changeCdaBank.getChildItems()) {
                JSONObject jsonChildId = new JSONObject();
                jsonChildId.put(SerializedNames.SEC_SERVICE_COMMON_CHILD_IDS, childItem.getChild().getId());
                childIds.put(jsonChildId );
            }

            CdaBankAccount cdaBankAccount = changeCdaBank.getCdaBankAccount();
            GenericDataItem genericDataItem = cdaBankAccount.getChangeReason();

            if (genericDataItem != null) {
                root.put(SerializedNames.SEC_SERVICE_CHANGE_CDAB_CHANGE_REASON, genericDataItem.getId());
                root.put(SerializedNames.SEC_SERVICE_CHANGE_CDAB_REASON_OTHER, cdaBankAccount.getChangeReasonDescription());
            }

            root.put(SerializedNames.SEC_SERVICE_COMMON_CHILD_IDS, childIds);
            root.put(SerializedNames.SN_CDAB_ID, cdaBankAccount.getBank().getId());
            root.put(SerializedNames.SEC_SERVICE_CHANGE_CDAB_IS_DECLARED_1,
                    changeCdaBank.isDeclared1() ? SerializedNames.SN_YES : SerializedNames.SN_NO);
            root.put(SerializedNames.SEC_SERVICE_CHANGE_CDAB_IS_DECLARED_2,
                    changeCdaBank.isDeclared2() ? SerializedNames.SN_YES : SerializedNames.SN_NO);

            JSONObject jsonFinal = new JSONObject();
            jsonFinal.put(SerializedNames.SN_USER_ID, user);
            jsonFinal.put(SerializedNames.SEC_SERVICE_CHANGE_CDAB_ROOT, root);

            serverResponse = post(url, jsonFinal);

            JSONObject jsonUpdateCdaBank = serverResponse.getJsonDataResponse().getJSONObject(SerializedNames.SEC_SERVICE_CHANGE_CDAB_ROOT_DEV);

            if(jsonUpdateCdaBank != null){
                if (serverResponse.getResponseType() == ServerResponseType.SUCCESS) {
                    serverResponse.setAppId(jsonUpdateCdaBank.getString(SerializedNames.SEC_RESPONSE_SERVICE_APP_ID));
                }else if (serverResponse.getResponseType() == ServerResponseType.SERVICE_ERROR) {
                    String[] serialNames = new String[] {
                            SerializedNames.SN_CDAB_CHANGE_REASON,
                            SerializedNames.SN_CDAB_CHANGE_REASON_OTHER,
                            SerializedNames.SN_CDAB_ID
                    };

                    addValidationInfo(SerializedNames.SEC_SERVICE_CHANGE_CDAB_ROOT_DEV, serialNames, serverResponse.getJsonDataResponse(), changeCdaBank, SerializedNames.SEC_SERVICE_CHANGE_CDAB_ROOT);
                }
            }

        } catch (Exception e) {
            serverResponse.setResponseType(ServerResponseType.APPLICATION_ERROR);
            serverResponse.setMessage(e.getMessage());
        }

        return serverResponse;
    }

    //-------------------------- PSEA --------------------------------------------------------------

    @Override
    public ServerResponse updateTransferCdaToPsea(ServiceTransferToPsea transferToPsea){
        //Change psea - Success
        //testOutputJsonString = "{\"status\":{\"code\":\"200\",\"message\":\"Transferred Child Development Account to PSEA successfully\"},\"data\":{\"childsNAHolderDetail\":{\"status\":\"\",\"message\":\"\",\"serviceAppId\":\"APP_345\"}}}";
        //Change psea - Error
        //testOutputJsonString = "{\"status\":{\"code\":\"900005\",\"message\":\"Invalid input\"},\"data\":{\"childsNAHolderDetail\":{\"cdaBank\":{\"branchId\":\"Invalid branch\",\"bankAccNo\":\"Invalid Bank Account No\"}}}}";

        String user = LoginManager.getSessionContainer().getNric();
        String url = AppUrls.SERVICES_UPDATE_URL_PSEA;

        ServerResponse serverResponse = new ServerResponse();

        try {
            JSONObject root = new JSONObject();
            JSONArray childIds = new JSONArray();

            for (ChildItem childItem : transferToPsea.getChildItems()) {
                childIds.put(childItem.getChild().getId());
            }

            JSONObject jsonCgAuthorize = BankAccount.serialize(transferToPsea.getNewBankAccount());

            root.put(SerializedNames.SEC_SERVICE_COMMON_CHILD_IDS, childIds);
            root.put(SerializedNames.SEC_SERVICE_CHANGE_NAH_CG_AUTHORIZER, jsonCgAuthorize);
            root.put(SerializedNames.SEC_SERVICE_CHANGE_NAH_IS_DECLARED, transferToPsea.isDeclared());

            JSONObject jsonFinal = new JSONObject();
            jsonFinal.put(SerializedNames.SEC_SERVICE_CHANGE_NAH_ROOT, root);
            serverResponse = post(url, jsonFinal);
            JSONObject jsonTransferToPsea = serverResponse.getJsonDataResponse().getJSONObject(SerializedNames.SEC_SERVICE_CHANGE_NAH_ROOT);

            if(jsonTransferToPsea != null) {
                if (serverResponse.getResponseType() == ServerResponseType.SUCCESS) {
                    serverResponse.setAppId(jsonTransferToPsea.getString(SerializedNames.SEC_RESPONSE_SERVICE_APP_ID));
                } else if (serverResponse.getResponseType() == ServerResponseType.SERVICE_ERROR) {
                    addValidationInfo(SerializedNames.SEC_SERVICE_TRANSFER_PSEA_CDA_BANK, BankAccount.SERIAL_NAMES, jsonTransferToPsea, transferToPsea, AppConstants.EMPTY_STRING);
                }
            }
        } catch (Exception e) {
            serverResponse.setResponseType(ServerResponseType.APPLICATION_ERROR);
            serverResponse.setMessage(e.getMessage());
        }

        return serverResponse;
    }

    //------------------- BO -----------------------------------------------------------------------

    @Override
    public ServerResponse updateBirthOrder(ServiceChangeBo changeBo){
        //--Change bo - dummy proxy
//        /String jsonString = AppConstants.EMPTY_STRING;

        String user = LoginManager.getSessionContainer().getNric();
        String url = AppUrls.SERVICES_UPDATE_URL_BO;

        ServerResponse updateResponse = new ServerResponse();

        try {
            JSONObject root = new JSONObject();
            JSONArray childIds = new JSONArray();

            for (ChildItem childItem : changeBo.getChildItems()) {
                childIds.put(childItem.getChild().getId());
            }

            JSONArray bitmaps = new JSONArray();
            for (Bitmap bitmap : changeBo.getBitmaps().values()){
                bitmaps.put(bitmap);
            }

            root.put(SerializedNames.SEC_SERVICE_COMMON_CHILD_IDS, childIds);
            root.put("reasonDesc", changeBo.getReason());
            root.put("bitmap", bitmaps);
            root.put(SerializedNames.SEC_SERVICE_CHANGE_NAH_IS_DECLARED, changeBo.isDeclared());

            JSONObject jsonFinal = new JSONObject();
            jsonFinal.put(SerializedNames.SEC_SERVICE_CHANGE_NAH_ROOT, root);

            String jsonString = httpJsonCaller.post(url,jsonFinal.toString());
            JSONObject jsonResponse = new JSONObject(jsonString).getJSONObject(
                    SerializedNames.SEC_RESPONSE_DATA).getJSONObject(
                    SerializedNames.SEC_SERVICE_CHANGE_NAH_ROOT);

            updateResponse.setResponseType(ServerResponseType.SUCCESS);
            updateResponse.setAppId(jsonResponse.getString(SerializedNames.SEC_RESPONSE_SERVICE_APP_ID));
            updateResponse.setMessage(jsonResponse.getString(SerializedNames.SEC_RESPONSE_MESSAGE));

        } catch (JSONException e) {
            updateResponse.setResponseType(ServerResponseType.APPLICATION_ERROR);
            updateResponse.setMessage(e.getMessage());
        } catch (Exception e) {
            updateResponse.setResponseType(ServerResponseType.SERVICE_ERROR);
            updateResponse.setMessage(e.getMessage());
        }

        return updateResponse;
    }

    //------------------- CDAB_TC -------------------------------------------------------------------

    @Override
    public ServerResponse updateCdaBankTermsAndCond(ServiceCdabTc cdaBankTc){
        //Change cdabtc - Success
        //testOutputJsonString = "{\"status\":{\"code\":\"200\",\"message\":\"CDA Bant T & C acceptance updated successfully\"},\"data\":{\"childsCDABankTC\":{\"status\":\"\",\"message\":\"\",\"serviceAppId\":\"APP_678\"}}}";
        //Change cdabtc - Error
        //testOutputJsonString = "{\"status\":{\"code\":\"900005\",\"message\":\"Invalid input\"},\"data\":{\"childsCDABankTC\":{\"isDeclared1\":\"Invalid Declaration 1\",\"isDeclared2\":\"Invalid Declaration 2\"}}}";

        String user = LoginManager.getSessionContainer().getNric();
        String url = AppUrls.SERVICES_UPDATE_URL_CDABTC;

        ServerResponse serverResponse = new ServerResponse();

        try {
            JSONObject root = new JSONObject();
            JSONArray childIds = new JSONArray();

//            for (ChildItem childItem : cdaBankTc.getChildItems()) {
//                childIds.put(childItem.getChild().getId());
//            }

            for (ChildItem childItem : cdaBankTc.getChildItems()) {
                JSONObject jsonChildId = new JSONObject();
                jsonChildId.put(SerializedNames.SEC_SERVICE_COMMON_CHILD_IDS, childItem.getChild().getId());
                childIds.put(jsonChildId );
            }

            root.put(SerializedNames.SEC_SERVICE_COMMON_CHILD_IDS, childIds);
            root.put(SerializedNames.SEC_SERVICE_CHANGE_CDABTC_IS_DECLARED_1, cdaBankTc.isDeclared1());
            root.put(SerializedNames.SEC_SERVICE_CHANGE_CDABTC_IS_DECLARED_2, cdaBankTc.isDeclared1());

            JSONObject jsonFinal = new JSONObject();
            jsonFinal.put(SerializedNames.SEC_SERVICE_CHANGE_CDABTC_ROOT, root);
            serverResponse = post(url, jsonFinal);
            JSONObject jsonUpdateCdaBankTc = serverResponse.getJsonDataResponse().getJSONObject(SerializedNames.SEC_SERVICE_CHANGE_CDABTC_ROOT);

            if(jsonUpdateCdaBankTc != null){
                if (serverResponse.getResponseType() == ServerResponseType.SUCCESS) {
                    serverResponse.setAppId(jsonUpdateCdaBankTc.getString(SerializedNames.SEC_RESPONSE_SERVICE_APP_ID));
                } else if (serverResponse.getResponseType() == ServerResponseType.SERVICE_ERROR) {
                    String[] serialNames = new String[] {
                            SerializedNames.SEC_SERVICE_COMMON_CHILD_IDS,
                            SerializedNames.SEC_SERVICE_CHANGE_CDABTC_IS_DECLARED_1,
                            SerializedNames.SEC_SERVICE_CHANGE_CDABTC_IS_DECLARED_2
                    };

                    addValidationInfo(SerializedNames.SEC_SERVICE_CHANGE_CDABTC_ROOT, serialNames, serverResponse.getJsonDataResponse(), cdaBankTc, AppConstants.EMPTY_STRING);
                }
            }
        } catch (Exception e) {
            serverResponse.setResponseType(ServerResponseType.APPLICATION_ERROR);
            serverResponse.setMessage(e.getMessage());
        }

        return serverResponse;
    }

    //----------- OPEN_CDA -------------------------------------------------------------------------

    @Override
    public ServerResponse updateOpenCDA(ServiceOpenCda openCda){
        //--Change nan - dummy proxy
        //String jsonString = AppConstants.EMPTY_STRING;

        String user = LoginManager.getSessionContainer().getNric();
        String url = AppUrls.SERVICES_UPDATE_URL_OPENCDA;

        ServerResponse updateResponse = new ServerResponse();

        try {
            JSONObject root = new JSONObject();
            JSONArray childIds = new JSONArray();

            for (ChildItem childItem : openCda.getChildItems()) {
                childIds.put(childItem.getChild().getId());
            }

            JSONObject jsonNah = new JSONObject();
            CdaTrustee cdaTrustee = openCda.getCdaTrustee();

            jsonNah.put(SerializedNames.SN_ADULT_RELATIONSHIP,
                    cdaTrustee.getRelationshipType().getCode());
            jsonNah.put(SerializedNames.SN_PERSON_ID,
                    cdaTrustee.getId());
            jsonNah.put(SerializedNames.SN_PERSON_NRIC,
                    cdaTrustee.getNric());
            jsonNah.put(SerializedNames.SN_ADULT_ID_TYPE,
                    cdaTrustee.getIdentificationType() == null ?
                            null : cdaTrustee.getIdentificationType().getCode());
            jsonNah.put(SerializedNames.SN_ADULT_ID_NO,
                    cdaTrustee.getIdentificationNo());
            jsonNah.put(SerializedNames.SN_PERSON_NAME,
                    cdaTrustee.getName());
            jsonNah.put(SerializedNames.SN_ADULT_NATIONALITY,
                    cdaTrustee.getNationality()== null ?
                            null : cdaTrustee.getNationality().getId());
            jsonNah.put(SerializedNames.SN_PERSON_BIRTHDAY,
                    StringHelper.formatDate(cdaTrustee.getDateOfBirth()));
            jsonNah.put(SerializedNames.SN_ADULT_MODE_OF_COMM,
                    cdaTrustee.getModeOfCommunication() == null ?
                            null : cdaTrustee.getModeOfCommunication().getCode());
            jsonNah.put(SerializedNames.SN_ADULT_MOBILE,
                    cdaTrustee.getMobileNumber());
            jsonNah.put(SerializedNames.SN_ADULT_EMAIL,
                    cdaTrustee.getEmailAddress());
            jsonNah.put(SerializedNames.SN_ADULT_ADDR_TYPE,
                    cdaTrustee.getAddressType() == null ?
                            null : cdaTrustee.getAddressType().getCode());

            if(cdaTrustee.getPostalAddress() != null) {
                JSONObject jsonNahAddress = new JSONObject();
                Address nahPostalAddress = cdaTrustee.getPostalAddress();

                jsonNahAddress.put(SerializedNames.SN_ADDRESS_POST_CODE,
                        nahPostalAddress.getPostalCode());
                jsonNahAddress.put(SerializedNames.SN_ADDRESS_UNIT_NO,
                        String.format("#%s-%s", nahPostalAddress.getFloorNo(),
                                nahPostalAddress.getUnitNo()));
                jsonNahAddress.put(SerializedNames.SN_ADDRESS_BLOCK_HOUSE_NO,
                        nahPostalAddress.getBlockHouseNo());
                jsonNahAddress.put(SerializedNames.SN_ADDRESS_STREET,
                        nahPostalAddress.getStreetName());
                jsonNahAddress.put(SerializedNames.SN_ADDRESS_BUILDING,
                        nahPostalAddress.getBuildingName());
                jsonNahAddress.put(SerializedNames.SN_ADDRESS_ADDRESS1,
                        nahPostalAddress.getAddress1());
                jsonNahAddress.put(SerializedNames.SN_ADDRESS_ADDRESS2,
                        nahPostalAddress.getAddress2());

                jsonNah.put(SerializedNames.SEC_ADDRESS, jsonNahAddress);
            }

            root.put(SerializedNames.SEC_SERVICE_COMMON_CHILD_IDS, childIds);
            root.put(SerializedNames.SEC_SERVICE_CHANGE_NAH_PARTICULARS, jsonNah);
            root.put(SerializedNames.SEC_SERVICE_CHANGE_NAH_IS_DECLARED, openCda.isDeclared1());

            JSONObject jsonFinal = new JSONObject();
            jsonFinal.put(SerializedNames.SEC_SERVICE_CHANGE_NAH_ROOT, root);

            String jsonString = httpJsonCaller.post(url, jsonFinal.toString());
            JSONObject jsonResponse = new JSONObject(jsonString).getJSONObject(
                    SerializedNames.SEC_RESPONSE_DATA).getJSONObject(
                    SerializedNames.SEC_SERVICE_CHANGE_NAH_ROOT);

            updateResponse.setResponseType(ServerResponseType.SUCCESS);
            updateResponse.setAppId(jsonResponse.getString(SerializedNames.SEC_RESPONSE_SERVICE_APP_ID));
            updateResponse.setMessage(jsonResponse.getString(SerializedNames.SEC_RESPONSE_MESSAGE));

        } catch (JSONException e) {
            updateResponse.setResponseType(ServerResponseType.APPLICATION_ERROR);
            updateResponse.setMessage(e.getMessage());
        } catch (Exception e) {
            updateResponse.setResponseType(ServerResponseType.SERVICE_ERROR);
            updateResponse.setMessage(e.getMessage());
        }

        return updateResponse;
    }

}
