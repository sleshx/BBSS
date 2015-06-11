package sg.gov.msf.bbss.logic.server.response;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by bandaray
 */
public class ServerResponse {
    private String code;
    private String message;
    private String appId;
    private ServerResponseType responseType;
    private File file;
    private JSONObject jsonDataResponse;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public ServerResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(ServerResponseType responseType) {
        this.responseType = responseType;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public JSONObject getJsonDataResponse() {
        return jsonDataResponse;
    }

    public void setJsonDataResponse(JSONObject jsonResponse) {
        this.jsonDataResponse = jsonResponse;
    }
}
