package sg.gov.msf.bbss.model.entity.common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import sg.gov.msf.bbss.logic.server.SerializedNames;

/**
 * Created by bandaray
 */
public class SupportingFile {

    private String code;
    private String fileName;

    public SupportingFile() {
    }

    public SupportingFile(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String message) {
        this.fileName = message;
    }

    public static JSONArray serialize(SupportingFile[] supportedFiles) throws JSONException {
        JSONArray supFiles = new JSONArray();

        for (SupportingFile supportingFile : supportedFiles){
            supFiles.put(supportingFile.getCode());
        }

        return supFiles;
    }

//    JSONArray supFiles = supFileRoot.getJSONArray(SerializedNames.SEC_SUPPORTING_FILES);
    public static SupportingFile[] deserialize(JSONArray jsonSupportingFiles) throws JSONException {

        int supFilesLength = jsonSupportingFiles.length();
        JSONObject jonsonFile = null;
        ArrayList<SupportingFile> supportedFiles = new ArrayList<SupportingFile>();

        for (int supFileIndex = 0; supFileIndex < supFilesLength; supFileIndex ++){
            jonsonFile = jsonSupportingFiles.getJSONObject(supFileIndex);
            supportedFiles.add(new SupportingFile(jonsonFile.getString(SerializedNames.SN_SUPPORTING_FILE_DOC_ID)));
        }

        return supportedFiles.toArray(new SupportingFile[0]);
    }
}
