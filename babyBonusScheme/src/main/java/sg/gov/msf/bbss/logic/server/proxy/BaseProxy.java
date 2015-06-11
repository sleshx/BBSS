package sg.gov.msf.bbss.logic.server.proxy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sg.gov.msf.bbss.apputils.AppConstants;
import sg.gov.msf.bbss.apputils.connect.HttpJsonCaller;
import sg.gov.msf.bbss.apputils.util.StringHelper;
import sg.gov.msf.bbss.apputils.validation.ValidationInfo;
import sg.gov.msf.bbss.apputils.validation.ValidationMessage;
import sg.gov.msf.bbss.apputils.validation.ValidationType;
import sg.gov.msf.bbss.apputils.wizard.WizardBase;
import sg.gov.msf.bbss.logic.BabyBonusConstants;
import sg.gov.msf.bbss.logic.server.SerializedNames;
import sg.gov.msf.bbss.logic.server.login.LoginManager;
import sg.gov.msf.bbss.logic.server.response.ServerResponse;
import sg.gov.msf.bbss.logic.server.response.ServerResponseType;

/**
 * Created by bandaray
 */
public class BaseProxy {
    protected HttpJsonCaller httpJsonCaller = new HttpJsonCaller(
            LoginManager.getSessionContainer().getSessionToken());
    protected String testOutputJsonString;

    protected ServerResponse post(String url, JSONObject jsonRequest) throws Exception {
//        String jsonString = StringHelper.isStringNullOrEmpty(testOutputJsonString) ?
//                httpJsonCaller.post(url, jsonRequest.toString()) : testOutputJsonString;

        String jsonString = testOutputJsonString;

        if(StringHelper.isStringNullOrEmpty(jsonString)) {
            jsonString = httpJsonCaller.post(url, jsonRequest.toString());
        }

        ServerResponse serverResponse = new ServerResponse();
        JSONObject jsonResponse = new JSONObject(jsonString);
        JSONObject jsonStatus = jsonResponse.getJSONObject(SerializedNames.SEC_RESPONSE_STATUS);
        JSONObject jsonData = jsonResponse.optJSONObject(SerializedNames.SEC_RESPONSE_DATA);

        serverResponse.setCode(jsonStatus.getString(SerializedNames.SEC_RESPONSE_CODE));
        serverResponse.setMessage(jsonStatus.getString(SerializedNames.SEC_RESPONSE_MESSAGE));
        serverResponse.setJsonDataResponse(jsonData);

        if(serverResponse.getCode().equals(BabyBonusConstants.HTTP_OK)){
            serverResponse.setResponseType(ServerResponseType.SUCCESS);
        } else {
            serverResponse.setResponseType(ServerResponseType.SERVICE_ERROR);
        }

        return serverResponse;
    }

    //----------------------------------------------------------------------------------------------

    protected void addValidationInfo(String sectionName, String[] serialNames, JSONObject jsonParent, WizardBase wizardBase, String pageValidationSectionName) throws JSONException {
        JSONObject jsonObject;
        ValidationInfo validationInfo;
        ValidationMessage validationMessage;
        String validationSectionName = pageValidationSectionName.equals(AppConstants.EMPTY_STRING) ? sectionName : pageValidationSectionName;

        if(jsonParent != null && (jsonObject = jsonParent.optJSONObject(sectionName)) != null){
            validationInfo = new ValidationInfo(validationSectionName);

            for (String serialName : serialNames) {
                if((validationMessage = getValidationMessage(serialName, jsonObject)) != null){
                    validationInfo.addValidationMessage(validationMessage);
                }
            }

            if(validationInfo.hasAnyValidationMessages()){
                wizardBase.addPageValidations(validationSectionName, validationInfo);
            }
        }
    }

    protected void addValidationInfo(String sectionName, String serialName, JSONObject jsonParent, WizardBase wizardBase, String pageValidationSectionName) throws JSONException {
        addValidationInfo(sectionName, new String[]{serialName}, jsonParent, wizardBase, pageValidationSectionName);
    }

    protected void addArrayValidationInfo(String sectionName, String[] serialNames, JSONObject jsonParent, WizardBase wizardBase, String pageValidationSectionName, String keySerialName) throws JSONException {
        JSONArray jsonArray;
        JSONObject jsonObject;
        ValidationInfo validationInfo;
        ValidationInfo arrayValidationInfo;
        ValidationMessage validationMessage;
        String validationSectionName = pageValidationSectionName.equals(AppConstants.EMPTY_STRING) ? sectionName : pageValidationSectionName;
        int seqNo;

        if(jsonParent != null && (jsonArray = jsonParent.optJSONArray(sectionName)) != null) {
            validationInfo = new ValidationInfo(validationSectionName);

            int arrayLength = jsonArray.length();

            for (int index = 0; index < arrayLength; index++) {
                jsonObject = jsonArray.getJSONObject(index);
                seqNo = jsonObject.getInt(keySerialName);
                arrayValidationInfo = new ValidationInfo(Integer.toString(seqNo));

                for (String serialName : serialNames) {
                    if ((validationMessage = getValidationMessage(serialName, jsonObject)) != null) {
                        arrayValidationInfo.addValidationMessage(validationMessage);
                    }
                }

                if (arrayValidationInfo.hasAnyValidationMessages()) {
                    validationInfo.addArrayValidationInfo(seqNo, arrayValidationInfo);
                }
            }

            if (validationInfo.hasAnyValidationMessages()) {
                wizardBase.addPageValidations(validationSectionName, validationInfo);
            }
        }
    }

    protected ValidationMessage getValidationMessage(String serialName, JSONObject jsonObject) throws JSONException {
        ValidationMessage validationMessage = null;

        if(jsonObject.has(serialName)) {
            validationMessage = new ValidationMessage(ValidationType.SERVER);
            validationMessage.setSerialName(serialName);
            validationMessage.setMessage(jsonObject.getString(serialName));
        }

        return validationMessage;
    }

    protected JSONObject getJSONObject(JSONObject jsonObject, String... nodes){
        JSONObject jsonNode = jsonObject;

        for(String node : nodes){
            if((jsonNode = jsonNode.optJSONObject(node)) == null){
                return null;
            }
        }

        return jsonNode;
    }
}
